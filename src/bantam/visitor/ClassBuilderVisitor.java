package bantam.visitor;

import bantam.ast.*;
import bantam.util.ClassTreeNode;
import bantam.util.SymbolTable;

/**
 * Created by Jacob on 01/03/17.
 */
public class ClassBuilderVisitor extends Visitor {

    SymbolTable varSymbolTable;
    SymbolTable methodSymbolTable;

    /**
     * Builds the class structure of the class tree node it is given
     * @param classTreeNode
     */
    public static void buildClass(ClassTreeNode classTreeNode){
        ClassBuilderVisitor classBuilderVisitor = new ClassBuilderVisitor(classTreeNode);
        classTreeNode.getASTNode().accept(classBuilderVisitor);
    }

    public ClassBuilderVisitor(ClassTreeNode classTreeNode){
        this.methodSymbolTable = classTreeNode.getMethodSymbolTable();
        this.varSymbolTable = classTreeNode.getVarSymbolTable();
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
