package de.emesit.easi18n;

import java.util.Locale;

public interface I18nLocalLanguage {
    I18nLanguage getLanguage();
    I18nCountry  getCountry();
    
    default Locale toLocale() {
        return Locale.of(getLanguage().getCode(), getCountry().getCode());
    }
}
