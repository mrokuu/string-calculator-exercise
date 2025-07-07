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


class StringCalculatorTest {

    @Test
    void add_shouldReturnZero_whenInputIsEmpty() {
        // given
        String value = StringUtils.EMPTY;

        // when
        var result = StringCalculator.add(value);

        // then
        assertThat(result).isZero();
    }

    @Test
    void add_shouldReturnSameNumber_whenInputIsSingleNumber() {
        // given
        String value = "34";

        // when
        var result = StringCalculator.add(value);

        // then
        assertThat(result).isEqualTo(Integer.parseInt(value));
    }

    @Test
    void add_shouldReturnSum_whenInputContainsTwoNumbersSeparatedByComma() {
        // given
        String value = "11,22";

        // when
        var result = StringCalculator.add(value);

        // then
        assertThat(result).isEqualTo(33);
    }

    @ParameterizedTest(name = "{index} ⇒ add(\"{0}\") = {1}")
    @MethodSource("provideInputsAndExpectedSums")
    void add_shouldReturnExpectedSum_forVariousInputs(String input, int expected) {
        // when
        int result = StringCalculator.add(input);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void add_shouldTreatNewlineAsSeparator_whenInputContainsCommaAndNewline() {
        // given
        String input = "1,2\n3";

        // when
        int result = StringCalculator.add(input);

        // then
        assertThat(result).isEqualTo(6);
    }

    @ParameterizedTest(name = "should throw IllegalArgumentException")
    @ValueSource(strings = {"1,2,",
            "1,2\n"})
    void add_shouldThrowIllegalArgumentException_whenSeparatorIsAtTheEnd(String input) {
        // when / then
        assertThatThrownBy(() -> StringCalculator.add(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Separator at end not allowed");
    }

    @ParameterizedTest(name = "{index} ⇒ add(\"{0}\") = {1}")
    @MethodSource("validInputs")
    @DisplayName("Should return the correct sum")
    void add_shouldReturnSum_whenCustomDelimiterSpecified(String input, int expected) {
        // when
        int result = StringCalculator.add(input);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("Should throw when delimiters are mixed")
    void add_shouldThrowIllegalArgumentException_whenDelimitersAreMixed() {
        // given
        String input = "//|\n1|2,3";

        // when / then
        assertThatThrownBy(() -> StringCalculator.add(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("'|' expected but ',' found at position 3.");
    }

    @Test
    @DisplayName("Throws when the custom delimiter appears at the very end")
    void shouldThrow_whenCustomDelimiterAtEnd() {
        String input = "//;\n1;2;";
        assertThatThrownBy(() -> StringCalculator.add(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Separator at end not allowed");
    }

    @Test
    @DisplayName("Throws when two delimiters occur consecutively (empty token)")
    void shouldThrow_whenTwoDelimitersInARow() {
        String input = "//|\n1||3";
        assertThatThrownBy(() -> StringCalculator.add(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Separator at end not allowed");
    }

    static Stream<Arguments> provideInputsAndExpectedSums() {
        return Stream.of(
                Arguments.of("1,2", 3),
                Arguments.of("11,22,33", 66),
                Arguments.of("10,20,30,40", 100),
                Arguments.of("10,20,30,40,50", 150)
        );
    }

    private static Stream<Arguments> validInputs() {
        return Stream.of(
                Arguments.of("//;\n1;3", 4),
                Arguments.of("//|\n1|2|3", 6),
                Arguments.of("//#\n2#2#5", 9),
                Arguments.of("//sep\n2sep5", 7)
        );
    }
}