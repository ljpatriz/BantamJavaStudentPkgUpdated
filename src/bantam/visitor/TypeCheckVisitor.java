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

    public void check(Program program){
        program.accept(this);
    }

    @Override
    public Object visit(DeclStmt node) {
        super.visit(node);
        if(!legalTypeCheck(node.getType(), node.getInit().getExprType()))
        errorHandler.register(1,"filename",node.getLineNum(),"Invalid Declaration. Must be of type " +
                node.getType()+"but was of type"+node.getInit().getExprType());
        return null;
    }

    @Override
    public Object visit(AssignExpr node) {
        //// TODO: 3/2/2017 must be valid assignment type
        super.visit(node)
        String varType = ""; //TODO properly resolve varType using ref path
        if(!legalTypeCheck(varType, node.getExpr().getExprType()))
            errorHandler.register(1,"filename",node.getLineNum(),"Invalid Assignment Type");
        return null;
    }


    /**
     * Registers and error if the types for not match
     * @param node the field node
     * @return
     */
    @Override
    public Object visit(Field node) {
        //// TODO: 3/2/2017 if assigned must be correct type
        super.visit(node);
        if(!legalTypeCheck(node.getType(), node.getInit().getExprType()))
            errorHandler.register(1,"filename",node.getLineNum(),"Invalid Assignment Type");
        return null;
    }
    
    @Override
    public Object visit(WhileStmt node) {
        //// TODO: 3/2/2017 expr must be boolean
        super.visit(node);
        if(node.getPredExpr().getExprType() != "boolean")
            errorHandler.register(2, "filename", node.getLineNum(), "PredExpression must be a boolean but was of type "
                    + node.getPredExpr().getExprType());
        return null;
    }

    @Override
    public Object visit(IfStmt node) {
        //// TODO: 3/2/2017 expr must be boolean
        super.visit(node);
        if(node.getPredExpr().getExprType() != "boolean")
            errorHandler.register(2, "filename", node.getLineNum(), "PredExpression must be a boolean but was of type "
                    + node.getPredExpr().getExprType());
        return null;
    }

    @Override
    public Object visit(Method node){
        methodType = node.getReturnType();
        super.visit(node);
        return null;
    }

    @Override
    public Object visit(ReturnStmt node) {
        ////TODO: fix filename
        super.visit(node);
        if(!legalTypeCheck(methodType,node.getExpr().getExprType()))
                errorHandler.register(2,"filename", node.getLineNum(), "invalid return type, must be of type:"
                        + methodType + "but was of type" + node.getExpr().getExprType());
        return null;
    }

    @Override
    public Object visit(DispatchExpr node) {
        //// TODO: 3/2/2017 method must exist and take any given params
        String type = node.getRefExpr().getExprType();
        ClassTreeNode classTreeNode = classMap.get(type);
        //// TODO perform a more proper lookup of the method.
        //Note: Still does not check if methods exists or takes those params
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
        super.visit(node);
        if(!node.getLeftExpr().getExprType().equals("int"))
            errorHandler.register(2, "filename", node.getLineNum(), "Left element of BinaryArithExpr must be of type int, is of type " + node.getLeftExpr().getExprType());
        if(!node.getRightExpr().getExprType().equals("int"))
            errorHandler.register(2, "filename", node.getLineNum(), "Right element of BinaryArithExpr must be of type int, is of type " + node.getRightExpr().getExprType());
        node.setExprType("boolean");
        return null;
    }

    @Override
    public Object visit(BinaryCompEqExpr node) {
        super.visit(node);
        if(!node.getLeftExpr().getExprType().equals(node.getRightExpr().getExprType()))
            errorHandler.register(2, "filename", node.getLineNum(), "Both elements of the BinaryCompEqExpr must be of the same type, " +
                    " left is of type " + node.getLeftExpr().getExprType() + "right is of type, "+node.getRightExpr().getExprType());
        node.setExprType("boolean");
        return null;
    }

    @Override
    public Object visit(BinaryCompNeExpr node) {
        //// TODO: 3/2/2017 must be same types
        super.visit(node);
        if(!node.getLeftExpr().getExprType().equals(node.getRightExpr().getExprType()))
            errorHandler.register(2, "filename", node.getLineNum(), "Both elements of the BinaryCompNeExpr must be of the same type, " +
                    " left is of type " + node.getLeftExpr().getExprType() + "right is of type, "+ node.getRightExpr().getExprType());
        node.setExprType("boolean");
        return null;
    }

    @Override
    public Object visit(BinaryCompExpr node) {
        //// TODO: 3/2/2017 must be numbers
        super.visit(node);
        if(!node.getLeftExpr().getExprType().equals("int"))
            errorHandler.register(2, "filename", node.getLineNum(), "Left element of BinaryCompExpr must be of type int, is of type " + node.getLeftExpr().getExprType());
        if(!node.getRightExpr().getExprType().equals("int"))
            errorHandler.register(2, "filename", node.getLineNum(), "Right element of BinaryCompExpr must be of type int, is of type " + node.getRightExpr().getExprType());
        node.setExprType("int");
        return null;
    }

    @Override
    public Object visit(BinaryLogicExpr node) {
        //// TODO: 3/2/2017 must be booleans
        super.visit(node);
        if(!node.getLeftExpr().getExprType().equals("boolean"))
            errorHandler.register(2, "filename", node.getLineNum(), "Left element of BinaryLogicExpr must be of type boolean, is of type " + node.getLeftExpr().getExprType());
        if(!node.getRightExpr().getExprType().equals("boolean"))
            errorHandler.register(2, "filename", node.getLineNum(), "Right element of BinaryLogicExpr must be of type boolean, is of type " + node.getRightExpr().getExprType());
        node.setExprType("boolean");
        return null;
    }

    @Override
    public Object visit(UnaryNegExpr node) {
        //// TODO: 3/2/2017 must be number
        super.visit(node);
        if(node.getExpr().getExprType() != "int")
            errorHandler.register(2, "filename", node.getLineNum(), "UnaryNegExpr must be of type int, is of type " + node.getExpr().getExprType());
        node.setExprType("int");
        return null;
    }

    @Override
    public Object visit(UnaryNotExpr node) {
        //// TODO: 3/2/2017 must be boolean
        super.visit(node);
        if(node.getExpr().getExprType() != "boolean")
            errorHandler.register(2, "filename", node.getLineNum(), "UnaryNotExpr must be of type boolean, is of type " + node.getExpr().getExprType());
        node.setExprType("boolean");
        return null;
    }

    @Override
    public Object visit(UnaryIncrExpr node) {
        //// TODO: 3/2/2017 must be VarExpr
        super.visit(node);
        if(node.getExpr().getExprType() != "int")
            errorHandler.register(2, "filename", node.getLineNum(), "UnaryIncrExpr must be of type int, is of type " + node.getExpr().getExprType());
        node.setExprType("int");
        return null;
    }

    @Override
    public Object visit(UnaryDecrExpr node) {
        //// TODO: 3/2/2017 must be VarExpr
        super.visit(node);
        if(node.getExpr().getExprType() != "int")
            errorHandler.register(2, "filename", node.getLineNum(), "UnaryDecrExpr must be of type int, is of type " + node.getExpr().getExprType());
        node.setExprType("int");
        return null;
    }

    @Override
    public Object visit(VarExpr node) {
        super.visit(node);
        //// TODO: 3/2/2017 path must be legal...

        return null;
    }

    @Override
    public Object visit(ConstIntExpr node) {
        super.visit(node);
        node.setExprType("int");
        return null;
    }

    @Override
    public Object visit(ConstBooleanExpr node) {
        super.visit(node);
        node.setExprType("boolean");
        return null;
    }

    @Override
    public Object visit(ConstStringExpr node) {
        super.visit(node);
        node.setExprType("String");
        return null;
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
