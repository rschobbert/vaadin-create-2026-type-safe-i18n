package de.emesit.demo.i18n;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Translation annotation for German.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TranslationDE {
    String value();
}
