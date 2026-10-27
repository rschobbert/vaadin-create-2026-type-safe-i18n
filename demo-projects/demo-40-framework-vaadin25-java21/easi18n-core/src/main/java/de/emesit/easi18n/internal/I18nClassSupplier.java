package de.emesit.easi18n.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class I18nClassSupplier implements Supplier<Class<? extends I18nBase>> {
    private final Class<? extends I18nBase> i18nClass;
    private final List<Class<?>> extraInterfaces = new ArrayList<>();
    private final MethodOverrideMode methodOverrideMode; // Bleibt absolut erhalten!
    
    public I18nClassSupplier(Class<? extends I18nBase> i18nClass) {
        this(i18nClass, null, MethodOverrideMode.FAILS);
    }
    
    public I18nClassSupplier(Class<? extends I18nBase> i18nClass, List<Class<?>> extraInterfaces, MethodOverrideMode methodOverrideMode) {
        this.i18nClass = Objects.requireNonNull(i18nClass, "'i18nClass' must not be null");
        if (extraInterfaces != null) {
            this.extraInterfaces.addAll(extraInterfaces);
        }
        this.methodOverrideMode = Objects.requireNonNull(methodOverrideMode, "'methodOverrideMode' must not be null");
    }

    /**
     * Adds an interface at the end of the additional interfaces list.
     * The final proxyInterfaces array will always be [ i18nClass , <extraInterfaces> ].
     * (-> higher priority on LAST_WINS / lower priority on FIRST_WINS)
     */
    public I18nClassSupplier appendInterface(Class<?> extraInterface) {
        Objects.requireNonNull(extraInterface, "'extraInterface' must not be null");
        if ( ! extraInterfaces.contains(extraInterface)) {
            checkForMethodCollisionsIfFailsMode(extraInterface);
            this.extraInterfaces.add(extraInterface);
        }
        return this; // enable method chaining
    }

    /**
     * Adds an interface at the beginning of the additional interfaces list.
     * The final proxyInterfaces array will always be [ i18nClass , <extraInterfaces> ].
     * (-> lower priority on LAST_WINS / higher priority on FIRST_WINS)
     */
    public I18nClassSupplier prependInterface(Class<?> extraInterface) {
        Objects.requireNonNull(extraInterface, "'extraInterface' must not be null");
        if ( ! extraInterfaces.contains(extraInterface)) {
            checkForMethodCollisionsIfFailsMode(extraInterface);
            this.extraInterfaces.add(0, extraInterface);
        }
        return this; // enable method chaining
    }

    /**
     * if methodOverrideMode == FAILS, we check the methods in the newInterface immediately
     * and fail fast.
     */
    private void checkForMethodCollisionsIfFailsMode(Class<?> newInterface) {
        if (this.methodOverrideMode == MethodOverrideMode.FAILS) {
            for (Method newMethod : newInterface.getMethods()) {
                // check against main i18nClass
                for (Method mainMethod : i18nClass.getMethods()) {
                    if (mainMethod.getName().equals(newMethod.getName())) {
                        throw new IllegalStateException("Collision detected: method [" + newMethod.getName() + 
                            "] already exists in main interface [" + i18nClass.getName() + "] and MethodOverrideMode is FAILS!");
                    }
                }
                // check against already registered interfaces
                for (Class<?> existingInterface : extraInterfaces) {
                    for (Method existingMethod : existingInterface.getMethods()) {
                        if (existingMethod.getName().equals(newMethod.getName())) {
                            throw new IllegalStateException("Collision detected: method [" + newMethod.getName() + 
                                "] already exists in interface [" + existingInterface.getName() + "] and MethodOverrideMode is FAILS!");
                        }
                    }
                }
            }
        }
    }
    
    public Class<? extends I18nBase> get() {
        return i18nClass;
    }
    
    public List<Class<?>> getExtraInterfaces() {
        return List.copyOf(extraInterfaces);
    }
    
    public Class<?>[] getProxyInterfaces() {
        int extraInterfacesSize = extraInterfaces.size();
        Class<?>[] proxyInterfaces =  new Class<?>[extraInterfacesSize + 1];
        proxyInterfaces[0] = i18nClass;
        if (extraInterfacesSize > 0) {
            System.arraycopy(extraInterfaces.toArray(), 0, proxyInterfaces, 1, extraInterfacesSize);
        }
        return proxyInterfaces;
    }
    
    public MethodOverrideMode getMethodOverrideMode() {
        return methodOverrideMode;
    }
}

