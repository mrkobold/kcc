package types;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
public abstract class Type {
    @Getter
    public static final Map<String, Type> TYPES_MAP = new HashMap<>();
    static {
        TYPES_MAP.put(IntType.INSTANCE.typeName, IntType.INSTANCE);
    }

    public static final Set<String> TYPES = TYPES_MAP.keySet();

    protected final String typeName;

    @Override
    public String toString() {
        return typeName;
    }
}
