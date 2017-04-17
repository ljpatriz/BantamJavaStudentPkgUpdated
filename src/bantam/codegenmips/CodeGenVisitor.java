package bantam.codegenmips;

import bantam.ast.*;
import bantam.util.ClassTreeNode;
import bantam.util.Location;
import bantam.util.SymbolTable;
import bantam.visitor.Visitor;

import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Created by larrypatrizio on 4/4/17.
 */
public class CodeGenVisitor extends Visitor{

    //// TODO: 4/14/2017 Nick - We have to add the syscall to exit at the end of main()

    private MipsSupport assemblySupport;
    private Map<String, String> stringMap;
    private PrintStream out;
    private SymbolTable varSymbolTable;
    private Map<String, Integer> classIndices;
    private Hashtable<String, ClassTreeNode> classMap;
    //// TODO: 4/11/17 ask Dale if we can just put things in MipsSupport, instead of having to pass the PrintStream (Talked to Dale its good to do)
    public CodeGenVisitor(MipsSupport assemblySupport, Map<String, String> stringMap, PrintStream out,
                          Hashtable<String, ClassTreeNode> classMap, Map<String, Integer> classIndices){
        this.assemblySupport = assemblySupport;
        this.stringMap = stringMap;
        this.out = out;
        this.varSymbolTable = new SymbolTable();
        this.classIndices = classIndices;
        this.classMap = classMap;
    }

    //// TODO: 4/11/17 Larry - So basically the structure is stupid and we would have to pass MipsCodeGenerator.java (Talked to Dale its good to do)
    //// just for these two methods......
    private void genPop(String destination){
        assemblySupport.genLoadWord(destination, 0, "$sp");
        assemblySupport.genAdd("$sp", "$sp", 4);
    }

    private void genPush(String source) {
        assemblySupport.genAdd("$sp", "$sp", -4);
        assemblySupport.genStoreWord(source, 0, "$sp");
    }
    
    //// TODO: 4/11/17 Nick - Make memory address symbol tables
    /**
     * Visit a program node
     *
     * @param node the program node
     * @return result of the visit
     */
    public Object visit(Program node) {
        node.getClassList().accept(this);
        return null;
    }

    /**
     * Visit a list node of classes
     *
     * @param node the class list node
     * @return result of the visit
     */
    public Object visit(ClassList node) {
        for (ASTNode aNode : node)
            aNode.accept(this);
        return null;
    }

    /**
     * Visit a class node
     *
     * @param node the class node
     * @return result of the visit
     */
    public Object visit(Class_ node) {
        varSymbolTable.enterScope();
        node.getMemberList().accept(this);
        varSymbolTable.exitScope();
        return null;
    }

    /**
     * Visit a list node of members
     *
     * @param node the member list node
     * @return result of the visit
     */
    public Object visit(MemberList node) {
        for (ASTNode child : node)
            child.accept(this);
        return null;
    }

    /**
     * Visit a field node
     *
     * @param node the field node
     * @return result of the visit
     */
    public Object visit(Field node) {
        //// TODO: 4/11/17 Larry - use location class here
        //TODO This belongs in the class initialization subroutine you dingus!
        if (node.getInit() != null) {
            node.getInit().accept(this);
        }
        return null;
    }

    /**
     * Visit a method node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(Method node) {
        //// TODO: 4/11/17 Larry - use location class here
        varSymbolTable.enterScope();
        node.getFormalList().accept(this);
        node.getStmtList().accept(this);
        varSymbolTable.exitScope();
        return null;
    }

    /**
     * Visit a list node of formals
     *
     * @param node the formal list node
     * @return result of the visit
     */
    public Object visit(FormalList node) {
        //// TODO: 4/11/17 Larry - use location class here
        for (Iterator it = node.iterator(); it.hasNext(); ) {
            Formal param = (Formal) it.next();
            Location location = new Location("$fp", varSymbolTable.getCurrScopeSize()*4);
            varSymbolTable.add(param.getName(), location);
        }
        return null;
    }

    /**
     * Visit a declaration statement node
     *
     * @param node the declaration statement node
     * @return result of the visit
     */
    public Object visit(DeclStmt node) {
        node.getInit().accept(this);
        Location location = new Location("$fp", varSymbolTable.getCurrScopeSize()*4);
        varSymbolTable.add(node.getName(), location);
        return null;
    }

