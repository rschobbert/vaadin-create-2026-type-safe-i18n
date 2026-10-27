package de.emesit.demo.i18n;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Translation annotation for Spanish.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TranslationES {
    String value();
}
