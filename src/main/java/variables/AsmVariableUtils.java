package variables;

import java.util.Map;

public class AsmVariableUtils {
    public static void handleDeclarationExpression(String declaration,
                                                   Map<String, AsmVariable> asmVariables,
                                                   String enclosingFunctionName) {
        String[] parts = declaration.split(" ");
        AsmVariable var = new AsmVariable(enclosingFunctionName + "_" + parts[1]);
        asmVariables.put(parts[1], var);
    }
}
