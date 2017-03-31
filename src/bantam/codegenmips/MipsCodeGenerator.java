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
import bantam.util.SymbolTable;
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
        //// TODO: 3/30/2017 generate comment header - how do we get the author & date?
        //1 - start the data section
        assemblySupport.genDataStart();

        //2 - generate data for the garbage collector
        assemblySupport.genLabel("gc_flag");
        assemblySupport.genWord("0");

        //3 - generate string constants
        generateStringConstants();

        //4 - generate class name table
        generateClassNameTable();

        generateTemplates();
        //6 - generate dispatch tables
        //7 - start the text section
        //8 - generate initialization subroutines
        //9 - generate user-defined methods
    }

    public void generateStringConstants(){
        StringConstantsVisitor stringConstantsVisitor = new StringConstantsVisitor();

        //generate strings
        Map<String, String> stringMap = stringConstantsVisitor.getStringConstants(this.root);
        for(Map.Entry<String, String> stringConstant : stringMap.entrySet()){
            String stringLabel =  stringConstant.getValue();
            String string = stringConstant.getKey();
            generateStringConstant(stringLabel, string);
        }

        //generate class name strings
        int classNum = 0;
        for(ClassTreeNode node: this.root.getClassMap().values()){
            generateStringConstant(node.getName(), "class_name_"+classNum);
            classNum++;
        }
    }

    /**
     * This methods generates the class name strings and the class name table
     * It puts into the table all the class name string labels
     *  as well as the template labels
     */
    public void generateClassNameTable(){
        assemblySupport.genLabel("class_name_table");
        //generate class name words
        int numClasses = this.root.getClassMap().values().size();
        for(int i = 0; i < numClasses; i++){
            assemblySupport.genWord("class_name_"+i);
        }
        //generate globals in table
        for(ClassTreeNode node: this.root.getClassMap().values()){
            assemblySupport.genGlobal(node.getName()+"_template");
        }
    }

    /**
     * Generates the templates for all the classes
     */
    public void generateTemplates(){
        //TODO be more elegant than this
        int classid = 0;
        for(ClassTreeNode node : this.root.getClassMap().values()){
            generateClassTemplate(node, classid);
            classid++;
        }
    }

    /**
     * Generates the template for the given class
     * @param node
     */
    public void generateClassTemplate(ClassTreeNode node, int id) {
        SymbolTable varTable = node.getVarSymbolTable();
        int numFields = varTable.getSize();
        String dispatchTableName = node.getName()+"_dispatch_table";
        assemblySupport.genWord(Integer.toString(id));
        assemblySupport.genWord(Integer.toString(numFields * 4));//verify that this is the right number
        assemblySupport.genWord(dispatchTableName);
        //TODO figure out what the other things in the templates are
        // some of them have words with 0 following them...
    }

    /**
     * Generates the dispatch tables for the given class
     */
    public void generateDispatchTables(){
        for(ClassTreeNode node : this.root.getClassMap().values()){
            generateClassTemplate(node);
        }
    }

    /**
     *
     */
    public void generateDispatchTable(){

    }


    /**
     * This method creates the assmebly for a string constants
     * @param label the label for the string
     * @param string the string itself
     */
    public void generateStringConstant(String label, String string){
        int stringLengthRounded = (int) Math.ceil((string.length()+1)/4.0)*4; //remember
        // to look this line over with dale,
        //add one or not to add one, that is the question
        assemblySupport.genLabel(label);
        assemblySupport.genWord("1"); //says its a string?
        //Won't this concat?, and isn't that bad?

        // Yes it will lol, leaving the original line
        // for now because it's hilarious -- Nick

        // assemblySupport.genWord(Integer.toString(16) + stringLengthRounded); //size of
        // all of this
        assemblySupport.genWord(Integer.toString(16 + stringLengthRounded)); //size of
        // all of this

        assemblySupport.genWord("String_dispatch_table"); //pointer to VFT
        assemblySupport.genWord(Integer.toString(string.length()));//this line is in example, but we should ask dale.
        assemblySupport.genAscii(string); //the actual string
        assemblySupport.genByte("0");
        assemblySupport.genAlign();
    }
}
