package functions.std;

import functions.Function;
import functions.Parameter;
import types.Type;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class PrintInt extends Function {
    private static final String CODE =
            "mov eax,[ebp + 8]\n" +
                    "push dword eax\n" +
                    "lop:\n" +
                    "pop eax\n" +
                    "cmp eax,0\n" +
                    "je vege\n" +
                    "mov edx,0\n" +
                    "mov ecx,10\n" +
                    "div ecx\n" +
                    "add edx,48\n" +
                    "mov [char_print_int],edx\n" +
                    "push eax\n" +
                    "mov eax,4\n" +
                    "mov ebx,1\n" +
                    "mov ecx, char_print_int\n" +
                    "mov edx,1\n" +
                    "int 80h\n" +
                    "jmp lop\n" +
                    "vege:\n" +
                    "ret";
//            "mov eax, 4             ; sys_write\n" +
//                    "mov ebx, 1             ; stdout (file descriptor)\n" +
//                    "mov ecx, [ebp + 8]     ; message to write\n" +
//                    "mov edx, [ebp + 12]    ; message length\n" +
//                    "int 80h                ; call kernel\n" +
//                    "ret                    ; return from procedure";
    public static final List<String> ASM_LINES = Arrays.stream(CODE.split("\n")).collect(Collectors.toList());

    public static final PrintInt INSTANCE = new PrintInt(
            "printInt",
            List.of(new Parameter(Type.getTYPES_MAP().get("int"), "num", -8)),
            ASM_LINES);

    public PrintInt(String name, List<Parameter> parameters, List<String> asmCode) {
        super(name, parameters, asmCode);
    }
}
