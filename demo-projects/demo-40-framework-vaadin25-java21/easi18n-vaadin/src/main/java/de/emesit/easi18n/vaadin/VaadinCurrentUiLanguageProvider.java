package de.emesit.easi18n.vaadin;

import java.util.Locale;

import com.vaadin.flow.component.UI;

import de.emesit.easi18n.I18nLanguage;
import de.emesit.easi18n.I18nLanguageSupplier;

public class VaadinCurrentUiLanguageProvider implements UiLanguageProvider {
    
    private final I18nLanguageSupplier languageSupplier;

    public VaadinCurrentUiLanguageProvider(I18nLanguageSupplier languageSupplier) {
        this.languageSupplier = languageSupplier;
    }

    @Override
    public I18nLanguage getCurrentUiLanguage() {
        Locale locale = (UI.getCurrent() != null) ? UI.getCurrent().getLocale() : null;
        return (locale != null) ? languageSupplier.of(locale) : languageSupplier.getDefault();
    }
}
