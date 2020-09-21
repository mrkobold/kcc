import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Mr.Kobold");

        KCC compiler = new KCC("1.c");

        compiler.compile();
    }
}
