package computeExpressionTryall;

import computeExpressionTryall.ComputeExpression.Node;
import computeExpressionTryall.ComputeExpression.OperandNode;
import computeExpressionTryall.ComputeExpression.OperatorNode;

import java.util.LinkedList;

public class ComputeExpression1 {
    //                                        1       5
//    private static final String E_ORIGINAL = "1+2*3/6*4-4*(1+1)*2*(2+3*(3+4))+1";
    private static final String E_ORIGINAL = "1+2*(  1+2* (1+2*3) *2+4*(5+6* (5+3) -3)  )";

    public static void main(String[] args) {
        Node<?> root = parseTree();

        System.out.println(ComputeExpression.compute(root));
    }

    private static Node<?> parseTree() {
        String e = E_ORIGINAL.replaceAll(" ", "");
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
                Node linkedNode = stack.removeLast();
                while (((OperatorNode) stack.peekLast()).getParenthesisDepth() == depth) {
                    stack.peekLast().right = linkedNode;
                    linkedNode = stack.removeLast();
                }
                stack.addLast(linkedNode);
                depth--;
                i++;
                continue;
            }

            if (Character.isDigit(e.charAt(i))) { // operand
                // read in operand
                int j = i;
                while (j < e.length() && Character.isDigit(e.charAt(j))) j++;
                int operand = Integer.parseInt(e.substring(i, j));
                i = j;
                stack.addLast(new OperandNode(operand));
                continue;
            }

            // character is operator
            OperatorNode operatorNode = new OperatorNode(ComputeExpression.Op.fromChar(e.charAt(i)), depth);

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
}
