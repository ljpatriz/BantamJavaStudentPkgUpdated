/*
*   File: SemanticErrorReporter.java
*   Names: Ana Sofia Solis Canales, He He, Josh Hews, Erin Lavoie
*   Class: CS461
*   Project: 3
*   Date: 3/12/17
*/

package bantam.semant;

import bantam.ast.*;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;
import bantam.util.SymbolTable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * Reports semantic errors to the error handler
 */
public class SemanticErrorReporter {

    /**
     * Table of classes in program
     */
    private Hashtable<String, ClassTreeNode> classMap;

    /**
     * Error handler for registering errors
     */
    private ErrorHandler err;

    /**
     * The current file that is being visited
     */
    private String currFile;

    /**
     * The current class that is being visited
     */
    private ClassTreeNode currClass;

    /**
     * Reserved words in language
     */
    private Set<String> reservedWords;

    /**
     * Primitive types in language
     */
    private Set<String> primitiveTypes;

    /**
     * That records the current classes field scope level
     */
    private int currClassFieldScopeLevel;

    /**
     * Creates an Error Checker that reports semantic errors to the error handler
     *
     * @param classMap Map of classes in program
     * @param err Error Handler for registering errors
     * @param reservedWords Set of reserved words in language
     */
    public SemanticErrorReporter(Hashtable<String, ClassTreeNode> classMap, ErrorHandler err,
                                 Set<String> reservedWords) {

        this.classMap = classMap;
        this.err = err;

        this.currFile = null;
        this.currClass = null;

        this.reservedWords = reservedWords;
        this.primitiveTypes = new HashSet<>(Arrays.asList("int", "boolean"));
    }

    /**
     * Sets the current file
     * @param currFile file to set the current file to
     */
    public void setCurrFile(String currFile) {
        this.currFile = currFile;
    }

    /**
     * Sets the current class
     * @param currClass class to set the current class to
     */
    public void setCurrClass(ClassTreeNode currClass) {
        this.currClass = currClass;
    }

    public void setCurrClassFieldScopeLevel(int level){
        this.currClassFieldScopeLevel = level;
    }



    // --------------------------------------------------------------------------
    // Node specific error reporting
    // --------------------------------------------------------------------------
    /**
     * Report errors associated with a field ASTnode
     *
     * @param field     Field node to check
     * */
    public void reportFieldErrors(Field field){
        String declaredType = field.getType();

        // Check declared type exists
        reportTypeNonexistence(declaredType, field.getLineNum());

        if(!this.typeExists(declaredType)){
            declaredType = declaredType.endsWith("[]") ? "Object[]" : "Object";
        }

        Expr init = field.getInit();
        if(init != null) {
            reportSubtypeError(declaredType, init.getExprType(), field.getLineNum());
        }
    }

    /**
     * Report errors associated with a method ASTnode
     *
     * @param method     Method node to check
     * */
    public void reportMethodErrors(Method method){
        // Check return type exists
        String returnType = method.getReturnType();
        if(!returnType.equals("void")){
            reportTypeNonexistence(returnType, method.getLineNum());
            if(!typeExists(returnType)){
                return;
            }
        }

        // If the method returns a value, make sure the last statement in the statement
        //  list is a return statement
        if(!returnType.equals("void")){
            StmtList methodStmtList = method.getStmtList();
            if(methodStmtList.getSize()==0){
                this.err.register(this.err.SEMANT_ERROR, this.currFile, method.getLineNum(),
                        "Last statement of non-void method " + method.getName() +" must be of type " + returnType);
            }
            else{
                Stmt lastStmt = (Stmt)methodStmtList.get(methodStmtList.getSize()-1);
                if(!(lastStmt instanceof ReturnStmt)){
                    this.err.register(this.err.SEMANT_ERROR, this.currFile, lastStmt.getLineNum(),
                            "Last statement of non-void method " + method.getName() +" must be of type " + returnType);
                }
            }
        }
    }


    /**
     * Report errors associated with a formal ASTnode
     *
     * @param formal     Formal node to check
     * */
    public void reportFormalErrors(Formal formal){
        // Check type existance
        reportTypeNonexistence(formal.getType(), formal.getLineNum());

        // formal names can be anything
    }

