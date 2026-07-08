package com.georgev22.skinoverlay.utilities;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Locale {
    ENGLISH("en"),
    FRENCH("fr"),
    GERMAN("de"),
    ITALIAN("it"),
    SPANISH("es"),
    PORTUGUESE("pt"),
    DUTCH("nl"),
    NORWEGIAN("no"),
    SWEDISH("sv"),
    DANISH("da"),
    FINNISH("fi"),
    POLISH("pl"),
    CZECH("cs"),
    SLOVAK("sk"),
    HUNGARIAN("hu"),
    ROMANIAN("ro"),
    BULGARIAN("bg"),
    GREEK("el"),
    RUSSIAN("ru"),
    TURKISH("tr"),

    CHINESE("zh"),
    JAPANESE("ja"),
    KOREAN("ko"),

    ENGLISH_US("en", "US"),
    ENGLISH_GR("en", "GR"),
    SPANISH_ES("es", "ES"),
    SPANISH_MX("es", "MX"),
    PORTUGUESE_BR("pt", "BR"),
    FRENCH_CA("fr", "CA");

    private final String languageCode;
    private final String countryCode;
    private final java.util.Locale locale;
    private static final Map<String, Locale> localeMap;

    static {
        localeMap = new HashMap<>();
        Arrays.stream(Locale.values()).forEach(l -> localeMap.put(l.countryCode != null ? l.languageCode.toLowerCase() + "_" + l.countryCode : l.languageCode.toLowerCase(), l));
    }

    Locale(String languageCode) {
        this.locale = new java.util.Locale(languageCode);
        this.languageCode = languageCode;
        this.countryCode = null;
    }

    Locale(String languageCode, String countryCode) {
        this.locale = new java.util.Locale(languageCode, countryCode);
        this.languageCode = languageCode;
        this.countryCode = countryCode;
    }

    public java.util.Locale getLocale() {
        return locale;
    }

    public String getStringLocale() {
        return countryCode != null ? languageCode + "_" + countryCode : languageCode;
    }

    public static Locale fromString(@NotNull String s) {
        return localeMap.containsKey(s) ? localeMap.get(s) : s.contains("_") ? localeMap.get(s.split("_")[0]) : ENGLISH_US;
    }
}
