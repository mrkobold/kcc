package computeExpressionTryall;

import lombok.Getter;

import java.util.LinkedList;

public class ComputeExpression {
    private static final int PP = 5;
    private static final String e = "4+2*3*6/9*4-4*(4+2*2)+5";

    public static void main(String[] args) {

        Node root = parseIntoTree();

        int result = compute(root);
    }

    private static Node parseIntoTree() {
        LinkedList<OperatorNode> openTrees = new LinkedList<>();
        int parenthesisDepth = 0;

        for (int i = 0; i < e.length(); i++) {
            char ch = e.charAt(i);
            if (ch == '(') {
                parenthesisDepth++;
                continue;
            }
            if (ch == ')') { // closed ')' after closed ')'
                // close all trees at level inside the "after" ')'
                OperatorNode operatorNode;
                while ((operatorNode = openTrees.peekLast()).getParenthesisDepth() == parenthesisDepth) {
                    openTrees.removeLast();
                    openTrees.peekLast().right = operatorNode;
                }
                continue;
            }

            OperandNode operandNode = new OperandNode(Character.getNumericValue(ch));
            char nextChar = e.charAt(++i);

            if (nextChar == ')') { // closed ')' after an operand e.g.: ...+6)...
                Node<?> currentlyLinkedSubnode = operandNode;
                while (openTrees.peekLast().getParenthesisDepth() == parenthesisDepth) {
                    openTrees.peekLast().right = currentlyLinkedSubnode;
                    currentlyLinkedSubnode = openTrees.removeLast();
                }
                parenthesisDepth--;
                if (openTrees.peekLast().getParenthesisDepth() == parenthesisDepth) {
                    openTrees.peekLast().right = currentlyLinkedSubnode;
                }
                continue;
            }


            OperatorNode operatorNode = new OperatorNode(Op.fromChar(nextChar), parenthesisDepth);

            if (openTrees.isEmpty()) { // starting out
                operatorNode.left = operandNode;
                openTrees.addLast(operatorNode);
                continue;
            }

            OperatorNode lastOpenTree = openTrees.peekLast();
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

    public static int compute(Node root) {
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

    public static class Node<T> {
        protected final T value;
        protected Node<?> left, right;

        Node(T value) {
            this.value = value;
        }
    }

    public static class OperandNode extends Node<Integer> {
        public OperandNode(Integer value) {
            super(value);
        }
    }

    public static class OperatorNode extends Node<Op> {
        @Getter
        private final int parenthesisDepth;

        public OperatorNode(Op value, int parenthesisDepth) {
            super(value);
            this.parenthesisDepth = parenthesisDepth;
        }

        public int getPrio() {
            return parenthesisDepth * PP + value.prio;
        }

        public Op getOp() {
            return value;
        }
    }

    public enum Op {
        ADD(0),
        SUB(0),
        MUL(1),
        DIV(1);

        public final int prio;

        Op(int prio) {
            this.prio = prio;
        }

        public static Op fromChar(char c) {
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
