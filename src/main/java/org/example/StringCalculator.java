package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class StringCalculator {

    private static final Pattern NEGATIVE_PATTERN = Pattern.compile("-\\d+");
    private static final int MAX_VALUE = 1000;

    public int add(String... args) {
        return Arrays.stream(args).mapToInt(this::add).sum();
    }

    public int add(String input) {
        if (input == null || input.isBlank()) {
            return 0;
        }

        try {
            List<Integer> numbers = extractAndValidateNumbers(input);
            return numbers.stream()
                    .filter(num -> num <= MAX_VALUE)
                    .mapToInt(Integer::intValue)
                    .sum();
        } catch (InputException e) {
            throw e;
        }
    }

    private static List<Integer> extractAndValidateNumbers(String input) {
        CalculationData calculationData = parseInputToData(input);

        if (!input.startsWith("//")) {
            validateEndSeparator(calculationData);
        }

        if (calculationData.delimiter().equals(Delimiters.COMMA + "|" + Delimiters.NEWLINE)) {
            return sumTokens(parseTokens(calculationData));
        } else {
            return addWithCustomDelimiter(input, calculationData);
        }
    }

    private static CalculationData parseInputToData(String input) {
        if (input.startsWith("//")) {
            InputParser.ParseResult parseResult = InputParser.parseInput(input);
            return new CalculationData(input, parseResult.getCustomDelimiter());
        } else {
            return new CalculationData(input, Delimiters.COMMA + "|" + Delimiters.NEWLINE);
        }
    }

    private static void validateEndSeparator(CalculationData calculationData) {
        String input = calculationData.input();
        if (input.endsWith(Delimiters.COMMA) || input.endsWith(Delimiters.NEWLINE)) {
            throw new InputException("Separator at end not allowed");
        }
    }

    private static List<String> parseTokens(CalculationData calculationData) {
        String input = calculationData.input();
        return java.util.Arrays.asList(input.split(calculationData.delimiter()));
    }

    private static List<Integer> addWithCustomDelimiter(String input, CalculationData calculationData) {
        int newlineIdx = input.indexOf('\n');
        String delimiter = calculationData.delimiter();
        String numbersPart = input.substring(newlineIdx + 1);

        if (numbersPart.endsWith(delimiter)) {
            throw new InputException("Separator at end not allowed");
        }

        List<String> errorMessages = new ArrayList<>();
        String delimiterError = findDelimiterMisuseMessage(numbersPart, delimiter);
        if (delimiterError != null) {
            errorMessages.add(delimiterError);
        }

        List<Integer> negatives = extractNegatives(numbersPart);
        if (!negatives.isEmpty()) {
            errorMessages.addFirst(buildNegativesMessage(negatives));
        }

        if (!errorMessages.isEmpty()) {
            throw new InputException(String.join("\n", errorMessages));
        }

        InputParser.ParseResult parseResult = InputParser.parseInput(input);
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

    private static List<Integer> sumTokens(List<String> tokens) {
        List<Integer> numbers = new ArrayList<>();
        List<Integer> negatives = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        for (String token : tokens) {
            if (token.isBlank()) {
                errorMessages.add("Separator at end not allowed");
                continue;
            }

            int value = Integer.parseInt(token.trim());

            if (value < 0) {
                negatives.add(value);
            } else {
                numbers.add(value);
            }
        }

        if (!negatives.isEmpty()) {
            errorMessages.addFirst(buildNegativesMessage(negatives));
        }

        if (!errorMessages.isEmpty()) {
            throw new InputException(String.join("\n", errorMessages));
        }

        return numbers;
    }
}