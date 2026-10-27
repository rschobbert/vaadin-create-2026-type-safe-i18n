package de.emesit.demo.i18n.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.emesit.demo.i18n.I18n;
import de.emesit.demo.i18n.Language;
import de.emesit.demo.util.Strings;

public class I18nMethodReader {
    private static final Logger LOG = LoggerFactory.getLogger(I18nMethodReader.class);

    private static final String MNAME_TRANSLATION_VALUE = "value";
    private static final String MNAME_SWITCH_LANGUAGE   = "switchLanguage";

    private final Class<? extends I18n> i18nClass;
    private Method methodSwitchLanguage;

    public I18nMethodReader(Class<? extends I18n> i18nClass) {
        this.i18nClass = i18nClass;
    }

    public Map<String, String[]> read() throws IllegalStateException {
        LOG.debug("loadI18nMethods() for {}", i18nClass.getSimpleName());
        Map<String, String[]> lblsMsgs = new HashMap<>();
        int count = 0;

        for (Method m : i18nClass.getMethods()) {
            String mname = m.getName();

            if (mname.equals(MNAME_SWITCH_LANGUAGE)) {
                this.methodSwitchLanguage = validateMethodParams(m, Language.class);
            } else {
                if (lblsMsgs.containsKey(mname)) {
                    throw new IllegalStateException("key [" + mname + "] exists twice");
                }

                String[] va = new String[Language.count()];
                lblsMsgs.put(mname, va);
                count++;

                int translationCount = 0;
                for (Language lang : Language.values()) {
                    Annotation annotation = m.getAnnotation(lang.getAnnotationClass());
                    if (annotation != null) {
                        va[lang.getIndex()] = getTranslation(lang, annotation);
                        translationCount++;
                    }
                }

                if (translationCount == 0) {
                    throw new IllegalStateException("method [" + mname + "] has no translations");
                }
            }
        }

        consistencyCheck(lblsMsgs);

        LOG.info("loaded {} i18n methods", count);
        return lblsMsgs;
    }

    private String getTranslation(Language lang, Annotation annotation) {
        try {
            return (String) lang.getAnnotationClass().getMethod(MNAME_TRANSLATION_VALUE).invoke(annotation);
        } catch (Exception exc) {
            throw new IllegalStateException("In language " + lang.name() + ": error while calling " + MNAME_TRANSLATION_VALUE + "() on annotation " + annotation, exc);
        }
    }

    private Method validateMethodParams(Method m, Class<?>... expectedParameterTypes) {
        Class<?>[] parameterTypes = m.getParameterTypes();
        int numParamTypes    = (parameterTypes         != null) ? parameterTypes.length         : 0;
        int numExpParamTypes = (expectedParameterTypes != null) ? expectedParameterTypes.length : 0;

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
        List<Language> mandatoryLangs = Arrays.stream(Language.values())
                .filter(Language::isMandatory)
                .collect(Collectors.toList());
        int numMandatory = mandatoryLangs.size();
        int numMissingTranslations = 0;

        Iterator<Entry<String, String[]>> it = lblsMsgs.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String[]> nextEntry = it.next();

            String   key          = nextEntry.getKey();
            String[] translations = nextEntry.getValue();

            for (int i = 0; i < numMandatory; i++) {
                Language nextMandatoryLang = mandatoryLangs.get(i);

                String mandatoryTranslation = translations[nextMandatoryLang.getIndex()];
                if (Strings.isBlank(mandatoryTranslation)) {
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
}