    /**
     * Visit an if statement node
     *
     * @param node the if statement node
     * @return result of the visit
     */
    public Object visit(IfStmt node) {
        varSymbolTable.enterScope();
        String afterLabel = assemblySupport.getLabel();
        String elseLabel = assemblySupport.getLabel();
        node.getPredExpr().accept(this);

        assemblySupport.genComment("Execute an If Statement: ");
        assemblySupport.genCondBeq("$v0","$zero", elseLabel);
        node.getThenStmt().accept(this);

        assemblySupport.genUncondBr(afterLabel);

        assemblySupport.genLabel(elseLabel);
        if (node.getElseStmt() != null) {
            node.getElseStmt().accept(this);
        }
        assemblySupport.genLabel(afterLabel);
        varSymbolTable.exitScope();
        return null;
    }

    /**
     * Visit a while statement node
     *
     * @param node the while statement node
     * @return result of the visit
     */
    public Object visit(WhileStmt node) {
        varSymbolTable.enterScope();
        String afterLabel = assemblySupport.getLabel();
        String predLabel = assemblySupport.getLabel();

        // save the return address for break statements
        assemblySupport.genComment("Save the return address and use post-while label for break statements");
        genPush("$ra");
        assemblySupport.genLoadAddr("$ra", afterLabel);

        assemblySupport.genComment("Predicate of while statement:");
        assemblySupport.genLabel(predLabel);

        node.getPredExpr().accept(this);

        assemblySupport.genComment("Leave while statement if predicate is false:");
        assemblySupport.genCondBeq("$v0","$zero",afterLabel);


        assemblySupport.genComment("Body of while statement:");
        node.getBodyStmt().accept(this);

        assemblySupport.genUncondBr(predLabel);

        assemblySupport.genLabel(afterLabel);
        genPop("$ra");
        varSymbolTable.exitScope();
        return null;
    }

    /**
     * Visit a for statement node
     *
     * @param node the for statement node
     * @return result of the visit
     */
    public Object visit(ForStmt node) {
        varSymbolTable.enterScope();
        String afterLabel = assemblySupport.getLabel();
        String predLabel = assemblySupport.getLabel();
        assemblySupport.genComment("Initializing expression of for loop:");
        if (node.getInitExpr() != null) {
            node.getInitExpr().accept(this);
        }

        assemblySupport.genComment("Save the return address and use post-while label for break statements");
        genPush("$ra");
        assemblySupport.genLoadAddr("$ra", afterLabel);

        assemblySupport.genComment("Predicate expression of for loop:");

        assemblySupport.genLabel(predLabel);

        if (node.getPredExpr() != null) {
            node.getPredExpr().accept(this);
        } else {
            assemblySupport.genComment("Always true.");
            assemblySupport.genLoadImm("$v0", 1);
        }

        assemblySupport.genComment("Body expression of for loop:");
        assemblySupport.genCondBeq("$v0", "$zero", afterLabel);
        node.getBodyStmt().accept(this);
        if (node.getUpdateExpr() != null) {
            node.getUpdateExpr().accept(this);
        }

        assemblySupport.genLabel(afterLabel);
        genPop("$ra");
        varSymbolTable.exitScope();
        return null;
    }

    /**
     * Visit a break statement node
     *
     * @param node the break statement node
     * @return result of the visit
     */
    public Object visit(BreakStmt node) {
        assemblySupport.genRetn();
        return null;
    }

    /**
     * Visit a block statement node
     *
     * @param node the block statement node
     * @return result of the visit
     */
    public Object visit(BlockStmt node) {
        varSymbolTable.enterScope();
        node.getStmtList().accept(this);
        varSymbolTable.exitScope();
        return null;
    }

    /**
     * Visit a return statement node
     *
     * @param node the return statement node
     * @return result of the visit
     */
    public Object visit(ReturnStmt node) {
        if (node.getExpr() != null) {
            node.getExpr().accept(this);
        }
        //// TODO: 4/11/17 Larry - Seems too easy, but looks right 
        assemblySupport.genRetn();
        return null;
    }

    /**
     * Visit a dispatch expression node
     *
     * @param node the dispatch expression node
     * @return result of the visit
     */
    public Object visit(DispatchExpr node) {
        if(node.getRefExpr() != null)
            node.getRefExpr().accept(this);
        node.getActualList().accept(this);
        //// TODO: 4/12/17 Larry and Jacob- This uses the location thing??
        return null;
    }

    /**
     * Visit a new expression node
     *
     * @param node the new expression node
     * @return result of the visit
     */
    public Object visit(NewExpr node) {
        assemblySupport.genLoadAddr("$a0", node.getType() + "_template");
        //// TODO: 4/14/2017 prelude
        assemblySupport.genDirCall("Object.clone");
        //// TODO: 4/14/2017 postlude
        assemblySupport.genMove("$a0", "$v0");
        assemblySupport.genDirCall(node.getType() + "_init");

        return null;
    }

