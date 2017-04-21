/*
*   File: TypeCheckerVisitor.java
*   Names: Ana Sofia Solis Canales, He He, Josh Hews, Erin Lavoie
*   Class: CS461
*   Project: 3
*   Date: 3/12/17
*/
package bantam.semant;

import bantam.ast.*;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;
import bantam.visitor.Visitor;

import java.util.*;

/**
 * Created by elavoie on 3/10/17.
 * This visitor will visit all ASTNode and do the type checker
 */
public class TypeCheckerVisitor extends Visitor {

    /**
     * Table of classes in program
     */
    private Hashtable<String, ClassTreeNode> classMap;

    /**
     * Current Class Tree Node
     */
    private ClassTreeNode currClass;

    /**
     * Error checker
     */
    private SemanticErrorReporter errorReporter;

    /**
     * current method's return type
     */
    private String currMethodReturnType;

    /**
     * Constructor of Type Checker Visitor
     * @param classMap class map of all class passed by the caller
     * @param err the error handler used to register errors
     * @param reservedWords the set of reserved words
     */
    public TypeCheckerVisitor(Hashtable<String, ClassTreeNode> classMap,
                              ErrorHandler err, Set<String> reservedWords) {
        this.classMap = classMap;
        this.currMethodReturnType = null;

        this.errorReporter = new SemanticErrorReporter(classMap, err, reservedWords);

    }

    /**
     * Check that types in the program are valid
     * @param program the given program
     */
    public void checkType(Program program) {
        program.getClassList().accept(this);
    }

    /**
     * Visits the class and visits all its members
     * @param class_ the class to visit
     * @return result of the visit
     */
    @Override
    public Object visit(Class_ class_) {
        this.currClass = this.classMap.get(class_.getName());

        this.errorReporter.setCurrClass(this.currClass);
        this.errorReporter.setCurrFile(class_.getFilename());

        class_.getMemberList().forEach(m -> m.accept(this));
        return null;
    }

    // --------------------------------------------------------------------------
    // Members
    // --------------------------------------------------------------------------

    /**
     * Visits the field and check types of the field
     * @param field the given field
     * @return the result of the visit
     */
    @Override
    public Object visit(Field field) {
        if(field.getInit() != null){
            field.getInit().accept(this);
        }

        this.errorReporter.reportFieldErrors(field);

        String fieldType = field.getType();
        boolean isArray = fieldType.endsWith("[]");

        if(!this.errorReporter.typeExists(fieldType)){
            fieldType = "Object" + (isArray ? "[]" : "");
            this.currClass.getVarSymbolTable().set(field.getName(), fieldType, 0);
        }

        return null;
    }

    /**
     * Visits the method and sets this.currMethodReturnType
     * to be this method's return type
     * @param method the given method
     * @return the result of the visit
     */
    @Override
    public Object visit(Method method) {
        this.errorReporter.setCurrClassFieldScopeLevel(this.currClass.getVarSymbolTable().getCurrScopeLevel());
        this.currClass.getVarSymbolTable().enterScope();

        // visit formal list
        method.getFormalList().accept(this);

        this.errorReporter.reportMethodErrors(method);

        this.currMethodReturnType = method.getReturnType();
        if(!this.currMethodReturnType.equals("void")){
            if(!this.errorReporter.typeExists(this.currMethodReturnType)){
                this.currMethodReturnType = "Object";
            }
        }

        method.getStmtList().accept(this);

        this.currClass.getVarSymbolTable().exitScope();
        return null;
    }

    // --------------------------------------------------------------------------
    // Formals
    // --------------------------------------------------------------------------

    /**
     * Visits a formal, meaning a parameter of a method
     * and checks if the type of it is valid
     * @param formal the given formal
     * @return the result of the visit
     */
    @Override
    public Object visit(Formal formal) {

        this.errorReporter.reportFormalErrors(formal);

        String type = formal.getType();
        if(!this.errorReporter.typeExists(type)){
            type = "Object";
        }

        // add to symbol table
        this.currClass.getVarSymbolTable().add(formal.getName(), type);

        return null;
    }

    // --------------------------------------------------------------------------
    // Statements
    // --------------------------------------------------------------------------

