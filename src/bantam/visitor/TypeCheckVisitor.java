package bantam.visitor;

import bantam.ast.*;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;

import java.util.Hashtable;

/**
 * Created by ncameron on 3/2/2017.
 */
public class TypeCheckVisitor extends Visitor {

    ErrorHandler errorHandler;

    /** Maps class names to ClassTreeNode objects describing the class */
    private Hashtable<String,ClassTreeNode> classMap = new Hashtable<String,ClassTreeNode>();

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


    /**
     * Registers and error if the types for not match
     * @param node the field node
     * @return
     */
    @Override
    public Object visit(Field node) {
        //This is wrong, must check if the types have an equal supertype
        if(!legalTypeCheck(node.getType(), node.getInit().getExprType())
            errorHandler.register(1,"filename",node.getLineNum(),"Invalid Assignment Type");
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
    public Object visit(Method node){
        String declaredType = node.getReturnType();
        //TODO verify that this will return what I think it will return
        //But it probably won't...
        String returnType = (String)super.visit(node);
        if(!legalTypeCheck(declaredType, returnType))
            errorHandler.register(1,"filename",node.getLineNum(),"Invalid Assignment Type");
        return returnType;

    }
    @Override
    public Object visit(ReturnStmt node) {
        //// TODO: 3/2/2017 must return the correct type as spec by method
        return node.getExpr().getExprType();
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

    /**
     * Checks to see if the declared type of an object is a type or a supertype of the object
     * @param declaredType
     * @param objectType
     * @return
     */
    public boolean legalTypeCheck(String declaredType, String objectType){
        ClassTreeNode declaredClass = classMap.get(declaredType);
        ClassTreeNode objectClass = classMap.get(objectType);
        while(objectClass != declaredClass){
            objectClass = objectClass.getParent();
            if(objectClass == null)
                return false;
        }
        return true;
    }
}