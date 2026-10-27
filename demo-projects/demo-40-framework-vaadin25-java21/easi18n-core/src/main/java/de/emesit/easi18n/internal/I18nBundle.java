package de.emesit.easi18n.internal;

import static de.emesit.easi18n.internal.I18nMethodTranslationReader.methodNameToConstant;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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

import de.emesit.easi18n.ApplicationTranslations;
import de.emesit.easi18n.CustomTranslationService;
import de.emesit.easi18n.I18nLanguage;

public class I18nBundle {
    private static final Logger LOG = LoggerFactory.getLogger(I18nBundle.class);
    
    final Map<String, String[]> lblsMsgs;
    final SortedSet<String>     i18nMethodNames;
    final Method                methodSwitchLanguage;

    private final Class<? extends I18nBase> i18nClass;
    private final Class<?>[] proxyInterfaces;
    
    private final Set<I18nLanguage>        translatedLanguages = new HashSet<>();
    private final CustomTranslationService translationService;

    public I18nBundle(I18nClassSupplier i18nClassSupplier, 
            I18nMethodReader i18nMethodReader, 
            ApplicationTranslations applicationTranslations, 
            Optional<CustomTranslationService> optionalTranslationService)
    {
        i18nClass            = Objects.requireNonNull(i18nClassSupplier, "'i18nClassSupplier' must not be null").get();
        translationService   = optionalTranslationService.orElse(null);
        proxyInterfaces      = i18nClassSupplier.getProxyInterfaces();
        lblsMsgs             = i18nMethodReader.read();
        methodSwitchLanguage = i18nMethodReader.getMethodSwitchLanguage();
        i18nMethodNames      = new TreeSet<>(lblsMsgs.keySet());
        
        translatedLanguages.addAll(i18nMethodReader.getTranslatedLanguages());
        
        LOG.info("loaded {} labels and messages. Translated languages={}.\nKeys={}", lblsMsgs.size(), translatedLanguages, new TreeSet<>(lblsMsgs.keySet()));
    }

    public I18nBase get(I18nLanguage language) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("creating PROXY with proxyInterfaces={}", Arrays.toString(proxyInterfaces));
        }
        return (I18nBase)Proxy.newProxyInstance(i18nClass.getClassLoader(), proxyInterfaces, new I18nHandler(language));
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

    private String getTranslation(I18nLanguage language, String methodName, Object[] args) {
        String customTranslation = getCustomTranslation(language, methodName, args);
        boolean hasCustomTranslation = ( customTranslation != null && !customTranslation.isBlank() );
        return hasCustomTranslation ? customTranslation : getInternalTranslation(language, methodName, args);
    }

    private String getCustomTranslation(I18nLanguage language, String methodName, Object[] args) {
        return (translationService != null)
                ? translationService.getTranslation(language, methodNameToConstant(methodName), args)
                : null;
    }

    private String getInternalTranslation(I18nLanguage language, String methodName, Object[] args) {
        String translation = null;
        String[] defaultTranslations = lblsMsgs.get(methodName);
        int languageIndex = language.getIndex();
        if (languageIndex < defaultTranslations.length) {
            translation = defaultTranslations[languageIndex];
        }
        if (translation != null) {
            return I18nMethodTranslationReader.formatTranslation(translation, args);
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
