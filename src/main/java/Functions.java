import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public enum Functions {

    PRINTF("printf") {
        @Override
        void parse(String line, List<String> constants, List<Operation> operations) {
            String content = line.substring(getName().length() + 2, line.length() - 3);
            constants.add(content);
            operations.add(new PrintOperation(constants.size() - 1));
        }
    },
    SCANF("scanf") {
        @Override
        void parse(String line, List<String> constants, List<Operation> operations) {

        }
    }
    ;

    public static Collection<String> names = Arrays.stream(values()).map(Functions::getName).collect(Collectors.toList());

    private final String name;

    Functions(String name) {
        this.name = name;
    }

    abstract void parse(String line, List<String> constants, List<Operation> operations);

    String getName() {
        return name;
    }
}
