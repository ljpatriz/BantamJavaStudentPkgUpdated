package bantam.visitor;

import bantam.ast.*;

/**
 * Created by ncameron on 3/2/2017.
 */
public class TypeCheckVisitor extends Visitor {

    @Override
    public Object visit(DeclStmt node) {
        //// TODO: 3/2/2017 must be valid assignment type
        return super.visit(node);
    }

    @Override
    public Object visit(AssignExpr node) {
        //// TODO: 3/2/2017 must be valid assignment type
        return super.visit(node);
    }


    @Override
    public Object visit(Field node) {
        //// TODO: 3/2/2017 if assigned must be correct type 
        return super.visit(node);
    }
    
    @Override
    public Object visit(WhileStmt node) {
        //// TODO: 3/2/2017 expr must be boolean 
        return super.visit(node);
    }

    @Override
    public Object visit(IfStmt node) {
        //// TODO: 3/2/2017 expr must be boolean 
        return super.visit(node);
    }

    @Override
    public Object visit(ReturnStmt node) {
        //// TODO: 3/2/2017 must return the correct type as spec by method 
        return super.visit(node);
    }

    @Override
    public Object visit(DispatchExpr node) {
        //// TODO: 3/2/2017 method must exist and take any given params 
        return super.visit(node);
    }

    @Override
    public Object visit(NewExpr node) {
        //// TODO: 3/2/2017 array expr must be int 
        return super.visit(node);
    }


    @Override
    public Object visit(CastExpr node) {
        //// TODO: 3/2/2017 must be a valid cast
        return super.visit(node);
    }

    @Override
    public Object visit(BinaryArithExpr node) {
        //// TODO: 3/2/2017 left & right must both be numbers
        return super.visit(node);
    }

    @Override
    public Object visit(BinaryCompEqExpr node) {
        //// TODO: 3/2/2017 must be same types
        return super.visit(node);
    }

    @Override
    public Object visit(BinaryCompNeExpr node) {
        //// TODO: 3/2/2017 must be same types
        return super.visit(node);
    }

    @Override
    public Object visit(BinaryCompExpr node) {
        //// TODO: 3/2/2017 must be numbers
        return super.visit(node);
    }

    @Override
    public Object visit(BinaryLogicExpr node) {
        //// TODO: 3/2/2017 must be booleans
        return super.visit(node);
    }

    @Override
    public Object visit(UnaryNegExpr node) {
        //// TODO: 3/2/2017 must be number
        return super.visit(node);
    }

    @Override
    public Object visit(UnaryNotExpr node) {
        //// TODO: 3/2/2017 must be boolean
        return super.visit(node);
    }

    @Override
    public Object visit(UnaryIncrExpr node) {
        //// TODO: 3/2/2017 must be VarExpr
        return super.visit(node);
    }

    @Override
    public Object visit(UnaryDecrExpr node) {
        //// TODO: 3/2/2017 must be VarExpr
        return super.visit(node);
    }

    @Override
    public Object visit(VarExpr node) {
        //// TODO: 3/2/2017 array expr must be int
        return super.visit(node);
    }


}
