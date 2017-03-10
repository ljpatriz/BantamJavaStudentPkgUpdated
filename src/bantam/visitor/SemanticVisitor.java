package bantam.visitor;

import bantam.ast.ASTNode;
import bantam.ast.Method;
import bantam.ast.Program;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;
import bantam.util.SymbolTable;

import java.util.Hashtable;

/**
 * Created by CarlPhilipMajgaard on 07/3/17.
 */

public abstract class SemanticVisitor extends Visitor {

    /** Static field for the int keyword */
    public static final String INT = "int";

    /** Static field for the boolean keyword */
    public static final String BOOLEAN = "boolean";

    /** Static field for the this keyword */
    public static final String THIS = "this";

    /** Static field for the super keyword */
    public static final String SUPER = "super";

    /** Static field for the void keyword */
    public static final String VOID = "void";

    /** Static field for the null keyword */
    public static final String NULL = "null";

    /** Static field for the class keyword */
    public static final String CLASS = "class";

    /** Static field for the extends keyword */
    public static final String EXTENDS = "extends";

    /** Static field for the if keyword */
    public static final String IF = "if";

    /** Static field for the else keyword */
    public static final String ELSE = "else";

    /** Static field for the new keyword */
    public static final String NEW = "new";

    /** Static field for the true keyword */
    public static final String TRUE = "TRUE";

    /** Static field for the false keyword */
    public static final String FALSE = "false";

    /** Static field for the for keyword */
    public static final String FOR = "for";

    /** Static field for the while keyword */
    public static final String WHILE = "while";

    /** Static field for the return keyword */
    public static final String RETURN = "return";

    /** The set of all keywords */
    public static final String[] KEYWORDS = {INT, BOOLEAN, THIS, SUPER, VOID,
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
    
    public void afterVisit() {}
    
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
            return this.getClassMap().get(getCurrentClassName()).getASTNode()
                    .getFilename();
        }
        return "";
    }

    public boolean isDeclaredType(String type){
        if(type == null || type.equals(""))
            return false;

        String stripped = type.replace("[]", "");

        if(stripped.equals(INT) || stripped.equals(BOOLEAN)){
            return true;
        }
        else {
            return this.getClassMap().containsKey(stripped);
        }
    }

    public boolean isKeyword(String term){
        for (String k : KEYWORDS){
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

    public void putInCurrentMethodSymbolTable(String name, Method method){
        this.getCurrentMethodSymbolTable().add(name, method);
    }

    public void putInCurrentVarSymbolTable(String name, String type){
        this.getCurrentVarSymbolTable().add(name, type);
    }

    public String getCurrentClassName() {
        return currentClassName;
    }

    public void setCurrentClassName(String currentClassName) {
        this.currentClassName = currentClassName;
    }

    public String getCurrentMethodName() {
        return currentMethodName;
    }

    public void setCurrentMethodName(String currentMethodName) {
        this.currentMethodName = currentMethodName;
    }

    public Hashtable<String, ClassTreeNode> getClassMap() {
        return classMap;
    }
//
//    public void enterCurrentVarScope(){
//        getCurrentVarSymbolTable().enterScope();
//    }
//
//    public void exitCurrentVarScope(){
//        getCurrentVarSymbolTable().exitScope();
//    }
//
//    public void enterCurrentMethodScope(){
//        getCurrentMethodSymbolTable().enterScope();
//    }
//
//    public void exitCurrentMethodScope(){
//        getCurrentMethodSymbolTable().exitScope();
//    }

}
