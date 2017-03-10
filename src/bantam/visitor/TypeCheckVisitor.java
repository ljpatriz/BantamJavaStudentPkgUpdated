package bantam.visitor;

import bantam.ast.*;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;
import bantam.util.SymbolTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Objects;

/**
 * Created by ncameron on 3/2/2017.
 */
public class TypeCheckVisitor extends SemanticVisitor {

    public TypeCheckVisitor (Hashtable<String, ClassTreeNode> classMap,
                             ErrorHandler errorHandler){
        super(classMap, errorHandler);
    }

    @Override
    public void check(Program ast) {
        ast.accept(this);
        this.afterVisit();
    }

    @Override
    public void afterVisit(){
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
        return false;
    }

    @Override
    public Object visit(AssignExpr node) {
        //// TODO: 3/2/2017 must be valid assignment type
        super.visit(node);
        Expr refVarExpr = node.getRefName() == null ?
                null :
                new VarExpr(-1, null, node.getRefName());
        VarExpr varExpr = new VarExpr(-1, refVarExpr, node.getName());
        varExpr.accept(this);
        node.setExprType(varExpr.getExprType());

        String varType = (String)this.getCurrentVarSymbolTable().lookup(node.getName());
        if(varType == null){
            this.registerError(node, "variable "+node.getName()+"was not declared");
        } else if(!isSuperType(varType, node.getExpr().getExprType())) {
            this.registerError(node, "Invalid Assignment the left-hand expression type " +
                    varType + " does not match the right-hand expression type " + node.getExpr().getExprType());
        }
        return true;
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
        if(node.getInit() != null && !isSuperType(node.getType(), node.getInit().getExprType())){
            this.registerError(node, "Invalid Assignment Type");
        }

        return null;
    }
    
    @Override
    public Object visit(WhileStmt node) {
        //// TODO: 3/2/2017 expr must be boolean
        node.getPredExpr().accept(this);
        System.out.println(node.getPredExpr().getExprType());
        if(!node.getPredExpr().getExprType().equals(this.BOOLEAN)) {
            this.registerError(node, "PredExpression must be a boolean but was of type "
                    + node.getPredExpr().getExprType());
        }
        this.getCurrentVarSymbolTable().enterScope();
        node.getBodyStmt().accept(this);
        this.getCurrentVarSymbolTable().exitScope();
        return false;
    }

    @Override
    public Object visit(IfStmt node) {
        node.getPredExpr().accept(this);
        if(!node.getPredExpr().getExprType().equals(this.BOOLEAN))
            this.registerError(node, "PredExpression must be a boolean but was of type "
                    + node.getPredExpr().getExprType());
        this.getCurrentVarSymbolTable().enterScope();
        node.getThenStmt().accept(this);
        this.getCurrentVarSymbolTable().exitScope();
        if(node.getElseStmt() != null){
            this.getCurrentVarSymbolTable().enterScope();
            node.getElseStmt().accept(this);
            this.getCurrentVarSymbolTable().exitScope();
        }
        return false;
    }

    @Override
    public Object visit(Method node){
        this.setCurrentMethodName(node.getName());
        this.getCurrentVarSymbolTable().enterScope();
        super.visit(node);

        if(!VOID.equals(node.getReturnType())){
            ////TODO this line is giving a "Must enter a scope before looking up in table" error
            StmtList stmtList = node.getStmtList();
            if(stmtList.getSize() == 0)
                registerError(node, "Method must have a return statement");
            else{
                Stmt lastStmt = (Stmt)stmtList.get(stmtList.getSize() - 1);
                if(!(lastStmt instanceof ReturnStmt)) {
                    registerError(node, "Last statement of the method must be a return statement");
                } else {
                    ReturnStmt returnStmt = (ReturnStmt) lastStmt;//TODO change to inherit stuff idiot
                    if(!returnStmt.getExpr().getExprType().equals(node.getReturnType())){
                        registerError(node, "Return type "+node.getReturnType() +
                                " does not match given return type "+returnStmt.getExpr().getExprType());
                    }
                }

            }
        }
        this.getCurrentVarSymbolTable().exitScope();
        return null;
    }

    @Override
    public Object visit(Formal node){
        this.putInCurrentVarSymbolTable(node.getName(), node.getType());
        super.visit(node);
        return null;
    }

    /**
     * Visit a return statement node
     * @param node the return statement node
     * @return null
     */
    @Override
    public Object visit(ReturnStmt node) {
        Method method = (Method) this.getCurrentMethodSymbolTable(
                            ).lookup(this.getCurrentMethodName());
        String declaredReturnType = method.getReturnType();

        if(VOID.equals(declaredReturnType)){
            if(node.getExpr() != null){
                node.getExpr().accept(this);
                this.registerError(node, "Methods of declared type cannot return " + node.getExpr().getExprType());
            }
            //for all non null types
            else{
                if(node.getExpr() != null){
                    node.getExpr().accept(this);
                    if(!this.isSuperType(declaredReturnType, node.getExpr().getExprType())){
                        this.registerError(node,
                                "Method's declared type isn't compatible the return type. Declared as type: " +
                                        declaredReturnType + " was of type " + node.getExpr().getExprType());
                    }
                }
                else{//didn't return anything
                    this.registerError(node, "Method of declared type "+declaredReturnType+"cannot return null");
                }
            }
        }
        else{

        }
        super.visit(node);
        return false;
    }

