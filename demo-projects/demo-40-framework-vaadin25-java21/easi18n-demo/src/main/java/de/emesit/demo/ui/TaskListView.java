package de.emesit.demo.ui;

import static com.vaadin.flow.component.button.ButtonVariant.PRIMARY;
import static com.vaadin.flow.component.notification.Notification.Position.BOTTOM_CENTER;
import static com.vaadin.flow.component.notification.NotificationVariant.ERROR;
import static com.vaadin.flow.component.notification.NotificationVariant.SUCCESS;
import static com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.END;
import static java.time.format.FormatStyle.MEDIUM;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.springframework.util.StringUtils;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;

import de.emesit.demo.i18n.I18n;
import de.emesit.demo.task.Task;
import de.emesit.demo.task.TaskService;
import de.emesit.easi18n.vaadin.AppLanguageWrapper;

@Route(value = "")
class TaskListView extends VerticalLayout implements LocaleChangeObserver, HasDynamicTitle {
    private static final long serialVersionUID = 1L;

    // Immutable, thread-safe base formatters
    static final DateTimeFormatter DATE_FORMATTER      = DateTimeFormatter.ofLocalizedDate    (MEDIUM);
    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(MEDIUM)
            .withZone(ZoneId.systemDefault());

    private static final String COL_DESC    = "desc";
    private static final String COL_DUE     = "due";
    private static final String COL_CREATED = "created";

    final TaskService taskService;
    final I18n        i18n;
    
    final AppLanguageWrapper appLanguageWrapper;

    final H2         viewHeading = new H2();
    final TextField  description = new TextField();
    final DatePicker dueDate     = new DatePicker();
    final Button     createBtn   = new Button();

    final List<Task>             tasks        = new ArrayList<>();
    final ListDataProvider<Task> dataProvider = new ListDataProvider<>(tasks);
    final Grid<Task>             taskGrid     = new Grid<>();

    public TaskListView(TaskService taskService, AppLanguageWrapper appLanguageWrapper) {
        this.taskService = taskService;

        // do NOT use "i18nBundleService.getI18n(currentLang)"
        // as that would create a new dynamic proxy.
        // Language switching is done on the instance inside AppLanguageWrapper
        // so we must use that.
        this.i18n = appLanguageWrapper.getI18n(I18n.class);
        
        this.appLanguageWrapper = appLanguageWrapper;

        // for demonstration purposes, give the user the possibility to exceed the limit.
        // If you want to prevent a validation error, just use 'setMaxLength':
        //description.setMaxLength(Task.DESCRIPTION_MAX_LENGTH);
        
        description.setMinWidth("15em");
        description.setAutofocus(true);

        createBtn.addClickListener(event -> createTask());
        createBtn.addThemeVariants(PRIMARY);

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setPadding(false);

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.add(description, dueDate, createBtn);
        toolbar.setVerticalComponentAlignment(END, description, dueDate, createBtn);
        toolbar.setFlexGrow(2, description);
        toolbar.getElement().getStyle().set("flex-wrap", "wrap");
        toolbar.setWidthFull();

        mainLayout.add(appLanguageWrapper, viewHeading, toolbar);

        taskGrid.addColumn(Task::getDescription               ).setKey(COL_DESC   );
        taskGrid.addColumn(task -> format(task.getDueDate  ())).setKey(COL_DUE    ).setWidth("200px").setFlexGrow(0);
        taskGrid.addColumn(task -> format(task.getCreatedOn())).setKey(COL_CREATED).setWidth("250px").setFlexGrow(0);
        taskGrid.setSizeFull();

        setSizeFull();
        add(mainLayout, taskGrid);

        updateUi18n(); // populate translations for the initial ui

        taskGrid.setDataProvider(dataProvider);
        tasks.addAll(taskService.list());
        dataProvider.refreshAll();
    }

    /**
     * Instead of setting a static title with @PageTitle, we implement the HasDynamicTitle interface
     * and use this method.
     */
    @Override
    public String getPageTitle() {
        return i18n.taskList();
    }

