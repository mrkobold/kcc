; kobold compiler
section .data

	c0 : db "Mr.Kobold!", 10
	c0l: equ $-c0
	c1 : db "Bogarkam", 10
	c1l: equ $-c1
	c2 : db "Mrs.sgsdgsdg!", 10
	c2l: equ $-c2

section .text
global _start:
_start:
	mov eax, 4
	mov ebx, 1
	mov ecx, c0
	mov edx, c0l
	int 80h
	mov eax, 4
	mov ebx, 1
	mov ecx, c1
	mov edx, c1l
	int 80h
	mov eax, 4
	mov ebx, 1
	mov ecx, c2
	mov edx, c2l
	int 80h

	mov eax, 1
	mov ebx, 0
	int 80h