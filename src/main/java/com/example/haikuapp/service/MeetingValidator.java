package com.example.haikuapp.service;

import java.time.LocalDate;

public final class MeetingValidator {

    private static final int MAX_NAME_LENGTH = 10;
    private static final int MAX_THEME_LENGTH = 2;

    private MeetingValidator() {
    }

    public static String normalizeName(String meetingName) {
        if (meetingName == null || meetingName.isBlank()) {
            throw new InvalidMeetingException(
                    "句会名を入力してください"
            );
        }

        String normalizedName = meetingName.strip();

        if (normalizedName.codePointCount(
                0,
                normalizedName.length()
        ) > MAX_NAME_LENGTH) {
            throw new InvalidMeetingException(
                    "句会名は十文字以内にしてください"
            );
        }

        return normalizedName;
    }

    public static void validateDate(LocalDate meetingDate) {
        if (meetingDate == null) {
            throw new InvalidMeetingException(
                    "開催日を入力してください"
            );
        }
    }

    public static String normalizeTheme(String theme) {
        if (theme == null) {
            return null;
        }

        String normalizedTheme = theme.strip();

        if (normalizedTheme.codePointCount(
                0,
                normalizedTheme.length()
        ) > MAX_THEME_LENGTH) {
            throw new InvalidMeetingException(
                    "兼題は二文字以内にしてください"
            );
        }

        return normalizedTheme.isEmpty() ? null : normalizedTheme;
    }
}