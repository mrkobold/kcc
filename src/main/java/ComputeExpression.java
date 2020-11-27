import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedList;

public class ComputeExpression {
    private static final int PP = 5;
    private static final String e = "4+2*3*6/9*4-4*(4+2*2)";

    public static void main(String[] args) {

        Node root = parseIntoTree();

        int result = compute(root);
    }

    private static int compute(Node root) {
        if (root instanceof OperandNode)
            return (int) root.value;

        OperatorNode operatorNode = (OperatorNode) root;
        if (operatorNode.value == Op.ADD)
            return compute(operatorNode.left) + compute(operatorNode.right);
        if (operatorNode.value == Op.SUB)
            return compute(operatorNode.left) - compute(operatorNode.right);
        if (operatorNode.value == Op.MUL)
            return compute(operatorNode.left) * compute(operatorNode.right);
        if (operatorNode.value == Op.DIV)
            return compute(operatorNode.left) / compute(operatorNode.right);
        return 0;
    }

    private static Node parseIntoTree() {
        LinkedList<OperatorNode> openTrees = new LinkedList<>();
        int parenthesisDepth = 0;

        for (int i = 0; i < e.length(); i++) {
            char ch = e.charAt(i);
            if (ch == '(') {
                parenthesisDepth++;
                continue;
            } else if (ch == ')') {
                parenthesisDepth--;
                continue;
            }

            Integer operand = Character.getNumericValue(ch);
            OperandNode operandNode = new OperandNode(operand);
            char operatorSymbol = e.charAt(++i);
            OperatorNode operatorNode = new OperatorNode(Op.fromChar(operatorSymbol), parenthesisDepth);

            if (openTrees.isEmpty()) { // starting out
                operatorNode.left = operandNode;
                openTrees.addLast(operatorNode);
                continue;
            }
            OperatorNode lastOpenTree = openTrees.peekLast();
            if (operatorSymbol == ')') {
                lastOpenTree.right = operandNode;
                Node<?> currentlyLinkedSubnode = operandNode;
                while (openTrees.peekLast().getParenthesisDepth() == parenthesisDepth) {
                    openTrees.peekLast().right = currentlyLinkedSubnode;
                    currentlyLinkedSubnode = openTrees.removeLast();
                }
                openTrees.peekLast().right = currentlyLinkedSubnode;
                parenthesisDepth--;
                continue;
            }
            int prioLastOpen = lastOpenTree.getPrio();
            int prioCurrent = operatorNode.getPrio();
            if (prioLastOpen < prioCurrent) { // current op is stronger than last open tree
                operatorNode.left = operandNode;
                openTrees.addLast(operatorNode);
            } else if (prioLastOpen == prioCurrent) { // current op is same strength as last open tree
                OperatorNode lastOpen = openTrees.removeLast();
                lastOpen.right = operandNode;
                operatorNode.left = lastOpen;
                openTrees.addLast(operatorNode);
            } else { // current op is weaker than last open tree
                OperatorNode lastOpen = openTrees.removeLast();
                lastOpen.right = operandNode;
                OperatorNode prevLastOpen = openTrees.removeLast();
                prevLastOpen.right = lastOpen;
                operatorNode.left = prevLastOpen;
                openTrees.addLast(operatorNode);
            }
        }

        openTrees.get(0).right = openTrees.removeLast();
        return openTrees.get(0);
    }

    private static class Node<T> {
        protected final T value;
        protected Node<?> left, right;

        Node(T value) {
            this.value = value;
        }
    }

    private static class OperandNode extends Node<Integer> {
        public OperandNode(Integer value) {
            super(value);
        }
    }

    private static class OperatorNode extends Node<Op> {
        @Getter
        private final int parenthesisDepth;
        private OperatorNode(Op value, int parenthesisDepth) {
            super(value);
            this.parenthesisDepth = parenthesisDepth;
        }

        private int getPrio() {
            return parenthesisDepth * PP + value.prio;
        }
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
