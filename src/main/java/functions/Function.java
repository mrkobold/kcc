package functions;

import functions.std.Printf;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

@AllArgsConstructor
@Getter
public abstract class Function {
    @Getter
    public static final Map<String, Function> FUNCTION_MAP = new HashMap<>();
    static {
        FUNCTION_MAP.put(Printf.INSTANCE.getName(), Printf.INSTANCE);
    }

    public final static Set<String> FUNCTIONS = FUNCTION_MAP.keySet();

    protected final String name;
    protected final List<Parameter> parameters;
    protected final List<String> asmCode;

    public abstract void parse(String line, Map<String, Object> constLabelToVal, List<String> mainOps); // TODO add operations also
}
