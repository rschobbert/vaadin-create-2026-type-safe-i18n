package de.emesit.demo.i18n.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.emesit.demo.i18n.I18n;
import de.emesit.demo.i18n.Language;

@Component
public class I18nBundle {
    private static final Logger LOG = LoggerFactory.getLogger(I18nBundle.class);

    final Map<String, String[]> lblsMsgs;
    final Method                methodSwitchLanguage;

    private final Class<? extends I18n> i18nClass = I18n.class;

    public I18nBundle() {
        I18nMethodReader i18nMethodReader = new I18nMethodReader(i18nClass);
        lblsMsgs             = i18nMethodReader.read();
        methodSwitchLanguage = i18nMethodReader.getMethodSwitchLanguage();

        LOG.info("loaded {} labels and messages. Keys={}", lblsMsgs.size(), new TreeSet<>(lblsMsgs.keySet()));
    }
    
    public I18n get(Language language) {
        return (I18n)Proxy.newProxyInstance(i18nClass.getClassLoader(), new Class[] { i18nClass }, new I18nHandler(language));
    }

    /**
     * Internal method which returns the translation for the specified methodName in the specified language
     * with the additional args applied (if applicable - i.e. if the I18n method has parameters).
     * @param language the desired language
     * @param methodName the I18n method to get the translation for
     * @param args the arguments for the I18n method, if any
     * @return the translation for the specified methodName in the specified language
     */
    private String getTranslation(Language language, String methodName, Object[] args) {
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
        private Language language;

        private I18nHandler(Language language) {
            this.language = language;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.equals(methodSwitchLanguage)) {
                language = ((Language)args[0]);
                return null;
            } else {
                return getTranslation(language, method.getName(), args);
            }
        }
    }
}
