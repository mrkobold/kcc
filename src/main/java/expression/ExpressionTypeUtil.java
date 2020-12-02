package expression;

import functions.Function;
import types.Type;

public final class ExpressionTypeUtil {

    public static boolean isDeclarationExpression(String expr) {
        return Type.TYPES.contains(expr.split(" ")[0]);
    }

    public static boolean isAssignmentExpression(String expr) {
        return expr.contains("=");
    }

    public static boolean isReturnExpression(String expr){
        return expr.startsWith("return");
    }

    public static boolean isFunctionCallExpression(String expr) {
        return Function.FUNCTIONS.contains(expr.split("\\(")[0]);
    }
}
