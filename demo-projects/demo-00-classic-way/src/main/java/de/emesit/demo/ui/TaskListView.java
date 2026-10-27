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
import java.util.Optional;

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
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.internal.LocaleUtil;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;

import de.emesit.demo.task.Task;
import de.emesit.demo.task.TaskService;

@Route(value = "")
class TaskListView extends VerticalLayout implements LocaleChangeObserver, HasDynamicTitle {
    private static final long serialVersionUID = 1L;

    // Immutable, thread-safe base formatters
    private static final DateTimeFormatter DATE_FORMATTER      = DateTimeFormatter.ofLocalizedDate    (MEDIUM);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(MEDIUM)
            .withZone(ZoneId.systemDefault());

    private static final String COL_DESC    = "desc";
    private static final String COL_DUE     = "due";
    private static final String COL_CREATED = "created";

    private final TaskService taskService;

    private final H2         viewHeading = new H2();
    private final TextField  description = new TextField();
    private final DatePicker dueDate     = new DatePicker();
    private final Button     createBtn   = new Button();

    private final List<Task>             tasks        = new ArrayList<>();
    private final ListDataProvider<Task> dataProvider = new ListDataProvider<>(tasks);
    private final Grid<Task>             taskGrid     = new Grid<>();

    public TaskListView(TaskService taskService, AppLanguageWrapper appLanguageWrapper) {
        this.taskService = taskService;

        description.setMaxLength(Task.DESCRIPTION_MAX_LENGTH);
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
    
    private List<String> splitTranslation(String translationKey, int numExpectedItems) {
        String items = getTranslation(translationKey);
        String[] itemsSplitted = items.split(",");
        if (itemsSplitted.length == numExpectedItems) {
            return Arrays.stream(itemsSplitted).filter(Objects::nonNull).map(String::trim).toList();
        } else {
            return List.of();
        }
    }
    
    private void setDatePickerI18n() {
        List<String> months = splitTranslation("months", 12);
        List<String> days   = splitTranslation("days", 7);
        List<String> daysShort = splitTranslation("daysShort", 7);
        
        String today  = getTranslation("today");
        String cancel = getTranslation("cancel");
        
        String key = "";
        Object[] params = null;
        final Optional<I18NProvider> i18NProvider = LocaleUtil.getI18NProvider();
        Locale locale = LocaleUtil.getLocale(() -> i18NProvider);
        i18NProvider.map(i18n -> i18n.getTranslation(key, locale, params)).orElseGet(() -> "!{" + key + "}!");
        
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

    /**
     * Instead of setting a static title with @PageTitle, we implement the HasDynamicTitle interface
     * and use this method.
     */
    @Override
    public String getPageTitle() {
        return getTranslation("taskList");
    }

    /*
     * This method is called by vaadin when AppLanguageWrapper sets the current locale
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
        // to demonstrate Parameters we add a String and Float parameter,
        // see translations_en/de.properties for usage of NumberFormat 
        viewHeading.setText(getTranslation("taskList", "Joe", 9999.1234F));
        
        // intentionally wrong translation key: 'tasklist' instead of 'taskList'
        // for demonstration purposes:
//        viewHeading.setText(getTranslation("tasklist", "Joe", 9999.1234F));

        description.setLabel(getTranslation("description"));
        description.setPlaceholder(getTranslation("descriptionHint"));

        dueDate.setLabel(getTranslation("dueDate"));
        dueDate.setPlaceholder(getTranslation("dueDateHint"));
        setDatePickerI18n();

        createBtn.setText(getTranslation("create"));

        taskGrid.getColumnByKey(COL_DESC   ).setHeader(getTranslation("description"));
        taskGrid.getColumnByKey(COL_DUE    ).setHeader(getTranslation("dueDate"));
        taskGrid.getColumnByKey(COL_CREATED).setHeader(getTranslation("createdAt"));
    }

    private void createTask() {
        String descriptionValue = description.getValue();
        if ( ! StringUtils.hasText(descriptionValue)) {
            description.setInvalid(true);
            showErrorNotification(getTranslation("fieldRequired", getTranslation("description")));
        } else {
            LocalDate dueDateValue = dueDate.getValue();
            try {
                tasks.add(taskService.create(descriptionValue, dueDateValue));
                dataProvider.refreshAll();
                description.clear();
                dueDate.clear();
                showSuccessNotification(getTranslation("taskAdded"));
            } catch (Exception exc) {
                showErrorNotification(getTranslation("unexpectedErrorOnCreateTask", getTranslation("description"), descriptionValue));
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
        return (date != null) ? DATE_FORMATTER.withLocale(getCurrentUiLocale()).format(date) : getTranslation("never");
    }

    private String format(Instant instant) {
        return (instant != null) ? DATE_TIME_FORMATTER.withLocale(getCurrentUiLocale()).format(instant) : "";
    }

    private static Locale getCurrentUiLocale() {
        return UI.getCurrent() != null ? UI.getCurrent().getLocale() : Locale.getDefault();
    }
}
