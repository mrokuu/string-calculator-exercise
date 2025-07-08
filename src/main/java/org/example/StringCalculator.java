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

final class InputParser {

    public static ParseResult parseInput(String input) {
        if (input == null || input.isBlank()) {
            return new ParseResult(new ArrayList<>(), null);
        }

        if (input.startsWith("//")) {
            return parseWithCustomDelimiter(input);
        } else {
            return parseWithDefaultDelimiters(input);
        }
    }

    private static ParseResult parseWithCustomDelimiter(String input) {
        int newlineIdx = input.indexOf('\n');
        if (newlineIdx == -1) {
            throw new IllegalArgumentException("Missing newline after delimiter declaration");
        }

        String delimiter = input.substring(2, newlineIdx);
        if (delimiter.isEmpty()) {
            throw new IllegalArgumentException("Delimiter cannot be empty");
        }

        String numbersPart = input.substring(newlineIdx + 1);
        List<String> tokens = tokenize(numbersPart, Pattern.quote(delimiter));

        return new ParseResult(tokens, delimiter);
    }

    private static ParseResult parseWithDefaultDelimiters(String input) {
        List<String> tokens = tokenize(input, Pattern.quote(Delimiters.COMMA));
        return new ParseResult(tokens, null);
    }

    private static List<String> tokenize(String numbers, String delimiterRegex) {
        String finalRegex = delimiterRegex + "|" + Pattern.quote(Delimiters.NEWLINE);
        return Arrays.asList(numbers.split(finalRegex));
    }

    static class ParseResult {
        private final List<String> tokens;
        private final String customDelimiter;

        public ParseResult(List<String> tokens, String customDelimiter) {
            this.tokens = tokens;
            this.customDelimiter = customDelimiter;
        }

        public List<String> getTokens() { return tokens; }
        public String getCustomDelimiter() { return customDelimiter; }
        public boolean hasCustomDelimiter() { return customDelimiter != null; }
    }
}

class StringCalculator {

    private static final Pattern NEGATIVE_PATTERN = Pattern.compile("-\\d+");

    public static int add(String input) {
        if (input == null || input.isBlank()) {
            return 0;
        }

        if (!input.startsWith("//")) {
            if (input.endsWith(Delimiters.COMMA) || input.endsWith(Delimiters.NEWLINE)) {
                throw new IllegalArgumentException("Separator at end not allowed");
            }
        }

        InputParser.ParseResult parseResult = InputParser.parseInput(input);

        if (parseResult.hasCustomDelimiter()) {
            return addWithCustomDelimiter(input, parseResult);
        } else {
            return sumTokens(parseResult.getTokens());
        }
    }

    private static int addWithCustomDelimiter(String input, InputParser.ParseResult parseResult) {
        int newlineIdx = input.indexOf('\n');
        String delimiter = parseResult.getCustomDelimiter();
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

        return sumTokens(parseResult.getTokens());
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