    /**
     * Visits the declaration statement and checks if
     * the type is valid. If so, add it to the variable
     * symbol table
     * @param declStmt the given declaration type
     * @return the result of the visit
     */
    @Override
    public Object visit(DeclStmt declStmt) {

        Expr init = declStmt.getInit();
        if(init != null){
            init.accept(this);
        }

        this.errorReporter.reportDeclStmtErrors(declStmt);

        String declType = declStmt.getType();
        if(!this.errorReporter.typeExists(declType)){
            declType = "Object";
        }

        String declName = declStmt.getName();

        // add name to symbol table
        this.currClass.getVarSymbolTable().add(declName, declType);

        return null;
    }

    /**
     * Visits the if statement and checks if the types
     * are valid. If so, add variables to variable symbol table.
     * @param ifStmt
     * @return the result of the visit
     */
    @Override
    public Object visit(IfStmt ifStmt) {

        Expr pred = ifStmt.getPredExpr();
        pred.accept(this);

        this.errorReporter.reportIfStmtErrors(ifStmt);

        // if scope
        this.currClass.getVarSymbolTable().enterScope();

        ifStmt.getThenStmt().accept(this);

        this.currClass.getVarSymbolTable().exitScope();

        Stmt elseStmt = ifStmt.getElseStmt();
        if (elseStmt != null) {
            // else scope
            this.currClass.getVarSymbolTable().enterScope();

            elseStmt.accept(this);

            this.currClass.getVarSymbolTable().exitScope();
        }


        return null;
    }

    /**
     * Visits the while statement and checks if the types
     * are valid. If so, add variables to variable symbol table.
     * @param whileStmt
     * @return the result of the visit
     */
    @Override
    public Object visit(WhileStmt whileStmt) {

        Expr pred = whileStmt.getPredExpr();
        pred.accept(this);

        this.errorReporter.reportWhileStmtErrors(whileStmt);

        this.currClass.getVarSymbolTable().enterScope();

        whileStmt.getBodyStmt().accept(this);

        this.currClass.getVarSymbolTable().exitScope();

        return null;
    }

    /**
     * Visits the for statement and checks if the types
     * are valid. If so, add variables to variable symbol table.
     * @param forStmt
     * @return the result of the visit
     */
    @Override
    public Object visit(ForStmt forStmt) {

        Expr initExpr = forStmt.getInitExpr();
        if (initExpr != null) {
            initExpr.accept(this);
        }

        Expr predExpr = forStmt.getPredExpr();
        if (predExpr != null) {
            predExpr.accept(this);
        }

        Expr updateExpr = forStmt.getUpdateExpr();
        if (updateExpr != null) {
            updateExpr.accept(this);
        }

        this.errorReporter.reportForStmtErrors(forStmt);

        this.currClass.getVarSymbolTable().enterScope();

        forStmt.getBodyStmt().accept(this);

        this.currClass.getVarSymbolTable().exitScope();

        return null;
    }

    /**
     * Visits the if statement and checks if the types
     * are valid. If so, add variabls to variable symbol table.
     * @param blockStmt
     * @return the result of the visit
     */
    @Override
    public Object visit(BlockStmt blockStmt) {

        this.currClass.getVarSymbolTable().enterScope();
        blockStmt.getStmtList().accept(this);
        this.currClass.getVarSymbolTable().exitScope();

        return null;
    }

    /**
     * Visits the return statement and checks if it returns
     * the correct type
     * @param returnStmt the return statement to visit
     * @return the result of the visit
     */
    @Override
    public Object visit(ReturnStmt returnStmt) {

        if(returnStmt.getExpr() != null)
            returnStmt.getExpr().accept(this);

        this.errorReporter.reportReturnStmtErrors(returnStmt, this.currMethodReturnType);

        return returnStmt;
    }

    // --------------------------------------------------------------------------
    // Expressions
    // --------------------------------------------------------------------------

    /**
     * Visits the dispatch expression and checks
     * if the caller exists and if the method exists
     * in the class
     * @param dispatchExpr the dispatch statement to visit
     * @return the result of the visit
     */
    @Override
    // @return method return type
    public Object visit(DispatchExpr dispatchExpr) {

        dispatchExpr.getActualList().accept(this);
        Expr ref = dispatchExpr.getRefExpr();
        if(ref != null){
            ref.accept(this);
        }
        this.errorReporter.reportDispatchExprErrors(dispatchExpr);

        ClassTreeNode callClass = currClass;

        if (ref != null) {
            String refType = ref.getExprType();
            if(refType.endsWith("[]")){
                callClass = classMap.get("Object");
            }
            else{
                if(!this.errorReporter.typeExists(refType)){
                    dispatchExpr.setExprType("Object");
                    return null;
                }
                callClass = this.classMap.get(refType);
            }
        }

        String methodName = dispatchExpr.getMethodName();

        Method method = (Method) callClass.getMethodSymbolTable().lookup(methodName);

        if(method == null || !this.errorReporter.typeExists(method.getReturnType())){
            dispatchExpr.setExprType("Object");
        }
        else{
            dispatchExpr.setExprType(method.getReturnType());
        }

        return null;
    }

