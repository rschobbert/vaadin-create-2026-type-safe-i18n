package de.emesit.easi18n.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.emesit.easi18n.I18nLanguage;
import de.emesit.easi18n.I18nLanguageSupplier;

public class I18nMethodReader {
    private static final Logger LOG = LoggerFactory.getLogger(I18nMethodReader.class);

    private static final String MNAME_TRANSLATION_VALUE = "value";
    private static final String MNAME_SWITCH_LANGUAGE   = "switchLanguage";

    final I18nClassSupplier         i18nClassSupplier;
    final Class<? extends I18nBase> i18nClass;
    final I18nLanguageSupplier      languageSupplier;

    private Method methodSwitchLanguage;

    private final Set<I18nLanguage> translatedLanguages = new HashSet<>();

    public I18nMethodReader(I18nClassSupplier i18nClassSupplier, I18nLanguageSupplier languageSupplier) {
        this.i18nClassSupplier = Objects.requireNonNull(i18nClassSupplier, "'i18nClassSupplier' must not be null");
        this.i18nClass         = i18nClassSupplier.get();
        this.languageSupplier  = languageSupplier;
    }

    public Map<String, String[]> read() throws IllegalStateException {
        LOG.debug("loadI18nMethods() for {}", i18nClass.getSimpleName());
        Map<String, String[]> lblsMsgs = new HashMap<>();
        int count = 0;

        for (Method m : i18nClass.getMethods()) {
            String mname = m.getName();

            if (mname.equals(MNAME_SWITCH_LANGUAGE)) {
                this.methodSwitchLanguage = validateMethodParams(m, I18nLanguage.class);
            } else {
                if (lblsMsgs.containsKey(mname)) {
                    throw new IllegalStateException("key [" + mname + "] exists twice");
                }

                String[] va = new String[languageSupplier.get().length];
                lblsMsgs.put(mname, va);
                count++;

                setLanguagesTranslations(m, va);
            }
        }

        consistencyCheck(lblsMsgs);

        LOG.info("loaded {} i18n methods", count);
        return lblsMsgs;
    }

    private void setLanguagesTranslations(Method m, String[] va) {
        int translationCount = 0;
        for (I18nLanguage language : languageSupplier.get()) {
            translationCount = setLanguageTranslation(m, va, translationCount, language);
        }
        if (translationCount == 0) {
            throw new IllegalStateException("method [" + m.getName() + "] has no translations");
        }
    }

    private int setLanguageTranslation(Method m, String[] va, int translationCount, I18nLanguage language) {
        Class<? extends Annotation> annotationClass = language.getAnnotationClass();
        Annotation annotation = m.getAnnotation(annotationClass);
        if (annotation != null) {
            va[language.getIndex()] = 
                    getLanguageTranslation(
                            annotationClass, 
                            annotation, 
                            () -> "In language " + language.getCode() + ": error while calling " + MNAME_TRANSLATION_VALUE + "() on annotation " + annotation);
            translationCount++;
            translatedLanguages.add(language);
        }
        return translationCount;
    }

    String getLanguageTranslation(Class<? extends Annotation> annotationClass, Annotation annotation, Supplier<String> exceptionMsgSupplier) {
        try {
            return (String)annotationClass.getMethod(MNAME_TRANSLATION_VALUE).invoke(annotation);
        } catch (Exception exc) {
            throw new IllegalStateException(exceptionMsgSupplier.get(), exc);
        }
    }

    private Method validateMethodParams(Method m, Class<?>... expectedParameterTypes) {
        Class<?>[] parameterTypes = m.getParameterTypes();
        int numParamTypes    = parameterTypes.length;
        int numExpParamTypes = expectedParameterTypes.length;

        if (numParamTypes != numExpParamTypes) {
            String actualParams   = Arrays.toString(parameterTypes        );
            String expectedParams = Arrays.toString(expectedParameterTypes);

            throw new IllegalStateException(String.format(
                "method [%s] with wrong number of parameterTypes (expected %d but was %d): %s, expected: %s",
                m.getName(), numExpParamTypes, numParamTypes, actualParams, expectedParams
            ));
        }
        for (int i = 0; i < numParamTypes; i++) {
            if (parameterTypes[i] != expectedParameterTypes[i]) {
                throw new IllegalStateException(String.format(
                        "method [%s] with wrong parameter type at index [%d]: expected [%s] but was [%s]",
                        m.getName(), i, expectedParameterTypes[i].getName(), parameterTypes[i].getName()
                    ));
            }
        }
        return m;
    }

    void consistencyCheck(Map<String, String[]> lblsMsgs) {
        List<I18nLanguage> mandatoryLangs = Arrays.stream(languageSupplier.get())
                .filter(I18nLanguage::isMandatory)
                .toList();
        int numMandatory = mandatoryLangs.size();
        int numMissingTranslations = 0;

        Iterator<Entry<String, String[]>> it = lblsMsgs.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String[]> nextEntry = it.next();

            String   key          = nextEntry.getKey();
            String[] translations = nextEntry.getValue();

            for (int i = 0; i < numMandatory; i++) {
                I18nLanguage nextMandatoryLang = mandatoryLangs.get(i);

                String mandatoryTranslation = translations[nextMandatoryLang.getIndex()];
                if ( mandatoryTranslation == null || mandatoryTranslation.isBlank() ) {
                    LOG.warn("No translation defined for key [{}] and language [{}]", key, nextMandatoryLang);
                    numMissingTranslations++;
                }
            }
        }
        if (numMissingTranslations > 0) {
            throw new IllegalStateException("Missing "+numMissingTranslations+" translations. See log for details.");
        }
    }

    public Method getMethodSwitchLanguage() {
        if (methodSwitchLanguage == null) {
            throw new IllegalStateException("read() method has not been called yet or switchLanguage() method is missing in interface "+i18nClass.getName());
        }
        return methodSwitchLanguage;
    }
    
    public Set<I18nLanguage> getTranslatedLanguages() {
        return translatedLanguages;
    }
}
