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

    }

    @Override
    public Object visit(Class_ node) {
        return super.visit(node);
    }

    @Override
    public Object visit(Field node) {
        return super.visit(node);
    }

    @Override
    public Object visit(Method node) {
        return super.visit(node);
    }

    @Override
    public Object visit(Formal node) {
        return super.visit(node);
    }

    @Override
    public Object visit(DeclStmt node) {
        return super.visit(node);
    }

    @Override
    public Object visit(NewExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(NewArrayExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(InstanceofExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(CastExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(VarExpr node) {
        return super.visit(node);
    }

    @Override
    public Object visit(ArrayExpr node) {
        return super.visit(node);
    }
}
