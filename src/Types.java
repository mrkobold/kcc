import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public enum Types {
    INT("int") {
        @Override
        void parse(String line, List<String> constants) {

        }
    },
    VOID("void") {
        @Override
        void parse(String line, List<String> constants) {

        }
    },
    CHAR("char") {
        @Override
        void parse(String line, List<String> constants) {

        }
    };

    public static Collection<String> names = Arrays.stream(values()).map(Types::getName).collect(Collectors.toList());

    private final String name;

    Types(String name) {
        this.name = name;
    }

    abstract void parse(String line, List<String> constants);

    String getName() {
        return name;
    }
}
