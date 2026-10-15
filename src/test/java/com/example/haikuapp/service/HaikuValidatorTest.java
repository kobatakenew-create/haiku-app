package com.example.haikuapp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import static org.junit.jupiter.api.Assertions.*;

class HaikuValidatorTest {
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "句 句", "句　句", "句\n句", "句\t句", "句\u00a0句"})
    void rejectsEmptyOrWhitespaceText(String text) {
        assertThrows(InvalidHaikuException.class, () -> HaikuValidator.validate(text, "春", "完成"));
    }
    @Test void countsUnicodeCharactersInsteadOfJavaCodeUnits() {
        assertDoesNotThrow(() -> HaikuValidator.validate("𠮷".repeat(20), "春", "完成"));
        assertThrows(InvalidHaikuException.class, () -> HaikuValidator.validate("𠮷".repeat(21), "春", "完成"));
    }
    @ParameterizedTest @ValueSource(strings = {"春", "夏", "秋", "冬"})
    void acceptsAllSeasons(String season) {
        assertDoesNotThrow(() -> HaikuValidator.validate("あ".repeat(20), season, "推敲中"));
    }
    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = {"梅雨"})
    void rejectsUnknownSeason(String season) {
        assertThrows(InvalidHaikuException.class, () -> HaikuValidator.validate("春の海", season, "完成"));
    }
    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = {"未完成"})
    void rejectsUnknownStatus(String status) {
        assertThrows(InvalidHaikuException.class, () -> HaikuValidator.validate("春の海", "春", status));
    }
    @ParameterizedTest @NullSource @ValueSource(ints = {1, 20})
    void acceptsEmptyAndBoundaryPoints(Integer points) {
        assertDoesNotThrow(() -> HaikuValidator.validatePoints(points));
    }
    @ParameterizedTest @ValueSource(ints = {-1, 0, 21})
    void rejectsPointsOutsideRange(Integer points) {
        assertThrows(InvalidHaikuException.class, () -> HaikuValidator.validatePoints(points));
    }
    @ParameterizedTest @ValueSource(strings = {"◎", "○", "無印"})
    void acceptsEvaluation(String evaluation) {
        assertDoesNotThrow(() -> HaikuValidator.validateSelfEvaluation(evaluation));
    }
    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = {"△"})
    void rejectsUnknownEvaluation(String evaluation) {
        assertThrows(InvalidHaikuException.class, () -> HaikuValidator.validateSelfEvaluation(evaluation));
    }
}
