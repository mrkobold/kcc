package functions;

import functions.std.Printf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import lombok.experimental.Wither;
import types.Type;

import java.util.*;

@AllArgsConstructor
@Getter
public abstract class Function {
    @Getter
    public static final Map<String, Function> FUNCTION_MAP = new HashMap<>();
    static {
        FUNCTION_MAP.put(Printf.INSTANCE.getName(), Printf.INSTANCE);
    }

    public static final Set<String> FUNCTIONS = FUNCTION_MAP.keySet();

    protected final String name;
    protected final List<Parameter> parameters;
    protected final List<String> asmCode;

    public abstract void parse(String line, Map<String, Object> constLabelToVal, List<String> mainOps); // TODO add operations also

    public static Builder getBuilder() {
        return new Builder();
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
    }
}
