import lombok.AllArgsConstructor;

import java.util.LinkedList;

public class ComputeExpression {

    private static final String e = "4+2*3*6/9*4-4*(4+1)";

    public static void main(String[] args) {

        LinkedList<Node> openTrees = new LinkedList<>();

        for (int i = 0; i < e.length(); i++) {
            Integer operand = Character.getNumericValue(e.charAt(i));
            Node operandNode = new Node(operand, null, null);
            i++;
            char operatorSymbol = e.charAt(i);
            Node operatorNode = new Node(Op.fromChar(operatorSymbol), null, null);

            if (openTrees.isEmpty()) { // starting out
                operatorNode.left = operandNode;
                openTrees.addLast(operatorNode);
                continue;
            }
            Node lastOpenTree = openTrees.peekLast();
            int prioLastOpen, prioCurrent;
            if ((prioLastOpen = ((Op) lastOpenTree.value).prio) <
                    (prioCurrent = ((Op)operatorNode.value).prio)) { // current op is stronger than last open tree
                operatorNode.left = operandNode;
                openTrees.addLast(operatorNode);
            } else if (prioLastOpen == prioCurrent) { // current op is same strength as last open tree
                Node lastOpen = openTrees.removeLast();
                lastOpen.right = operandNode;
                operatorNode.left = lastOpen;
                openTrees.addLast(operatorNode);
            } else { // current op is weaker than last open tree
                Node lastOpen = openTrees.removeLast();
                lastOpen.right = operandNode;
                Node prevLastOpen = openTrees.removeLast();
                prevLastOpen.right = lastOpen;
                operatorNode.left = prevLastOpen;
                openTrees.addLast(operatorNode);
            }
        }
    }

    @AllArgsConstructor
    private static class Node {
        private Object value;
        private Node left, right;
    }

    private enum Op {
        ADD(0),
        SUB(0),
        MUL(1),
        DIV(1);

        private final int prio;
        Op(int prio) {
            this.prio = prio;
        }

        static Op fromChar(char c) {
            switch (c) {
                case '+':
                    return ADD;
                case '-':
                    return SUB;
                case '*':
                    return MUL;
                case '/':
                    return DIV;
                default:
                    return null;
            }
        }
    }
}
