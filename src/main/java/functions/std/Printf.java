package functions.std;

import functions.Function;
import functions.FunctionUtils;
import functions.Parameter;
import lombok.AllArgsConstructor;
import types.Type;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static functions.FunctionUtils.lab_;

public final class Printf extends Function {
    private static final String CODE =
            "push ebp               ; push caller bp\n" +
            "mov ebp, esp           ; current bp = current sp\n" +
            "mov eax, 4             ; sys_write\n" +
            "mov ebx, 1             ; stdout (file descriptor)\n" +
            "mov ecx, [ebp + 8]     ; message to write\n" +
            "mov edx, [ebp + 12]    ; message length\n" +
            "int 80h                ; call kernel\n" +
            "pop ebp                ; restore caller bp\n" +
            "ret                    ; return from procedure";
    public static final List<String> ASM_LINES = Arrays.stream(CODE.split("\n")).collect(Collectors.toList());

    public static final Printf INSTANCE = new Printf("printf_length",
            List.of(new Parameter(Type.getTYPES_MAP().get("string"), "s", -12)), ASM_LINES);

    private Printf(String name, List<Parameter> parameters, List<String> asmCode) {
        super(name, parameters, asmCode);
    }

    @Override
    public void parse(String line, Map<String, Object> constLabelToVal, List<String> mainOps) {
        List<String> args = FunctionUtils.getTrimmedArguments(line.substring(name.length() + 1, line.length() - 2));
        String textLabel = lab_();
        FinalStringWithLength finalStringWithL = buildString(args.get(0));
        constLabelToVal.put(textLabel, finalStringWithL.s); // the string itself
        String lengthLabel = lab_();
        constLabelToVal.put(lengthLabel, finalStringWithL.length); // string length

        mainOps.add("push dword [" + lengthLabel + "]");
        mainOps.add("push " + textLabel);
        mainOps.add("call printf_length");
    }

    /**
     * Treat '\n'
     */
    private FinalStringWithLength buildString(String template) {
        template = template.substring(1, template.length() - 1);
        StringBuilder sb = new StringBuilder();
        boolean insideCitation = false;
        int length = 0;
        for (int i = 0; i < template.length(); i++) {
            if (template.charAt(i) == '\\' && template.charAt(i + 1) == 'n') { // meet \n
                if (!insideCitation) {
                    sb.append("10,");
                } else {
                    sb.append("\",10,");
                    insideCitation = false;
                }
                i++;
            } else {
                if (insideCitation) {
                    sb.append(template.charAt(i));
                } else {
                    sb.append("\"").append(template.charAt(i));
                    insideCitation = true;
                }
            }
            length++;
        }
        String s = sb.toString();
        s = s.endsWith(",") ? s.substring(0, s.length() - 1) : s;
        return new FinalStringWithLength(s, length);
    }

    @AllArgsConstructor
    private static class FinalStringWithLength {
        private final String s;
        private final int length;
    }
}
