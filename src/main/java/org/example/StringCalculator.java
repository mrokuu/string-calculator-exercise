package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

final class Delimiters {
    static final String COMMA = ",";
    static final String NEWLINE = "\n";
    static final int MAX_VALUE = 1000;

    private Delimiters() {}
}

class StringCalculator {

    private static final Pattern NEGATIVE_PATTERN = Pattern.compile("-\\d+");

    public static int add(String input) {
        if (input == null || input.isBlank()) {
            return 0;
        }
        if (input.startsWith("//")) {
            return addWithCustomDelimiter(input);
        }
        if (input.endsWith(Delimiters.COMMA) || input.endsWith(Delimiters.NEWLINE)) {
            throw new IllegalArgumentException("Separator at end not allowed");
        }
        return sumTokens(tokenize(input, Pattern.quote(Delimiters.COMMA)));
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
        List<String> errorMessages = new ArrayList<>();
        String delimiterError = findDelimiterMisuseMessage(numbersPart, delimiter);
        if (delimiterError != null) {
            errorMessages.add(delimiterError);
        }
        List<Integer> negatives = extractNegatives(numbersPart);
        if (!negatives.isEmpty()) {
            errorMessages.add(0, buildNegativesMessage(negatives));
        }
        if (!errorMessages.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errorMessages));
        }
        return sumTokens(tokenize(numbersPart, Pattern.quote(delimiter)));
    }

    private static List<String> tokenize(String numbers, String delimiterRegex) {
        String finalRegex = delimiterRegex + "|" + Pattern.quote(Delimiters.NEWLINE);
        return Arrays.asList(numbers.split(finalRegex));
    }

    private static String findDelimiterMisuseMessage(String numbers, String delimiter) {
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
            return "'" + delimiter + "' expected but '" + ch + "' found at position " + i + ".";
        }
        return null;
    }

    private static List<Integer> extractNegatives(String numbers) {
        List<Integer> negatives = new ArrayList<>();
        Matcher m = NEGATIVE_PATTERN.matcher(numbers);
        while (m.find()) {
            negatives.add(Integer.parseInt(m.group()));
        }
        return negatives;
    }

    private static String buildNegativesMessage(List<Integer> negatives) {
        return "Negative number(s) not allowed: " + negatives.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
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
            } else if (value <= Delimiters.MAX_VALUE) {
                sum += value;
            }
        }
        if (!negatives.isEmpty()) {
            throw new IllegalArgumentException(buildNegativesMessage(negatives));
        }
        return sum;
    }
}