Kobold C Compiler

NASM assemble:
nasm -f elf file.asm

Linker:
ld -m elf_i386 -s -o file file.o

Function: always has parameter list in the parantheses
foo(Type1 param1, Type2 param2, ...) { // code }
When compiler finds a function definition F, it stores it in Function.FUNCTIONS.
When the compiler finds an invocation of F, it'll search in Function.FUNCTIONS for F to parse the parameters.

Obvious: somewhere entities for functions have to be collected
Each entity can parse the arguments in (), these will be used when calling it
Each entity can write out itself to an assembly function / routine, starting with how popping args from stack


Function gets translated to asm code popping stuff from stack
Function header used to parse arguments and place on stack