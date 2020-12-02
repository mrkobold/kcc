package arithmetic;

import arithmetic.Node.OperatorNode;
import arithmetic.Node.SymbolNode;
import functions.Parameter;

import java.util.LinkedList;
import java.util.List;

public final class ArithmeticToAsm {
    public static StringBuilder toAsm(Node<?> root, List<Parameter> parameters) {
        StringBuilder stringBuilder = new StringBuilder();
        build(root, stringBuilder, parameters);
        return stringBuilder;
    }

    private static void build(Node<?> root, StringBuilder sb, List<Parameter> parameters) {
        if (root.left == null) {
            sb.append("mov eax,").append(getImmediateOrSymbolToAsm(root, parameters)).append("\n");
            return;
        }
        build(root.left, sb, parameters);
        sb.append("push eax").append("\n");
        build(root.right, sb, parameters);
        sb
                .append("mov ebx,eax\n")
                .append("pop eax\n")
                .append(operation(root)).append(" eax,ebx\n");
    }

    private static Object getImmediateOrSymbolToAsm(Node<?> root, List<Parameter> parameters) {
        if(root instanceof Node.IntegerNode) {
            return root.value;
        }
        SymbolNode node = (SymbolNode) root;
        StringBuilder result = new StringBuilder("[");
        for (Parameter p: parameters) {
            if (p.getName().equals(node.value)) {
                return result.append("ebp+").append(p.getStackOffset() * -1).append("]").toString();
            }
        }
        return "";
    }

    private static String operation(Node<?> root) {
        OperatorNode node = (OperatorNode) root;
        return node.getOp().name().toLowerCase();
    }
}
