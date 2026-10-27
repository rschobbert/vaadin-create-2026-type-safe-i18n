package de.emesit.demo.ui;

import java.util.Locale;

import org.springframework.stereotype.Component;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.spring.annotation.UIScope;

import de.emesit.demo.i18n.I18n;
import de.emesit.demo.i18n.I18nBundleService;
import de.emesit.demo.i18n.Language;

@Component
@UIScope
public class AppLanguageWrapper extends Div {
    private static final long serialVersionUID = 1L;

    private final Span             label          = new Span();
    private final Select<Language> languageSelect = new Select<>();
    private final I18n             i18n;
    
    public AppLanguageWrapper(I18nBundleService i18nBundleService) {
        Language activeLanguage = Language.of(getCurrentUiLocale());

        this.i18n = i18nBundleService.getI18n(activeLanguage);

        languageSelect.setItems(Language.VALUES);
        languageSelect.setValue(activeLanguage);
        languageSelect.addValueChangeListener(this::onLanguageSelected);
        add(label, languageSelect);

        updateUi18n();
    }

    private void onLanguageSelected(ComponentValueChangeEvent<Select<Language>, Language> event) {
        Language newLanguage = event.getValue();
        if (newLanguage != null) {
            // mutate the state inside the dynamic proxy
            i18n.switchLanguage(newLanguage);

            // notify vaadin about the changed Locale
            setCurrentUiLocale(newLanguage.getLocale());

            // refresh this wrapper's own texts
            updateUi18n();
        }
    }

    private String generateItemLabel(Language itemLang) {
        return "[" + itemLang.name() + "] " + itemLang.getName(languageSelect.getValue());
    }

    private void updateUi18n() {
        // dynamic item label generation based on the currently selected language
        // we set a new ItemLabelGenerator each time to force a refresh of the labels
        //
        languageSelect.setItemLabelGenerator(this::generateItemLabel);

        // Translate the label via our proxy
        label.setText(i18n.language() + ": ");
    }

    private Locale getCurrentUiLocale() {
        return (UI.getCurrent() != null) ? UI.getCurrent().getLocale() : Locale.getDefault();
    }

    private void setCurrentUiLocale(Locale locale) {
        // setLocale() on UI triggers localeChange() on all views implementing LocaleChangeObserver
        UI.getCurrent().setLocale(locale);
    }

    public I18n getI18n() {
        return i18n;
    }
}