    @Override
    public Object visit(ExprStmt node) {
        //TODO must be a legal expr
        if (!(boolean) node.getExpr().accept(this)) {
            this.registerError(node, "Legal statement expressions are only assignments," +
                    " increment/decrement operations, methods calls, and new object " +
                    "constructions. Invalid expresion type: " + node.getExpr().getExprType());
        }

        return null;
    }
    @Override
    public Object visit(DispatchExpr node) {
        //// TODO: 3/2/2017 method must exist and take any given params
        node.getActualList().accept(this);
        String refRetType;
        if(node.getRefExpr() != null){
            node.getRefExpr().accept(this);
            refRetType = node.getRefExpr().getExprType();
        }
        else{
            refRetType = this.getCurrentClassName();
        }

        if(!this.getClassMap().containsKey(refRetType)){
            registerError(node, "Reference of type "+refRetType+"does not exist");
        }
        else{//find method
            SymbolTable methodSymbolTable =
                    this.getClassMap().get(refRetType).getMethodSymbolTable();
            Method method = (Method) methodSymbolTable.lookup(node.getMethodName());

            if(method == null){//method does not exist
                registerError(node, "Method "+node.getMethodName()+ "does not exist for type"+
                refRetType);
            } else {//unequal param numbers
                if(method.getFormalList().getSize() != node.getActualList().getSize()){
                    registerError(node, "Parameters given of size "+node.getActualList()+
                    " does not match actual size of "+method.getFormalList().getSize());
                } else{
                    //unmatched params
                    for(int i = 0; i < node.getActualList().getSize(); i++){
                        Expr actualParam = (Expr) node.getActualList().get(0);
                        Formal declaredFormal = (Formal) method.getFormalList().get(0);
                        if(!this.isSuperType(declaredFormal.getType(),
                                actualParam.getExprType())){
                            registerError(node, "declared type of parameter"+i+
                                    declaredFormal.getType()+
                                    "does not match given type: " + actualParam.getExprType());
                        }
                    }

                }
            }
        }
        return true;
    }

    @Override
    public Object visit(NewExpr node) {
        if(!this.getClassMap().containsKey(node.getType())){
            this.registerError(node,
                    "Object type " + node.getType() + " does not exist.");
        }
        node.setExprType(node.getType());
        super.visit(node);
        return true;
    }

