package org.example;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;


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


    static Stream<Arguments> provideInputsAndExpectedSums() {
        return Stream.of(
                Arguments.of("1,2", 3),
                Arguments.of("11,22,33", 66),
                Arguments.of("10,20,30,40", 100),
                Arguments.of("10,20,30,40,50", 150)
        );
    }
}