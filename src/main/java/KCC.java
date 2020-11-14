import functions.Function;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static functions.Function.FUNCTIONS;
import static functions.Function.FUNCTION_MAP;

public class KCC {

    private final String sourceFilePath;
    private final String outputFilePath;

    KCC(String sourceFileName) {
        this.sourceFilePath = "src/main/resources/c_source/" + sourceFileName;
        this.outputFilePath = "src/main/resources/compiled/" + sourceFileName.split("\\.")[0] + ".asm";
    }

    void compile() throws IOException {
        List<String> lines = Files.lines(Path.of(sourceFilePath)).collect(Collectors.toList());

        int lineI = 0;
        // skip to header line of "int main()"
        while (lineI < lines.size() && Util.isNullOrEmpty(lines.get(lineI))) lineI++;
        // skip to first line of "int main() \n {"
        lineI += 2;

        Map<String, Object> constLabelToVal = new HashMap<>();
        List<String> mainOps = new ArrayList<>();

        String line;
        while (!(line = lines.get(lineI)).equals("}")) { // inside main(){} function
            processLine(constLabelToVal, mainOps, line);
            lineI++;
        }

        writeOFile(constLabelToVal, mainOps);
    }

    /**
     * processes a line containing code
     */
    private void processLine(Map<String, Object> constLabelToVal, List<String> mainOps, String line) {
        line = line.trim();
        for (int i = 0; i < line.length(); i++) {
            String substring = line.substring(0, i);
//            if (Types.names.contains(substring)) { // e.g. "int"
//                Types varType = Types.valueOf(substring.toUpperCase());
//                varType.parse(line, constants);
//
//                int j = i + 1;
//                while (line.charAt(j) == ' ') j++; // skip space between type and variable name
//
//                int k = j + 1;
//                while (k < line.length() && Character.isAlphabetic(line.charAt(k))) k++; // only alphabetic characters in variable name
//                String varName = line.substring(j, k);
//
//                while (line.charAt(j) == ' ') j++; // skip space between variable name and potential initialization
//                if (line.charAt(j) != '=') continue; // we don't have initialization
//
//                // if we have variable initialization
//                k = j + 1;
//
//            } else
            if (FUNCTIONS.contains(substring)) { // e.g. "printf_length"
                // get function object
                Function function = FUNCTION_MAP.get(substring);
                function.parse(line, constLabelToVal, mainOps);
            }
        }
    }

    private void writeOFile(Map<String, Object> constLabelToVal, List<String> mainOps) throws IOException {
        String dataSection = writeDataSection(constLabelToVal);
        String textSection = writeTextSection(mainOps);
        String procedures = writeProcedures();

        FileWriter fw = new FileWriter(outputFilePath);
        fw.write("; kobold compiler\n");
        fw.write(dataSection);
        fw.write(textSection);
        fw.write(procedures);
        fw.close();
    }

    private String writeProcedures() {
        StringBuilder sb = new StringBuilder("; procedures section\n");
        Function.getFUNCTION_MAP().values().forEach(f -> sb
                .append(f.getName())
                .append(":\n\t")
                .append(String.join("\n\t", f.getAsmCode()))
                .append("\n"));
        return sb.toString();
    }

    private String writeTextSection(List<String> mainOps) {
        StringBuilder sb = new StringBuilder("section .text\nglobal _start:\n_start:\n\t");
        mainOps.forEach(s -> sb.append(s).append("\n\t"));

        // shutdown
        sb.append("\n\t")
                .append("; over and out")
                .append("\n\t")
                .append("mov eax, 1     ; system exit")
                .append("\n\t")
                .append("mov ebx, 0     ; exit code 0")
                .append("\n\t")
                .append("int 80h        ; call kernel")
                .append("\n\t");
        return sb.append("\n").toString();
    }

    private static String writeDataSection(Map<String, Object> constLabelToVal) {
        StringBuilder sb = new StringBuilder("section .data\n");
        for (Map.Entry<String, Object> entry : constLabelToVal.entrySet()) {
            sb.append("\t")
                    .append(entry.getKey())
                    .append(entry.getValue() instanceof Integer ? ": dd " : ": db ")
                    .append(entry.getValue())
                    .append("\n");
        }
        return sb.append("\n").toString();
    }
}
