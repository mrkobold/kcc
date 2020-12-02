package expression;

import arithmetic.ArithmeticParser;
import arithmetic.ArithmeticToAsm;
import functions.Function;
import variables.AsmVariable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static expression.ExpressionTypeUtil.isFunctionCallExpression;

public final class AssignmentExpressionUtils {
    public static void handleValueAssignment(Function.Builder b,
                                             StringBuilder functionAsmCode,
                                             Map<String, AsmVariable> currentFunctionAsmVariables,
                                             String currentExpression) {
        String lValueName = currentExpression.split("=")[0].trim();
        AsmVariable lValue = currentFunctionAsmVariables.get(lValueName);
        String rValue = currentExpression.split("=")[1].trim();

        if (!isFunctionCallExpression(rValue)) {
            String lValueAsm = ArithmeticToAsm.toAsm(ArithmeticParser.parseTree(rValue), b.getParameters()).toString();
            functionAsmCode.append(lValueAsm);
        } else {
            assignCallResult(b, functionAsmCode, currentFunctionAsmVariables, rValue);
        }
        functionAsmCode.append("\n").append("mov [").append(lValue.getName()).append("],eax\n");
    }

    private static void assignCallResult(Function.Builder b, StringBuilder functionAsmCode, Map<String, AsmVariable> currentFunctionAsmVariables,
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

    private static Optional<Integer> tryParsingInteger(String s) {
        try {
            return Optional.of(Integer.parseInt(s));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

}
