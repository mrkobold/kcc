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
        fw.append("; kobold c compiler output\n\n")

        writeDataSection(fw);
        writeVarsSection(fw,asmVars);
        writeTestSection(fw,functions);
    }

    private static void writeDataSection(FileWriter fw) throws IOException {
        fw.append(".data");
    }
}
