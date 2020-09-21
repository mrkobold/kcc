import java.io.*;

public class KCC {

    private final String sourcePath;

    KCC(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    void compile() throws IOException {
        BufferedReader rd = new BufferedReader(new FileReader(sourcePath));
        System.out.println(rd.readLine());


    }
}
