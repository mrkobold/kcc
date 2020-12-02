package writeAsmOutput;

import functions.Function;
import variables.AsmVariable;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public final class AsmWriter {

    public static void writeAsmOutput(String file,
                                      Set<AsmVariable> asmVars,
                                      Map<String, Function> functions) throws IOException {

        FileWriter fw = new FileWriter(file);
        fw.append("; kobold c compiler output\n\n");

        writeDataSection(fw);
        writeVarsSection(fw, asmVars);
        writeTextSection(fw, functions);

        fw.close();
    }

    private static void writeTextSection(FileWriter fw, Map<String, Function> functions) throws IOException {
        fw.write("section .text\nglobal _start:\n");
        writeFunction(fw, functions.remove("main"));

        for (Function f : functions.values()) {
            writeFunction(fw, f, f.getName());
        }
    }

    private static void writeFunction(FileWriter fw, Function f) throws IOException {
        writeFunction(fw, f, "_start");
        // shutdown
        fw.append("\t")
                .append("; over and out")
                .append("\n\t")
                .append("mov eax, 1     ; system exit")
                .append("\n\t")
                .append("mov ebx, 0     ; exit code 0")
                .append("\n\t")
                .append("int 80h        ; call kernel")
                .append("\n\n");
    }

    private static void writeFunction(FileWriter fw, Function f, String label) throws IOException {
        fw.append(label).append(":\n");
        fw.append("\tpush ebp\n").append("\tmov ebp, esp\n\n");
        for (String line : f.getAsmCode()) {
            if (line.trim().isEmpty()) continue;
            if ("ret".equals(line)) {
                fw.append("\tpop ebp\n");
            }
            fw.append("\t").append(line).append("\n");
        }
        fw.append("\n");
    }

    private static void writeVarsSection(FileWriter fw, Set<AsmVariable> asmVars) throws IOException {
        fw.write("section .bss\n");

        for (AsmVariable var : asmVars) {
            fw.append("\t").append(var.getName()).append(": resb 4\n");
        }

        fw.append("\n");
    }

    private static void writeDataSection(FileWriter fw) throws IOException {
        fw.append("section .data\n");

        fw.append("\n");
    }
}
