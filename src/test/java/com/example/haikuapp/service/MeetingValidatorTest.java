package com.example.haikuapp.service;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import static org.junit.jupiter.api.Assertions.*;

class MeetingValidatorTest {
    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = {"　 ", "あいうえおかきくけこさ"})
    void rejectsMissingOrLongName(String name) {
        assertThrows(InvalidMeetingException.class, () -> MeetingValidator.normalizeName(name));
    }
    @Test void trimsNameAndAcceptsTenUnicodeCharacters() {
        assertEquals("秋の句会", MeetingValidator.normalizeName("　秋の句会 "));
        assertEquals("𠮷".repeat(10), MeetingValidator.normalizeName("𠮷".repeat(10)));
    }
    @Test void requiresDate() {
        assertThrows(InvalidMeetingException.class, () -> MeetingValidator.validateDate(null));
        assertDoesNotThrow(() -> MeetingValidator.validateDate(LocalDate.of(2026, 10, 15)));
    }
    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = {"　 "})
    void emptyThemeBecomesNull(String theme) {
        assertNull(MeetingValidator.normalizeTheme(theme));
    }
    @Test void allowsTwoCharacterThemeAndRejectsThree() {
        assertEquals("秋風", MeetingValidator.normalizeTheme(" 秋風　"));
        assertThrows(InvalidMeetingException.class, () -> MeetingValidator.normalizeTheme("秋の風"));
    }
}