    /**
     * Visits the new expression and checks if the type
     * exists and it's giving the right params
     * @param newExpr the new expression to visit
     * @return the result of visit
     */
    @Override
    public Object visit(NewExpr newExpr) {

        this.errorReporter.reportNewExprErrors(newExpr);

        // check type exists
        String type = newExpr.getType();
        if(!this.errorReporter.typeExists(type)){
            type = "Object";
        }
        // set expression type
        newExpr.setExprType(type);

        return null;
    }

    /**
     * Visits a new array expression and checks
     * the type of the array exists and a right
     * size of array is given
     * @param newArrayExpr the new array expression to visit
     * @return the result of the visit
     */
    @Override
    public Object visit(NewArrayExpr newArrayExpr) {
        newArrayExpr.getSize().accept(this);

        this.errorReporter.reportNewArrayExprErrors(newArrayExpr);

        String type = newArrayExpr.getType();
        if(!this.errorReporter.typeExists(type)){
            type = "Object[]";
        }
        // set expression type
        newArrayExpr.setExprType(type);

        return null;
    }

    /**
     * Visits an instanceof Expression and checks if it's
     * being used properly
     * @param instanceofExpr the instanceof expression to visit
     * @return the result of the viist
     */
    @Override
    public Object visit(InstanceofExpr instanceofExpr) {
        instanceofExpr.getExpr().accept(this);

        this.errorReporter.reportInstanceofExprErrors(instanceofExpr);

        String checkType = instanceofExpr.getType();
        String exprType = instanceofExpr.getExpr().getExprType();


        if(this.errorReporter.typeExists(checkType) && (this.errorReporter.isSubclass(checkType, exprType))){
            instanceofExpr.setUpCheck(true);
        }

        instanceofExpr.setExprType("boolean");

        return null;
    }

    /**
     * Visits the cast expression and checks the casting is legal
     * @param castExpr the cast expression to visit
     * @return the result of the visit
     */
    @Override
    public Object visit(CastExpr castExpr) {
        castExpr.getExpr().accept(this);
        this.errorReporter.reportCastExprErrors(castExpr);

        String castType = castExpr.getType();
        String exprType = castExpr.getExpr().getExprType();

        if(!this.errorReporter.typeExists(castType)){
            castType = "Object";
        }

        if(this.errorReporter.typeExists(castType) && (this.errorReporter.isSubclass(castType, exprType))){
            castExpr.setUpCast(true);
        }

        castExpr.setExprType(castType);

        return null;
    }

    /**
     * Visits an assignment expression and checks the
     * type of left is compatible with that of right
     * @param assignExpr the assign expression to visit
     * @return the result of the visit
     */
    @Override
    public Object visit(AssignExpr assignExpr) {
        assignExpr.getExpr().accept(this);

        this.errorReporter.reportAssignExprErrors(assignExpr);

        String refName = assignExpr.getRefName();
        String name = assignExpr.getName();

        String type = null;

        if(refName == null){
            type = (String)this.currClass.getVarSymbolTable().lookup(name);
        }
        else if(refName.equals("this") || refName.equals("super")){
            ClassTreeNode lookupClass = refName.equals("this") ? this.currClass : this.currClass.getParent();
            type = (String)lookupClass.getVarSymbolTable().lookup(name, 0);
        }

        if(type == null){
            type = assignExpr.getExpr().getExprType();
        }


        // assign type to expr
        assignExpr.setExprType(type);

        return null;
    }

