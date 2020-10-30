package functions;

import java.util.List;

public abstract class Function {

    final String name;
    final List<Parameter> parameters;

    Function(String name, List<Parameter> parameters) {
        this.name = name;
        this.parameters = parameters;
    }

}
