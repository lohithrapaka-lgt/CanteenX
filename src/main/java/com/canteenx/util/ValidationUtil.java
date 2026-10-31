package com.canteenx.util;

import java.util.regex.Pattern;

/**
 * Utility for input validation using Regular Expressions (WT Syllabus Topic).
 */
public class ValidationUtil {

    // RFC 5322 compatible email regex pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    // 10-digit Indian mobile number starting with 6, 7, 8, or 9
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[6-9]\\d{9}$"
    );

    // Name allowing letters, spaces, and dots (2-100 characters)
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[A-Za-z\\s.]{2,100}$"
    );

    // Password: at least 6 characters
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^.{6,50}$"
    );

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean isPositiveDecimal(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        try {
            double val = Double.parseDouble(str.trim());
            return val > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPositiveInteger(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        try {
            int val = Integer.parseInt(str.trim());
            return val > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
