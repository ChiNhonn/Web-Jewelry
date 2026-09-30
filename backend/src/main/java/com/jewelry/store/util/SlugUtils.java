package com.jewelry.store.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Utility for generating SEO-friendly URL slugs from Vietnamese and English text.
 */
public final class SlugUtils {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern MULTIDASH = Pattern.compile("-+");

    private SlugUtils() {
        // Private constructor for utility class
    }

    /**
     * Converts a raw string (including Vietnamese with diacritics) into a clean URL slug.
     * Example: "Nhẫn Kim Cương Vàng Trắng 18K" -> "nhan-kim-cuong-vang-trang-18k"
     */
    public static String toSlug(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }

        // Replace 'Đ' and 'đ' specifically as Normalizer does not decompose them
        String normalized = input.replace('Đ', 'D').replace('đ', 'd');

        // Decompose accented characters (e.g., 'á' -> 'a' + acute)
        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD);
        normalized = Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(normalized).replaceAll("");

        // Replace whitespaces with hyphens
        String noWhitespace = WHITESPACE.matcher(normalized).replaceAll("-");

        // Remove all non-alphanumeric characters (except hyphens)
        String slug = NONLATIN.matcher(noWhitespace).replaceAll("");

        // Replace multiple consecutive hyphens with a single hyphen
        slug = MULTIDASH.matcher(slug).replaceAll("-");

        // Trim leading and trailing hyphens and convert to lowercase
        return slug.replaceAll("^-|-$", "").toLowerCase(Locale.ENGLISH);
    }
}
