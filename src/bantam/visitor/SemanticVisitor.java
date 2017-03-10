package bantam.visitor;

import bantam.ast.ASTNode;
import bantam.ast.Method;
import bantam.ast.Program;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;
import bantam.util.SymbolTable;

import java.util.Hashtable;

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

    public SemanticVisitor(Hashtable<String, ClassTreeNode> classMap, ErrorHandler
            errorHandler){
        this.classMap = classMap;
        this.errorHandler = errorHandler;
    }

    public abstract void check(Program ast);

    public void registerError(ASTNode node, String message) {
        this.getErrorHandler().register(
                this.getErrorHandler().SEMANT_ERROR,
                this.getCurrentFileName(),
                node.getLineNum(),
                message);
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }


    public String getCurrentFileName() {
        if(this.getCurrentClassName() != null){
            return this.getClassMap()
                    .get(getCurrentClassName())
                    .getASTNode()
                    .getFilename();
        }
        return "";
    }

    public boolean isReservedWord(String term){
        for (String k : RESERVED_WORDS){
            if(k.equals(term)) return true;
        }
        return false;
    }

    public SymbolTable getCurrentVarSymbolTable(){
        return this.getClassMap().get(this.getCurrentClassName()).getVarSymbolTable();
    }

    public SymbolTable getCurrentMethodSymbolTable(){
        return this.getClassMap().get(this.getCurrentClassName()).getMethodSymbolTable();
    }

    public String getCurrentClassName() {
        return this.currentClassName;
    }

    public void setCurrentClassName(String currentClassName) {
        this.currentClassName = currentClassName;
    }

    public String getCurrentMethodName() {
        return this.currentMethodName;
    }

    public void setCurrentMethodName(String currentMethodName) {
        this.currentMethodName = currentMethodName;
    }

    public Hashtable<String, ClassTreeNode> getClassMap() {
        return this.classMap;
    }

}