    /**
     * Report errors associated with a DeclStmt ASTnode
     *
     * @param declStmt     DeclStmt node to check
     * */
    public void reportDeclStmtErrors(DeclStmt declStmt){
        // Check type exists
        String declaredType = declStmt.getType();

        reportTypeNonexistence(declaredType, declStmt.getLineNum());

        if(!typeExists(declaredType)){
            declaredType = declaredType.endsWith("[]") ? "Object[]" : "Object";
        }

        // check for name conflicts
        reportVariableNameErrors(declStmt.getName(), declStmt.getLineNum());

        // check assign type matches declared type
        reportSubtypeError(declaredType, declStmt.getInit().getExprType(), declStmt.getLineNum());

    }

    /**
     * Report errors associated with a IfStmt ASTnode
     *
     * @param ifStmt     IfStmt node to check
     * */
    public void reportIfStmtErrors(IfStmt ifStmt){
        // Check predicate is of type boolean
        String predType = ifStmt.getPredExpr().getExprType();

        reportSubtypeError("boolean", predType, ifStmt.getLineNum());
    }

    /**
     * Report errors associated with a WhileStmt ASTnode
     *
     * @param whileStmt     WhileStmt node to check
     * */
    public void reportWhileStmtErrors(WhileStmt whileStmt){
        // Check predicate is of type boolean
        String predType = whileStmt.getPredExpr().getExprType();

        reportSubtypeError("boolean", predType, whileStmt.getLineNum());
    }

    /**
     * Report errors associated with a forStmt ASTnode
     *
     * @param forStmt     ForStmt node to check
     * */
    public void reportForStmtErrors(ForStmt forStmt){
        // Check predicate is of type boolean
        Expr pred = forStmt.getPredExpr();
        if(pred != null){
            reportSubtypeError("boolean", pred.getExprType(), forStmt.getLineNum());
        }
    }

    /**
     * Report errors associated with a ReturnStmt ASTnode
     *
     * @param returnStmt     Field node to check
     * @param expectedType   The expected return type for the given return stmt
     *                       (should be void or a valid type)
     * */
    public void reportReturnStmtErrors(ReturnStmt returnStmt, String expectedType){
        // Check return type matches expected type
        if(expectedType.equals("void") && returnStmt.getExpr() != null){
            this.err.register(this.err.SEMANT_ERROR, this.currFile, returnStmt.getLineNum(),
                    "Cannot return value from void method");
        }
        else{
            String returnType = returnStmt.getExpr() == null ? "void" : returnStmt.getExpr().getExprType();
            reportSubtypeError(expectedType, returnType, returnStmt.getLineNum());
        }
    }

    /**
     * Report errors associated with a DispatchExpr ASTnode
     *
     * @param dispatchExpr     DispatchExpr node to check
     * */
    public void reportDispatchExprErrors(DispatchExpr dispatchExpr){
        // Check reference type is not primitive
        //  If primitive type, report special error indicating that and quit
        Expr ref = dispatchExpr.getRefExpr();
        ClassTreeNode callClass = this.currClass;

        if(ref != null){

            String refType = ref.getExprType();

            if (this.primitiveTypes.contains(ref.getExprType())){

                this.err.register(this.err.SEMANT_ERROR, this.currFile, dispatchExpr.getLineNum(),
                        refType + " cannot be dereferenced.");

                return;
            }
            else{
                callClass = refType.endsWith("[]") ? this.classMap.get("Object") : this.classMap.get(refType);
            }
        }

        String methodName = dispatchExpr.getMethodName();
        Method method = (Method)callClass.getMethodSymbolTable().lookup(methodName);

        if(method == null){
            this.err.register(this.err.SEMANT_ERROR, this.currFile, dispatchExpr.getLineNum(),
                    "Class " + callClass.getName() + " has no member " + methodName + ".");
            return;
        }

        FormalList formals = method.getFormalList();
        ExprList actuals = dispatchExpr.getActualList();

        if(actuals.getSize() != formals.getSize()){

            // building FormalList types
            StringBuilder formalTypes = new StringBuilder("(");
            String sep = ", ";
            formals.forEach(f -> formalTypes.append( ((Formal) f).getType() ).append(sep));

            int end = formalTypes.length() > 1 ? formalTypes.length() - sep.length() : 0;
            formalTypes.substring(0, end);

            formalTypes.append(")");

            // building ActualList types
            StringBuilder actualTypes = new StringBuilder("(");

            actuals.forEach(a -> actualTypes.append(((Expr) a).getExprType()).append(sep));

            end = actualTypes.length() > 1 ? actualTypes.length() - sep.length() : 0;
            actualTypes.substring(0, end);

            actualTypes.append(")");

            this.err.register(this.err.SEMANT_ERROR, this.currFile, dispatchExpr.getLineNum(),
                    "Method " + methodName + " in class " + callClass.getName() +
                            " cannot be applied to given types.\n" +
                            formalTypes.toString() + "\n" + actualTypes.toString() +
                            "\nreason: actual and formal list differ in length.");
            return;
        }

        for(int i=0; i<formals.getSize(); i++){
            String expected  = ((Formal)formals.get(i)).getType();
            String actual = ((Expr)actuals.get(i)).getExprType();
            // Check parameter types match expected types
            reportSubtypeError(expected, actual, dispatchExpr.getLineNum());
        }
    }

