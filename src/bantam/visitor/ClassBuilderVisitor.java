package bantam.visitor;

import bantam.ast.*;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;
import bantam.util.SymbolTable;

/**
 * Connects and fills in the symbol tables for each class.
 */
public class ClassBuilderVisitor extends Visitor {

    // var symbol table
    private SymbolTable varSymbolTable;
    // method symbol table
    private SymbolTable methodSymbolTable;
    // error handler
    private ErrorHandler errorHandler;
    // override visitor to prevent overloading
    private MethodOverrideVisitor methodOverrideVisitor;


    /**
     * Create a new ClassBuilderVisitor with the given error handler.
     * @param errorHandler
     */
    public ClassBuilderVisitor(ErrorHandler errorHandler){
        this.errorHandler = errorHandler;
    }

    /**
     * Builds the class structure of the class tree node it is given
     * @param classTreeNode
     */
    public void buildClass(ClassTreeNode classTreeNode){
        this.methodOverrideVisitor = new MethodOverrideVisitor(
                classTreeNode.getClassMap(), errorHandler);
        this.methodSymbolTable = classTreeNode.getMethodSymbolTable();
        this.varSymbolTable = classTreeNode.getVarSymbolTable();
        if(!classTreeNode.getName().equals("Object")) {
            this.methodSymbolTable.setParent(classTreeNode.getParent().getMethodSymbolTable());
            this.varSymbolTable.setParent(classTreeNode.getParent().getVarSymbolTable());
        }
        classTreeNode.getASTNode().accept(this);
    }


    /**
     * Puts a method in the method table if it is not overloading
     * @param node the method node
     * @return
     */
    @Override
    public Object visit(Method node) {
        this.methodOverrideVisitor.visit(node);
        methodSymbolTable.add(node.getName(),node);

        return super.visit(node);
    }


    /**
     * Puts a field in the var table
     * @param node the field node
     * @return
     */
    @Override
    public Object visit(Field node) {
        varSymbolTable.add(node.getName(),node.getType());
        return super.visit(node);
    }
}
