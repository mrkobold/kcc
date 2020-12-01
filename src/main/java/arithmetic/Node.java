package arithmetic;

import lombok.Getter;

import static arithmetic.ArithmeticParser.PP;

public class Node<T> {
    public final T value;
    public Node<?> left, right;

    Node(T value) {
        this.value = value;
    }

    public static class IntegerNode extends Node<Integer> {
        public IntegerNode(Integer value) {
            super(value);
        }
    }

    public static class SymbolNode extends Node<String> {
        public SymbolNode(String value) {
            super(value);
        }

        @Override
        public String toString() {
            return value;
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

        @Override
        public String toString() {
            return value.c + "(" + parenthesisDepth;
        }
    }

    public enum Op {
        ADD(0, '+'),
        SUB(0, '-'),
        MUL(1, '*'),
        DIV(1, '/');

        public final int prio;
        public final char c;

        Op(int prio, char c) {
            this.prio = prio;
            this.c = c;
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
