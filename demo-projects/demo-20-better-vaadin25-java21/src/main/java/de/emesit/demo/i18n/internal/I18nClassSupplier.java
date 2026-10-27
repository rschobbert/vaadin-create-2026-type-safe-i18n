package de.emesit.demo.i18n.internal;

import java.util.Objects;
import java.util.function.Supplier;

public class I18nClassSupplier implements Supplier<Class<? extends I18nBase>> {
    private final Class<? extends I18nBase> i18nClass;
    
    public I18nClassSupplier(Class<? extends I18nBase> i18nClass) {
        this.i18nClass = Objects.requireNonNull(i18nClass, "'i18nClass' must not be null");
    }
    
    public Class<? extends I18nBase> get() {
        return i18nClass;
    }
}
