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
        //given
        String input = "//;\n1;2;";

        //when / then
        assertThatThrownBy(() -> StringCalculator.add(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Separator at end not allowed");
    }

    @Test
    @DisplayName("Throws when two delimiters occur consecutively (empty token)")
    void shouldThrow_whenTwoDelimitersInARow() {
        //given
        String input = "//|\n1||3";

        //when / then
        assertThatThrownBy(() -> StringCalculator.add(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Separator at end not allowed");
    }

    @ParameterizedTest(name = "{index} ⇒ \"{0}\" → \"{1}\"")
    @MethodSource("negativeInputs")
    @DisplayName("Throws an exception listing every negative number")
    void add_shouldThrowExceptionWithListOfNegativeNumbers_whenInputHasNegatives(String input,
                                                                                 String expectedMessage) {
        // when / then
        assertThatThrownBy(() -> StringCalculator.add(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    @DisplayName("Works the same with a custom delimiter")
    void add_shouldThrowExceptionWithListOfNegativeNumbers_whenInputHasNegativesAndCustomDelimiter() {
        // given
        String input = "//#\n2#-4#-9";

        // when / then
        assertThatThrownBy(() -> StringCalculator.add(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Negatives not allowed: -4, -9");
    }

    @Test
    @DisplayName("Should aggregate negative-number and mixed-delimiter errors")
    void add_shouldThrowIllegalArgumentExceptionWithAggregatedMessages_whenInputHasNegativesAndMixedDelimiters() {
        // given
        String input = "//|\n1|2,-3";

        // when / then
        assertThatThrownBy(() -> StringCalculator.add(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Negatives not allowed: -3\n'|' expected but ',' found at position 3.");
    }

    @ParameterizedTest(name = "{index} ⇒ add(\"{0}\") = {1}")
    @MethodSource("provideLargeNumberInputs")
    void add_shouldReturnExpectedSum_whenInputContainsLargeNumbers(String input, int expected) {
        assertThat(StringCalculator.add(input)).isEqualTo(expected);
    }

    @Test
    @DisplayName("Null input returns 0")
    void add_nullInput_returnsZero() {
        assertThat(StringCalculator.add(null)).isZero();
    }

    @Test
    @DisplayName("Only newlines as default separators")
    void add_onlyNewlines_returnsSum() {
        assertThat(StringCalculator.add("1\n2\n3")).isEqualTo(6);
    }

    @Test
    @DisplayName("Comma immediately before newline causes exception")
    void add_commaBeforeNewline_throws() {
        assertThatThrownBy(() -> StringCalculator.add("2,\n3"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Whitespace around numbers is ignored")
    void add_numbersWithWhitespace_stillWorks() {
        assertThat(StringCalculator.add(" 1 ,\t2 ")).isEqualTo(3);
    }

    @Test
    @DisplayName("Large numbers ignored with newline-only separators")
    void add_largeNumberWithNewlines_ignored() {
        assertThat(StringCalculator.add("2\n1001\n6")).isEqualTo(8);
    }

    @Test
    @DisplayName("Delimiter that is a regex meta-character is handled correctly")
    void add_regexMetaDelimiter_works() {
        assertThat(StringCalculator.add("//*\n1*2*3")).isEqualTo(6);
    }

    private static Stream<Arguments> provideLargeNumberInputs() {
        return Stream.of(
                Arguments.of("2,1001", 2),
                Arguments.of("1000,1", 1001),
                Arguments.of("1000,1001,2", 1002),
                Arguments.of("//;\n2;1001;3", 5),
                Arguments.of("//sep\n2sep1001sep1002", 2)
        );
    }

    private static Stream<Arguments> negativeInputs() {
        return Stream.of(
                Arguments.of("1,-2", "Negatives not allowed: -2"),
                Arguments.of("2,-4,-9", "Negatives not allowed: -4, -9"),
                Arguments.of("-2,-4,-9", "Negatives not allowed: -2, -4, -9"),
                Arguments.of("-10", "Negatives not allowed: -10")
        );
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