; kobold compiler
section .data
	_1:	db "Mr.Kobold!", 10
	_2:	equ $-_1
	_3:	db "Kingucim", 10
	_4:	equ $-_3
	_5:	db "1234_sdg++45", 10
	_6:	equ $-_5

section .text
	global _start:
_start:
	mov eax, 4
	mov ebx, 1
	mov ecx, _1
	mov edx, _2
	int 80h
	mov eax, 4
	mov ebx, 1
	mov ecx, _3
	mov edx, _4
	int 80h
	mov eax, 4
	mov ebx, 1
	mov ecx, _5
	mov edx, _6
	int 80h

	mov eax, 1
	mov ebx, 0
	int 80h