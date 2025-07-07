package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

class StringCalculator {
    private static final String DEFAULT_DELIMITER = ",";
    private static final String NEWLINE_DELIMITER = "\n";
    private static final Pattern SPLIT_PATTERN =
            Pattern.compile(DEFAULT_DELIMITER + "|" + NEWLINE_DELIMITER);

    public static int add(String input) {
        if (input == null || input.isBlank()) {
            return 0;
        }

        List<String> tokens = splitNumbers(input);

        List<Integer> numbers = new ArrayList<>();
        for (String token : tokens) {
            String trimmed = token.trim();
            int value = Integer.parseInt(trimmed);
            ensureNonNegative(value);
            numbers.add(value);
        }

        return numbers.stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    private static List<String> splitNumbers(String input) {
        return Arrays.asList(SPLIT_PATTERN.split(input));
    }

    private static void ensureNonNegative(int number) {
        if (number < 0) {
            throw new IllegalArgumentException(
                    "Negatives not allowed: " + number
            );
        }
    }
}
