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
import de.emesit.demo.i18n.I18nLanguage;
import de.emesit.demo.i18n.I18nLanguageSupplier;
import de.emesit.demo.i18n.Language;

/**
 * A variant of the AppLanguageWrapper1. This class uses an instance variable
 * for the currently selected language. It sets the ItemLabelGenerator only
 * once in the constructor. If the user changes the selected language, we call
 * dataProvider.refreshAll(). After that call, the SelectBox does not show its
 * currently selected item anymore. Therefore we have to call 
 * languageSelect.setValue(...) again, which obviously forces vaadin to do a redraw.
 * 
 * In onLanguageSelected we differentiate between a user click
 * and the programmatic selection change, by using event.isFromClient() to prevent
 * a StackOverflowError.
 */
@Component
@UIScope
public class AppLanguageWrapper2 extends Div {
    private static final long serialVersionUID = 1L;

    final Span                 label          = new Span();
    final Select<I18nLanguage> languageSelect = new Select<>();
    final I18n                 i18n;
    
    private I18nLanguage currentLanguage;

    public AppLanguageWrapper2(I18nLanguageSupplier languageSupplier, I18nBundleService i18nBundleService) {
        currentLanguage = languageSupplier.of(getCurrentUiLocale());

        this.i18n = i18nBundleService.getI18n(currentLanguage);

        languageSelect.setItems(languageSupplier.get());
        languageSelect.addValueChangeListener(this::onLanguageSelected);
        languageSelect.setItemLabelGenerator(this::generateItemLabel);

        add(label, languageSelect);

        updateUi18n();
    }

    private void onLanguageSelected(ComponentValueChangeEvent<Select<I18nLanguage>, I18nLanguage> event) {
        I18nLanguage newLanguage = event.getValue();
        if (newLanguage != null) {
            currentLanguage = newLanguage;
            
            // only react if the user clicked
            // otherwise we get a StackOverflowError
            if (event.isFromClient()) {
                // mutate the state inside the dynamic proxy
                i18n.switchLanguage(newLanguage);

                // notify vaadin about the changed Locale
                setCurrentUiLocale(newLanguage.getLocale());

                // refresh this wrapper's own texts
                updateUi18n();
            }
        }
    }
    
    public static String generateItemLabel(I18nLanguage itemLang, I18nLanguage displayLanguage) {
        return "[" + itemLang.getCode() + "] " + itemLang.getName(displayLanguage);
    }
    
    private String generateItemLabel(I18nLanguage itemLang) {
        return generateItemLabel(itemLang, currentLanguage);
    }
    
    private void updateUi18n() {
        // instead of setting a new ItemLabelGenerator on each
        // language change, we tell vaadin that the underlying data
        // has changed. That forces the select box to redraw
        // its internal items.
        // But - why so ever - the SelectBox looses its currently selected
        // value. Therefore we have to call setValue() again (see next call).
        languageSelect.getDataProvider().refreshAll();

        // force the ui to redraw the closed select box
        // otherwise the selected Language is not shown
        languageSelect.setValue(currentLanguage);

        // Translate the label via our proxy
        label.setText(i18n.language() + ": ");
    }

    Locale getCurrentUiLocale() {
        // UI.getCurrent() even when called during constructor initialization should
        // never return null. But there might be edge cases e.g. in tests where it may
        // happen. Therefore prevent a NullPointerException when that happens.
        return (UI.getCurrent() != null) ? UI.getCurrent().getLocale() : Language.DEFAULT.getLocale();
    }

    private void setCurrentUiLocale(Locale locale) {
        // setLocale() on UI triggers localeChange() on all views implementing LocaleChangeObserver
        UI.getCurrent().setLocale(locale);
    }

    public I18n getI18n() {
        return i18n;
    }
}
