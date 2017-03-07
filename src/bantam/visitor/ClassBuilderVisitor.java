package bantam.visitor;

import bantam.ast.*;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;
import bantam.util.SymbolTable;

/**
 * Created by Jacob on 01/03/17.
 */
public class ClassBuilderVisitor extends Visitor {

    private SymbolTable varSymbolTable;
    private SymbolTable methodSymbolTable;
    private ErrorHandler errorHandler;



    public ClassBuilderVisitor(ErrorHandler errorHandler){
        this.errorHandler = errorHandler;
    }

    /**
     * Builds the class structure of the class tree node it is given
     * @param classTreeNode
     */
    public void buildClass(ClassTreeNode classTreeNode){
        this.methodSymbolTable = classTreeNode.getMethodSymbolTable();
        this.varSymbolTable = classTreeNode.getVarSymbolTable();
        if(!classTreeNode.getName().equals("Object")) {
            this.methodSymbolTable.setParent(classTreeNode.getParent().getMethodSymbolTable());
            this.varSymbolTable.setParent(classTreeNode.getParent().getVarSymbolTable());
        }
        varSymbolTable.enterScope();
        methodSymbolTable.enterScope();
        classTreeNode.getASTNode().accept(this);
        varSymbolTable.exitScope();
        methodSymbolTable.exitScope();
    }



    @Override
    public Object visit(Method node) {

        methodSymbolTable.add(node.getName(),node);

        return super.visit(node);
    }

    @Override
    public Object visit(Field node) {
        varSymbolTable.add(node.getName(),node.getType());
        return super.visit(node);
    }
}
