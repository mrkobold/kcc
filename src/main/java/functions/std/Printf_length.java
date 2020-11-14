package functions.std;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class Printf_length {
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
}
