package arithmetic;

import arithmetic.Node.Op;
import arithmetic.Node.IntegerNode;
import arithmetic.Node.OperatorNode;
import arithmetic.Node.SymbolNode;

import java.util.LinkedList;

public final class ArithmeticParser {
    static final int PP = 5;

    public static Node<?> parseTree(String expression) {
        String e = expression.replaceAll(" ", "");
        int i = 0;
        int depth = 0;
        LinkedList<Node<?>> stack = new LinkedList<>();

        while (i < e.length()) {
            if (e.charAt(i) == '(') { // '('
                depth++;
                i++;
                continue;
            }

            if (e.charAt(i) == ')') { // ')'
                // close parenthesis tree
                Node<?> linkedNode = stack.removeLast();
                while (!stack.isEmpty() &&
                        ((OperatorNode) stack.peekLast()).getParenthesisDepth() == depth) {
                    stack.peekLast().right = linkedNode;
                    linkedNode = stack.removeLast();
                }
                stack.addLast(linkedNode);
                depth--;
                i++;
                continue;
            }

            if (isPartOfSymbol(e, i)) { // operand
                // read in operand
                i = getOperand(e, i, stack);
                continue;
            }

            // character is operator
            OperatorNode operatorNode = new OperatorNode(Op.fromChar(e.charAt(i)), depth);

            int currentOperatorPrio = operatorNode.getPrio();
            int prevOperatorPrio;
            if (stack.size() < 2 || // getting stronger
                    currentOperatorPrio > (prevOperatorPrio = ((OperatorNode) stack.get(stack.size() - 2)).getPrio())) {
                operatorNode.left = stack.removeLast();
                stack.addLast(operatorNode);
            } else if (currentOperatorPrio == prevOperatorPrio) { // same level chain
                stack.get(stack.size() - 2).right = stack.removeLast();
                operatorNode.left = stack.removeLast();
                stack.addLast(operatorNode);
            } else { // getting weaker
                stack.get(stack.size() - 2).right = stack.removeLast();
                stack.get(stack.size() - 2).right = stack.peekLast();
                stack.removeLast();
                operatorNode.left = stack.removeLast();
                stack.addLast(operatorNode);
            }
            i++;
        }

        for (int j = 0; j < stack.size() - 1; j++) {
            stack.get(j).right = stack.get(j + 1);
        }
        return stack.getFirst();
    }

    private static int getOperand(String e, int i, LinkedList<Node<?>> stack) {
        int j = i;
        boolean onlyNumber = true;
        while (j < e.length() && isPartOfSymbol(e, j)) {
            if (Character.isAlphabetic(e.charAt(j)) || e.charAt(j) == '_') {
                onlyNumber = false;
            }
            j++;
        }
        String operandString = e.substring(i, j);
        i = j;

        if (onlyNumber) {
            int operand = Integer.parseInt(operandString);
            stack.addLast(new IntegerNode(operand));
        } else {
            stack.addLast(new SymbolNode(operandString));
        }
        return i;
    }

    private static boolean isPartOfSymbol(String e, int i) {
        char c = e.charAt(i);
        return Character.isDigit(c) ||
                Character.isAlphabetic(c) ||
                '_' == c;
    }
}