    /**
     * Visits an array assign expression and checks if the
     * type of left array is compatible with the type of the
     * right array
     * @param arrayAssignExpr the array assign expression to visit
     * @return the result of the visit
     */
    @Override
    public Object visit(ArrayAssignExpr arrayAssignExpr) {
        arrayAssignExpr.getExpr().accept(this);
        arrayAssignExpr.getIndex().accept(this);

        this.errorReporter.reportArrayAssignExprErrors(arrayAssignExpr);

        String refName = arrayAssignExpr.getRefName();
        String name = arrayAssignExpr.getName();

        String type = null;

        if(refName == null){
            type = (String)this.currClass.getVarSymbolTable().lookup(name);
        }
        else if(refName.equals("this") || refName.equals("super")){
            ClassTreeNode lookupClass = refName.equals("this") ? this.currClass : this.currClass.getParent();
            type = (String)lookupClass.getVarSymbolTable().lookup(name, 0);
            type = type.replace("[]", "");
        }

        if(type == null){
            type = arrayAssignExpr.getExpr().getExprType();
        }

        // assign type to expr
        arrayAssignExpr.setExprType(type);

        return null;

    }

    /**
     * Visits a variable expression and checks if the type exists
     * @param varExpr the variable expression to visit
     * @return the result of the visit
     */
    @Override
    public Object visit(VarExpr varExpr) {
        String name = varExpr.getName();
        VarExpr refExpr = (VarExpr)varExpr.getRef();
        if(refExpr != null){
            refExpr.accept(this);
        }

        this.errorReporter.reportVarExprErrors(varExpr);


        // if ref is null, annotate expr type with the correct type
        if(refExpr == null){
            if(name.equals("this")){
                varExpr.setExprType(this.currClass.getName());
            }
            else if(name.equals("super")){
                varExpr.setExprType(this.currClass.getParent().getName());
            }
            else if(name.equals("null")){
                varExpr.setExprType("null");
            }
            else {
                String nameType = (String) this.currClass.getVarSymbolTable().lookup(name);
                varExpr.setExprType((nameType == null) ? "Object" : nameType);
            }
        }
        else {
            String ref = refExpr.getName();
            String refType = refExpr.getExprType();
            String finalType = "Object";

            if(ref.equals("this") || ref.equals("super")){
                String nameType = (String)(this.classMap.get(refType).getVarSymbolTable().lookup(name));
                if(nameType != null){
                    finalType = nameType;
                }
            }
            else if(refType.endsWith("[]")){
                finalType = "int";
            }
            varExpr.setExprType(finalType);
        }
        return null;
    }

    /**
     * Visits an array expression
     * @param arrayExpr the array expression to visit
     * @return
     */
    @Override
    public Object visit(ArrayExpr arrayExpr) {
        String name = arrayExpr.getName();
        VarExpr refExpr = (VarExpr)arrayExpr.getRef();
        if(refExpr != null){
            refExpr.accept(this);
        }
        arrayExpr.getIndex().accept(this);

        this.errorReporter.reportArrayExprErrors(arrayExpr);



        String finalType = "Object";

        if(refExpr == null){
            String nameType = (String)this.currClass.getVarSymbolTable().lookup(name);
            if(nameType != null){
                finalType = nameType.replace("[]", "");
            }
        }
        else{
            String refType = refExpr.getExprType();
            ClassTreeNode checkClass = this.classMap.get(refType);
            if(checkClass != null){
                String nameType = (String)checkClass.getVarSymbolTable().lookup(name, 0);
                if(nameType != null){
                    finalType = nameType.replace("[]", "");
                }
            }
        }
        arrayExpr.setExprType(finalType);

        return null;
    }

    // --------------------------------------------------------------------------
    // Binary Expressions
    // --------------------------------------------------------------------------

    /**
     * Visit a binary expression and checks if the types of
     * operands are proper for the operation
     * @param binaryExpr the binary expression to visit
     * @return the result of the visit
     */
    @Override
    public Object visit(BinaryExpr binaryExpr) {

        Expr leftExpr = binaryExpr.getLeftExpr();
        leftExpr.accept(this);
        Expr rightExpr = binaryExpr.getRightExpr();
        rightExpr.accept(this);

        if (binaryExpr.getOperandType() == null) {
            errorReporter.checkBinaryExpressionTypeCompatible(leftExpr.getExprType(), rightExpr.getExprType(),
                    binaryExpr.getOpName(), binaryExpr.getLineNum());
        }

        else {
            errorReporter.reportBinaryOperandTypeError(leftExpr.getExprType(), rightExpr.getExprType(),
                    binaryExpr.getOperandType(), binaryExpr.getOpName(), binaryExpr.getLineNum());
        }

        binaryExpr.setExprType(binaryExpr.getOpType());

        return null;
    }

