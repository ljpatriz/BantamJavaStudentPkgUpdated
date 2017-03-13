/**
 * File: ParserTest.java
 * Author: Jacob, Nick, Larry, Luis, CP
 * Date: 2/23/17
 */
package bantam.visitor;

import bantam.ast.ASTNode;
import bantam.ast.Method;
import bantam.ast.Program;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;
import bantam.util.SymbolTable;

import java.util.Hashtable;

/**
 * Abstract class for the creation of visitors specifically entailed for the Semantic
 * Analyzer.
 */
public abstract class SemanticVisitor extends Visitor {

    /** Declares a set of constants, for cleaner code */
    public static final String INT = "int";
    public static final String BOOLEAN = "boolean";
    public static final String THIS = "this";
    public static final String SUPER = "super";
    public static final String VOID = "void";
    public static final String NULL = "null";
    public static final String CLASS = "class";
    public static final String EXTENDS = "extends";
    public static final String IF = "if";
    public static final String ELSE = "else";
    public static final String NEW = "new";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String FOR = "for";
    public static final String WHILE = "while";
    public static final String RETURN = "return";

    public static final String[] RESERVED_WORDS = {INT, BOOLEAN, THIS, SUPER, VOID,
            NULL, CLASS, EXTENDS, IF, ELSE, NEW, TRUE, FALSE, FOR, WHILE, RETURN};


    private Hashtable<String, ClassTreeNode> classMap;

    private ErrorHandler errorHandler;

    private String currentClassName;

    private String currentMethodName;

    /**
     * Builds the semantic visitor
     * @param classMap - class map from the other visits
     * @param errorHandler - error handler to be registered to
     */
    public SemanticVisitor(Hashtable<String, ClassTreeNode> classMap, ErrorHandler
            errorHandler){
        this.classMap = classMap;
        this.errorHandler = errorHandler;
    }

    /**
     * Abstract method that needs to be overrode.
     * @param ast
     */
    public abstract void check(Program ast);

    /**
     * Registers a detailed error based on a node and string
     * @param node - node where error occurred
     * @param message - detailed message about the error
     */
    public void registerError(ASTNode node, String message) {
        this.getErrorHandler().register(
                this.getErrorHandler().SEMANT_ERROR,
                this.getCurrentFileName(),
                node.getLineNum(),
                message);
    }

    /**
     * Returns the error handler
     * @return - the error handler
     */
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    /**
     * Returns the current file name
     * @return - the current file name
     */
    public String getCurrentFileName() {
        if(this.getCurrentClassName() != null){
            return this.getClassMap()
                    .get(getCurrentClassName())
                    .getASTNode()
                    .getFilename();
        }
        return "";
    }

    /**
     * Checks if the given string is a reserved word
     * @param term
     * @return
     */
    public boolean isReservedWord(String term){
        for (String k : RESERVED_WORDS){
            if(k.equals(term)) return true;
        }
        return false;
    }

    /**
     * returns the current var symbol table
     * @return
     */
    public SymbolTable getCurrentVarSymbolTable(){
        return this.getClassMap().get(this.getCurrentClassName()).getVarSymbolTable();
    }

    /**
     * returns the current method symbol table
     * @return
     */
    public SymbolTable getCurrentMethodSymbolTable(){
        return this.getClassMap().get(this.getCurrentClassName()).getMethodSymbolTable();
    }

    /**
     * returns the current class name
     * @return
     */
    public String getCurrentClassName() {
        return this.currentClassName;
    }

    /**
     * sets the current class name
     * @param currentClassName
     */
    public void setCurrentClassName(String currentClassName) {
        this.currentClassName = currentClassName;
    }

    /**
     * gets the current method name
     * @return
     */
    public String getCurrentMethodName() {
        return this.currentMethodName;
    }

    /**
     * sets the current method name
     * @param currentMethodName
     */
    public void setCurrentMethodName(String currentMethodName) {
        this.currentMethodName = currentMethodName;
    }

    /**
     * returns the class map
     * @return
     */
    public Hashtable<String, ClassTreeNode> getClassMap() {
        return this.classMap;
    }

}
