package bantam.visitor;

import bantam.ast.*;
import bantam.util.ClassTreeNode;
import bantam.util.SymbolTable;

/**
 * Created by Jacob on 01/03/17.
 */
public class ClassBuilderVisitor extends Visitor {

    private SymbolTable varSymbolTable;
    private SymbolTable methodSymbolTable;



    /**
     * Builds the class structure of the class tree node it is given
     * @param classTreeNode
     */
    public void buildClass(ClassTreeNode classTreeNode){
        classTreeNode.getASTNode().accept(this);
        this.methodSymbolTable = classTreeNode.getMethodSymbolTable();
        this.varSymbolTable = classTreeNode.getVarSymbolTable();
        this.methodSymbolTable.setParent(classTreeNode.getParent().getMethodSymbolTable());
        this.varSymbolTable.setParent(classTreeNode.getParent().getVarSymbolTable());
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
