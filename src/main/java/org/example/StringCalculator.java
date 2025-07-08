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

        if (CustomDelimiterService.hasCustomDelimiter(input)) {
            return processCustomDelimiter(calculationData);
        } else {
            return processDefaultDelimiters(calculationData);
        }
    }

    private static CalculationData parseInputToData(String input) {
        if (CustomDelimiterService.hasCustomDelimiter(input)) {
            return CustomDelimiterService.parseCustomDelimiter(input);
        } else {
            return new CalculationData(input, DelimiterService.DEFAULT_DELIMITERS);
        }
    }

    private static List<Integer> processDefaultDelimiters(CalculationData calculationData) {
        validateEndSeparator(calculationData);
        List<Integer> numbers = DelimiterService.extractNumbers(calculationData);
        validateNegativeNumbers(numbers);
        return numbers;
    }

    private static List<Integer> processCustomDelimiter(CalculationData calculationData) {
        String numbersPart = calculationData.input();
        String delimiter = calculationData.delimiter();

        List<String> errorMessages = new ArrayList<>();

        if (numbersPart.endsWith(delimiter)) {
            errorMessages.add("Separator at end not allowed");
        }

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

        List<Integer> numbers = DelimiterService.extractNumbers(calculationData);
        return numbers;
    }

    private static void validateEndSeparator(CalculationData calculationData) {
        String input = calculationData.input();
        if (input.endsWith(DelimiterService.COMMA) || input.endsWith(DelimiterService.NEWLINE)) {
            throw new InputException("Separator at end not allowed");
        }
    }

    private static void validateNegativeNumbers(List<Integer> numbers) {
        List<Integer> negatives = numbers.stream()
                .filter(num -> num < 0)
                .toList();

        if (!negatives.isEmpty()) {
            throw new InputException(buildNegativesMessage(negatives));
        }
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
}
