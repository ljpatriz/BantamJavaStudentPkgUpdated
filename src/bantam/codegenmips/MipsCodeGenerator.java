/* Bantam Java Compiler and Language Toolset.

   Copyright (C) 2009 by Marc Corliss (corliss@hws.edu) and 
                         David Furcy (furcyd@uwosh.edu) and
                         E Christopher Lewis (lewis@vmware.com).
   ALL RIGHTS RESERVED.

   The Bantam Java toolset is distributed under the following 
   conditions:

     You may make copies of the toolset for your own use and 
     modify those copies.

     All copies of the toolset must retain the author names and 
     copyright notice.

     You may not sell the toolset or distribute it in 
     conjunction with a commerical product or service without 
     the expressed written consent of the authors.

   THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS 
   OR IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE 
   IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A 
   PARTICULAR PURPOSE. 
*/

package bantam.codegenmips;

import bantam.ast.Program;
import bantam.util.ClassTreeNode;
import bantam.visitor.StringConstantsVisitor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;

/**
 * The <tt>MipsCodeGenerator</tt> class generates mips assembly code
 * targeted for the SPIM emulator.  Note: this code will only run
 * under SPIM.
 * <p/>
 * This class is incomplete and will need to be implemented by the student.
 */
public class MipsCodeGenerator {
    /**
     * Root of the class hierarchy tree
     */
    private ClassTreeNode root;

    /**
     * Print stream for output assembly file
     */
    private PrintStream out;

    /**
     * Assembly support object (using Mips assembly support)
     */
    private MipsSupport assemblySupport;

    /**
     * Boolean indicating whether garbage collection is enabled
     */
    private boolean gc = false;

    /**
     * Boolean indicating whether optimization is enabled
     */
    private boolean opt = false;

    /**
     * Boolean indicating whether debugging is enabled
     */
    private boolean debug = false;

    /**
     * MipsCodeGenerator constructor
     *
     * @param root    root of the class hierarchy tree
     * @param outFile filename of the assembly output file
     * @param gc      boolean indicating whether garbage collection is enabled
     * @param opt     boolean indicating whether optimization is enabled
     * @param debug   boolean indicating whether debugging is enabled
     */
    public MipsCodeGenerator(ClassTreeNode root, String outFile,
                             boolean gc, boolean opt, boolean debug) {
        this.root = root;
        this.gc = gc;
        this.opt = opt;
        this.debug = debug;

        try {
            out = new PrintStream(new FileOutputStream(outFile));
            assemblySupport = new MipsSupport(out);
        } catch (IOException e) {
            // if don't have permission to write to file then report an error and exit
            System.err.println("Error: don't have permission to write to file '" + outFile + "'");
            System.exit(1);
        }
    }

    /**
     * Generate assembly file
     * <p/>
     * In particular, will need to do the following:
     * 1 - start the data section ---- done on 3/29
     * 2 - generate data for the garbage collector ---- done on 3/29
     * 3 - generate string constants ---- done on 3/29
     * 4 - generate class name table
     * 5 - generate object templates
     * 6 - generate dispatch tables
     * 7 - start the text section
     * 8 - generate initialization subroutines
     * 9 - generate user-defined methods
     * See the lab manual for the details of each of these steps.
     */
    public void generate() {
        // comment out
        //throw new RuntimeException("MIPS code generator unimplemented");

        assemblySupport.genDataStart();
        assemblySupport.genLabel("gc_flag");
        assemblySupport.genWord("0");
        generateStringConstants();
        // add code below...
    }

    public void generateStringConstants(){
        StringConstantsVisitor stringConstantsVisitor = new StringConstantsVisitor();
        Map<String, String> stringMap = stringConstantsVisitor.getStringConstants(this.root);
        for(Map.Entry<String, String> stringConstant : stringMap.entrySet()){
            String stringLabel =  stringConstant.getValue();
            String string = stringConstant.getKey();
            String stringLengthRounded = Integer.toString((int) Math.ceil((string.length()+1)/4.0)*4); //remember to look this line over with dale,
                                                                        //add one or not to add one, that is the question
            assemblySupport.genLabel(stringLabel);
            assemblySupport.genWord("1"); //says its a string?
            assemblySupport.genWord(Integer.toString(16) + stringLengthRounded); //size of all of this
            assemblySupport.genWord("String_dispatch_table"); //pointer to VFT
            assemblySupport.genWord(Integer.toString(string.length()));//this line is in example, but we should ask dale.
            assemblySupport.genAscii(string); //the actual string
            assemblySupport.genByte("0");
            assemblySupport.genAlign();
        }
    }
}