    /**
     * Visit an instanceof expression node
     *
     * @param node the instanceof expression node
     * @return result of the visit
     */
    public Object visit(InstanceofExpr node) {
        //Visit the expression
        node.getExpr().accept(this);
        //stored in v0
        //go to that location in memory, plus one
        assemblySupport.genLoadByte("$t0",0,"$v0"); //$t0 is the object value for the instance
        //this number is the class identifier for the object
        //verify that T0 number is greater than
        //the class number, but less than the class number
        //plus the number of children that class has
        int classNumber = classIndices.get(node.getType());
        int classSubtypes = classMap.get(node.getType()).getNumDescendants();
        String setValueToZero = assemblySupport.getLabel();
        String setValueToOne = assemblySupport.getLabel();
        assemblySupport.genLoadImm("$t1",classNumber); //$t1 is the type
        String escape = assemblySupport.getLabel();

        assemblySupport.genComment("Verify that instance class number is greater than the class number");
        assemblySupport.genCondBgt("$t0", "$t1", setValueToZero);
        assemblySupport.genComment("Verify that instance class number is less than the class number plus the number of children");
        assemblySupport.genLoadImm("$t1",classNumber+classSubtypes);
        assemblySupport.genCondBlt("$t0", "$t1",setValueToOne);
        assemblySupport.genUncondBr(setValueToZero);
        //True
        assemblySupport.genLabel(setValueToOne);
        assemblySupport.genLoadImm("$v0",1);
        assemblySupport.genUncondBr(escape);

        //False
        assemblySupport.genLabel(setValueToZero);
        assemblySupport.genLoadImm("$v0",0);

        //Out
        assemblySupport.genLabel(escape);
        //TODO insert less than calls
        //if true, store 1 in v0 otherwise store 0
        return null;
    }

    /**
     * Visit a cast expression node
     *
     * @param node the cast expression node
     * @return result of the visit
     */
    public Object visit(CastExpr node) {
        node.getExpr().accept(this);
        //stored in v0
        assemblySupport.genLoadByte("$t0",0,"$v0"); //$t0 is the object value for the instance
        int classNumber = classIndices.get(node.getType());
        int classSubtypes = classMap.get(node.getType()).getNumDescendants();
        assemblySupport.genLoadImm("$t1",classNumber); //$t1 is the type
        
        String escape = assemblySupport.getLabel();
        assemblySupport.genComment("Verify that instance class number is greater than the class number, otherwise escape");
        assemblySupport.genCondBgt("$t0", "$t1", escape);
        assemblySupport.genComment("Verify that instance class number is less than the class number plus the number of children");
        assemblySupport.genLoadImm("$t1",classNumber+classSubtypes);
        assemblySupport.genCondBlt("$t0", "$t1",escape);
        assemblySupport.genComment("Error, illegal case case");
        assemblySupport.genUncondBr("_class_cast_error");
        //Out
        assemblySupport.genLabel(escape);
        return null;
    }

    /**
     * Visit an assignment expression node
     *
     * @param node the assignment expression node
     * @return result of the visit
     */
    public Object visit(AssignExpr node) {
        node.getExpr().accept(this);
        Location l = (Location)varSymbolTable.lookup(node.getName());
        assemblySupport.genComment("Assignment statement");
        assemblySupport.genStoreWord("$v0", l.getOffset(), l.getBaseReg());
        return null;
    }