    /**
     * Report errors associated with a NewExpr ASTnode
     *
     * @param newExpr     NewExpr node to check
     * */
    public void reportNewExprErrors(NewExpr newExpr){
        // check type existence
        String type = newExpr.getType();
        reportTypeNonexistence(type, newExpr.getLineNum());
    }

    /**
     * Report errors associated with a NewArrayExpr ASTnode
     *
     * @param newArrayExpr     NewArrayExpr node to check
     * */
    public void reportNewArrayExprErrors(NewArrayExpr newArrayExpr){
        // check type existence
        String type = newArrayExpr.getType();
        reportTypeNonexistence(type, newArrayExpr.getLineNum());

        // check size expr is type int
        reportSubtypeError("int", newArrayExpr.getSize().getExprType(), newArrayExpr.getLineNum());
    }

    /**
     * Report errors associated with a InstanceofExpr ASTnode
     *
     * @param instanceofExpr     InstanceofExpr node to check
     * */
    public void reportInstanceofExprErrors(InstanceofExpr instanceofExpr){
        reportConvertingExpressionError(instanceofExpr.getType(),
                instanceofExpr.getExpr().getExprType(),
                instanceofExpr.getLineNum());


    }

    /**
     * Report errors associated with a CastExpr ASTnode
     *
     * @param castExpr     CastExpr node to check
     * */
    public void reportCastExprErrors(CastExpr castExpr){
        reportConvertingExpressionError(castExpr.getType(),
                castExpr.getExpr().getExprType(),
                castExpr.getLineNum());
    }

    /**
     * Report errors associated with a VarExpr ASTnode
     *
     * @param varExpr     VarExpr node to check
     * */
    public void reportVarExprErrors(VarExpr varExpr){
        // If ref expr is null
        //  Check name is either this, super, null, or exists
        VarExpr ref = (VarExpr)varExpr.getRef();
        if(ref == null){
            String name = varExpr.getName();
            if(!(name.equals("this") || name.equals("super") || name.equals("null"))){
                reportVariableNonexistence(name, varExpr.getLineNum());
            }
        }
        else{
            if(ref.getName().equals("null")){
                // If ref is null, eport error
                this.err.register(this.err.SEMANT_ERROR, this.currFile, varExpr.getLineNum(),
                        "Cannot dereference null");
            }
            else if(ref.getName().equals("this") || ref.getName().equals("super")){
                // If ref is this or super
                //  Check name exists as field in corresponding class
                ClassTreeNode lookupClass = ref.getName().equals("this") ?
                        this.currClass : this.currClass.getParent();
                reportFieldNonexistence(varExpr.getName(), lookupClass, varExpr.getLineNum());
            }
            else if(!(ref.getExprType().endsWith("[]") && varExpr.getName().equals("length"))){
                // If ref is array type
                //  Check that name is length
                this.err.register(this.err.SEMANT_ERROR, this.currFile, varExpr.getLineNum(),
                        "Cannot access members of " + ref.getName() + ".");

            }
        }

    }

