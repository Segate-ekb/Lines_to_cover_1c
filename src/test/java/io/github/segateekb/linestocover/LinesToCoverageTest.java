package io.github.segateekb.linestocover;

import com.github._1c_syntax.bsl.parser.BSLTokenizer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Тесты расчёта покрываемых строк. Фикстуры и ожидаемые значения перенесены из Coverage41C
 * (src/test/resources/linestocoverage) - проверка паритета правил.
 */
class LinesToCoverageTest {

    @Test
    void toCover() throws IOException {
        assertArrayEquals(new int[]{
                13, 17, 19, 22, 23, 24, 27, 29, 30, 31, 33, 34, 35, 38, 39, 41, 42, 45, 46, 50, 51, 52, 54,
                56, 58, 60, 66, 67, 69, 70, 71, 73, 74, 75, 76, 78, 79, 80, 81, 82, 84, 92, 93, 94, 98, 103, 105,
                112}, linesOf("tocover"));
    }

    @Test
    void parseError() throws IOException {
        assertArrayEquals(new int[]{4, 6, 8}, linesOf("error"));
    }

    @Test
    void simple() throws IOException {
        assertArrayEquals(new int[]{
                3, 8, 15, 18, 19, 20, 21, 23, 24, 29, 30, 31, 32, 33, 34, 35, 36, 37, 41, 42, 48,
                50, 53, 57, 60, 63, 66, 67, 68, 69, 70, 72}, linesOf("simple"));
    }

    @Test
    void ifStatement() throws IOException {
        assertArrayEquals(new int[]{3, 8, 10, 16, 17, 19, 21, 22, 23}, linesOf("if"));
    }

    @Test
    void assignment() throws IOException {
        assertArrayEquals(new int[]{
                5, 11, 18, 22, 26, 32, 38, 44, 46, 52, 58, 63, 68, 70, 79, 84, 92, 102, 106, 110, 114,
                117, 121, 125, 127, 128}, linesOf("assigment"));
    }

    @Test
    void doLoops() throws IOException {
        assertArrayEquals(new int[]{
                4, 5, 6, 7, 8, 12, 16, 22, 23, 25, 30, 31, 32, 33, 34, 35, 36, 37, 39, 43, 44, 53, 54,
                56, 61, 64, 65, 66, 67, 68, 70, 75, 76, 77, 78, 79, 80, 82, 85, 86, 89, 95, 96, 97, 99, 102, 103, 106,
                107, 109, 110, 111, 112}, linesOf("do"));
    }

    @Test
    void other() throws IOException {
        assertArrayEquals(new int[]{
                4, 10, 15, 18, 19, 21, 34, 48, 50, 53, 55, 59, 61, 62, 64, 65,
                70, 72, 76, 80, 84, 87, 91, 92, 94, 95, 96}, linesOf("other"));
    }

    @Test
    void notCovered() throws IOException {
        assertArrayEquals(new int[]{6, 7, 8, 9, 13, 14, 16, 17, 19, 21, 22, 23}, linesOf("notcovered"));
    }

    @Test
    void filtered() throws IOException {
        assertArrayEquals(new int[]{
                3, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 23, 24, 26, 28, 30, 32, 34, 36, 38, 40, 42, 44, 45, 46, 47,
                48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 62, 63, 65, 70, 73, 75, 77, 79, 81, 83, 85, 87, 89, 91, 93,
                95, 97, 99, 101, 103, 105, 107, 109, 111, 113, 115, 117, 119, 122, 126, 128, 130, 132, 134, 135, 136,
                137, 139, 143, 146, 149, 152, 155, 156, 157, 158}, linesOf("filtered"));
    }

    @Test
    void opcodes() throws IOException {
        assertArrayEquals(new int[]{
                3, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40, 42, 44, 46, 48, 50, 52, 54, 56,
                58, 60, 62, 64, 66, 68, 70, 72, 74, 76, 78, 80, 82, 84, 86, 88, 90, 92, 94, 96, 98, 100, 102, 104, 106,
                108, 111, 113, 115, 117, 119, 121, 123, 125, 127, 129, 131, 133, 135, 137, 139, 141, 143, 145, 147, 149,
                152, 153, 155, 156, 159, 161}, linesOf("opcode"));
    }

    private int[] linesOf(String fixture) throws IOException {
        String content = read("/linestocoverage/" + fixture + ".bsl");
        return LinesToCoverage.getLines(new BSLTokenizer(content).getAst());
    }

    private String read(String resource) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(resource)) {
            assertNotNull(is, "Не найден ресурс: " + resource);
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
