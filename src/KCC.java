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

        List<String> constants = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String line;
        while (!(line = lines.get(lineI)).equals("}")) { // inside main(){} function
            processLine(constants, operations, line);
            lineI++;
        }

        writeOFile(constants, operations);
    }

    private void processLine(List<String> constants, List<Operation> operations, String line) {
        line = line.trim();
        for (int i = 0; i < line.length(); i++) {
            String substring = line.substring(0, i);
            if (Types.names.contains(substring)) { // e.g. "int"
                Types varType = Types.valueOf(substring.toUpperCase());
                varType.parse(line, constants);

                int j = i + 1;
                while (line.charAt(j) == ' ') j++; // skip space between type and variable name

                int k = j + 1;
                while (k < line.length() && Character.isAlphabetic(line.charAt(k))) k++; // only alphabetic characters in variable name
                String varName = line.substring(j, k);

                while (line.charAt(j) == ' ') j++; // skip space between variable name and potential initialization
                if (line.charAt(j) != '=') continue; // we don't have initialization

                // if we have variable initialization
                k = j + 1;

            } else if (Functions.names.contains(substring)) { // e.g. "printf"
                Functions function = Functions.valueOf(substring.toUpperCase());
                function.parse(line, constants, operations);
            }
        }
    }

    private void writeOFile(List<String> constants, List<Operation> operations) throws IOException {

        StringBuilder constantsBuilder = new StringBuilder("section .data\n");
        for (int i = 0; i < constants.size(); i++) {
            String line = "\n\tc" + i + " : db \"" + constants.get(i) + "\", 10";
            constantsBuilder.append(line);

            String lineLen = "\n\tc" + i + "l: equ $-c" + i;
            constantsBuilder.append(lineLen);
        }
        constantsBuilder.append("\n\n");

        StringBuilder textBuilder = new StringBuilder("section .text\nglobal _start:\n_start:");
        for (int i = 0; i < operations.size(); i++) {
            Operation op = operations.get(i);
            op.writeYourself(textBuilder);
        }

        FileWriter wr = new FileWriter(outputFilePath);
        wr.write("; kobold compiler\n");
        wr.write(constantsBuilder.toString());
        wr.write(textBuilder.toString());

        // shutdown
        wr.write("\n\n\tmov eax, 1");
        wr.write("\n\tmov ebx, 0");
        wr.write("\n\tint 80h");

        wr.close();
    }
}
