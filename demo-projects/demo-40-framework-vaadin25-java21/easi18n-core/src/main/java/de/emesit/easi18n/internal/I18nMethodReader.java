package de.emesit.easi18n.internal;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.emesit.easi18n.I18nLanguage;
import de.emesit.easi18n.I18nLanguageSupplier;

public class I18nMethodReader {
    private static final Logger LOG = LoggerFactory.getLogger(I18nMethodReader.class);

    private static final String MNAME_SWITCH_LANGUAGE   = "switchLanguage";

    final I18nClassSupplier           i18nClassSupplier;
    final I18nLanguageSupplier        languageSupplier;
    final I18nMethodTranslationReader translationReader;

    private Method methodSwitchLanguage;

    private final Set<I18nLanguage> translatedLanguages = new HashSet<>();


    public I18nMethodReader(I18nClassSupplier i18nClassSupplier,  I18nLanguageSupplier languageSupplier, I18nMethodTranslationReader translationReader) {
        this.i18nClassSupplier = Objects.requireNonNull(i18nClassSupplier, "'i18nClassSupplier' must not be null");
        this.languageSupplier  = languageSupplier;
        this.translationReader = Objects.requireNonNull(translationReader, "'translationReader' must not be null");
    }

    public Map<String, String[]> read() throws IllegalStateException {
        Map<String, String[]> lblsMsgs = new HashMap<>();
        int count = 0;

        Class<?>[] allInterfaces = i18nClassSupplier.getProxyInterfaces();
        for (Class<?> nextInterface : allInterfaces) {
            LOG.debug("Loading methods from: {}", nextInterface.getName());

            for (Method m : nextInterface.getMethods()) {
                String mname = m.getName();
    
                if (mname.equals(MNAME_SWITCH_LANGUAGE)) {
                    
                    if (m.getDeclaringClass() != I18nBase.class) {
                        throw new IllegalStateException("The method ["+MNAME_SWITCH_LANGUAGE+"] must not be declared in ["+nextInterface.getName()+"]!");
                    }

                    // prevent multiple validations of switchLanguage method, which may happen through inheritance
                    if (this.methodSwitchLanguage == null) {
                        this.methodSwitchLanguage = validateMethodParams(m, I18nLanguage.class);
                    }

                } else {
                    if (lblsMsgs.containsKey(mname)) {
                        switch (i18nClassSupplier.getMethodOverrideMode()) {
                        case FAILS:
                            throw new IllegalStateException("key [" + mname + "] exists twice");
                        case FIRST_WINS:
                            continue; 
                        case LAST_WINS:
                            setLanguagesTranslations(lblsMsgs, m, mname);
                        }
                    } else {
                        setLanguagesTranslations(lblsMsgs, m, mname);
                        count++;
                    }
                }
            }
        }
 
        consistencyCheck(lblsMsgs);

        LOG.info("loaded {} i18n methods", count);
        return lblsMsgs;
    }

    private void setLanguagesTranslations(Map<String, String[]> lblsMsgs, Method m, String mname) {
        String[] va = new String[languageSupplier.get().length];
        lblsMsgs.put(mname, va);
        setLanguagesTranslations(m, va);
    }

    private void setLanguagesTranslations(Method m, String[] va) {
        int translationCount = 0;
        for (I18nLanguage language : languageSupplier.get()) {
            
            String staticTranslation = translationReader.readTranslation(m, language);
            
            if (staticTranslation != null) {
                va[language.getIndex()] = staticTranslation;
                translationCount++;
                translatedLanguages.add(language);
            }
        }
        if (translationCount == 0) {
            throw new IllegalStateException("method [" + m.getName() + "] has no translations");
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
        Class<? extends I18nBase> i18nClass = i18nClassSupplier.get();
        if ( ! I18nBase.class.isAssignableFrom(i18nClass)) {
            throw new IllegalStateException("The configured ["+i18nClass.getName()+"] interface does not inherit from ["+I18nBase.class.getName()+"]!");
        }

        if (methodSwitchLanguage == null) {
            throw new IllegalStateException("read() method has not been called yet");
        }

        return methodSwitchLanguage;
    }

    public Set<I18nLanguage> getTranslatedLanguages() {
        return translatedLanguages;
    }
}
