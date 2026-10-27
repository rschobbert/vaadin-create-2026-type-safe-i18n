package de.emesit.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.ColorScheme;
import com.vaadin.flow.theme.lumo.Lumo;

@SpringBootApplication
@StyleSheet(Lumo.STYLESHEET)
@ColorScheme(ColorScheme.Value.LIGHT)
public class Demo00SimpleApplication implements AppShellConfigurator {
    private static final long serialVersionUID = 1L;
    
    public static void main(String[] args) {
        SpringApplication.run(Demo00SimpleApplication.class, args);
    }
}
