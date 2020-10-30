Kobold C Compiler

NASM assemble:
nasm -f elf file.asm

Linker:
ld -m elf_i386 -s -o file file.o

Function: always has parameter list in the parantheses
foo(Type1 param1, Type2 param2, ...)