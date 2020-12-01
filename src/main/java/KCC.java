import arithmetic.ArithmeticParser;
import arithmetic.ArithmeticToAsm;
import arithmetic.Node;
import functions.Function;
import functions.Parameter;
import types.Type;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static functions.Function.*;

public class KCC {

    private static final HashSet<Character> SPACERS = new HashSet<>();
    static {
        SPACERS.add(' ');
        SPACERS.add('\n');
        SPACERS.add('\t');
    }

    private final String sourceFilePath;
    private final String outputFilePath;

    KCC(String sourceFileName) {
        this.sourceFilePath = "src/main/resources/c_source/" + sourceFileName;
        this.outputFilePath = "src/main/resources/compiled/" + sourceFileName.split("\\.")[0] + ".asm";
    }

    void compile() throws IOException {
        String s = Files.lines(Path.of(sourceFilePath)).collect(Collectors.joining("\n"));

        for (int i = 0; i < s.length(); i++) {
            if (SPACERS.contains(s.charAt(i))) continue; // skip useless characters on level 0 (outside of anything)
            parseFunction(s, i);
        }
    }

    private void parseFunction(String s, int i) {
        Function.Builder b = new Function.Builder();
        i = parseFunctionReturnType(s, i, b);
        i = parseFunctionName(s, b, i);
        i = parseFunctionParameters(s, b, i);

        // function body asm code
        while (s.charAt(i) != '{') i++;

        while (true) { // parse unit by unit
            while (SPACERS.contains(s.charAt(++i)));
            if (s.charAt(i) == 'i' && s.charAt(i + 1) == 'f' && SPACERS.contains(s.charAt(i + 2)))
                throw new RuntimeException("IF statements not allowed yet");

            int j = i;
            while(s.charAt(++j) != ';');
            String currentExpression = s.substring(i, j);

//          if (declaration) {
//              add varName to symbols collection
//          }
            if (currentExpression.contains("return")) { // compute result into eax -> ret
                String resultExpression = currentExpression.substring("return".length()).trim();
                Node<?> root = ArithmeticParser.parseTree(resultExpression);
                String asmCode = ArithmeticToAsm.toAsm(root);
                System.out.println();
                continue;
            }
            if (currentExpression.contains("=")) { // compute result into eax -> mov [varName], eax
                // computeIntoEAX(resultExpression, b.getParameters());
                continue;
            }
//            if (currentExpression is a simple function call) {
//                continue;
//            }
        }
    }

    /**
     * TODO currently doesn't support grouping, no priorities (+ vs *)
     */
    private static String computeIntResultIntoEAX(String expression, List<Parameter> parameters) {
        StringBuilder asmBuilder = new StringBuilder();



        return asmBuilder.toString();
    }

    private int parseFunctionParameters(String s, Builder b, int j) {
        int i;
        int varCounter = 0;
        while (s.charAt(j - 1) != ')') {
            while (SPACERS.contains(s.charAt(j))) j++;
            i = j;
            while (!SPACERS.contains(s.charAt(j))) j++;
            String typeName = s.substring(i, j);

            while (SPACERS.contains(s.charAt(j))) j++;
            i = j;
            while (!SPACERS.contains(s.charAt(j)) && s.charAt(j) != ')' && s.charAt(j) != ',') j++;
            String varName = s.substring(i, j++);

            b.withParameter(new Parameter(Type.getTYPES_MAP().get(typeName), varName, (-4) * (++varCounter) - 8));
        }
        return j;
    }

    private int parseFunctionName(String s, Builder b, int j) {
        int i;
        while (SPACERS.contains(s.charAt(j++)));
        i = j - 1;
        while (s.charAt(j++) != '(');
        String functionName = s.substring(i, j - 1);
        b.withName(functionName);
        return j;
    }

    private int parseFunctionReturnType(String s, int i, Builder b) {
        int j = i;
        while (!SPACERS.contains(s.charAt(j++)));
        String returnTypeName = s.substring(i, j - 1);
        b.withReturnType(Type.getTYPES_MAP().get(returnTypeName));
        return j;
    }

    /**
     * processes a line containing code
     */
    private void processLine(Map<String, Object> constLabelToVal, List<String> mainOps, String line) {
        line = line.trim();
        for (int i = 0; i < line.length(); i++) {
            String substring = line.substring(0, i);
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
        List<Map.Entry<String, Object>> entryList = new ArrayList<>(constLabelToVal.entrySet());
        entryList.sort(Map.Entry.comparingByKey());
        for (Map.Entry<String, Object> entry : entryList) {
            sb.append("\t")
                    .append(entry.getKey())
                    .append(entry.getValue() instanceof Integer ? ": dd " : ": db ")
                    .append(entry.getValue())
                    .append("\n");
        }
        return sb.append("\n").toString();
    }
}
