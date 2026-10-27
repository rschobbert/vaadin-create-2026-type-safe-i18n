package de.emesit.demo.i18n;

public interface I18n {
    void switchLanguage(Language language);

    @TranslationDE("Sprache")
    @TranslationEN("Language")
    @TranslationES("Idioma")
    String language();

    @TranslationDE("Aufgaben Liste")
    @TranslationEN("Task List")
    @TranslationES("Lista de Tareas")
    String taskList();

    @TranslationDE("Beschreibung")
    @TranslationEN("Description")
    @TranslationES("Descripción")
    String description();

    @TranslationDE("Was möchtest du tun?")
    @TranslationEN("What do you want to do?")
    @TranslationES("¿Qué quieres hacer?")
    String descriptionHint();

    @TranslationDE("Fälligkeitsdatum")
    @TranslationEN("Due Date")
    @TranslationES("Fecha de vencimiento")
    String dueDate();

    @TranslationDE("Bis wann?")
    @TranslationEN("Until when?")
    @TranslationES("¿Hasta cuándo?")
    String dueDateHint();

    @TranslationDE("Nie")
    @TranslationEN("Never")
    @TranslationES("Nunca")
    String never();

    @TranslationDE("Erstellen")
    @TranslationEN("Create")
    @TranslationES("Crear")
    String create();

    @TranslationDE("Erstellt am")
    @TranslationEN("Created on")
    @TranslationES("Creado el")
    String createdAt();

    @TranslationDE("Gratulation<br><br>Die Aufgabe wurde erfolgreich hinzugefügt")
    @TranslationEN("Congratulations<br><br>Task was successfully added")
    @TranslationES("Felicidades<br><br>Tarea añadida con éxito")
    String taskAdded();

    @TranslationDE("\"{0}\" darf nicht leer sein!")
    @TranslationEN("\"{0}\" is required")
    @TranslationES("¡\"{0}\" no puede estar vacío!")
    String fieldRequired(String fieldName);

    @TranslationDE("Task mit {0} \"{1}\" konnte nicht angelegt werden!<br>Bitte wenden Sie sich an den Support.")
    @TranslationEN("Task with {0} \"{1}\" could not be created!<br>Please contact the support team.")
    @TranslationES("¡No se pudo crear una tarea con {0} «{1}»! Por favor, póngase en contacto con el soporte técnico.")
    String unexpectedErrorOnCreateTask(String fieldName, String fieldValue);
}
