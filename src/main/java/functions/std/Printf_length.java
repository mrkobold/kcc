package functions.std;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class Printf_length {
    private static final String CODE =
            "mov eax, 4 ; sys_write\n" +
            "mov ebx, 1 ; stdout (file descriptor)\n" +
            "pop ecx    ; message to write\n" +
            "pop edx    ; message length\n" +
            "int 80h    ; call kernel\n" +
            "ret        ; return from procedure";

    public static final List<String> ASM_LINES = Arrays.stream(CODE.split("\n")).collect(Collectors.toList());
}