    /**
     * Report errors associated with a ArrayExpr ASTnode
     *
     * @param arrayExpr     ArrayExpr node to check
     * */
    public void reportArrayExprErrors(ArrayExpr arrayExpr){
        // Check index is of type int
        reportSubtypeError("int", arrayExpr.getIndex().getExprType(), arrayExpr.getLineNum());


        VarExpr ref = (VarExpr)arrayExpr.getRef();
        if(ref == null) {
            // If ref is null
            //  Check name exists and is of type array
            reportVariableNonexistence(arrayExpr.getName(), arrayExpr.getLineNum());
            if(this.currClass.getVarSymbolTable().getScopeLevel(arrayExpr.getName()) >= 0){
                String expectedType = (String)this.currClass.getVarSymbolTable().lookup(arrayExpr.getName());
                if(!expectedType.endsWith("[]")){
                    this.err.register(this.err.SEMANT_ERROR, this.currFile, arrayExpr.getLineNum(),
                            "Array required, but found " + expectedType + ".");
                }
            }
        }
        else if(ref.getName().equals("this") || ref.getName().equals("super")){
            // If ref is this or super
            //  Check name exists as field and is of type array
            ClassTreeNode lookupClass = ref.getName().equals("this") ?
                    this.currClass : this.currClass.getParent();
            reportFieldNonexistence(arrayExpr.getName(), lookupClass, arrayExpr.getLineNum());
            if(lookupClass.getVarSymbolTable().getScopeLevel(arrayExpr.getName()) == 0){
                String expectedType = (String)lookupClass.getVarSymbolTable().lookup(arrayExpr.getName(), 0);
                if(!expectedType.endsWith("[]")){
                    this.err.register(this.err.SEMANT_ERROR, this.currFile, arrayExpr.getLineNum(),
                            "Array required, but found " + expectedType + ".");
                }
            }

        }
        else{
            this.err.register(this.err.SEMANT_ERROR, this.currFile, arrayExpr.getLineNum(),
                    "Cannot dereference " + ref.getName() + ".");
        }

    }

    /**
     * Report errors associated with a AssignExpr ASTnode
     *
     * @param assignExpr     AssignExpr node to check
     * */
    public void reportAssignExprErrors(AssignExpr assignExpr){
        // Check ref is this or super if exists
        String refName = assignExpr.getRefName();
        String name = assignExpr.getName();
        int lineNum = assignExpr.getLineNum();
        if(refName != null && !(refName.equals("this") || refName.equals("super"))){
            this.err.register(this.err.SEMANT_ERROR, this.currFile, lineNum,
                    "Cannot assign to members of " + refName + ".");
            return;
        }

        // Check name exists (depends on what ref is whether to look at fields or local vars)
        String expectedType = getExpectedTypeForAssign(refName, name, lineNum);


        // Check assign type matches expected type
        reportSubtypeError(expectedType, assignExpr.getExpr().getExprType(), assignExpr.getLineNum());
    }

    /**
     * Report errors associated with a ArrayAssignExpr ASTnode
     *
     * @param arrayAssignExpr     ArrayAssignExpr node to check
     * */
    public void reportArrayAssignExprErrors(ArrayAssignExpr arrayAssignExpr){
        // Check ref is this or super if exists
        String refName = arrayAssignExpr.getRefName();
        String name = arrayAssignExpr.getName();
        int lineNum = arrayAssignExpr.getLineNum();
        if(refName != null && !(refName.equals("this") || refName.equals("super"))){
            this.err.register(this.err.SEMANT_ERROR, this.currFile, lineNum,
                    "Cannot assign to members of " + refName + ".");
            return;
        }

        // Check name exists (depends on what ref is whether to look at fields or local vars)
        String expectedType = getExpectedTypeForAssign(refName, name, lineNum);


        // Check type of name is an array type
        if(!expectedType.endsWith("[]")){
            this.err.register(this.err.SEMANT_ERROR, this.currFile, arrayAssignExpr.getLineNum(),
                    "Array required, but found " + expectedType + ".");
        }

        expectedType = expectedType.replace("[]", "");

        // Check index is of type int
        reportSubtypeError("int", arrayAssignExpr.getIndex().getExprType(), arrayAssignExpr.getLineNum());


        // Check assign type matches base type of name
        reportSubtypeError(expectedType, arrayAssignExpr.getExpr().getExprType(), arrayAssignExpr.getLineNum());
    }

    // --------------------------------------------------------------------------
    // Declaration and Name error reporting
    // --------------------------------------------------------------------------

