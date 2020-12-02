package variables;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AsmVariable {
    private final String name;

    @Override
    public String toString() {
        return name;
    }
}
