package de.emesit.easi18n.vaadin;

import de.emesit.easi18n.annotation.TranslationBG;
import de.emesit.easi18n.annotation.TranslationCZ;
import de.emesit.easi18n.annotation.TranslationDE;
import de.emesit.easi18n.annotation.TranslationDK;
import de.emesit.easi18n.annotation.TranslationEE;
import de.emesit.easi18n.annotation.TranslationEN;
import de.emesit.easi18n.annotation.TranslationES;
import de.emesit.easi18n.annotation.TranslationFI;
import de.emesit.easi18n.annotation.TranslationGR;
import de.emesit.easi18n.annotation.TranslationHR;
import de.emesit.easi18n.annotation.TranslationHU;
import de.emesit.easi18n.annotation.TranslationIS;
import de.emesit.easi18n.annotation.TranslationIT;
import de.emesit.easi18n.annotation.TranslationLT;
import de.emesit.easi18n.annotation.TranslationLV;
import de.emesit.easi18n.annotation.TranslationNL;
import de.emesit.easi18n.annotation.TranslationNO;
import de.emesit.easi18n.annotation.TranslationPL;
import de.emesit.easi18n.annotation.TranslationPT;
import de.emesit.easi18n.annotation.TranslationRO;
import de.emesit.easi18n.annotation.TranslationRU;
import de.emesit.easi18n.annotation.TranslationSE;
import de.emesit.easi18n.annotation.TranslationSI;
import de.emesit.easi18n.annotation.TranslationSK;
import de.emesit.easi18n.annotation.TranslationTR;
import de.emesit.easi18n.annotation.TranslationUA;
import de.emesit.easi18n.annotation.TranslationZH;
import de.emesit.easi18n.internal.I18nBase;

public interface I18nVaadin extends I18nBase {
    
    @TranslationDE("Sprache")
    @TranslationEN("Language")
    @TranslationES("Idioma")
    @TranslationIT("Lingua")
    @TranslationPT("Idioma")
    @TranslationPL("Język")
    @TranslationNL("Taal")
    @TranslationTR("Dil")
    @TranslationRU("Язык")
    @TranslationUA("Мова")
    @TranslationSE("Språk")
    @TranslationDK("Sprog")
    @TranslationNO("Språk")
    @TranslationFI("Kieli")
    @TranslationCZ("Jazyk")
    @TranslationSK("Jazyk")
    @TranslationHU("Nyelv")
    @TranslationRO("Limbă")
    @TranslationBG("Език")
    @TranslationHR("Jezik")
    @TranslationSI("Jezik")
    @TranslationGR("Γλώσσα")
    @TranslationEE("Keel")
    @TranslationLV("Valoda")
    @TranslationLT("Kalba")
    @TranslationIS("Tungumál")
    @TranslationZH("语言")
    String lblAppLanguage();
}
