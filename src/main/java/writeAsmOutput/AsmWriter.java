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
        fw.write("section .text\nglobal _start:\n_start:\n\t");
        writeFunction(fw, functions.remove("main"));

        for (Function f : functions.values()) {
            writeFunction(fw, f, f.getName());
        }
    }

    private static void writeFunction(FileWriter fw, Function f) throws IOException {
        writeFunction(fw, f, "_start");
    }

    private static void writeFunction(FileWriter fw, Function f, String label) throws IOException {
        fw.append(label).append(":\n");
        for (String line : f.getAsmCode()) {
            if (line.trim().isEmpty()) continue;
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