    /**
     * Report necessary errors if the given method is not a valid declaration
     * @param method        Method ASTnode to check if validly declared
     * */
    public void reportMethodDeclarationErrors(Method method){
        String name = method.getName();
        int lineNum = method.getLineNum();
        this.reportReserveWordConflict("Method", name, lineNum);
        this.reportClassNameConflict("Method", name, lineNum);
        if(this.currClass.getMethodSymbolTable().peek(name) != null){
            this.err.register(this.err.SEMANT_ERROR, this.currFile, lineNum,
                    "Method " + name + " already defined in " + this.currClass.getName() + ".");
        }
        Method parentMethod = (this.currClass.getParent() == null) ? null : (Method)this.currClass.getParent().getMethodSymbolTable().lookup(name);

        if(parentMethod != null){
            boolean registerError = !parentMethod.getReturnType().equals(method.getReturnType());
            registerError |= method.getFormalList().getSize() != parentMethod.getFormalList().getSize();
            if(!registerError){
                for(int i=0; i<method.getFormalList().getSize(); i++){
                    Formal A = (Formal)method.getFormalList().get(i);
                    Formal B = (Formal)parentMethod.getFormalList().get(i);
                    registerError |= !A.getType().equals(B.getType());
                }
            }
            if(registerError){
                this.err.register(this.err.SEMANT_ERROR, this.currFile, lineNum,
                        "Method " + name + " already defined in super class and has non-matching signature.");
            }
        }

    }

    /**
     * Report necessary errors if the given field name is not valid.
     * @param name String name of field
     * @param lineNum int corresponding line in program
     */
    public void reportFieldNameErrors(String name, int lineNum) {

        this.reportReserveWordConflict("field", name, lineNum);

        this.reportClassNameConflict("field", name, lineNum);

        int scope = this.currClass.getVarSymbolTable().getCurrScopeLevel()-1;

        this.reportVarSymbolTableAtScopeConflict("field", name, scope, lineNum,0);
    }

    /**
     * Report necessary errors if the given variable name is not a valid declaration.
     * @param name String name of variable
     * @param lineNum int corresponding line in program
     */
    public void reportVariableNameErrors(String name, int lineNum) {

        this.reportReserveWordConflict("variable", name, lineNum);
        this.reportClassNameConflict("variable", name, lineNum);

        SymbolTable symbolTable = this.currClass.getVarSymbolTable();

        for (int i = symbolTable.getCurrScopeLevel() - 1; i >= this.currClassFieldScopeLevel; i--) {
            reportVarSymbolTableAtScopeConflict("variable", name, i, lineNum,i);
        }
    }


    /**
     * Report an error if the given class name is not a valid declaration
     * @param name      Name of the class to check
     * @param lineNum   Line number to report the error on
     * */
    public void reportClassNameErrors(String name, int lineNum) {
        this.reportReserveWordConflict("class", name, lineNum);
        this.reportClassNameConflict("class", name, lineNum);
    }


    // --------------------------------------------------------------------------
    // Conflict error reporting
    // --------------------------------------------------------------------------

    /**
     * Report an error if the given name conflicts with a reserved word
     *
     * @param context string describes what aspect of program is being checked
     * @param name String name to check
     * @param lineNum int line number to report the error on
     */
    private void reportReserveWordConflict(String context, String name, int lineNum) {
        if (this.reservedWords.contains(name)) {
            this.err.register(this.err.SEMANT_ERROR, this.currFile, lineNum,
                    "Cannot use reserve word for " + context + " name: " + name);
        }
    }

    /**
     * Report an error if the given name conflicts with a declared class
     *
     * @param context string describes what aspect of program is being checked
     * @param name String name to check
     * @param lineNum int line number of program that corresponds to the context
     */
    private void reportClassNameConflict(String context, String name, int lineNum) {
        if (this.classMap.containsKey(name)) {
            this.err.register(this.err.SEMANT_ERROR, this.currFile, lineNum,
                    context + " name " + name + " is already assigned to a class.");
        }
    }

    /**
     * Reports an error if the given name conflicts with a defined variable at the given
     * scope.
     *
     * @param context string describes what aspect of program is being checked
     * @param name String name to check
     * @param scope int scope of varSymbolTable to check
     * @param lineNum int line number of program that corresponds to the context
     */
    private void reportVarSymbolTableAtScopeConflict(String context, String name, int scope, int lineNum, int loop) {
        if (this.currClass.getVarSymbolTable().peek(name, scope) != null) {
            this.err.register(this.err.SEMANT_ERROR, this.currFile, lineNum,
                    "Name " + name + "is already defined in the " + context + "'s scope." + loop);
        }
    }

