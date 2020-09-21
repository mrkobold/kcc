import java.io.*;

public class KCC {

    private final String sourceFilePath;
    private final String outputFilePath;

    KCC(String sourceFileName) {
        this.sourceFilePath = "c_source/" + sourceFileName;
        this.outputFilePath = "compiled/" + sourceFileName.split("\\.")[0] + ".asm";
    }

    void compile() throws IOException {
        BufferedReader rd = new BufferedReader(new FileReader(sourceFilePath));
        System.out.println(rd.readLine());

        FileWriter wr = new FileWriter(outputFilePath);
        wr.write("; kobold compiler\n");
        wr.write("section .text\n\tglobal _start:\n");
        wr.write("_start:\n\tmov eax, 1\n\tmov ebx, 0\n\tint 80h\n");
        wr.close();

    }
}
