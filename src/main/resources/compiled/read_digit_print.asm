section .data
    q: db "Input digit: "
    q_len: equ $ - q
    test: db 10

section .bss
    input: resb 1

section .text
    global _start:

_start:
    ; prompt user
    mov eax, 4
    mov ebx, 1
    mov ecx, q
    mov edx, q_len
    int 80h

    ; read user input
    mov eax, 3
    mov ebx, 0
    mov ecx, test
    mov edx, 1
    int 80h

    ; show user input
    mov eax, 4
    mov ebx, 1
    mov ecx, test
    mov edx, 1
    int 80h

    mov eax, 1
    mov ebx, 0
    int 80h