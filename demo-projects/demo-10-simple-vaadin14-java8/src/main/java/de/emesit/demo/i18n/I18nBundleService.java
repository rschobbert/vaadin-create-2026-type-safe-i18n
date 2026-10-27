package de.emesit.demo.i18n;

import org.springframework.stereotype.Service;

import de.emesit.demo.i18n.internal.I18nBundle;

@Service
public class I18nBundleService {
    private final I18nBundle i18nBundle;

    public I18nBundleService(I18nBundle i18nBundle) {
        this.i18nBundle = i18nBundle;
    }

    public I18n getI18n(Language language) {
        return i18nBundle.get(language);
    }
}
