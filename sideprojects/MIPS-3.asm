# file:  MIPS-3.asm
# author: Jacob Adamson, Nick Cameron, CP Majgaard, Larry Partridge
# date: Mar. 14, 2017
#
# This program reads in integers from the
# user until -999 is read. It will then
# print the read integers in reverse order.

    .data
    	space:	.asciiz " "
    .text       		# the code segment of the program
    .globl main 		# the starting point of the program

main:
    nop
    li $t0, -999   		# This block pushes -999 on the stack
    addi $sp, $sp, -4
    sw $t0, 0($sp)

loop:
    li $v0 5			# This block checks if the number entered is not -999
    syscall
    beq $v0, -999, endloop 	# exit the loop if the int entered (in $v0) is -999
    addi $sp, $sp, -4		# Push the currently read number to the stack
    sw $v0, 0($sp)
    b loop

endloop:
    lw $a0, 0($sp)		# these two lines pop the stack
    addi $sp, $sp, 4
    beq $a0, -999, exit
    li $v0, 1			# load print int syscall
    syscall     		# output the value in $a0
    la $a0, space
    li $v0, 4			# print a space
    syscall
    b endloop
exit:
    li $v0, 10
    syscall     # exit