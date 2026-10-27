package de.emesit.demo.i18n;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * A very simple implementation of a mutable CustomTranslationService. It uses an in-memory ConcurrentHashMap
 * and you can set and get translations. It demonstrates the usage of a custom CustomTranslationService. Translations
 * from this Service are used in preference of the annotated translations in you I18n class.
*/
@Service
public class SimpleTranslationService implements CustomTranslationService {
    private final Map<String, Map<I18nLanguage, String>> translations = new ConcurrentHashMap<>();

    /**
     * Sets a translation for the specified key and language. Keys are usually all uppercase
     * with optional underscores in between, e.g. 'DESCRIPTION' or 'TASK_LIST'. Although you
     * could use any key, only keys defined in you I18n class make sense.
     * 
     * If you pass null or a blank string, and the key / language combination exists, the entry is removed.
     * 
     * @param key
     * @param language
     * @param translation
     */
    public boolean setTranslation(String key, I18nLanguage language, String translation) {
        if (StringUtils.hasText(key) && language != null) {
            if (StringUtils.hasText(translation)) {
                translations
                .computeIfAbsent(key, k -> new ConcurrentHashMap<>())
                .put(language, translation);
                return true;
            } else {
                Map<I18nLanguage, String> entriesForKey = translations.get(key);
                if (entriesForKey != null) {
                    String removed = entriesForKey.remove(language);
                    return removed != null;
                }
            }
        }
        return false;
    }

    /**
     * Returns all keys which are currently present as a SortedSet.
     * @return all keys which are currently present as a SortedSet.
     */
    public SortedSet<String> getSortedKeys() {
        return new TreeSet<>(translations.keySet());
    }

    /**
     * Returns the raw translations map, as an unmodifiable map.
     * @return the raw translations map, as an unmodifiable map.
     */
    public Map<String, Map<I18nLanguage, String>> getRawTranslations() {
        return translations.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                    Map.Entry::getKey,
                    entry -> Collections.unmodifiableMap(entry.getValue())
                ));
    }

    /**
     * Returns the number of keys, which are currently present.
     * @return the number of keys, which are currently present.
     */
    public int keySize() {
        return translations.size();
    }

    /**
     * Returns the total number of translations, which are currently present.
     * @return the total number of translations, which are currently present.
     */
    public int totalSize() {
        return translations.values().stream()
                .mapToInt(Map::size)
                .sum();
    }
    
    /**
     * Removes all translations.
     */
    public void clear() {
        translations.clear();
    }

    @Override
    public String getTranslation(I18nLanguage language, String key, List<String> args) {
        return getTranslation(language, key, (args != null) ? args.toArray() : null);
    }

    @Override
    public String getTranslation(I18nLanguage language, String key, String[] args) {
        return getTranslation( key != null ? translations.get(key) : null, language, args );
    }

    @Override
    public String getTranslation(I18nLanguage language, String key, Object[] args) {
        return getTranslation( key != null ? translations.get(key) : null, language, args );
    }

    protected String getTranslation(Map<I18nLanguage, String> values, I18nLanguage language, Object[] args) {
        if (values != null) {
            String translation = values.get(language);
            if (StringUtils.hasText(translation)) {
                return (args == null || args.length == 0) ? translation : MessageFormat.format(translation, args);
            }
        }
        return null;
    }
}
