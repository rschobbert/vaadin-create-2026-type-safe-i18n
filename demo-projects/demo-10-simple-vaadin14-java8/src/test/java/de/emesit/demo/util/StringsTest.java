package de.emesit.demo.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Tests for Strings utility class")
class StringsTest {

    @Nested
    @DisplayName("Tests for isBlank()")
    class IsBlankTests {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Should return true when string is null or empty")
        void shouldReturnTrueForNullOrEmpty(String input) {
            assertTrue(Strings.isBlank(input));
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "   ", "\t", "\n", "\r"})
        @DisplayName("Should return true when string contains only whitespaces")
        void shouldReturnTrueForWhitespaces(String input) {
            assertTrue(Strings.isBlank(input));
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", " a ", "text", "123", "._."})
        @DisplayName("Should return false when string contains non-whitespace characters")
        void shouldReturnFalseForValidContent(String input) {
            assertFalse(Strings.isBlank(input));
        }
    }
}
