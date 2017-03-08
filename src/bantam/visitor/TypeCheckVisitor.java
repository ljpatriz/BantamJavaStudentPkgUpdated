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

    public TypeCheckVisitor (ErrorHandler errorHandler){
        this.errorHandler = errorHandler;
    }

    /** Maps class names to ClassTreeNode objects describing the class */
    private Hashtable<String,ClassTreeNode> classMap;

    private String methodType;

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
        //// TODO: 3/6/2017  remove comment
        //This is wrong, must check if the types have an equal supertype
        if(!legalTypeCheck(node.getType(), node.getInit().getExprType()))
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
        //NOTE: This implementation works in a depth first traversal, which is the way this visitor works
        methodType = node.getReturnType();
        return super.visit(node);
    }

    @Override
    public Object visit(ReturnStmt node) {
        ////TODO: fix filename
        //TODO: also - is getExprType the right thing to have here... Unclear what that is
        //So we have to find a way to get the actual type of the expression up here...
        //maybe we can use that "set expression type method in our other methods
        //It is unused otherwise
        if(legalTypeCheck(methodType,node.getExpr().getExprType()))
                errorHandler.register(2,"filename", node.getLineNum(), "invalid return type");
        return super.visit(node);
    }

    @Override
    public Object visit(DispatchExpr node) {
        //// TODO: 3/2/2017 method must exist and take any given params
        String type = node.getRefExpr().getExprType();
        ClassTreeNode classTreeNode = classMap.get(type);
        //// TODO perform a more proper lookup of the method.
        //Still does not check if methods exists or takes those params
        Method methodNode = (Method)classTreeNode.getMethodSymbolTable().lookup(node.getMethodName());
        node.setExprType(methodNode.getReturnType());
        return super.visit(node);
    }


    // $$$$ TOP HALF IS NICK and Bottom HALF IS CP


    @Override
    public Object visit(NewExpr node) {
        //// TODO: 3/2/2017 array expr must be int
        // TODO: 3/7/2017 by Larry - must make sure new object based on class is correct
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
