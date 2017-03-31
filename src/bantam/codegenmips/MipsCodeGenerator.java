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

//// TODO: 3/30/2017 dale wants any new classes or changed classes to go in this package
//// TODO: 3/31/2017 figure out how to test this
//// TODO: 3/31/2017 test this
//// TODO: 3/31/2017 other to-dos


import bantam.ast.Class_;
import bantam.ast.Method;
import bantam.ast.Program;
import bantam.util.ClassTreeNode;
import bantam.util.SymbolTable;
import bantam.visitor.StringConstantsVisitor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

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
     * Indices of the builtin classes mapped from their names.
     */
    Map<String, Integer> builtinClassIndices = new HashMap<>();

    /**
     * Indices of the methods in the dispatch tables.
     */
    Map<String, Integer> methodIndices = new HashMap<>();

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


        builtinClassIndices.put("Object", 0);
        builtinClassIndices.put("String", 1);
        builtinClassIndices.put("Sys", 2);
        builtinClassIndices.put("Main", 3);
        builtinClassIndices.put("TextIO", 4);

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
        assemblySupport.genComment(" Author:\tJacob Adamson, Nicholas Cameron, Larry Patrizio");
        assemblySupport.genComment(" Date:\tMarch, 2017");
        assemblySupport.genComment(" Compiled from sources:");
        assemblySupport.genComment(" \t" + " Unknown");     //// TODO: 3/31/2017

        // make sure they are distinct
        // root.getClassMap().values().forEach(n -> n.getASTNode().getFilename());

        out.println();

        //1 - start the data section
        assemblySupport.genDataStart();

        //2 - generate data for the garbage collector
        assemblySupport.genLabel("gc_flag");
        assemblySupport.genWord(this.gc ? "1": "0");

        //3 - generate string constants, including class names
        generateStringConstants();

        //4 - generate class name table
        generateClassNameTable();

        //5 - generate object templates
        generateTemplates();

        //6 - generate dispatch tables
        generateDispatchTables();

        //7 - start the text section
        //8 - generate initialization subroutines
        //9 - generate user-defined methods
    }

    /**
     * This method generates the assembly code for all string constants, including both
     * string constants found in the code, and string constants for class names.
     */
    private void generateStringConstants(){
        StringConstantsVisitor stringConstantsVisitor = new StringConstantsVisitor();

        //generate strings
        Map<String, String> stringMap = stringConstantsVisitor.getStringConstants(this.root);
        for(Map.Entry<String, String> stringConstant : stringMap.entrySet()){
            String stringLabel =  stringConstant.getValue();
            String string = stringConstant.getKey();
            generateStringConstant(stringLabel, string);
        }

        Set<String> filenames = new HashSet<>();
        int fileId = 0;
        for (Map.Entry<String, Integer> nameAndId : builtinClassIndices.entrySet()) {
            int classId = nameAndId.getValue();
            String className = nameAndId.getKey();
            generateStringConstant("class_name_"+classId, className);

            // does filenames
            String filename = root.getClassMap().get(className).getName();
            if (!filenames.contains(filename)) {
                filenames.add(filename);
                generateStringConstant("file_name_"+fileId, filename);
                fileId++;
            }

        }

    }

    /**
     * This method creates the assembly for a string constant.
     * @param label the label for the string
     * @param string the string itself
     */
    private void generateStringConstant(String label, String string){
        int stringLengthRounded = (int) Math.ceil((string.length()+1)/4.0)*4; //remember
        // to look this line over with dale,
        //add one or not to add one, that is the question
        assemblySupport.genLabel(label);
        assemblySupport.genWord("1"); //says its a string

        assemblySupport.genWord(Integer.toString(16 + stringLengthRounded)); //size of
        // all of this

        assemblySupport.genWord("String_dispatch_table"); //pointer to VFT
        assemblySupport.genWord(Integer.toString(string.length()));
        assemblySupport.genAscii(string); //the actual string
    }

    /**
     * This methods generates the full class name table, containing the labels for the
     * class_name strings and the global declarations for the class templates.
     */
    private void generateClassNameTable(){
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
     * Generates the templates for all the classes.
     */
    private void generateTemplates(){
        for (Map.Entry<String, Integer> nameAndId : builtinClassIndices.entrySet()) {
            generateClassTemplate(root.getClassMap().get(nameAndId.getKey()), nameAndId.getValue());
        }
    }

    /**
     * Generates the template for the given class with the given id.
     * @param node the ClassTreeNode corresponding to the class
     * @param id the int id of the class that is having a template generated
     */
    private void generateClassTemplate(ClassTreeNode node, int id) {
        //// TODO: 3/31/17
        //// TODO: 3/31/17
        //// TODO: 3/31/17
        assemblySupport.genLabel(node.getName()+"_template");

        // filter this to only have fields
        // node.getASTNode().getMemberList()
        // and then count them
        // parents must be included

        SymbolTable varTable = node.getVarSymbolTable();
        //// TODO: 3/31/2017 figure this out, if it should be 1 or 2 or 0 or what
        System.out.println(varTable.getCurrScopeLevel()); // should this be 1?
//        varTable.enterScope();
        int numFields = varTable.getSize();
        System.out.println("numField: "+numFields);
        String dispatchTableName = node.getName()+"_dispatch_table";
        assemblySupport.genWord(Integer.toString(id));
        assemblySupport.genWord(Integer.toString(numFields * 4));
        //TODO verify that this is the right number ^^^
        assemblySupport.genWord(dispatchTableName);
//        varTable.exitScope();
        //// TODO: 3/31/17 initialize each field to zero
        //// TODO: 3/31/17 initialize each field to zero
    }

    /**
     * Generates the dispatch tables for all the classes in the class tree.
     */
    private void generateDispatchTables(){
        // order matters here, do parents first
        Map<Class_, List<String>> methodNameListMap = new HashMap<>();
        for(ClassTreeNode node : this.root.getClassMap().values()){
            generateDispatchTable(node.getASTNode(), methodNameListMap);
        }
    }

    /**
     * Generates the dispatch table for the given class, and map of pre-existing method
     * names.
     * @param clazz    the class to make the table for
     * @param methodNameListMap     the map of classes to lists of their method names
     */
    private void generateDispatchTable(Class_ clazz,
                                       Map<Class_, List<String>> methodNameListMap) {

        Class_ parent = null;
        // if haven't done parent yet, do parent first
        if (clazz.getParent() != null) {
            parent = root.lookupClass(clazz.getParent()).getASTNode();
            if (!methodNameListMap.containsKey(parent)) {
                generateDispatchTable(parent, methodNameListMap);
            }
        }

        List<String> currentTable;
        if (parent != null) {
            // copy the dispatch table of the parent
            currentTable = new ArrayList<>(methodNameListMap.get(parent));
        } else {
            // impl: we cannot initialize at declaration because currentTable
            // must be final or effectively final to be used in the forEach lambda
            currentTable = new ArrayList<>();
        }

        // the below code copies the parent dispatch table and overwrites methods if
        // necessary
        assemblySupport.genLabel(clazz.getName()+"_dispatch_table");
        clazz.getMemberList().forEach(n -> {
            // if its a method then we'll actually do things
            if (n instanceof Method) {
                Method m = (Method) n;
                // checks if we are overriding a method and if so removes it
                // from the dispatch table.
                currentTable.replaceAll(s -> {
                    if (Objects.equals(s.substring(s.indexOf(".")+1), m.getName())) {
                        return clazz.getName()+"."+m.getName();
                    }
                    return s;
                });

                // adds the method to the symbol table
                String name = clazz.getName()+"."+m.getName();
                currentTable.add(name);
                currentTable.forEach(s -> methodIndices.putIfAbsent(
                        s.substring(s.indexOf(".")+1),
                        methodIndices.size()));
            }
        });
        // generate the methods

        currentTable.forEach(s -> assemblySupport.genWord(s));
        methodNameListMap.put(clazz, currentTable);
    }
}
