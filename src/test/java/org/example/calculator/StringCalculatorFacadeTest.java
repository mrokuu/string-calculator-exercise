package org.example.calculator;

import org.apache.commons.lang3.StringUtils;
import org.example.calculator.exceptions.InputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StringCalculatorFacadeTest {

    private StringCalculatorFacade calc;

    @BeforeEach
    void setUp() {
        calc = new StringCalculatorFacade();
    }

    @Test
    @DisplayName("Empty or null input returns zero")
    void add_shouldReturnZero_whenInputIsEmptyOrNull() {
        // given
        String empty = StringUtils.EMPTY;
        String nul = null;
        // when
        int resultEmpty = calc.add(empty);
        int resultNull = calc.add(nul);
        // then
        assertThat(resultEmpty).isZero();
        assertThat(resultNull).isZero();
    }

    @Test
    @DisplayName("Single number returns itself")
    void add_shouldReturnSameNumber_whenSingleNumber() {
        // given
        String input = "34";
        // when
        int result = calc.add(input);
        // then
        assertThat(result).isEqualTo(34);
    }

    @Test
    @DisplayName("Varargs adds each string in turn")
    void add_shouldReturnSum_whenCalledWithVarargs() {
        // given
        String a = "1,2";
        String b = "3\n4";
        String c = "//;\n5;6";
        // when
        int result = calc.add(a, b, c);
        // then
        assertThat(result).isEqualTo(21);
    }

    @ParameterizedTest(name = "\"{0}\" -> {1}")
    @MethodSource("provideMultipleSums")
    @DisplayName("Multiple numbers sum correctly")
    void add_shouldReturnSum_whenMultipleNumbers(String input, int expected) {
        // when
        int result = calc.add(input);
        // then
        assertThat(result).isEqualTo(expected);
    }

    static Stream<Arguments> provideMultipleSums() {
        return Stream.of(
                Arguments.of("1,2", 3),
                Arguments.of("11,22,33", 66),
                Arguments.of("10,20,30,40,50", 150)
        );
    }

    @Test
    @DisplayName("Newline treated as comma")
    void add_shouldTreatNewlineAsComma_whenNewlinePresent() {
        // when / then
        assertThat(calc.add("1,2\n3")).isEqualTo(6);
        assertThat(calc.add("1\n2\n3")).isEqualTo(6);
    }

    @ParameterizedTest(name = "\"{0}\" throws trailing-separator")
    @ValueSource(strings = {"1,2,", "1,2\n", "//;\n1;2;", "//|\n1||3"})
    @DisplayName("Trailing separator throws exception")
    void add_shouldThrowException_whenTrailingSeparator(String input) {
        // when / then
        assertThatThrownBy(() -> calc.add(input))
                .isInstanceOf(InputException.class)
                .hasMessageContaining(ErrorMessages.TRAILING_SEPARATOR);
    }

    @Test
    @DisplayName("Comma before newline is invalid")
    void add_shouldThrowException_whenCommaBeforeNewline() {
        // when / then
        assertThatThrownBy(() -> calc.add("2,\n3"))
                .isInstanceOf(InputException.class)
                .hasMessageContaining(ErrorMessages.TRAILING_SEPARATOR);
    }

    @Test
    @DisplayName("Whitespace around numbers is ignored")
    void add_shouldIgnoreWhitespace_whenWhitespaceInInput() {
        // when
        int result = calc.add(" 1 ,\t2 ");
        // then
        assertThat(result).isEqualTo(3);
    }

    @ParameterizedTest(name = "\"{0}\" -> {1}")
    @MethodSource("provideCustomSums")
    @DisplayName("Custom delimiters sum correctly")
    void add_shouldReturnSum_whenCustomDelimiter(String input, int expected) {
        // when
        int result = calc.add(input);
        // then
        assertThat(result).isEqualTo(expected);
    }

    static Stream<Arguments> provideCustomSums() {
        return Stream.of(
                Arguments.of("//;\n1;3", 4),
                Arguments.of("//|\n1|2|3", 6),
                Arguments.of("//#\n2#2#5", 9),
                Arguments.of("//sep\n2sep5", 7),
                Arguments.of("//*\n1*2*3", 6)
        );
    }

    @Test
    @DisplayName("Custom delimiter as space sums correctly")
    void add_shouldReturnSum_whenCustomDelimiterIsSpace() {
        // given
        String input = "// \n1 2 3";
        // when
        int result = calc.add(input);
        // then
        assertThat(result).isEqualTo(6);
    }

    @Test
    @DisplayName("Custom delimiter as digit inside numbers sums correctly")
    void add_shouldReturnSum_whenCustomDelimiterIsDigit() {
        // given
        String input = "//9\n19293";
        // when
        int result = calc.add(input);
        // then
        assertThat(result).isEqualTo(6);
    }

    @Test
    @DisplayName("Custom delimiter identical to comma works")
    void add_shouldReturnSum_whenCustomDelimiterIsComma() {
        // given
        String input = "//,\n1,2,3";
        // when
        int result = calc.add(input);
        // then
        assertThat(result).isEqualTo(6);
    }

    @Test
    @DisplayName("Single 1000 is accepted")
    void add_shouldAcceptNumber_whenValueIs1000() {
        // when
        int result = calc.add("1000");
        // then
        assertThat(result).isEqualTo(1000);
    }

    @Test
    @DisplayName("Single number above limit is ignored")
    void add_shouldIgnoreNumber_whenValueOverLimit() {
        // when
        int result = calc.add("1001");
        // then
        assertThat(result).isZero();
    }

    @Test
    @DisplayName("Newline after custom delimiter is invalid")
    void add_shouldThrowException_whenNewlineAfterCustomDelimiter() {
        // given
        String input = "//;\n1;2\n3";
        // when / then
        assertThatThrownBy(() -> calc.add(input))
                .isInstanceOf(InputException.class)
                .hasMessageContaining("';' expected");
    }

    @Test
    @DisplayName("Mixed delimiters throws specific error")
    void add_shouldThrowSpecificError_whenMixedDelimiters() {
        // when / then
        assertThatThrownBy(() -> calc.add("//|\n1|2,3"))
                .isInstanceOf(InputException.class)
                .hasMessage("'|' expected but ',' found at position 3.");
    }

    @Test
    @DisplayName("Single negative number is not allowed")
    void add_shouldThrowException_whenSingleNegative() {
        // when / then
        assertThatThrownBy(() -> calc.add("1,-2"))
                .isInstanceOf(InputException.class)
                .hasMessage("Negative number(s) not allowed: -2");
    }

    @ParameterizedTest(name = "\"{0}\" → {1}")
    @MethodSource("provideNegativeLists")
    @DisplayName("Multiple negatives list in exception")
    void add_shouldThrowException_whenMultipleNegatives(String input, String expectedMsg) {
        // when / then
        assertThatThrownBy(() -> calc.add(input))
                .isInstanceOf(InputException.class)
                .hasMessage(expectedMsg);
    }

    static Stream<Arguments> provideNegativeLists() {
        return Stream.of(
                Arguments.of("2,-4,-9", "Negative number(s) not allowed: -4, -9"),
                Arguments.of("-2,-4,-9", "Negative number(s) not allowed: -2, -4, -9"),
                Arguments.of("-10", "Negative number(s) not allowed: -10")
        );
    }

    @Test
    @DisplayName("Aggregated negative + delimiter errors")
    void add_shouldAggregateErrors_whenMultipleIssues() {
        // given
        String input = "//|\n1|2,-3";
        // when / then
        assertThatThrownBy(() -> calc.add(input))
                .hasMessage(
                        "Negative number(s) not allowed: -3\n" +
                                "'|' expected but ',' found at position 3."
                );
    }

    @ParameterizedTest(name = "\"{0}\" -> {1}")
    @MethodSource("provideLargeNumberInputs")
    @DisplayName("Numbers greater than 1000 are ignored")
    void add_shouldIgnoreNumbersOverLimit_whenCalculatingSum(String input, int expected) {
        // when
        int result = calc.add(input);
        // then
        assertThat(result).isEqualTo(expected);
    }

    static Stream<Arguments> provideLargeNumberInputs() {
        return Stream.of(
                Arguments.of("2,1001", 2),
                Arguments.of("1000,1", 1001),
                Arguments.of("1000,1001,2", 1002),
                Arguments.of("//;\n2;1001;3", 5),
                Arguments.of("//sep\n2sep1001sep1002", 2)
        );
    }
}
