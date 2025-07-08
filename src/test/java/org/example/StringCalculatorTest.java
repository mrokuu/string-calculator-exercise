package org.example;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringCalculatorTest {

    @Test
    @DisplayName("Empty input returns zero")
    void add_emptyString_returnsZero() {
        assertThat(StringCalculator.add(StringUtils.EMPTY)).isZero();
    }

    @Test
    @DisplayName("Null input returns zero")
    void add_nullInput_returnsZero() {
        assertThat(StringCalculator.add(null)).isZero();
    }

    @Test
    @DisplayName("Single number returns itself")
    void add_singleNumber_returnsSame() {
        assertThat(StringCalculator.add("34")).isEqualTo(34);
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @MethodSource("provideInputsAndExpectedSums")
    @DisplayName("Sum of multiple numbers")
    void add_multipleNumbers_sumsCorrectly(String input, int expected) {
        assertThat(StringCalculator.add(input)).isEqualTo(expected);
    }

    static Stream<Arguments> provideInputsAndExpectedSums() {
        return Stream.of(
                Arguments.of("1,2", 3),
                Arguments.of("11,22,33", 66),
                Arguments.of("10,20,30,40", 100),
                Arguments.of("10,20,30,40,50", 150)
        );
    }

    @Test
    @DisplayName("Newline treated as separator")
    void add_newlineSeparator_works() {
        assertThat(StringCalculator.add("1,2\n3")).isEqualTo(6);
    }

    @Test
    @DisplayName("Only newlines as separators")
    void add_onlyNewlines_returnsSum() {
        assertThat(StringCalculator.add("1\n2\n3")).isEqualTo(6);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1,2,", "1,2\n"})
    @DisplayName("Separator at end throws InputException")
    void add_separatorAtEnd_throws(String input) {
        assertThatThrownBy(() -> StringCalculator.add(input))
                .isInstanceOf(InputException.class)
                .hasMessageContaining("Separator at end not allowed");
    }

    @Test
    @DisplayName("Comma immediately before newline throws")
    void add_commaBeforeNewline_throws() {
        assertThatThrownBy(() -> StringCalculator.add("2,\n3"))
                .isInstanceOf(InputException.class);
    }

    @Test
    @DisplayName("Whitespace around numbers is ignored")
    void add_whitespaceIgnored() {
        assertThat(StringCalculator.add(" 1 ,\t2 ")).isEqualTo(3);
    }

    @ParameterizedTest(name = "{0} with custom delimiter -> {1}")
    @MethodSource("validCustomDelimiterInputs")
    @DisplayName("Custom delimiter sums correctly")
    void add_customDelimiter_sumsCorrectly(String input, int expected) {
        assertThat(StringCalculator.add(input)).isEqualTo(expected);
    }

    static Stream<Arguments> validCustomDelimiterInputs() {
        return Stream.of(
                Arguments.of("//;\n1;3", 4),
                Arguments.of("//|\n1|2|3", 6),
                Arguments.of("//#\n2#2#5", 9),
                Arguments.of("//sep\n2sep5", 7),
                Arguments.of("//*\n1*2*3", 6)
        );
    }

    @Test
    @DisplayName("Mixed delimiters throws specific InputException")
    void add_mixedDelimiters_throws() {
        assertThatThrownBy(() -> StringCalculator.add("//|\n1|2,3"))
                .isInstanceOf(InputException.class)
                .hasMessage("'|' expected but ',' found at position 3.");
    }

    @Test
    @DisplayName("Custom delimiter at end throws")
    void add_customDelimiterAtEnd_throws() {
        assertThatThrownBy(() -> StringCalculator.add("//;\n1;2;"))
                .isInstanceOf(InputException.class)
                .hasMessageContaining("Separator at end not allowed");
    }

    @Test
    @DisplayName("Consecutive delimiters throw")
    void add_consecutiveDelimiters_throws() {
        assertThatThrownBy(() -> StringCalculator.add("//|\n1||3"))
                .isInstanceOf(InputException.class)
                .hasMessageContaining("Separator at end not allowed");
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @MethodSource("negativeInputs")
    @DisplayName("Negative numbers list in exception")
    void add_negativeNumbers_throws(String input, String expectedMessage) {
        assertThatThrownBy(() -> StringCalculator.add(input))
                .isInstanceOf(InputException.class)
                .hasMessage(expectedMessage);
    }

    static Stream<Arguments> negativeInputs() {
        return Stream.of(
                Arguments.of("1,-2", "Negative number(s) not allowed: -2"),
                Arguments.of("2,-4,-9", "Negative number(s) not allowed: -4, -9"),
                Arguments.of("-2,-4,-9", "Negative number(s) not allowed: -2, -4, -9"),
                Arguments.of("-10", "Negative number(s) not allowed: -10")
        );
    }

    @Test
    @DisplayName("Aggregates negative and mixed-delimiter errors")
    void add_negativeAndMixedErrors_throwsAggregated() {
        String input = "//|\n1|2,-3";
        InputException ex = assertThrows(InputException.class, () -> StringCalculator.add(input));
        String expected = "Negative number(s) not allowed: -3\n" +
                "'|' expected but ',' found at position 3.";
        assertThat(ex.getMessage()).isEqualTo(expected);
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @MethodSource("largeNumberInputs")
    @DisplayName("Numbers >1000 are ignored")
    void add_largeNumbers_ignored(String input, int expected) {
        assertThat(StringCalculator.add(input)).isEqualTo(expected);
    }

    static Stream<Arguments> largeNumberInputs() {
        return Stream.of(
                Arguments.of("2,1001", 2),
                Arguments.of("1000,1", 1001),
                Arguments.of("1000,1001,2", 1002),
                Arguments.of("//;\n2;1001;3", 5),
                Arguments.of("//sep\n2sep1001sep1002", 2)
        );
    }
}