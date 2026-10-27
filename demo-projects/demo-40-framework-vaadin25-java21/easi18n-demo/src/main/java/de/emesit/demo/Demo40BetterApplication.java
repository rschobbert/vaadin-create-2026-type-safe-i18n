package de.emesit.demo;

import java.io.File;
import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.ColorScheme;
import com.vaadin.flow.theme.lumo.Lumo;

@SpringBootApplication
@StyleSheet(Lumo.STYLESHEET)
@ColorScheme(ColorScheme.Value.LIGHT)
public class Demo40BetterApplication implements AppShellConfigurator {
    private static final long serialVersionUID = 1L;
    
    public static void main(String[] args) throws IOException {
        // Most importantly upfront: Running from the commandline via './gradlew :easi18n-demo:bootRun' works without problems.
        // 
        // If running from the eclipse/STS IDE
        // set 'vaadin.project.basedir' to prevent exception:
        //
        // java.lang.RuntimeException: Unable to initialize com.vaadin.flow.spring.VaadinServletContextInitializer$DevModeServletContextListener
        //         at com.vaadin.flow.spring.VaadinServletContextInitializer$FailFastServletContextListener.contextInitialized(VaadinServletContextInitializer.java:217) ~[vaadin-spring-25.3.3.jar:na]
        //         ...
        // Caused by: java.lang.IllegalStateException: Failed to determine project directory for dev mode. Directory '...' does not look like a Maven or Gradle project. Ensure that you have run the prepare-frontend Maven goal, which generates 'flow-build-info.json', prior to deploying your application
        //         at com.vaadin.flow.server.AbstractConfiguration.getProjectFolder(AbstractConfiguration.java:270) ~[flow-server-25.3.3.jar:25.3.3]
        //         ...
        // 
        // Since this project is a gradle multi-project build, the normal vaadin auto-detection fails, if started from the IDE.
        // 
        // A different workaround which does not work perfect is, to set the working directory to the gradle project root 
        // in your Run Configuration of eclipse to the parent path, e.g. '${workspace_loc:demo-30-framework-demo}/..',
        // but then you get a different exception (which does not prevent your application from working):
        //
        // java.lang.IndexOutOfBoundsException: Index 0 out of bounds for length 0
        //         at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100) ~[na:na]
        //         at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106) ~[na:na]
        //         at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302) ~[na:na]
        //         at java.base/java.util.Objects.checkIndex(Objects.java:385) ~[na:na]
        //         at java.base/java.util.ArrayList.get(ArrayList.java:427) ~[na:na]
        //         at com.vaadin.copilot.ProjectFileManager.getFileForClass(ProjectFileManager.java:348) ~[copilot-25.3.2.jar:25.3.2]
        //         ...
        
        // when running from IDE, the current directory is the  root project dir (demo-30-framework-vaadin25-java21)
        // when running from command line, the current dir is the subproject dir (demo-30-framework-vaadin25-java21/easi18n-demo)
        // therefore we check, if appending the subproject path is necessary:
        String subPrjPath = "/easi18n-demo";
        String vaadinProjectBasedir = new File(".").getCanonicalPath().toString();
        if ( ! vaadinProjectBasedir.endsWith(subPrjPath)) {
            vaadinProjectBasedir += subPrjPath;
        }
        
        System.out.println("Message from "+Demo40BetterApplication.class.getName()+": setting system property 'vaadin.project.basedir' = '"+vaadinProjectBasedir+"'");
        System.setProperty("vaadin.project.basedir", vaadinProjectBasedir);
        
        SpringApplication.run(Demo40BetterApplication.class, args);
    }
}
