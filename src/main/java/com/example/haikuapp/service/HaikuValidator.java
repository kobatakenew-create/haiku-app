package com.example.haikuapp.service;

import java.util.Set;

public final class HaikuValidator {

    private static final int MAX_TEXT_LENGTH = 20;

    private static final Set<String> SEASONS =
            Set.of("春", "夏", "秋", "冬");

    private static final Set<String> STATUSES =
            Set.of("推敲中", "完成");

    private static final Set<String> SELF_EVALUATIONS =
            Set.of("◎", "○", "無印");

    private HaikuValidator() {
    }

    public static void validate(
            String text,
            String season,
            String status
    ) {
        validateText(text);
        validateSeason(season);
        validateStatus(status);
    }

    public static void validateSelfEvaluation(String selfEvaluation) {
        if (selfEvaluation == null
                || !SELF_EVALUATIONS.contains(selfEvaluation)) {
            throw new InvalidHaikuException(
                    "自己評価の選択が正しくありません");
        }
    }

    public static void validatePoints(Integer points) {
        if (points != null && (points < 1 || points > 20)) {
            throw new InvalidHaikuException(
                    "点数は1〜20から選んでください");
        }
    }

    private static void validateText(String text) {
        if (text == null || text.isBlank()) {
            throw new InvalidHaikuException("俳句を入力してください");
        }

        if (text.codePointCount(0, text.length()) > MAX_TEXT_LENGTH) {
            throw new InvalidHaikuException("俳句は20文字以内にしてください");
        }

        boolean hasWhitespace = text.codePoints()
                .anyMatch(character ->
                        Character.isWhitespace(character)
                                || Character.isSpaceChar(character));

        if (hasWhitespace) {
            throw new InvalidHaikuException(
                    "俳句に空白や改行を入れないでください");
        }
    }

    private static void validateSeason(String season) {
        if (season == null || !SEASONS.contains(season)) {
            throw new InvalidHaikuException("季節を選んでください");
        }
    }

    private static void validateStatus(String status) {
        if (status == null || !STATUSES.contains(status)) {
            throw new InvalidHaikuException(
                    "状態は推敲中・完成から選んでください");
        }
    }
}