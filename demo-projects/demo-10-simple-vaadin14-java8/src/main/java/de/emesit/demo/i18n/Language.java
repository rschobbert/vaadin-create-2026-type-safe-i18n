package de.emesit.demo.i18n;

import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Objects;

public enum Language {
    DE(true , TranslationDE.class), 
    EN(true , TranslationEN.class), 
    ES(false, TranslationES.class);

    public static final Language[] VALUES = Language.values();

    public static int count() {
        return VALUES.length;
    }

    public static Language of(Locale locale) {
        try {
            return Language.valueOf(locale.getLanguage().toUpperCase());
        } catch (Exception e) {
            return EN; // Standard-Fallback
        }
    }

    private final Locale  locale = Locale.forLanguageTag(name().toLowerCase());
    private final boolean mandatory;

    private final Class<? extends Annotation> annotationClass;

    private Language(boolean mandatory, Class<? extends Annotation> annotationClass) {
        this.mandatory       = mandatory;
        this.annotationClass = annotationClass;
    }

    public int getIndex() {
        return ordinal();
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public Class<? extends Annotation> getAnnotationClass() {
        return annotationClass;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getName() {
        return locale.getDisplayLanguage(locale);
    }

    public String getName(Locale nameLocale) {
        return locale.getDisplayLanguage( Objects.requireNonNull(nameLocale, "'nameLocale' must not be null") );
    }

    public String getName(Language nameLanguage) {
        return locale.getDisplayLanguage( Objects.requireNonNull(nameLanguage, "'nameLanguage' must not be null").getLocale() );
    }
}
