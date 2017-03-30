# file:  MIPS-0.asm
# author: Jacob Adamson
# date: Mar. 16, 2017
# Program reads in two numbers and multiplies them
# This program has a multiply function in it
.include "macros.asm"
.data

.text       		# the code segment of the program
.globl main 		# the starting point of the program		# the starting point of the program
main:
    li $t0, 0   # initialize the sum to 0

    li $v0 5
    syscall     # read next int into $v0
    bltz $v0 endloop # go to the end of program if lt 0
    move $a1, $v0 #puts value in param register

    li $v0 5
    syscall     # read next int into $v0
    bltz $v0 endloop # go to the end of program if lt 0
    move $a2, $v0 #puts the value in param register

   preamble
   call multiply #calls the function
   postable

   #print returned value
   move $a0, $v0 # move result to a0
   li $v0, 1
   syscall     # output the value in $a0
   li $v0, 10
   syscall     # exit

multiply:
    prolog 0
    move $t1 $a1
    move $t2 $a2
    b loop
    loop:
        subi $t2, $t2, 1 # sub 1 from the counter
        bltz $t2 endloop # if counter lt 0, end
        add $t0, $t0, $t1 # add t1 to t0
        b loop

    endloop:
        mv $v0 $t0
        epiloge 0
        return



    #save multiplied thing to return