    // --------------------------------------------------------------------------
    // Existence error reporting
    // --------------------------------------------------------------------------
    /**
     * Report an error if the given type does not exist.
     *
     * @param type the given type
     * @param lineNum the number of line that type occurs in the program
     */
    public void reportTypeNonexistence(String type, int lineNum) {
        if (!typeExists(type)) {
            this.err.register(this.err.SEMANT_ERROR, this.currFile, lineNum,
                    "Type " + type + " does not exist.");
        }

    }

    /**
     * Report an error if the given class name does not exists, or if the classname
     * of a primitive type.
     * @param className the given class name
     * @param lineNum the number of line that the class name occurs
     */
    public void reportClassNonexistence(String className, int lineNum) {
        if (this.primitiveTypes.contains(className)) {
            this.err.register(this.err.SEMANT_ERROR, this.currFile, lineNum,
                    className + " is of primitive type.");
        }
        else if (!this.classMap.containsKey(className)) {
            this.err.register(this.err.SEMANT_ERROR, this.currFile, lineNum,
                    "Class "+ className + " does not exist.");
        }
    }

    /**
     * Report an error if the given variable does not exist.
     *
     * @param varName the given variable
     * @param lineNum the number of line that variable occurs
     */
    public void reportVariableNonexistence(String varName, int lineNum) {
        if (this.currClass.getVarSymbolTable().getScopeLevel(varName) < 0) {
            this.err.register(this.err.SEMANT_ERROR, this.currFile, lineNum,
                    "Variable " + varName + " not found.");
        }
    }

    /**
     * Report an error if the given field does not exist
     *
     * @param fieldName the given field
     * @param class_ the class of the field
     * @param lineNum the line number of the field
     */
    void reportFieldNonexistence(String fieldName, ClassTreeNode class_, int lineNum) {
        if (class_.getVarSymbolTable().lookup(fieldName) == null) {
            this.err.register(this.err.SEMANT_ERROR, this.currFile, lineNum,
                    "Field " + fieldName + " not found in " + class_.getName() + " and parents.");
        }
    }



    // --------------------------------------------------------------------------
    // Type error reporting
    // --------------------------------------------------------------------------

    /**
     * Report an error if the given specific type is not a subtype of the general
     * type.
     *
     * @param general       General type to check
     * @param specific      Specific type to check
     * @param lineNum       Line number to report the error on
     * */
    private void reportSubtypeError(String general, String specific, int lineNum){
        if(!isSubtype(general, specific)){
            this.err.register(this.err.SEMANT_ERROR, this.currFile, lineNum,
                    "Mismatched type: expected " + general + ", found " + specific);
        }
    }


    /**
     * Report an error if the given converting type can be converted to the given
     * convertTo type
     * @param convertTo     Target type of conversion
     * @param converting    Src type of converting
     * @param lineNum       Line number to report the error on
     * */
    private void reportConvertingExpressionError(String convertTo, String converting, int lineNum) {
        reportTypeNonexistence(convertTo, lineNum);

        if(!this.classMap.containsKey(convertTo) && !this.primitiveTypes.contains(convertTo)){
            convertTo = "Object";
        }

        if(this.primitiveTypes.contains(convertTo) || this.primitiveTypes.contains(converting)){
            this.err.register(this.err.SEMANT_ERROR, this.currFile, lineNum,
                    "Cannot convert primitive types.");
            return;
        }
        if(!(isSubclass(convertTo, converting) || isSubclass(converting, convertTo))){
            this.err.register(this.err.SEMANT_ERROR, this.currFile, lineNum,
                    "Cannot convert " + converting + " to " + convertTo);
        }
    }


    /**
     * Report an error if the types do not match the gievn unary expression operation
     *
     * @param expected expected type of the operation
     * @param actual actual type of the operation
     * @param operation the given operation
     * @param lineNum line number of that operation
     */
    public void reportUnaryOperationTypeError(String expected, String actual, String operation, int lineNum) {

        if (!this.isSubtype(expected, actual)) {
            this.err.register(this.err.SEMANT_ERROR, this.currFile, lineNum,
                    "Unary Operation " + operation + " can only be applied to type " + expected + ".");
        }

    }


