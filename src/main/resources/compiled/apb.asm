; kobold c compiler output

section .data
lab_0: db 48

section .bss
	main_a: resb 4
	main_c: resb 4
	main_b: resb 4

section .text
global _start:
_start:
	mov eax,4
	mov [main_a],eax
	mov eax,3
	mov [main_b],eax
	push dword [main_a]
	push dword [main_b]
	call sum
	mov [main_c],eax


;   FOR PRINTING
    add eax, 48
    mov [main_c], eax
	mov eax, 4              ; sys_write
    mov ebx, 1              ; stdout (file descriptor)
    mov ecx, main_c       ; message to write
    mov edx, 8              ; message length
    int 80h
;   UNTIL HERE

	; over and out
	mov eax, 1     ; system exit
	mov ebx, 0     ; exit code 0
	int 80h        ; call kernel

sum:
    push ebp               ; push caller bp
	mov ebp, esp           ; current bp = current sp

	mov eax,[ebp+8]
	push eax
	mov eax,[ebp+12]
	mov ebx,eax
	pop eax
	add eax,ebx

	pop ebp
	ret

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

