import java.io.IOException;

public class Main {

    private static final String FIRST = "1.c";
    private static final String PRINTF = "printf.c";

    public static void main(String[] args) throws IOException {
        System.out.println("Mr.Kobold");
        new KCC(PRINTF).compile();
    }
}
