package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

class StringCalculator {

    private static final String DEFAULT_DELIMITER = ",";
    private static final String NEWLINE_DELIMITER = "\n";
    private static final Pattern DEFAULT_SPLIT_PATTERN =
            Pattern.compile(DEFAULT_DELIMITER + "|" + NEWLINE_DELIMITER);

    public static int add(String input) {
        if (input == null || input.isBlank()) {
            return 0;
        }

        if (input.startsWith("//")) {
            return addWithCustomDelimiter(input);
        }

        if (input.endsWith(DEFAULT_DELIMITER) || input.endsWith(NEWLINE_DELIMITER)) {
            throw new IllegalArgumentException("Separator at end not allowed");
        }

        return sumTokens(splitNumbers(input));
    }

    private static int addWithCustomDelimiter(String input) {
        int newlineIdx = input.indexOf('\n');
        if (newlineIdx == -1) {
            throw new IllegalArgumentException("Missing newline after delimiter declaration");
        }

        String delimiter = input.substring(2, newlineIdx);
        if (delimiter.isEmpty()) {
            throw new IllegalArgumentException("Delimiter cannot be empty");
        }

        String numbersPart = input.substring(newlineIdx + 1);

        if (numbersPart.endsWith(delimiter)) {
            throw new IllegalArgumentException("Separator at end not allowed");
        }

        validateDelimiterUsage(numbersPart, delimiter);

        String[] tokens = numbersPart.split(Pattern.quote(delimiter));
        return sumTokens(Arrays.asList(tokens));
    }

    private static void validateDelimiterUsage(String numbers, String delimiter) {
        for (int i = 0; i < numbers.length(); ) {
            if (numbers.startsWith(delimiter, i)) {
                i += delimiter.length();
                continue;
            }
            char ch = numbers.charAt(i);
            if (Character.isDigit(ch) || ch == '-') {
                i++;
                continue;
            }
            throw new IllegalArgumentException(
                    "'" + delimiter + "' expected but '" + ch + "' found at position " + i + "."
            );
        }
    }

    private static int sumTokens(List<String> tokens) {
        int sum = 0;
        List<Integer> negatives = new ArrayList<>();

        for (String token : tokens) {
            if (token.isBlank()) {
                throw new IllegalArgumentException("Separator at end not allowed");
            }

            int value = Integer.parseInt(token.trim());

            if (value < 0) {
                negatives.add(value);
                continue;
            }

            sum += value;
        }

        if (!negatives.isEmpty()) {
            String msg = "Negatives not allowed: " +
                    String.join(", ", negatives.stream().map(Object::toString).toList());
            throw new IllegalArgumentException(msg);
        }

        return sum;
    }

    private static List<String> splitNumbers(String input) {
        return Arrays.asList(DEFAULT_SPLIT_PATTERN.split(input));
    }
}
