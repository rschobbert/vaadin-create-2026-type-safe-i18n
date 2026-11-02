package de.emesit.demo.i18n;

import de.emesit.easi18n.annotation.TranslationDE;
import de.emesit.easi18n.annotation.TranslationEN;
import de.emesit.easi18n.annotation.TranslationES;

public interface I18nMsg {
    
    @TranslationDE("extra Nachricht")
    @TranslationEN("extra Message")
    @TranslationES("Mensaje adicional")
    String extraMsg();
}
