import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KCC {

    private final String sourceFilePath;
    private final String outputFilePath;

    KCC(String sourceFileName) {
        this.sourceFilePath = "c_source/" + sourceFileName;
        this.outputFilePath = "compiled/" + sourceFileName.split("\\.")[0] + ".asm";
    }

    void compile() throws IOException {
        List<String> lines = Files.lines(Path.of(sourceFilePath)).collect(Collectors.toList());

        int lineI = 0;
        // skip to header line of "int main()"
        while (lineI < lines.size() && Util.isNullOrEmpty(lines.get(lineI))) lineI++;

        // skip to first line of "int main() \n {"
        lineI += 2;

        List<String> data = new ArrayList<>();

        while (lines.get(lineI).contains("printf")) {
            String[] lineParts = lines.get(lineI).split("\"");
            String text = lineParts[1];
            data.add(text);
            lineI++;
        }

        writeOFile(data);
    }

    private void writeOFile(List<String> data) throws IOException {
        FileWriter wr = new FileWriter(outputFilePath);
        wr.write("; kobold compiler\n");

        // section .data
        wr.write("section .data");
        for (String s : data) {
            String stringPointer = Util.nextDataName();
            wr.write("\n\t" + stringPointer + ":\tdb \"" + s + "\", 10");
            wr.write("\n\t" + Util.nextDataName() + ":\tequ $-" + stringPointer);
        }

        // section .text
        wr.write("\n\nsection .text\n\tglobal _start:\n");
        wr.write("_start:");
        for (int i = 0; i < data.size(); i++) {
            wr.write("\n\tmov eax, 4");
            wr.write("\n\tmov ebx, 1");
            wr.write("\n\tmov ecx, _" + (2 * i + 1));
            wr.write("\n\tmov edx, _" + (2 * i + 2));
            wr.write("\n\tint 80h");
        }


        // shutdown
        wr.write("\n\n\tmov eax, 1");
        wr.write("\n\tmov ebx, 0");
        wr.write("\n\tint 80h");

        wr.close();
    }
}
