package de.emesit.demo.i18n;

import de.emesit.easi18n.annotation.TranslationDE;
import de.emesit.easi18n.annotation.TranslationEN;

public interface I18nMsg {
    
    @TranslationDE("extra Nachricht")
    @TranslationEN("extra Message")
    String extraMsg();
}