    @Override
    public Object visit(NewArrayExpr node) {
        super.visit(node);

        if(!node.getSize().getExprType().equals(INT)){
            this.registerError(node,
                    "Expression determining size of array does not resolve to int.");
        }

        return true;
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
            "cannot be cast to " + node.getType());
        }
        node.setExprType(node.getType()); //From Jake: Necessary?
        //// TODO: 3/2/2017 must be a valid cast
        return false;
    }

    public Object visit(BinaryArithDivideExpr node){
        super.visit(node);
        visitBinaryArithExpr(node);
        return false;
    }

    public Object visit(BinaryArithMinusExpr node){
        super.visit(node);
        visitBinaryArithExpr(node);
        return false;
    }

    public Object visit(BinaryArithModulusExpr node){
        super.visit(node);
        visitBinaryArithExpr(node);
        return false;
    }

    public Object visit(BinaryArithPlusExpr node){
        super.visit(node);
        visitBinaryArithExpr(node);
        return false;
    }

    public Object visit(BinaryArithTimesExpr node){
        super.visit(node);
        visitBinaryArithExpr(node);
        return false;
    }


    public Object visitBinaryArithExpr(BinaryArithExpr node) {
        //Both left and right must be numbers
        if(!node.getLeftExpr().getExprType().equals(this.INT))
            this.registerError(node, "Left element of BinaryArithExpr must be of type int, it is of type " +
                    node.getLeftExpr().getExprType());
        if(!node.getRightExpr().getExprType().equals(this.INT))
            this.registerError(node, "Right element of BinaryArithExpr must be of type int, it is of type " +
                            node.getRightExpr().getExprType());
        node.setExprType(this.INT);

        return false;
    }

    public Object visitBinaryCompExpr(BinaryCompExpr node){
        //if node.opName() = "==" or "!="
        if(node.getOpName().equals("==") || node.getOpName().equals("!=")){
            //check they are same types
            if(!node.getLeftExpr().getExprType().equals(node.getRightExpr().getExprType()))
                this.registerError(node, "Both elements of the BinaryCompEqExpr must be of the same type, " +
                        " left is of type " + node.getLeftExpr().getExprType() + "right is of type, "+node.getRightExpr().getExprType());
        }
        else{
            //if left expr not an int, register error
            if(!node.getLeftExpr().getExprType().equals(this.INT) ){
                this.registerError(node, "Left element of the BinaryCompExpr \""+
                        node.getOpName()+"\" at line number " +node.getLineNum()+
                        " must be of type int, it is of type " +
                        node.getLeftExpr().getExprType());
            }
            //if right expr not an int, register error
            if(!node.getRightExpr().getExprType().equals(this.INT) ){
                this.registerError(node, "Right element of the BinaryCompExpr \""+
                        node.getOpName()+"\" at line number " +node.getLineNum()+
                        " must be of type int, it is of type " +
                        node.getRightExpr().getExprType());
            }
        }
        node.setExprType(this.BOOLEAN);
        return false;
    }

    @Override
    public Object visit(BinaryCompEqExpr node) {
        super.visit(node);
        visitBinaryCompExpr(node);
        return false;
    }

    @Override
    public Object visit(BinaryCompNeExpr node) {
        super.visit(node);
        visitBinaryCompExpr(node);
        return false;
    }

    @Override
    public Object visit(BinaryCompGeqExpr node) {
        super.visit(node);
        visitBinaryCompExpr(node);
        return false;
    }

    @Override
    public Object visit(BinaryCompGtExpr node) {
        super.visit(node);
        visitBinaryCompExpr(node);
        return false;
    }

    @Override
    public Object visit(BinaryCompLeqExpr node) {
        super.visit(node);
        visitBinaryCompExpr(node);
        return false;
    }

    @Override
    public Object visit(BinaryCompLtExpr node) {
        super.visit(node);
        visitBinaryCompExpr(node);
        return false;
    }

    @Override
    public Object visit(BinaryLogicExpr node) {
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
        return false;
    }

    @Override
    public Object visit(UnaryNegExpr node) {
        //// TODO: 3/2/2017 must be number
        super.visit(node);
        if(node.getExpr().getExprType() != this.INT)
            this.registerError(node, "UnaryNegExpr must be of type int, is of type " + node.getExpr().getExprType());
        node.setExprType(this.INT);
        return false;
    }

    @Override
    public Object visit(UnaryNotExpr node) {
        //// TODO: 3/2/2017 must be boolean
        super.visit(node);
        if(node.getExpr().getExprType() != this.BOOLEAN)
            this.registerError(node, "UnaryNotExpr must be of type boolean, is of type " + node.getExpr().getExprType());
        node.setExprType(this.BOOLEAN);
        return false;
    }

    @Override
    public Object visit(UnaryIncrExpr node) {
        //// TODO: 3/2/2017 must be VarExpr
        super.visit(node);
        if(node.getExpr().getExprType() != this.INT)
            this.registerError(node,
                    "UnaryIncrExpr must be of type int, is of type " + node.getExpr().getExprType());
        node.setExprType(this.INT);
        return true;
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
        return false;
    }

    @Override
    public Object visit(VarExpr node) {
        super.visit(node);
        String type = null;
        if(node.getRef() != null) {
            String ref = ((VarExpr) node.getRef()).getName();
            System.out.println("ref is reached");
            if(THIS.equals(ref)){
                int scopeLevel = this.getClassMap()
                        .get(this.getCurrentClassName())
                        .getParent()
                        .getVarSymbolTable()
                        .getCurrScopeLevel() +1;
                type = (String) this.getCurrentVarSymbolTable().lookup(node.getName(),
                        scopeLevel);
            }
            else if(SUPER.equals(ref)){
                type = (String) this.getClassMap()
                        .get(this.getCurrentClassName())
                        .getParent()
                        .getVarSymbolTable()
                        .lookup(node.getName());
            }
            else {
                System.out.println("Else is reached");
                String refType  = (String) this.getCurrentVarSymbolTable().lookup(ref);
                if(refType != null && refType.endsWith("[]")
                        && node.getName().equals("length")){
                    type = INT;
                }
                else{
                    this.registerError(node, "Variable may only have 'this' or 'super' as " +
                            "its reference. Reference: "+ ref + " not permitted.");
                    return null;
                }
            }
        }
        else {
            if(node.getName().equals(THIS)){
                type = this.getCurrentClassName();
            }
            else if (node.getName().equals(SUPER)){
                type = this.getClassMap()
                        .get(this.getCurrentClassName())
                        .getParent()
                        .getName();
            }
            else if(this.isKeyword(node.getName())){
                if(node.getName().equals(NULL)){
                    node.setExprType(NULL);
                }
                else {
                    this.registerError(node, "Illegal use of keyword: " + node.getName());
                    node.setExprType(null);
                }
                return null;
            }
            else{
                type = (String) this.getCurrentVarSymbolTable().lookup(node.getName());
            }
        }
        if (type == null){
            this.registerError(node, "Variable " + node.getName() + " could not be " +
                    "found in the current scope.");
            return null;
        }

        node.setExprType(type);
        return false;
    }

    @Override
    public Object visit(ConstIntExpr node) {
        super.visit(node);
        node.setExprType(this.INT);
        return false;
    }

    @Override
    public Object visit(ConstBooleanExpr node) {
        super.visit(node);
        node.setExprType(this.BOOLEAN);
        return false;
    }

    @Override
    public Object visit(ConstStringExpr node) {
        super.visit(node);
        node.setExprType("String");
        return false;
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
