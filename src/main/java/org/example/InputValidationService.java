package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class InputValidationService {
    private static final Pattern NEGATIVE_PATTERN = Pattern.compile("-\\d+");

    public static void validateInputFormat(CalculationData calculationData) {
        List<String> errorMessages = new ArrayList<>();

        validateEndSeparator(calculationData, errorMessages);
        validateMixedSeparator(calculationData, errorMessages);
        validateDelimiterConsistency(calculationData, errorMessages);

        if (!errorMessages.isEmpty()) {
            throw new InputException(String.join("\n", errorMessages));
        }
    }

    public static void validateNegativeNumbers(List<Integer> numbers) {
        List<Integer> negatives = numbers.stream()
                .filter(num -> num < 0)
                .toList();

        if (!negatives.isEmpty()) {
            String message = "Negative number(s) not allowed: " +
                    negatives.stream()
                            .map(Object::toString)
                            .collect(Collectors.joining(", "));
            throw new InputException(message);
        }
    }

    private static void validateEndSeparator(CalculationData calculationData, List<String> errorMessages) {
        String input = calculationData.input();
        String delimiter = calculationData.delimiter();

        if (DelimiterService.DEFAULT_DELIMITERS.equals(delimiter)) {
            if (input.endsWith(DelimiterService.COMMA) || input.endsWith(DelimiterService.NEWLINE)) {
                errorMessages.add("Separator at end not allowed");
            }
        } else {
            if (input.endsWith(delimiter)) {
                errorMessages.add("Separator at end not allowed");
            }
        }
    }

    private static void validateMixedSeparator(CalculationData calculationData, List<String> errorMessages) {
        String input = calculationData.input();
        String delimiter = calculationData.delimiter();

        if (DelimiterService.DEFAULT_DELIMITERS.equals(delimiter)) {
            if (input.contains(DelimiterService.COMMA + DelimiterService.NEWLINE)) {
                errorMessages.add("Separator at end not allowed");
            }
        }
    }

    private static void validateDelimiterConsistency(CalculationData calculationData, List<String> errorMessages) {
        String input = calculationData.input();
        String delimiter = calculationData.delimiter();

        if (!DelimiterService.DEFAULT_DELIMITERS.equals(delimiter)) {
            if (input.contains(delimiter + delimiter)) {
                errorMessages.add("Separator at end not allowed");
            }

            String delimiterError = findDelimiterMismatch(input, delimiter);
            if (delimiterError != null) {
                errorMessages.add(delimiterError);
            }

            List<Integer> negatives = extractNegativeNumbers(input);
            if (!negatives.isEmpty()) {
                String negativeMessage = "Negative number(s) not allowed: " +
                        negatives.stream()
                                .map(Object::toString)
                                .collect(Collectors.joining(", "));
                errorMessages.add(0, negativeMessage);
            }
        }
    }

    private static String findDelimiterMismatch(String input, String delimiter) {
        for (int i = 0; i < input.length(); ) {
            if (input.startsWith(delimiter, i)) {
                i += delimiter.length();
                continue;
            }

            char ch = input.charAt(i);
            if (Character.isDigit(ch) || ch == '-') {
                i++;
                continue;
            }

            return "'" + delimiter + "' expected but '" + ch + "' found at position " + i + ".";
        }
        return null;
    }

    private static List<Integer> extractNegativeNumbers(String input) {
        return NEGATIVE_PATTERN.matcher(input)
                .results()
                .map(MatchResult::group)
                .map(Integer::parseInt)
                .toList();
    }
}