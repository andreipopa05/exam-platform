package examplatform;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExamplesTest {

    @ParameterizedTest
    @ValueSource(strings = {"01-basics", "02-questions-added-after-print", "03-ordering-and-two-exams"})
    void producesTheExpectedFiles(String example, @TempDir Path output) throws IOException {
        Path directory = Path.of("examples", example);

        Main.main(new String[]{directory.resolve("input.in").toString(), output.toString()});

        Path expected = directory.resolve("expected");

        // Exactly the same files, with exactly the same content
        assertEquals(fileNames(expected), fileNames(output));

        for (String name : fileNames(expected)) {
            assertEquals(Files.readString(expected.resolve(name)), Files.readString(output.resolve(name)), name);
        }
    }

    private static List<String> fileNames(Path directory) throws IOException {
        try (Stream<Path> files = Files.list(directory)) {
            return files.map(file -> file.getFileName().toString()).sorted().toList();
        }
    }
}