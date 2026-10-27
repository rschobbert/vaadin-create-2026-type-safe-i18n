package de.emesit.demo.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.select.Select;

import de.emesit.demo.Demo30BetterApplication;
import de.emesit.demo.task.Task;
import de.emesit.easi18n.I18nLanguage;
import de.emesit.easi18n.Language;

@SpringBootTest
public class TaskViewVaadinTest extends SpringBrowserlessTest implements WithAssertions {

    @BeforeAll
    static void setup() throws IOException {
        
        // see long comment in
        // de.emesit.demo.Demo30BetterApplication
        // for explanation, why we need to set the system property below.
        
        String subPrjPath = "/easi18n-demo";
        String vaadinProjectBasedir = new File(".").getCanonicalPath().toString();
        if ( ! vaadinProjectBasedir.endsWith(subPrjPath)) {
            vaadinProjectBasedir += subPrjPath;
        }
        
        System.out.println("Message from "+Demo30BetterApplication.class.getName()+": setting system property 'vaadin.project.basedir' = '"+vaadinProjectBasedir+"'");
        System.setProperty("vaadin.project.basedir", vaadinProjectBasedir);
        
    }
    @Test
    void testSelectLanguages() {
        TaskListView view = navigate(TaskListView.class);
        
        Select<I18nLanguage> languageSelect      = view.appLanguageWrapper.languageSelect;
        Span                 languageSelectLabel = view.appLanguageWrapper.label;
        
        Locale       currentUiLocale = view.appLanguageWrapper.getCurrentUiLocale();
        I18nLanguage currentLanguage = languageSelect.getValue();
        
        assertEquals(currentLanguage.getCode(), currentUiLocale.getLanguage(), "current UI Locale should be preselected in language Select box");
        
        String   currentLabelEN  = AppLanguageWrapper.generateItemLabel(Language.EN, currentLanguage);
        String   currentLabelDE  = AppLanguageWrapper.generateItemLabel(Language.DE, currentLanguage);
        
        test(languageSelect).selectItem(currentLabelEN);
        
        currentLanguage = languageSelect.getValue();
        currentLabelEN  = AppLanguageWrapper.generateItemLabel(Language.EN, currentLanguage);
        currentLabelDE  = AppLanguageWrapper.generateItemLabel(Language.DE, currentLanguage);
        
        assertEquals(Language.EN, currentLanguage, "after selecting Language.EN, the language select box value should be Language.EN");
        assertEquals("Language:", languageSelectLabel.getText().trim(), "after selecting Language.EN, the label of the language select box should contain the word 'Language'");
        
        test(languageSelect).selectItem(currentLabelDE);
        
        currentLanguage = languageSelect.getValue();
        currentLabelDE  = AppLanguageWrapper.generateItemLabel(Language.DE, currentLanguage);
        currentLabelEN  = AppLanguageWrapper.generateItemLabel(Language.EN, currentLanguage);

        assertEquals(Language.DE, languageSelect.getValue(), "after selecting Language.DE, the language select box value should be Language.DE");
        assertEquals("Sprache:", languageSelectLabel.getText().trim(), "after selecting Language.DE, the label of the language select box should contain the german translation of the word 'Language'");
    }

    @DisplayName("Tests for adding tasks")
    @Test
    void testAddTask() {
        TaskListView view = navigate(TaskListView.class);
        
        Select<I18nLanguage> languageSelect = view.appLanguageWrapper.languageSelect;
        
        I18nLanguage currentLanguage = languageSelect.getValue();
        String       currentLabelEN  = AppLanguageWrapper.generateItemLabel(Language.EN, currentLanguage);
        
        test(languageSelect).selectItem(currentLabelEN);
        
        assertEquals(0, view.dataProvider.getItems().size());
        
        // EMPTY value should fail
        test(view.description).setValue("");
        test(view.createBtn).click();
        assertTrue(view.description.isInvalid());
        assertEquals(0, view.dataProvider.getItems().size());
        
        String longDescription = createString(Task.DESCRIPTION_MAX_LENGTH + 1, "A");
        view.description.setValue(longDescription);
        test(view.createBtn).click();
        assertEquals(0, view.dataProvider.getItems().size());
        
        String taskDescription = "Just some task";
        test(view.description).setValue(taskDescription);
        test(view.createBtn).click();
        
        assertEquals(1, view.dataProvider.getItems().size());
        Task task = view.dataProvider.getItems().iterator().next();
        assertEquals(taskDescription, task.getDescription());
        assertNull(task.getDueDate());
        assertNotNull(task.getCreatedOn());
        
        taskDescription = "Another urgent task";
        LocalDate nextWeek = LocalDate.now().plusWeeks(1);
        test(view.description).setValue(taskDescription);
        test(view.dueDate).setValue(nextWeek);
        test(view.createBtn).click();
        Collection<Task> items = view.dataProvider.getItems();
        assertEquals(2, items.size());
        
        Iterator<Task> taskIter = items.iterator();
        taskIter.next(); // skip task 1
        task = taskIter.next();
        
        assertEquals(taskDescription, task.getDescription());
        assertEquals(nextWeek, task.getDueDate());
        assertNotNull(task.getCreatedOn());
    }
    
    private static String createString(int length, String base) {
        // base is repeating until it reaches the desired length.
        // If the length is larger than the base string, it repeats it.
        // The final substring ensures the exact length is matched.
        return base.repeat((length / base.length()) + 1).substring(0, length);
    }}
