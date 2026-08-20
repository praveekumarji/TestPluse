package com.testpulse.util;

import java.util.List;

public final class LocalizedTextResolver {

    private LocalizedTextResolver() {
    }

    public static String resolve(String fallbackText, String localizedText, String lang) {
        if (isHindiRequested(lang) && localizedText != null && !localizedText.isBlank()) {
            return localizedText;
        }

        return fallbackText == null ? "" : fallbackText;
    }

    public static List<String> resolveList(List<String> fallbackList, List<String> localizedList, String lang) {
        if (isHindiRequested(lang) && localizedList != null && !localizedList.isEmpty()) {
            return localizedList;
        }

        return fallbackList == null ? List.of() : fallbackList;
    }

    private static boolean isHindiRequested(String lang) {
        return lang != null && (lang.equalsIgnoreCase("hi") || lang.equalsIgnoreCase("hindi"));
    }
}
