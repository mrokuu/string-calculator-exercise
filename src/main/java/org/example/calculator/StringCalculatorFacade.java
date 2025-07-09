package org.example.calculator;

import java.util.Arrays;
import java.util.List;

public class StringCalculatorFacade {
    private static final int MAX_VALUE = 1000;

    public int add(String... args) {
        return Arrays.stream(args)
                .mapToInt(this::add)
                .sum();
    }

    public int add(String input) {
        if (input == null || input.trim().isEmpty()) {
            return 0;
        }

        List<Integer> numbers = extractAndValidateNumbers(input);
        return numbers.stream()
                .filter(num -> num <= MAX_VALUE)
                .mapToInt(Integer::intValue)
                .sum();
    }

    private List<Integer> extractAndValidateNumbers(String input) {
        ParsedData parsedData = parseInput(input);

        InputValidationService.validateInputFormat(parsedData);
        List<Integer> numbers = DelimiterService.extractNumbers(parsedData);
        InputValidationService.validateNegativeNumbers(numbers);

        return numbers;
    }

    private ParsedData parseInput(String input) {
        if (CustomDelimiterService.hasCustomDelimiter(input)) {
            return CustomDelimiterService.parseCustomDelimiter(input);
        }
        return new ParsedData(input, DelimiterService.DEFAULT_DELIMITERS);
    }
}