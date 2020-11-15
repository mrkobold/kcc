; kobold compiler
section .data
	lab_0: db "Kingucim",10,"MrKobold",10
	lab_1: dd 18

section .text
global _start:
_start:
	push dword [lab_1]
	push lab_0
	call printf_length
	
	; over and out
	mov eax, 1     ; system exit
	mov ebx, 0     ; exit code 0
	int 80h        ; call kernel
	
; procedures section
printf_length:
	push ebp               ; push caller bp
	mov ebp, esp           ; current bp = current sp
	mov eax, 4             ; sys_write
	mov ebx, 1             ; stdout (file descriptor)
	mov ecx, [ebp + 8]     ; message to write
	mov edx, [ebp + 12]    ; message length
	int 80h                ; call kernel
	pop ebp                ; restore caller bp
	ret                    ; return from procedure