    /**
     * Visit a binary comparison equals expression node
     *
     * @param node the binary comparison equals expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompEqExpr node) {
        node.getLeftExpr().accept(this);
        // put $v0 on stack
        genPush("$v0");
        node.getRightExpr().accept(this);
        // pop stack into $v1
        assemblySupport.genComment("Does a Binary Comparison Equality Expression: ");
        genPop("$v1");
        // do an EQ $v1 and $v0
        out.println("\tseq $v0 $v1 $v0");
        return null;
    }

    /**
     * Visit a binary comparison not equals expression node
     *
     * @param node the binary comparison not equals expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompNeExpr node) {
        node.getLeftExpr().accept(this);
        // put $v0 on stack
        genPush("$v0");
        node.getRightExpr().accept(this);
        // pop stack into $v1
        assemblySupport.genComment("Does a Binary Comparision Inequality Expression: ");
        genPop("$v1");
        // do an NE of $v1 and $v0
        out.println("\tsne $v0 $v1 $v0");
        return null;
    }

    /**
     * Visit a binary comparison less than expression node
     *
     * @param node the binary comparison less than expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompLtExpr node) {
        node.getLeftExpr().accept(this);
        // put $v0 on stack
        genPush("$v0");
        node.getRightExpr().accept(this);
        // pop stack into $v1
        assemblySupport.genComment("Does a Binary Comparision Less Than Expression: ");
        genPop("$v1");
        // do an LT $v1 and $v0
        out.println("\tslt $v0 $v1 $v0");
        return null;
    }

    /**
     * Visit a binary comparison less than or equal to expression node
     *
     * @param node the binary comparison less than or equal to expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompLeqExpr node) {
        node.getLeftExpr().accept(this);
        // put $v0 on stack
        genPush("$v0");
        node.getRightExpr().accept(this);
        // pop stack into $v1
        assemblySupport.genComment("Does a Binary Comparision Less Than or Equal Expression: ");
        genPop("$v1");
        // do an LEQ of $v1 and $v0
        out.println("\tsle $v0 $v1 $v0");
        return null;
    }

    /**
     * Visit a binary comparison greater than expression node
     *
     * @param node the binary comparison greater than expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompGtExpr node) {
        node.getLeftExpr().accept(this);
        // put $v0 on stack
        genPush("$v0");
        node.getRightExpr().accept(this);
        // pop stack into $v1
        assemblySupport.genComment("Does a Binary Comparision Greater Than Expression: ");
        genPop("$v1");
        // do an GT of $v1 and $v0
        out.println("\tsgt $v0 $v1 $v0");

        return null;
    }

    /**
     * Visit a binary comparison greater than or equal to expression node
     *
     * @param node the binary comparison greater to or equal to expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompGeqExpr node) {
        node.getLeftExpr().accept(this);
        // put $v0 on stack
        genPush("$v0");
        node.getRightExpr().accept(this);
        // pop stack into $v1
        assemblySupport.genComment("Does a Binary Comparision Greater Than or Equal Expression: ");
        genPop("$v1");
        // do an GEQ of $v1 and $v0
        out.println("\tsge $v0 $v1 $v0");
        return null;
    }

    /**
     * Visit a binary arithmetic plus expression node
     *
     * @param node the binary arithmetic plus expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithPlusExpr node) {
        node.getLeftExpr().accept(this);
        // put $v0 on stack
        genPush("$v0");
        node.getRightExpr().accept(this);
        // pop stack into $v1
        assemblySupport.genComment("Does a Binary Arithmetic Plus Expression: ");
        genPop("$v1");
        // do an ADD of $v1 and $v0 into $v0
        assemblySupport.genAdd("$v0","$v0","v1");
        return null;
    }

    /**
     * Visit a binary arithmetic minus expression node
     *
     * @param node the binary arithmetic minus expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithMinusExpr node) {
        node.getLeftExpr().accept(this);
        // put $v0 on stack
        genPush("$v0");
        node.getRightExpr().accept(this);
        // pop stack into $v1
        assemblySupport.genComment("Does a Binary Arithmetic Minus Expression: ");
        genPop("$v1");

        // checks for divide by zero error, branches to the error register if it does
        assemblySupport.genCondBeq("$v0", "$zero", "_divide_zero");

        // do an SUB of $v1 and $v0 into $v0
        assemblySupport.genSub("$v0","$v0","v1");

        return null;
    }

    /**
     * Visit a binary arithmetic times expression node
     *
     * @param node the binary arithmetic times expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithTimesExpr node) {
        node.getLeftExpr().accept(this);
        // put $v0 on stack
        genPush("$v0");
        node.getRightExpr().accept(this);
        // pop stack into $v1
        assemblySupport.genComment("Does a Binary Arithmetic Multiplication Expression: ");
        genPop("$v1");
        // do an MUL of $v1 and $v0 into $v0
        assemblySupport.genMul("$v0","$v0","v1");

        return null;
    }

    /**
     * Visit a binary arithmetic divide expression node
     *
     * @param node the binary arithmetic divide expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithDivideExpr node) {
        node.getLeftExpr().accept(this);
        // put $v0 on stack
        genPush("$v0");
        node.getRightExpr().accept(this);
        // pop stack into $v1
        assemblySupport.genComment("Does a Binary Arithmetic Divide Expression: ");
        genPop("$v1");
        // do an DIV of $v1 and $v0 into $v0
        assemblySupport.genDiv("$v0","$v0","v1");

        return null;
    }

    /**
     * Visit a binary arithmetic modulus expression node
     *
     * @param node the binary arithmetic modulus expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithModulusExpr node) {
        node.getLeftExpr().accept(this);
        // put $v0 on stack
        genPush("$v0");
        node.getRightExpr().accept(this);
        // pop stack into $v1
        assemblySupport.genComment("Does a Binary Arithmetic Modulus Expression: ");
        genPop("$v1");

        // checks for divide by zero error, branches to the error register if it does
        assemblySupport.genCondBeq("$v0", "$zero", "_divide_zero");


        // do an MOD of $v1 and $v0 into $v0
        assemblySupport.genMod("$v0","$v0","v1");

        return null;
    }

    /**
     * Visit a binary logical AND expression node
     *
     * @param node the binary logical AND expression node
     * @return result of the visit
     */
    public Object visit(BinaryLogicAndExpr node) {
        node.getLeftExpr().accept(this);
        // put $v0 on stack
        genPush("$v0");
        node.getRightExpr().accept(this);
        // pop stack into $v1
        assemblySupport.genComment("Does a Binary Logic And Expression: ");
        genPop("$v1");
        // do an AND of $v1 and $v0 into $v0
        assemblySupport.genAnd("$v0","$v0","v1");
        return null;
    }

