package functions;

import functions.std.PrintInt;
import functions.std.Printf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import types.Type;

import java.util.*;

@AllArgsConstructor
@Getter
public class Function {
    @Getter
    public static final Map<String, Function> FUNCTION_MAP = new HashMap<>();

    static {
        FUNCTION_MAP.put(Printf.INSTANCE.getName(), Printf.INSTANCE);
        FUNCTION_MAP.put(PrintInt.INSTANCE.getName(), PrintInt.INSTANCE);
    }

    public static final Set<String> FUNCTIONS = new HashSet<>(FUNCTION_MAP.keySet());

    protected final String name;
    protected final List<Parameter> parameters;
    protected final List<String> asmCode;

    public static void addFunction(Builder b) {
        FUNCTION_MAP.put(b.name, b.build());
        FUNCTIONS.add(b.name);
    }

    public void parse(String line, Map<String, Object> constLabelToVal, List<String> mainOps) {

    }

    @Getter
    public static class Builder {
        private String name;
        private final List<Parameter> parameters = new ArrayList<>();
        private Type returnType;
        private final List<String> asmCode = new ArrayList<>();

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withReturnType(Type returnType) {
            this.returnType = returnType;
            return this;
        }

        public Builder withParameters(List<Parameter> parameters) {
            this.parameters.addAll(parameters);
            return this;
        }

        public Builder withParameter(Parameter parameter) {
            this.parameters.add(parameter);
            return this;
        }

        public Builder withAsmCode(String asmCode) {
            this.asmCode.addAll(Arrays.asList(asmCode.split("\n")));
            return this;
        }

        public Function build() {
            return new Function(this.name, this.parameters, this.asmCode);
        }
    }
}
