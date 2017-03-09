package bantam.visitor;

import bantam.ast.*;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;

import java.util.Hashtable;

/**
 * Created by ncameron on 3/2/2017.
 */
public class TypeCheckVisitor extends SemanticVisitor {

    public TypeCheckVisitor (Hashtable<String, ClassTreeNode> classMap,
                             ErrorHandler errorHandler){
        super(classMap, errorHandler);
    }

    @Override
    public void executeTask(Program ast) {
        ast.accept(this);
        this.afterVisit();
    }

    @Override
    public void afterVisit(){
        for (String key : this.getClassMap().keySet()){
            this.setCurrentClassName(key);
            this.exitCurrentMethodScope();
            this.exitCurrentVarScope();
        }
    }

    public void check(Program program){
        program.accept(this);
    }

    @Override
    public Object visit(Class_ node){
        this.setCurrentClassName(node.getName());
        super.visit(node);
        return null;
    }

    @Override
    public Object visit(DeclStmt node) {
        super.visit(node);
        if(node.getInit() != null) {
            if (!isSuperType(node.getType(), node.getInit().getExprType()))
                this.registerError(node, "Invalid Declaration. Must be of type " +
                        node.getType() + "but was of type" + node.getInit().getExprType());
        }
        this.putInCurrentVarSymbolTable(node.getName(), node.getType());
        return null;
    }

    @Override
    public Object visit(AssignExpr node) {
        //// TODO: 3/2/2017 must be valid assignment type
        super.visit(node);
        String varType = (String)this.getCurrentVarSymbolTable().lookup(node.getName());
        if(varType == null){
            this.registerError(node, "variable "+node.getName()+"was not declared");
        } else if(!isSuperType(varType, node.getExpr().getExprType()))
            this.registerError(node, "Invalid Assignment the lefthand expression type " +
                    varType+" does not match the righthand expression type "+node.getExpr().getExprType());
        return null;
    }

    //TODO ArrayAssignExpr

    /**
     * Registers and error if the types for not match
     * @param node the field node
     * @return
     */
    @Override
    public Object visit(Field node) {
        //// TODO: 3/2/2017 if assigned must be correct type
        super.visit(node);
        if(!isSuperType(node.getType(), node.getInit().getExprType()))
            this.registerError(node, "Invalid Assignment Type");
        return null;
    }
    
    @Override
    public Object visit(WhileStmt node) {
        //// TODO: 3/2/2017 expr must be boolean
        node.getPredExpr().accept(this);
        if(!node.getPredExpr().getExprType().equals(this.BOOLEAN))
            this.registerError(node, "PredExpression must be a boolean but was of type "
                    + node.getPredExpr().getExprType());
        this.enterCurrentVarScope();
        node.getBodyStmt().accept(this);
        this.exitCurrentVarScope();
        return null;
    }

    @Override
    public Object visit(IfStmt node) {
        node.getPredExpr().accept(this);
        if(!node.getPredExpr().getExprType().equals(this.BOOLEAN))
            this.registerError(node, "PredExpression must be a boolean but was of type "
                    + node.getPredExpr().getExprType());
        this.enterCurrentVarScope();
        node.getThenStmt().accept(this);
        this.exitCurrentVarScope();
        if(node.getElseStmt() != null){
            this.enterCurrentVarScope();
            node.getElseStmt().accept(this);
            this.exitCurrentVarScope();
        }
        return null;
    }

    @Override
    public Object visit(Method node){
        this.setCurrentMethodName(node.getName());
        this.enterCurrentVarScope();
        super.visit(node);
        this.exitCurrentVarScope();
        //TODO check that the last statement is a return statment
        return null;
    }

    @Override
    public Object visit(Formal node){
        this.putInCurrentVarSymbolTable(node.getName(), node.getType());
        super.visit(node);
        return null;
    }
    @Override
    public Object visit(ReturnStmt node) {
        ////TODO: fix filename
        super.visit(node);
        Method methodNode = (Method)this.getCurrentMethodSymbolTable().lookup(this.getCurrentMethodName());
        if(!isSuperType(methodNode.getReturnType(), node.getExpr().getExprType()))
            this.registerError(node, "invalid return type, must be of type:"
                    + methodNode.getReturnType() + "but was of type" + node.getExpr().getExprType());
        return null;
    }

    @Override
    public Object visit(ExprStmt node){
        //TODO must be a legal expr
        super.visit(node);

    }

