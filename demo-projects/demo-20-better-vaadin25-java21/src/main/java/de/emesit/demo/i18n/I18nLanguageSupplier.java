package de.emesit.demo.i18n;

import java.util.Locale;
import java.util.function.Supplier;

public interface I18nLanguageSupplier extends Supplier<I18nLanguage[]> {
    I18nLanguage of(String code);
    I18nLanguage of(Locale locale);
    I18nLanguage[] get(); // repeat method declaration for plantuml
    String[] getCodes();
}
