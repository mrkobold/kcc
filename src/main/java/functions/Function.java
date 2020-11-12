package functions;

import functions.std.Printf_length;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

@AllArgsConstructor
@Getter
public abstract class Function {
    @Getter
    public static final Map<String, Function> FUNCTION_MAP = new HashMap<>();

    private static int labelId = 0;

    static {
        Function printf_length = new Function("printf_length", List.of(new Parameter("String"), new Parameter("int")), Printf_length.ASM_LINES) {
            @Override
            public void parse(String line, Map<String, String> constLabelToVal, List<String> mainOps) {
                String[] args = line.substring(name.length() + 1, line.length() - 2).split(",");
                String textLabel = Function.nextLabel();
                constLabelToVal.put(textLabel, args[0]); // the string itself
                String lengthLabel = Function.nextLabel();
                constLabelToVal.put(lengthLabel, args[1]); // string length

                mainOps.add("push " + lengthLabel);
                mainOps.add("push " + textLabel);
                mainOps.add("call printf_length");
            }
        };
        FUNCTION_MAP.put(printf_length.name, printf_length);
    }

    public final static Set<String> FUNCTIONS = FUNCTION_MAP.keySet();

    final String name;
    final List<Parameter> parameters;
    final List<String> asmCode;

    public abstract void parse(String line, Map<String, String> constLabelToVal, List<String> mainOps); // TODO add operations also

    private static String nextLabel() {
        return "lab" + (labelId++);
    }
}
