package de.emesit.easi18n.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.emesit.easi18n.I18nLanguage;
import de.emesit.easi18n.CustomTranslationService;

public class I18nBundle {
    private static final Logger LOG = LoggerFactory.getLogger(I18nBundle.class);
    
    final Map<String, String[]> lblsMsgs;
    final SortedSet<String>     i18nMethodNames;
    final Method                methodSwitchLanguage;

    private final Class<? extends I18nBase> i18nClass;
    
    private final Set<I18nLanguage>        translatedLanguages = new HashSet<>();
    private final CustomTranslationService translationService;

    public I18nBundle(I18nClassSupplier i18nClassSupplier, I18nMethodReader i18nMethodReader, Optional<CustomTranslationService> optionalTranslationService) {
        i18nClass            = Objects.requireNonNull(i18nClassSupplier, "'i18nClassSupplier' must not be null").get();
        lblsMsgs             = i18nMethodReader.read();
        methodSwitchLanguage = i18nMethodReader.getMethodSwitchLanguage();
        i18nMethodNames      = new TreeSet<>(lblsMsgs.keySet());
        translationService   = optionalTranslationService.orElse(null);
        
        translatedLanguages.addAll(i18nMethodReader.getTranslatedLanguages());
        
        LOG.info("loaded {} labels and messages. Translated languages={}.\nKeys={}", lblsMsgs.size(), translatedLanguages, new TreeSet<>(lblsMsgs.keySet()));
    }

    public I18nBase get(I18nLanguage language) {
        return (I18nBase)Proxy.newProxyInstance(i18nClass.getClassLoader(), new Class[] { i18nClass }, new I18nHandler(language));
    }

    public SortedSet<String> getI18nMethodNames() {
        return i18nMethodNames;
    }
    
    /**
     * Returns all translation keys, which are all method names (except 'switchLanguage') converted to
     * constant names (uppercase with underscore at camelCase boundaries).
     * @return all translation keys
     */
    public SortedSet<String> getTranslationKeys() {
        return getI18nMethodNames().stream().map(it -> methodNameToConstant(it)).collect(Collectors.toCollection(TreeSet::new));
    }
    
    public Map<String, String[]> getRawTranslations() {
        return Collections.unmodifiableMap(lblsMsgs);
    }

    
    public Set<I18nLanguage> getTranslatedLanguages() {
        return Collections.unmodifiableSet(translatedLanguages);
    }

    /**
     * Converts a method name to a constant name, by inserting an underscore at appropriate positions
     * @param methodName
     * @return
     */
    static String methodNameToConstant(String methodName) {
        if (methodName == null || methodName.isBlank()) {
            return methodName;
        }
        // inserts an underscore 
        // - before every capital letter which has a small letter or a number in front
        // - before every number
        return methodName.replaceAll("(?<=[a-z0-9])(?=[A-Z])|(?<=[a-zA-Z])(?=[0-9])", "_").toUpperCase();
    }

    private String getTranslation(I18nLanguage language, String methodName, Object[] args) {
        String customTranslation = getCustomTranslation(language, methodName, args);
        boolean hasCustomTranslation = ( customTranslation != null && !customTranslation.isBlank() );
        return hasCustomTranslation ? customTranslation : getAnnotatedTranslation(language, methodName, args);
    }

    private String getCustomTranslation(I18nLanguage language, String methodName, Object[] args) {
        return (translationService != null)
                ? translationService.getTranslation(language, methodNameToConstant(methodName), args)
                : null;
    }

    private String getAnnotatedTranslation(I18nLanguage language, String methodName, Object[] args) {
        String translation = null;
        String[] defaultTranslations = lblsMsgs.get(methodName);
        int languageIndex = language.getIndex();
        if (languageIndex < defaultTranslations.length) {
            translation = defaultTranslations[languageIndex];
        }
        if (translation != null) {
            if (args == null || args.length == 0) {
                return translation;
            } else {
                return MessageFormat.format(translation, args);
            }
        } else {
            LOG.debug("no translation found for {}() and language [{}] in translations {}, returning the EMPTY string", methodName, language, Arrays.toString(defaultTranslations));
            return "";
        }
    }

    /**
     * The inner (non-static) stateful InvocationHandler which does the method dispatching.
     * When the switchLanguage(Language) method of the I18n interface is called, the language is set in this
     * instance. Otherwise it returns the translation for the I18n method (= translation key) called.
     */
    private class I18nHandler implements InvocationHandler {
        private I18nLanguage language;

        private I18nHandler(I18nLanguage language) {
            this.language = language;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.equals(methodSwitchLanguage)) {
                language = ((I18nLanguage)args[0]);
                return null;
            } else {
                return getTranslation(language, method.getName(), args);
            }
        }
    }
}
