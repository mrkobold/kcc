public class PrintOperation extends Operation {

    private final int whichConstant;

    PrintOperation(int whichConstant) {
        this.whichConstant = whichConstant;
    }

    public int getWhichConstant() {
        return whichConstant;
    }

    @Override
    public void writeYourself(StringBuilder textBuilder) {
        textBuilder.append("\n\tmov eax, 4");
        textBuilder.append("\n\tmov ebx, 1");
        textBuilder.append("\n\tmov ecx, c" + whichConstant);
        textBuilder.append("\n\tmov edx, c" + whichConstant + "l");
        textBuilder.append("\n\tint 80h");
    }
}