    @Override
    public Object visit(DispatchExpr node) {
        //// TODO: 3/2/2017 method must exist and take any given params
        String type = node.getRefExpr().getExprType();
        ClassTreeNode classTreeNode = this.getClassMap().get(type);
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
    public Object visit(NewArrayExpr node) {
        super.visit(node);

        if(!node.getSize().getExprType().equals(INT)){
            this.registerError(node,
                    "Expression determining size of array does not resolve to int.");
        }

        return super.visit(node);
    }


    @Override
    public Object visit(CastExpr node) {
        super.visit(node);
        if(INT.equals(node.getType())||
           BOOLEAN.equals(node.getType())||
           INT.equals(node.getExpr().getExprType())){
            this.registerError(node, "Primitives cannot be involved in casts");
            //primitive casting is illegal
        } else if(this.isSuperType(node.getType(), node.getExpr().getExprType())){
            node.setUpCast(true);
        } else if(this.isSuperType(node.getExpr().getExprType(),node.getType())) {
            node.setUpCast(false);
        } else {
            this.registerError(node, "The type "+ node.getExpr().getExprType() +
            "cannot be case to " + node.getType());
        }
        node.setExprType(node.getType()); //Jake: Necessary?
        //// TODO: 3/2/2017 must be a valid cast
        return super.visit(node);
    }

    //TODO must change to sub expressions
    @Override
    public Object visit(BinaryArithExpr node) {
        //// TODO: 3/2/2017 left & right must both be numbers
        super.visit(node);
        if(!node.getLeftExpr().getExprType().equals(this.INT))
            this.registerError(node, "Left element of BinaryArithExpr must be of type int, is of type " +
                    node.getLeftExpr().getExprType());
        if(!node.getRightExpr().getExprType().equals(this.INT))
            this.registerError(node, "Right element of BinaryArithExpr must be of type int, is of type " +
                            node.getRightExpr().getExprType());
        node.setExprType(this.BOOLEAN);
        return null;
    }

    @Override
    public Object visit(BinaryCompEqExpr node) {
        super.visit(node);
        if(!node.getLeftExpr().getExprType().equals(node.getRightExpr().getExprType()))
            this.registerError(node, "Both elements of the BinaryCompEqExpr must be of the same type, " +
                    " left is of type " + node.getLeftExpr().getExprType() + "right is of type, "+node.getRightExpr().getExprType());
        node.setExprType(this.BOOLEAN);
        return null;
    }

    @Override
    public Object visit(BinaryCompNeExpr node) {
        //// TODO: 3/2/2017 must be same types
        super.visit(node);
        if(!node.getLeftExpr().getExprType().equals(node.getRightExpr().getExprType()))
            this.registerError(node,"Both elements of the BinaryCompNeExpr must be of the same type, " +
                    " left is of type " + node.getLeftExpr().getExprType() + "right is of type, "+ node.getRightExpr().getExprType());
        node.setExprType(this.BOOLEAN);
        return null;
    }

    @Override
    public Object visit(BinaryCompExpr node) {
        //// TODO: 3/2/2017 must be numbers
        super.visit(node);
        if(!node.getLeftExpr().getExprType().equals(this.INT))
            this.registerError(node,
                    "Left element of BinaryCompExpr must be of type int, is of type " + node.getLeftExpr().getExprType());
        if(!node.getRightExpr().getExprType().equals(this.INT))
            this.registerError(node,
                    "Right element of BinaryCompExpr must be of type int, is of type " + node.getRightExpr().getExprType());
        node.setExprType(this.INT);
        return null;
    }

    @Override
    public Object visit(BinaryLogicExpr node) {
        //// TODO: 3/2/2017 must be booleans
        super.visit(node);
        if(!node.getLeftExpr().getExprType().equals(this.BOOLEAN))
            this.registerError(node,
                    "Left element of BinaryLogicExpr must be of type boolean, is of type "
                            + node.getLeftExpr().getExprType());
        if(!node.getRightExpr().getExprType().equals(this.BOOLEAN))
            this.registerError(node,
                    "Right element of BinaryLogicExpr must be of type boolean, is of type "
                            + node.getRightExpr().getExprType());
        node.setExprType("boolean");
        return null;
    }

    @Override
    public Object visit(UnaryNegExpr node) {
        //// TODO: 3/2/2017 must be number
        super.visit(node);
        if(node.getExpr().getExprType() != this.INT)
            this.registerError(node, "UnaryNegExpr must be of type int, is of type " + node.getExpr().getExprType());
        node.setExprType(this.INT);
        return null;
    }

    @Override
    public Object visit(UnaryNotExpr node) {
        //// TODO: 3/2/2017 must be boolean
        super.visit(node);
        if(node.getExpr().getExprType() != this.BOOLEAN)
            this.registerError(node, "UnaryNotExpr must be of type boolean, is of type " + node.getExpr().getExprType());
        node.setExprType(this.BOOLEAN);
        return null;
    }

    @Override
    public Object visit(UnaryIncrExpr node) {
        //// TODO: 3/2/2017 must be VarExpr
        super.visit(node);
        if(node.getExpr().getExprType() != this.INT)
            this.registerError(node,
                    "UnaryIncrExpr must be of type int, is of type " + node.getExpr().getExprType());
        node.setExprType(this.INT);
        return null;
    }

    @Override
    public Object visit(UnaryDecrExpr node) {
        //// TODO: 3/2/2017 must be VarExpr
        super.visit(node);
        if(node.getExpr().getExprType() != this.INT)
            this.registerError(node,
                    "UnaryDecrExpr must be of type int, is of type " +
                            node.getExpr().getExprType());
        node.setExprType(this.INT);
        return null;
    }

    @Override
    public Object visit(VarExpr node) {
        super.visit(node);
        System.out.println(node.getName());
        System.out.println(((Field) getCurrentVarSymbolTable().lookup(node.getName())).getType() );

        String type = ((Field)getClassMap().get(getCurrentClassName()).getVarSymbolTable().lookup(node.getName())).getType();
        node.setExprType(type);
        //// TODO: 3/2/2017 path must be legal...

        return null;
    }

    @Override
    public Object visit(ConstIntExpr node) {
        super.visit(node);
        node.setExprType(this.INT);
        return null;
    }

    @Override
    public Object visit(ConstBooleanExpr node) {
        super.visit(node);
        node.setExprType(this.BOOLEAN);
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
    public boolean isSuperType(String declaredType, String objectType){
        ClassTreeNode declaredClass = this.getClassMap().get(declaredType);
        ClassTreeNode objectClass = this.getClassMap().get(objectType);

        while(objectClass != declaredClass){
            objectClass = objectClass.getParent();
            if(objectClass == null)
                return false;
        }
        return true;
    }
}
