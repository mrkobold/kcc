package functions;

import lombok.Data;
import types.Type;

@Data
public class Parameter {
    private final Type type;
    private final String name;
    private final int stackOffset;

}
