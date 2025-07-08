package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

class InputParser {

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
            throw new InputException("Missing newline after delimiter declaration");
        }

        String delimiter = input.substring(2, newlineIdx);
        if (delimiter.isEmpty()) {
            throw new InputException("Delimiter cannot be empty");
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
    }
}