    /*
     * This method is called by vaadin when AppLanguageWrapper1 sets the current locale
     * after the user selected a different language.
     */
    @Override
    public void localeChange(LocaleChangeEvent event) {
        UI.getCurrent().getPage().setTitle(getPageTitle());
        updateUi18n();

        // force the row formatters to rewrite the grid data
        // e.g. to show locale specific dates / times
        dataProvider.refreshAll(); 
    }

    private void updateUi18n() {
        viewHeading.setText(i18n.taskList());

        description.setLabel(i18n.description());
        description.setPlaceholder(i18n.descriptionHint());

        dueDate.setLabel(i18n.dueDate());
        dueDate.setPlaceholder(i18n.dueDateHint());
        setDatePickerI18n();

        createBtn.setText(i18n.create());

        taskGrid.getColumnByKey(COL_DESC   ).setHeader(i18n.description());
        taskGrid.getColumnByKey(COL_DUE    ).setHeader(i18n.dueDate    ());
        taskGrid.getColumnByKey(COL_CREATED).setHeader(i18n.createdAt  ());
    }

    private List<String> splitTranslation(String value, int numExpectedItems) {
        String[] itemsSplitted = value.split(",");
        if (itemsSplitted.length == numExpectedItems) {
            return Arrays.stream(itemsSplitted).filter(Objects::nonNull).map(String::trim).toList();
        } else {
            return List.of();
        }
    }
    
    private void setDatePickerI18n() {
        List<String> months = splitTranslation(i18n.months(), 12);
        List<String> days   = splitTranslation(i18n.days(), 7);
        List<String> daysShort = splitTranslation(i18n.daysShort(), 7);
        
        String today  = i18n.today();
        String cancel = i18n.cancel();
        
        DatePicker.DatePickerI18n datePickerI18n = new DatePicker.DatePickerI18n();
        if ( ! months.isEmpty()) {
            datePickerI18n.setMonthNames(months);
        }
        if ( ! days.isEmpty()) {
            datePickerI18n.setWeekdays(days);
        }
        if ( ! daysShort.isEmpty()) {
            datePickerI18n.setWeekdaysShort(daysShort);
        }
        datePickerI18n.setToday(today);
        datePickerI18n.setCancel(cancel);
        dueDate.setI18n(datePickerI18n);
    }

    private void createTask() {
        String descriptionValue = description.getValue();
        if ( ! StringUtils.hasText(descriptionValue)) {
            description.setInvalid(true);
            showErrorNotification(i18n.fieldRequired(i18n.description()));
        } else {
            LocalDate dueDateValue = dueDate.getValue();
            try {
                tasks.add(taskService.create(descriptionValue, dueDateValue));
                dataProvider.refreshAll();
                description.clear();
                dueDate.clear();
                showSuccessNotification(i18n.taskAdded());
            } catch (Exception exc) {
                showErrorNotification(i18n.unexpectedErrorOnCreateTask(i18n.description(), descriptionValue));
            }
        }
        description.focus();
    }

    private void showSuccessNotification(String msg) {
        showNotification(msg, SUCCESS, 3000);
    }

    private void showErrorNotification(String msg) {
        showNotification(msg, ERROR, 6000);
    }

    private void showNotification(String msg, NotificationVariant notificationVariant, int timeout) {
        Html htmlMsg = new Html("<span style='font-size: x-large;'>" + msg + "</span>");
        Notification n = new Notification(htmlMsg);
        n.setDuration(timeout);
        n.setPosition(BOTTOM_CENTER);
        n.addThemeVariants(notificationVariant);
        n.open();
    }

    private String format(LocalDate date) {
        return (date != null) ? DATE_FORMATTER.withLocale(getCurrentUiLocale()).format(date) : i18n.never();
    }

    private String format(Instant instant) {
        return (instant != null) ? DATE_TIME_FORMATTER.withLocale(getCurrentUiLocale()).format(instant) : "";
    }

    private static Locale getCurrentUiLocale() {
        return UI.getCurrent() != null ? UI.getCurrent().getLocale() : Locale.getDefault();
    }
}
