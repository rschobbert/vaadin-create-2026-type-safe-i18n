package de.emesit.easi18n.vaadin;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.select.Select;

import de.emesit.easi18n.I18nBundleService;
import de.emesit.easi18n.I18nLanguage;
import de.emesit.easi18n.I18nLanguageSupplier;
import de.emesit.easi18n.Language;
import de.emesit.easi18n.internal.I18nBase;

/**
 * The framework variant of the AppLanguageWrapper. Here we use this subproject's
 * I18nVaadin interface, which carries the translations only for this subproject.
 */
public class AppLanguageWrapper extends Div {
    private static final long serialVersionUID = 1L;

    final Span                 label          = new Span();
    final Select<I18nLanguage> languageSelect = new Select<>();
    final I18nVaadin           i18n;
    
    private I18nLanguage currentLanguage;

    public AppLanguageWrapper(I18nLanguageSupplier languageSupplier, I18nBundleService i18nBundleService) {
        currentLanguage = languageSupplier.of(getCurrentUiLocale());

        this.i18n = (I18nVaadin)i18nBundleService.getI18n(currentLanguage);

        languageSelect.setItems(i18nBundleService.getTranslatedLanguages().stream().sorted(I18nLanguage.CODE_COMPARATOR).toList());
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
        label.setText(i18n.lblAppLanguage() + ": ");
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

    /**
     * Returns the i18n proxy in the type the caller expects.
     * 
     * @param <T> The concrete type (must inherit from I18nBase)
     * @param targetClass Das Class-Objekt des Ziel-Interfaces (z.B. I18n.class)
     * @return Der in den Zieltyp gecastete Proxy
     */
    @SuppressWarnings("unchecked")
    public <T extends I18nBase> T getI18n(Class<T> targetClass) {
        Objects.requireNonNull(targetClass, "'targetClass' must not be null");
        
        if (!targetClass.isInstance(this.i18n)) {
            throw new IllegalArgumentException("The internal proxy does not implement the specified interface ["+targetClass.getName()+"]. Available interfaces: "+Arrays.toString(this.i18n.getClass().getInterfaces()));
        }
        return (T) this.i18n;
    }}
