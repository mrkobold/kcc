public enum Types {
    INT("int"),
    VOID("void")
    ;


    private final String name;
    Types(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }
}
