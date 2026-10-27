package de.emesit.demo.i18n;

/**
 * The interface in which you define the translation keys and the translations.
 */
public interface I18n {
    /**
    * Switches the language to the specified language.
    * @param language the language to switch to
    */
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

    @TranslationDE("Januar, Februar, März, April, Mai, Juni, Juli, August, September, Oktober, November, Dezember")
    @TranslationEN("January, February, March, April, May, June, July, August, September, October, November, December")
    @TranslationES("Enero, Febrero, Marzo, Abril, Mayo, Junio, Julio, Agosto, Septiembre, Octubre, Noviembre, Diciembre")
    String months();

    @TranslationDE("Sonntag, Montag, Dienstag, Mittwoch, Donnerstag, Freitag, Samstag")
    @TranslationEN("Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday")
    @TranslationES("Domingo, Lunes, Martes, Miércoles, Jueves, Viernes, Sábado")
    String days();

    @TranslationDE("So, Mo, Di, Mi, Do, Fr, Sa")
    @TranslationEN("Su, Mo, Tu, We, Th, Fr, Sa")
    @TranslationES("Do, Lu, Ma, Mi, Ju, Vi, Sá")
    String daysShort();

    @TranslationDE("Heute")
    @TranslationEN("Today")
    @TranslationES("Hoy")
    String today();

    @TranslationDE("Abbrechen")
    @TranslationEN("Cancel")
    @TranslationES("Cancelar")
    String cancel();


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


    // ----- for demonstration purposes -----

    @TranslationDE("Überladen ohne Parameter")
    @TranslationEN("Overloaded without parameters")
    String overloaded();

    // the following method will result in an exception, because
    // duplicate keys are not supported
//    @TranslationDE("Überladen mit String Parameter")
//    @TranslationEN("Overloaded with string parameter")
//    String overloaded(String param1);
}