    @Override
    public Object visit(BinaryCompEqExpr binaryCompEqExpr) {
        this.visit((BinaryExpr) binaryCompEqExpr);
        return null;
    }

    @Override
    public Object visit(BinaryCompNeExpr binaryCompNeExpr) {
        this.visit((BinaryExpr) binaryCompNeExpr);
        return null;
    }

    // Must be ints
    @Override
    public Object visit(BinaryCompLtExpr binaryCompLtExpr) {
        this.visit((BinaryExpr) binaryCompLtExpr);
        return null;
    }

    @Override
    public Object visit(BinaryCompLeqExpr binaryCompLeqExpr) {
        this.visit((BinaryExpr) binaryCompLeqExpr);
        return null;
    }

    @Override
    public Object visit(BinaryCompGtExpr binaryCompGtExpr) {
        this.visit((BinaryExpr) binaryCompGtExpr);
        return null;
    }

    @Override
    public Object visit(BinaryCompGeqExpr binaryCompGeqExpr) {
        this.visit((BinaryExpr) binaryCompGeqExpr);
        return null;
    }

    // Must be ints
    @Override
    public Object visit(BinaryArithPlusExpr binaryArithPlusExpr) {
        this.visit((BinaryExpr) binaryArithPlusExpr);
        return null;
    }

    @Override
    public Object visit(BinaryArithMinusExpr binaryArithMinusExpr) {
        this.visit((BinaryExpr) binaryArithMinusExpr);
        return null;
    }

    @Override
    public Object visit(BinaryArithTimesExpr binaryArithTimesExpr) {
        this.visit((BinaryExpr) binaryArithTimesExpr);
        return null;
    }

    @Override
    public Object visit(BinaryArithDivideExpr binaryArithDivideExpr) {
        this.visit((BinaryExpr) binaryArithDivideExpr);
        return null;
    }

    @Override
    public Object visit(BinaryArithModulusExpr binaryArithModulusExpr) {
        this.visit((BinaryExpr) binaryArithModulusExpr);
        return null;
    }

    // Must be booleans
    @Override
    public Object visit(BinaryLogicAndExpr binaryLogicAndExpr) {
        this.visit((BinaryExpr) binaryLogicAndExpr);
        return null;
    }

    @Override
    public Object visit(BinaryLogicOrExpr binaryLogicOrExpr) {
        this.visit((BinaryExpr) binaryLogicOrExpr);
        return null;
    }

    // --------------------------------------------------------------------------
    // Unary Expressions
    // Visit all unary expression and checks if the type of the operand is proper
    // --------------------------------------------------------------------------

    @Override
    public Object visit(UnaryExpr unaryExpr) {
        Expr innerExpr = unaryExpr.getExpr();

        innerExpr.accept(this);
        errorReporter.reportUnaryOperationTypeError(unaryExpr.getOperandType(), innerExpr.getExprType(),
                unaryExpr.getOpName(), unaryExpr.getLineNum());

        unaryExpr.setExprType(unaryExpr.getOpType());
        return null;
    }

    // Must be int
    @Override
    public Object visit(UnaryNegExpr unaryNegExpr) {
        this.visit((UnaryExpr) unaryNegExpr);
        return null;
    }

    // Must be boolean
    @Override
    public Object visit(UnaryNotExpr unaryNotExpr) {
        this.visit((UnaryExpr) unaryNotExpr);
        return null;
    }

    // Must be Var expression with type int
    @Override
    public Object visit(UnaryIncrExpr unaryIncrExpr) {
        this.visit((UnaryExpr) unaryIncrExpr);
        return null;
    }

    @Override
    public Object visit(UnaryDecrExpr unaryDecrExpr) {
        this.visit((UnaryExpr) unaryDecrExpr);
        return null;
    }

    // --------------------------------------------------------------------------
    // Constant Expressions
    // Visit all constant expressions
    // --------------------------------------------------------------------------

    @Override
    public Object visit(ConstIntExpr constIntExpr) {
        constIntExpr.setExprType("int");
        return null;
    }

    @Override
    public Object visit(ConstBooleanExpr constBooleanExpr) {
        constBooleanExpr.setExprType("boolean");
        return null;
    }

    @Override
    public Object visit(ConstStringExpr constStringExpr) {
        constStringExpr.setExprType("String");
        return null;
    }

}
