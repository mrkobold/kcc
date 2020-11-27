package types;

public class IntType extends Type {

    public static final IntType INSTANCE = new IntType();

    private IntType() {
        super("int");
    }
}
