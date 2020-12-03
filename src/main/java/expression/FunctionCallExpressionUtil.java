package expression;

import functions.Function;
import variables.AsmVariable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static expression.AssignmentExpressionUtils.tryParsingInteger;

public final class FunctionCallExpressionUtil {
    public static void handleFunctionCall(StringBuilder functionAsmCode,
                                          Map<String, AsmVariable> currentFunctionAsmVariables,
                                          Function.Builder b,
                                          String calledFunctionWithArgs) {
        // take symbols and push on stack: immediate, var, arg
        String[] varsDirty = calledFunctionWithArgs
                .split("\\(")[1] // "a, 4, b)"
                .replace(")", "") // a, 4, b
                .split(","); // "a", " 4", " b"
        List<String> varStrings = Arrays.stream(varsDirty)
                .map(String::trim) // "a", "4", "b"
                .collect(Collectors.toList());
        for (int j = varStrings.size() - 1; j >= 0; j--) {
            String varString = varStrings.get(j);
            functionAsmCode.append("push dword ");
            Optional<Integer> optionalInteger = tryParsingInteger(varString);
            if (optionalInteger.isPresent()) {
                functionAsmCode.append(optionalInteger).append("\n");
            } else {
                functionAsmCode.append("[");
                if (currentFunctionAsmVariables.get(varString) != null) { // local var
                    functionAsmCode.append(currentFunctionAsmVariables.get(varString)).append("]\n");
                } else { // argument
                    String offsetString = b.getParameters().stream()
                            .filter(p -> p.getName().equals(varString))
                            .findFirst()
                            .map(p -> -1 * p.getStackOffset())
                            .map(i -> i + "")
                            .orElse("");
                    functionAsmCode.append("ebp+").append(offsetString).append("]\n");
                }
            }
        }
        functionAsmCode.append("call ").append(calledFunctionWithArgs.split("\\(")[0]).append("\n");
    }
}
