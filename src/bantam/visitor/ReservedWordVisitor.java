package bantam.visitor;

import bantam.ast.*;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;

import java.util.Hashtable;

/**
 * Created by Jacob on 10/03/17.
 */
public class ReservedWordVisitor extends SemanticVisitor {

    public ReservedWordVisitor(Hashtable<String, ClassTreeNode> classMap, ErrorHandler errorHandler) {
        super(classMap, errorHandler);
    }

    @Override
    public void check(Program ast) {
        ast.accept(this);
    }

    @Override
    public Object visit(Class_ node) {
        if(isReservedWord(node.getName()))
            registerError(node, node.getName()+"is not a legal identifier");
        return super.visit(node);
    }

    @Override
    public Object visit(Field node) {
        if(isReservedWord(node.getName()))
            registerError(node, node.getName()+"is not a legal identifier");
        return super.visit(node);
    }

    @Override
    public Object visit(Method node) {
        if(isReservedWord(node.getName()))
            registerError(node, node.getName()+"is not a legal identifier");
        return super.visit(node);
    }

    @Override
    public Object visit(Formal node) {
        if(isReservedWord(node.getName()))
            registerError(node, node.getName()+"is not a legal identifier");
        return super.visit(node);
    }

    @Override
    public Object visit(DeclStmt node) {
        if(isReservedWord(node.getName()))
            registerError(node, node.getName()+"is not a legal identifier");
        return super.visit(node);
    }

    @Override
    public Object visit(CastExpr node) {
        if(isReservedWord(node.getType())){
            if(!node.getType().equals(this.INT)|| !node.getType().equals(this.BOOLEAN)){
                registerError(node, node.getType()+"is not a legal identifier");
            }
        }
        return super.visit(node);
    }

    @Override
    public Object visit(NewExpr node) {
        if(isReservedWord(node.getType())){
            if(!node.getType().equals(this.INT)|| !node.getType().equals(this.BOOLEAN)){
                registerError(node, node.getType()+"is not a legal identifier");
            }
        }
        return super.visit(node);
    }

    @Override
    public Object visit(VarExpr node) {
        if(isReservedWord(node.getName())){
            registerError(node, node.getName()+"is not a legal identifier");
        }
        return super.visit(node);
    }

    @Override
    public Object visit(ArrayExpr node) {
        if(isReservedWord(node.getName())){
            registerError(node, node.getName()+"is not a legal identifier");
        }
        return super.visit(node);
    }
}
