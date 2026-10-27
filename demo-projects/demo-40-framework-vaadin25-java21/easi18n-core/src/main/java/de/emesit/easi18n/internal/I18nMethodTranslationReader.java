package de.emesit.easi18n.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import de.emesit.easi18n.ApplicationTranslations;
import de.emesit.easi18n.I18nLanguage;

public class I18nMethodTranslationReader {
    private static final String MNAME_TRANSLATION_VALUE = "value";
    
    // for inserting an underscore 
    // - before every capital letter which has a small letter or a number in front
    // - before every number
    // 
    // The regex matches the splitting point, so no characters are actually replaced
    //
    // First half of the regex: (?<=[a-z0-9])(?=[A-Z])
    // * (?<=[a-z0-9]) (Positive Lookbehind): look left . There must be a lowercase letter or a number
    // * (?=[A-Z])     (Positive Lookahead) : look right. There must be a uppercase letter
    // 
    // Second half of the regex: (?<=[a-zA-Z])(?=[0-9])
    // * (?<=[a-zA-Z]) (Positive Lookbehind): look left . There must be a lowercase or uppercase letter
    // * (?=[0-9])     (Positive Lookahead) : look right. There must be a number
    private static final Pattern CAMEL_CASE_SEPARATION_REGEX  = Pattern.compile("(?<=[a-z0-9])(?=[A-Z])|(?<=[a-zA-Z])(?=[0-9])");
    
    private final ApplicationTranslations applicationTranslations;

    public I18nMethodTranslationReader(ApplicationTranslations applicationTranslations) {
        this.applicationTranslations = applicationTranslations;
    }
    
    public static String methodNameToConstant(String methodName) {
        if (methodName == null || methodName.isBlank()) {
            return methodName;
        }
        return CAMEL_CASE_SEPARATION_REGEX .matcher(methodName).replaceAll("_");
    }

    /**
     * @param method the method of the I18n interface, i.e. the translationKey
     * @param language the language to translate to
     * @param args the arguments for the MessageFormat, or null if none or if parameters should stay as is
     */
    public String readTranslation(Method method, I18nLanguage language) {
        String constantKey = methodNameToConstant(method.getName());

        if (applicationTranslations != null) {
            String appTranslation = applicationTranslations.getTranslation(language, constantKey);
            if (appTranslation != null && !appTranslation.isBlank()) {
                return formatTranslation(appTranslation, null);
            }
        }

        Class<? extends Annotation> annotationClass = language.getAnnotationClass();
        Annotation annotation = method.getAnnotation(annotationClass);
        if (annotation != null) {
            String annotatedValue = extractAnnotationValue(annotationClass, annotation, () -> 
                "In language " + language.getCode() + ": error while calling " + MNAME_TRANSLATION_VALUE + "() on annotation " + annotation
            );
            return formatTranslation(annotatedValue, null);
        }

        return null;
    }

    public static String formatTranslation(String rawTranslation, Object[] args) {
        if (rawTranslation != null && args != null && args.length > 0) {
            return MessageFormat.format(rawTranslation, args);
        }
        return rawTranslation;
    }

    private String extractAnnotationValue(Class<? extends Annotation> annotationClass, Annotation annotation, Supplier<String> exceptionMsgSupplier) {
        try {
            return (String)annotationClass.getMethod(MNAME_TRANSLATION_VALUE).invoke(annotation);
        } catch (Exception exc) {
            throw new IllegalStateException(exceptionMsgSupplier.get(), exc);
        }
    }
}
