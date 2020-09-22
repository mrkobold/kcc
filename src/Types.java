import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public enum Types {
    INT("int"),
    VOID("void"),
    CHAR("char");

    public static Collection<String> names = Arrays.stream(values()).map(Types::getName).collect(Collectors.toList());

    private final String name;

    Types(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }
}
