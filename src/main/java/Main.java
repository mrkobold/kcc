import java.io.IOException;

public class Main {

    private static final String TRIVIAL = "1.c";
    private static final String PRINTF = "printf.c";

    public static void main(String[] args) throws IOException {
        System.out.println("Mr.Kobold");
//        new KCC(DECLARE_ONE_CHAR_PRINT).compile();
        new KCC(PRINTF).compile();
    }
}
