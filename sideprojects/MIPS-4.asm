# file:  MIPS-4.asm
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

     #### Preamble for calling a subroutine ####
     #  Stores the following registers, in     #
     #  the presented order:                   #
     #  a0, a1, a2, a3,                        #
     #  t0, t1, t2, t3, t4, t5, t6, t7,        #
     #  v0, v1                                 #
     ####                                   ####

     addi $sp, $sp, -4
     sw $a0, 0($sp)
     addi $sp, $sp, -4
     sw $a1, 0($sp)
     addi $sp, $sp, -4
     sw $a2, 0($sp)
     addi $sp, $sp, -4
     sw $a3, 0($sp)
     addi $sp, $sp, -4
     sw $t0, 0($sp)
     addi $sp, $sp, -4
     sw $t1, 0($sp)
     addi $sp, $sp, -4
     sw $t2, 0($sp)
     addi $sp, $sp, -4
     sw $t3, 0($sp)
     addi $sp, $sp, -4
     sw $t4, 0($sp)
     addi $sp, $sp, -4
     sw $t5, 0($sp)
     addi $sp, $sp, -4
     sw $t6, 0($sp)
     addi $sp, $sp, -4
     sw $t7, 0($sp)
     addi $sp, $sp, -4
     sw $v0, 0($sp)
     addi $sp, $sp, -4
     sw $v1, 0($sp)

     jal whomrecurses	# call the whomrecurses subroutine and link the return address


     #### Postamble for calling a subroutine ####
     #  A place for everything and everything   #
     #  in its place.                           #
     ####                                    ####

     lw $v1, 0($sp)
     addi $sp, $sp, 4
     lw $v0, 0($sp)
     addi $sp, $sp, 4
     lw $t7, 0($sp)
     addi $sp, $sp, 4
     lw $t6, 0($sp)
     addi $sp, $sp, 4
     lw $t5, 0($sp)
     addi $sp, $sp, 4
     lw $t4, 0($sp)
     addi $sp, $sp, 4
     lw $t3, 0($sp)
     addi $sp, $sp, 4
     lw $t2, 0($sp)
     addi $sp, $sp, 4
     lw $t1, 0($sp)
     addi $sp, $sp, 4
     lw $t0, 0($sp)
     addi $sp, $sp, 4
     lw $a3, 0($sp)
     addi $sp, $sp, 4
     lw $a2, 0($sp)
     addi $sp, $sp, 4
     lw $a1, 0($sp)
     addi $sp, $sp, 4
     lw $a0, 0($sp)
     addi $sp, $sp, 4

     # Quit
     li $v0, 10
     syscall



whomrecurses:
    # Preamble for callee
    addi $sp, $sp, -4
    sw $fp, 0($sp)
    addi $sp, $sp, -4
    sw $ra, 0($sp)
    addi $sp, $sp, -4
    sw $s0, 0($sp)
    move $fp, $sp

    ###### PRE RECURSE ######

    li $v0 5			# This block checks if the number entered is not -999
    syscall
    beq $v0, -999, norecurse 	# exit the loop if the int entered (in $v0) is -999
    move $s0, $v0

    ######             ######

    # Preamble for caller
    addi $sp, $sp, -4
    sw $a0, 0($sp)
    addi $sp, $sp, -4
    sw $a1, 0($sp)
    addi $sp, $sp, -4
    sw $a2, 0($sp)
    addi $sp, $sp, -4
    sw $a3, 0($sp)
    addi $sp, $sp, -4
    sw $t0, 0($sp)
    addi $sp, $sp, -4
    sw $t1, 0($sp)
    addi $sp, $sp, -4
    sw $t2, 0($sp)
    addi $sp, $sp, -4
    sw $t3, 0($sp)
    addi $sp, $sp, -4
    sw $t4, 0($sp)
    addi $sp, $sp, -4
    sw $t5, 0($sp)
    addi $sp, $sp, -4
    sw $t6, 0($sp)
    addi $sp, $sp, -4
    sw $t7, 0($sp)
    addi $sp, $sp, -4
    sw $v0, 0($sp)
    addi $sp, $sp, -4
    sw $v1, 0($sp)

    #
    # recursion here
    #
    jal whomrecurses
    #
    # no more recursion thank you
    #

    # Postamble for caller
    lw $v1, 0($sp)
    addi $sp, $sp, 4
    lw $v0, 0($sp)
    addi $sp, $sp, 4
    lw $t7, 0($sp)
    addi $sp, $sp, 4
    lw $t6, 0($sp)
    addi $sp, $sp, 4
    lw $t5, 0($sp)
    addi $sp, $sp, 4
    lw $t4, 0($sp)
    addi $sp, $sp, 4
    lw $t3, 0($sp)
    addi $sp, $sp, 4
    lw $t2, 0($sp)
    addi $sp, $sp, 4
    lw $t1, 0($sp)
    addi $sp, $sp, 4
    lw $t0, 0($sp)
    addi $sp, $sp, 4
    lw $a3, 0($sp)
    addi $sp, $sp, 4
    lw $a2, 0($sp)
    addi $sp, $sp, 4
    lw $a1, 0($sp)
    addi $sp, $sp, 4
    lw $a0, 0($sp)
    addi $sp, $sp, 4

    # Postamble for callee
    lw $s0, 0($sp)
    addi $sp, $sp, 4
    lw $ra, 0($sp)
    addi $sp, $sp, 4
    lw $fp, 0($sp)
    addi $sp, $sp, -4



norecurse:

    ##### POST RECURSE #####

    move $a0, $s0		# get ready for printing
    li $v0, 1			# load print int syscall
    syscall     		# output the value in $a0
    la $a0, space
    li $v0, 4			# print a space
    syscall

    #####               #####

    # Postamble for callee
    lw $s0, 0($sp)
    addi $sp, $sp, 4
    lw $ra, 0($sp)
    addi $sp, $sp, 4
    lw $fp, 0($sp)
    addi $sp, $sp, -4

    # Return
    jr $ra