    /**
     * Visit a binary logical OR expression node
     *
     * @param node the binary logical OR expression node
     * @return result of the visit
     */
    public Object visit(BinaryLogicOrExpr node) {
        node.getLeftExpr().accept(this);
        // put $v0 on stack
        genPush("$v0");
        node.getRightExpr().accept(this);
        // pop stack into $v1
        assemblySupport.genComment("Does a Binary Logic And Expression: ");
        genPop("$v1");
        // do an OR of $v1 and $v0 into $v0
        assemblySupport.genOr("$v0","$v0","v1");
        return null;
    }

    /**
     * Visit a unary negation expression node
     *
     * @param node the unary negation expression node
     * @return result of the visit
     */
    public Object visit(UnaryNegExpr node) {
        node.getExpr().accept(this);
        assemblySupport.genComment("Does a Unary Negation Expression: ");
        assemblySupport.genMul("$v0", "$v0", -1);
        return null;
    }

    /**
     * Visit a unary NOT expression node
     *
     * @param node the unary NOT expression node
     * @return result of the visit
     */
    public Object visit(UnaryNotExpr node) {
        node.getExpr().accept(this);
        assemblySupport.genComment("Does a Unary Not Expression: ");
        out.println("\tseq $v0 $v0 $zero");
        return null;
    }


    /**
     * Visit a unary increment expression node
     *
     * @param node the unary increment expression node
     * @return result of the visit
     */
    public Object visit(UnaryIncrExpr node) {
        //// TODO: 4/11/17 Larry - use location class here 
        node.getExpr().accept(this);
        return null;
    }

    /**
     * Visit a unary decrement expression node
     *
     * @param node the unary decrement expression node
     * @return result of the visit
     */
    public Object visit(UnaryDecrExpr node) {
        //// TODO: 4/11/17 Larry - use location class here
        Location location = (Location)varSymbolTable.lookup(((VarExpr) node.getExpr()).getName());
        String baseReg = location.getBaseReg();
        int offset = location.getOffset();
        assemblySupport.genLoadWord("$v0", offset, baseReg);

            node.getExpr();

        assemblySupport.genAdd("$v0", "$v0", -1);
        assemblySupport.genStoreWord("$v0", offset, baseReg);
        if(node.isPostfix()) {
            assemblySupport.genAdd("$v0", "$v0", 1);
        }

        return null;
    }

    /**
     * Visit a variable expression node
     *
     * @param node the variable expression node
     * @return result of the visit
     */
    public Object visit(VarExpr node) {
        if (node.getRef() != null) {
            node.getRef().accept(this);
        }
        Location location = (Location)varSymbolTable.lookup(node.getName());
        String baseReg = location.getBaseReg();
        int offset = location.getOffset();
        assemblySupport.genLoadWord("$v0", offset, baseReg);
        return null;
    }

    /**
     * Visit an int constant expression node
     *
     * @param node the int constant expression node
     * @return result of the visit
     */
    public Object visit(ConstIntExpr node) {
        assemblySupport.genLoadImm("$v0", node.getIntConstant());
        return null;
    }

    /**
     * Visit a boolean constant expression node
     *
     * @param node the boolean constant expression node
     * @return result of the visit
     */
    public Object visit(ConstBooleanExpr node) {
        assemblySupport.genLoadImm("$v0", node.getConstant().equals("true") ? 1 : 0);
        return null;
    }

    /**
     * Visit a string constant expression node
     *
     * @param node the string constant expression node
     * @return result of the visit
     */
    public Object visit(ConstStringExpr node) {
        assemblySupport.genLoadAddr("$v0", this.stringMap.get(node.getConstant()));
        return null;
    }
}
