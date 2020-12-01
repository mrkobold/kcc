package arithmetic;

import arithmetic.Node.OperatorNode;

public final class ArithmeticToAsm {
    public static String toAsm(Node<?> root) {
        StringBuilder stringBuilder = new StringBuilder();
        build(root, stringBuilder);
        return stringBuilder.toString();
    }

    private static void build(Node<?> root, StringBuilder sb) {
        if (root.left == null) {
            sb.append("mov eax,").append(root.value instanceof Node.IntegerNode ?
                    root.value : "[" + root.value + "]").append("\n");
            return;
        }
        build(root.left, sb);
        sb.append("push eax").append("\n");
        build(root.right, sb);
        sb
                .append("mov ebx,eax\n")
                .append("pop eax\n")
                .append(operation(root)).append(" eax,ebx\n");
    }

    private static String operation(Node<?> root) {
        OperatorNode node = (OperatorNode) root;
        return node.getOp().name().toLowerCase();
    }
}
