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

    public I18nBundle() {
        I18nMethodReader i18nMethodReader = new I18nMethodReader(getI18nClass());

        lblsMsgs             = i18nMethodReader.read();
        methodSwitchLanguage = i18nMethodReader.getMethodSwitchLanguage();
        
        LOG.info("loaded {} labels and messages. Keys={}", lblsMsgs.size(), new TreeSet<>(lblsMsgs.keySet()));
    }

    Class<? extends I18n> getI18nClass() {
        return I18n.class;
    }

    public I18n get(Language language) {
        return (I18n)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { getI18nClass() }, new I18nHandler(language));
    }

    private class I18nHandler implements InvocationHandler {
        private int languageIndex;

        private I18nHandler(Language language) {
            languageIndex = language.getIndex();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.equals(methodSwitchLanguage)) {
                languageIndex = ((Language)args[0]).getIndex();
                return null;
            } else {
                String mname = method.getName();
                String[] translations = lblsMsgs.get(mname);
                if (translations != null) {
                    String translation = translations[languageIndex];
                    if (translation != null) {
                        if (args == null || args.length == 0) {
                            return translation;
                        } else {
                            return MessageFormat.format(translation, args);
                        }
                    } else {
                        LOG.debug("no translation found for {}() and language index [{}] in translations {}, returning the EMPTY string", mname, languageIndex, Arrays.toString(translations));
                        return "";
                    }
                } else {
                    LOG.debug("no translations found for {}(), returning the EMPTY string", mname);
                    return "";
                }
            }
        }
    }
}