    /**
     * Report an error if the given types do not match the operand type for the given
     * binary operation.
     *
     * @param left type of left operand
     * @param right type of right operand
     * @param operandType the expected operand
     * @param operation the given operation
     * @param lineNum the line number of the operation
     */
    public void reportBinaryOperandTypeError(String left, String right, String operandType,
                                             String operation, int lineNum) {

        if (!isSubtype(operandType, left)) {
            this.err.register(this.err.SEMANT_ERROR, this.currFile, lineNum,
                    "Type " + left + " is incompatible with operation " + operation +
                            ". Must be of type " + operandType + ".");
        }

        if (!isSubtype(operandType, right)) {
            this.err.register(this.err.SEMANT_ERROR, this.currFile, lineNum,
                    "Type " + right + " is incompatible with operation " + operation +
                            ". Must be of type " + operandType + ".");
        }

    }

    /**
     * Report an error if the given left and right operands are not valid types for the
     * given binary operation.
     *
     * @param left left operand
     * @param right right operand
     * @param operation the given operation
     * @param lineNum the line number of the operation
     */
    public void checkBinaryExpressionTypeCompatible(String left, String right, String operation, int lineNum) {

        if(left.equals("null") || right.equals("null")){
            return;
        }
        if (!(isSubtype(left, right) || isSubtype(right, left))) {
            this.err.register(this.err.SEMANT_ERROR, this.currFile, lineNum,
                    "Incompatible types for Binary Operation " + operation + ": " + left + " and " + right + ".");
        }

    }

    // --------------------------------------------------------------------------
    // Type related helper methods
    // --------------------------------------------------------------------------

    /**
     * Test if a given specific type is a subtype of the given general type
     * @param general       General type to check
     * @param specific      Specific type to check
     * @return              If the specific type is a subtype of the generic type
     * */
    private boolean isSubtype(String general, String specific){
        return isSubclass(general, specific) || general.equals(specific);
    }

    /**
     * Determines if the given child is a subclass of the given parent. Will return false
     * if either type is primitive.
     *
     * @param parent classname of parent class
     * @param child classname of child class
     * @return if child is a subclass of parent
     */
    public boolean isSubclass(String parent, String child){
        if (this.primitiveTypes.contains(parent) || this.primitiveTypes.contains(child)) return false;
        // Handles when both are primitive arrays
        if (parent.equals(child)){
            return true;
        }

        if (parent.endsWith("[]") && child.endsWith("[]")){
            parent = parent.replace("[]", "");
            child = child.replace("[]", "");
        }
        else if (child.endsWith("[]")){
            return parent.equals("Object");
        }
        else if (parent.endsWith("[]")){
            return false;
        }

        ClassTreeNode tempClass = this.classMap.get(child);

        while(tempClass != null){
            if(tempClass.getName().equals(parent)){
                return true;
            }
            tempClass = tempClass.getParent();
        }
        // This allows null to be a subtype of any Object
        return child.equals("null");
    }

    /**
     * Determine if the given type has been declared.
     *
     * @param type      The type to determine if exists
     * @return          If the type exists
     * */
    public boolean typeExists(String type){
        String modType = type.endsWith("[]") ? type.replace("[]", "") : type;
        return this.classMap.containsKey(modType) || this.primitiveTypes.contains(modType);
    }

    /**
     * Get the expected type relative to a reference name that can be this or super and
     * a variable/field name. Reports any errors that are found.
     *
     * @param refName       reference name (this or super)
     * @param name          Name to get the type of
     * @param lineNum       Line number of the calling expression, used for error reporting
     * @return              The declared type of name, or Object if name is not declared
     * */
    private String getExpectedTypeForAssign(String refName, String name, int lineNum) {
        String expectedType;
        if(refName == null){
            reportVariableNonexistence(name, lineNum);
            if(this.currClass.getVarSymbolTable().getScopeLevel(name) < 0){
                expectedType = "Object";
            }
            else{
                expectedType = (String)this.currClass.getVarSymbolTable().lookup(name);
            }
        }
        else{
            ClassTreeNode lookupClass = refName.equals("this") ? this.currClass : this.currClass.getParent();
            reportFieldNonexistence(name, lookupClass, lineNum);
            if(lookupClass.getVarSymbolTable().lookup(name) == null){
                expectedType = "Object";
            }
            else{
                expectedType = (String)lookupClass.getVarSymbolTable().lookup(name);
            }
        }
        return expectedType;
    }

}
