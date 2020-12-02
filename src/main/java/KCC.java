import arithmetic.ArithmeticParser;
import arithmetic.ArithmeticToAsm;
import arithmetic.Node;
import functions.Function;
import functions.Parameter;
import types.Type;
import variables.AsmVariable;
import writeAsmOutput.AsmWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static expression.AssignmentExpressionUtils.handleValueAssignment;
import static expression.ExpressionTypeUtil.*;
import static variables.AsmVariableUtils.handleDeclarationExpression;
import static functions.Function.*;
import static writeAsmOutput.AsmWriter.writeAsmOutput;

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
        String cleanedSource = cleanSourceCode(sourceFilePath);

        Set<AsmVariable> asmVariables = new HashSet<>();

        for (int i = 0; i < cleanedSource.length(); i++) {
            if (SPACERS.contains(cleanedSource.charAt(i))) continue; // skip useless characters on level 0 (outside of anything)
            i = parseFunction(cleanedSource, i, asmVariables);
        }

        writeAsmOutput(outputFilePath, asmVariables, FUNCTION_MAP);
    }

    private static String cleanSourceCode(String filePath) throws IOException {
        return Files.lines(Path.of(filePath))
                .map(String::trim)
                .collect(Collectors.joining("\n"));
    }

    private int parseFunction(String s, int i, Set<AsmVariable> asmVariables) {
        Function.Builder b = new Function.Builder();
        i = parseFunctionReturnType(s, i, b);
        i = parseFunctionName(s, b, i);
        i = parseFunctionParameters(s, b, i);

        StringBuilder functionAsmCode = new StringBuilder();
        Map<String, AsmVariable> currentFunctionAsmVariables = new HashMap<>();

        // function body asm code
        while (s.charAt(i++) != '{');

        while (s.charAt(i) != '}') { // parse until function closed
            // skip any emptiness
            if (SPACERS.contains(s.charAt(i))) {
                i++;
                continue;
            }

            // get expression (only expressions ending in ';' are supported
            int j = i;
            while (s.charAt(++j) != ';') ;
            String currentExpression = s.substring(i, j);

            if (isDeclarationExpression(currentExpression)) {
                handleDeclarationExpression(currentExpression, currentFunctionAsmVariables, b.getName());
                i = j + 1;
                continue;
            }
            if (isAssignmentExpression(currentExpression)) { // compute result into eax -> mov [varName], eax
                handleValueAssignment(b, functionAsmCode, currentFunctionAsmVariables, currentExpression);
                i = j + 1;
                continue;
            }
            if (isFunctionCallExpression(currentExpression)) {

                continue;
            }
            if (isReturnExpression(currentExpression)) { // compute result into eax -> ret
                String resultExpression = currentExpression.substring("return".length()).trim();
                Node<?> root = ArithmeticParser.parseTree(resultExpression);
                StringBuilder returnAsm = ArithmeticToAsm.toAsm(root, b.getParameters())
                        .append("ret\n");
                functionAsmCode.append(returnAsm);
                i = j + 1;
                continue;
            }
//            if (currentExpression is a simple function call) {
//                continue;
//            }
        }
        b.withAsmCode(functionAsmCode.toString());
        Function.addFunction(b);
        asmVariables.addAll(currentFunctionAsmVariables.values());
        return i;
    }

    private int parseFunctionParameters(String s, Builder b, int j) {
        j++; // skip '('
        int i;
        int varCounter = 0;
        while (s.charAt(j) != ')') {
            if (s.charAt(j) == ',') {
                j++;
                continue;
            }
            while (SPACERS.contains(s.charAt(j))) j++;
            i = j;
            while (!SPACERS.contains(s.charAt(j))) j++;
            String typeName = s.substring(i, j);

            while (SPACERS.contains(s.charAt(j))) j++;
            i = j;
            while (!SPACERS.contains(s.charAt(j)) && s.charAt(j) != ')' && s.charAt(j) != ',') j++;
            String varName = s.substring(i, j);

            b.withParameter(new Parameter(Type.getTYPES_MAP().get(typeName), varName, (-4) * (++varCounter) - 8));
        }
        return j;
    }

    private int parseFunctionName(String s, Builder b, int j) {
        int i;
        while (SPACERS.contains(s.charAt(j))) j++;
        i = j;
        while (s.charAt(j) != '(') j++;
        String functionName = s.substring(i, j);
        b.withName(functionName);
        return j;
    }

    private int parseFunctionReturnType(String s, int i, Builder b) {
        int j = i;
        while (!SPACERS.contains(s.charAt(j))) j++;
        String returnTypeName = s.substring(i, j);
        b.withReturnType(Type.getTYPES_MAP().get(returnTypeName));
        return j;
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
