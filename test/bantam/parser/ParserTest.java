package bantam.parser;

import bantam.ast.*;
import com.sun.tools.internal.jxc.ap.Const;
import java_cup.runtime.Symbol;
import bantam.lexer.Lexer;
import jdk.nashorn.internal.ir.Block;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import bantam.util.ErrorHandler;

import java.io.StringReader;

import static org.junit.Assert.*;

/*
 * File: ParserTest.java
 * Author: djskrien
 * Date: 2/13/17
 */
public class ParserTest {
    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @BeforeClass
    public static void begin() {
        /* add here any initialization code for all test methods. For example,
         you might want to initialize some fields here. */
    }

    /** tests the case of a Main class with no members */
    @Test
    public void emptyMainClassTest() throws Exception {
        Parser parser = new Parser(new Lexer(new StringReader("class Main {  }")));
        Symbol result = parser.parse();
        assertEquals(0, parser.getErrorHandler().getErrorList().size());
        assertNotNull(result);
        ClassList classes = ((Program) result.value).getClassList();
        assertEquals(1, classes.getSize());
        Class_ mainClass = (Class_) classes.get(0);
        assertEquals("Main", mainClass.getName());
        assertEquals(0, mainClass.getMemberList().getSize());
    }

    /**
     * tests the case of a missing right brace at end of a class def
     * using an ExpectedException Rule
     */
    @Test
    public void unmatchedLeftBraceTest1() throws Exception {
        Parser parser = new Parser(new Lexer(new StringReader("class Main {  ")));
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Bantam parser found errors.");
        parser.parse();
    }

    /**
     * tests the case of a missing right brace at end of a class def.
     * This version is like unmatchedLeftBraceTest1 except that it
     * doesn't use an ExpectedException Rule and it also prints the error messages.
     */
    @Test
    public void unmatchedLeftBraceTest2() throws Exception {
        Parser parser = new Parser(new Lexer(new StringReader("class Main {  ")));
        boolean thrown = false;

        try {
            parser.parse();
        } catch (RuntimeException e) {
            thrown = true;
            assertEquals("Bantam parser found errors.", e.getMessage());
            for (ErrorHandler.Error err : parser.getErrorHandler().getErrorList()) {
                System.out.println(err);
            }
        }
        assertTrue(thrown);
    }

    @Test
    /**
     * Tests the case of a method
     */
    public void emptyMethodTest() throws Exception{
        Method method = getMethod("class Main{ int myInt(){ }}");
        assertEquals("myInt", method.getName());
    }

    /**
     * Tests an empty method with parameters
     * @throws Exception
     */
    @Test
    public void parameterizedMethodTest() throws Exception {
        Method method = getMethod("class Main{int myInt(int p1, String p2){int x=5; if (true) break;}}");
        ASTNode n1 = method.getFormalList().get(0);
        ASTNode n2 = method.getFormalList().get(1);
        assertTrue(n1 instanceof Formal);
        assertTrue(n2 instanceof Formal);
        Formal formal1 = (Formal) n1;
        Formal formal2 = (Formal) n2;
        assertEquals(formal1.getName(), "p1");
        assertEquals(formal1.getType(), "int");
        assertEquals(formal2.getName(), "p2");
        assertEquals(formal2.getType(), "String");
        method.getStmtList().forEach(a -> System.out.println(a));
    }

    /**
     * Tests all of the statements.
     */
    @Test
    public void statementTest() throws Exception {
        String program = "class Main {" +
                "int myInt(){ ident = 5; type ident = 5; if (true) ident = 5;" +
                "if (false) ident = 5; else ident = 5; while (true) ident = 5;" +
                "for (;;) ident = 5; break; return; return ident = 5;" +
                "{ ident = 5; break; } } }";

        assertTrue(getStmt(program, 0) instanceof ExprStmt);
        assertTrue(getStmt(program, 1) instanceof DeclStmt);
        assertTrue(getStmt(program, 2) instanceof IfStmt);
        assertTrue(getStmt(program, 3) instanceof IfStmt);
        assertTrue(getStmt(program, 4) instanceof WhileStmt);
        assertTrue(getStmt(program, 5) instanceof ForStmt);
        assertTrue(getStmt(program, 6) instanceof BreakStmt);
        assertTrue(getStmt(program, 7) instanceof ReturnStmt);
        assertTrue(getStmt(program, 8) instanceof ReturnStmt);
        assertTrue(getStmt(program, 9) instanceof BlockStmt);



    }

    /**
     * Test Assign Expressions
     * @throws Exception
     */
    @Test
    public void assignExpressionTest() throws Exception{
        String program = "class a{int a(){a = 2;}}";
        assertTrue(((ExprStmt)getStmt(program,0)).getExpr()  instanceof AssignExpr);
    }

    /**
     * Test Dispatch Expressions
     * @throws Exception
     */
    @Test
    public void dispatchExpressionTest() throws Exception{
        String program = "class a{int a(){a = 2;}}";
        assertTrue(((ExprStmt)getStmt(program,0)).getExpr()  instanceof AssignExpr);
    }
    /**
     * Test New Expressions
     * @throws Exception
     */
    @Test
    public void newExpressionTest() throws Exception{
        String program = "class a{int a(){a = 2;}}";
        assertTrue(((ExprStmt)getStmt(program,0)).getExpr()  instanceof AssignExpr);
    }
    /**
     * Test InstanceOf Expressions
     * @throws Exception
     */
    @Test
    public void instanceofExpressionTest() throws Exception{
        String program = "class a{int a(){a instanceof b;}}";
        assertTrue(((ExprStmt)getStmt(program,0)).getExpr()  instanceof InstanceofExpr);
    }
    /**
     * Test Cast Expressions
     * @throws Exception
     */
    @Test
    public void castExpressionTest() throws Exception{
        String program = "class a{int a(){(help)(me);}}";
        assertTrue(((ExprStmt)getStmt(program,0)).getExpr()  instanceof CastExpr);
    }

    /**
     * Test Binary Expressions
     * @throws Exception
     */
    @Test
    public void binaryExpressionTest() throws Exception{
        String program = "class a{int a(){a + b;}}";
        assertTrue(((ExprStmt)getStmt(program,0)).getExpr()  instanceof BinaryExpr);
    }

    /**
     * Test Unary Expressions
     * @throws Exception
     */
    @Test
    public void unaryExpressionTest() throws Exception{
        String program = "class a{int a(){!a;}}";
        assertTrue(((ExprStmt)getStmt(program,0)).getExpr()  instanceof UnaryExpr);
    }

    /**
     * Test Const Expressions
     * @throws Exception
     */
    @Test
    public void constExpressionTest() throws Exception{
        String program = "class a{int a(){2;}}";
        assertTrue(((ExprStmt)getStmt(program,0)).getExpr()  instanceof ConstExpr);
    }

    /**
     * Test Var Expressions
     * @throws Exception
     */
    @Test
    public void varExpressionTest() throws Exception{
        String program = "class a{int a(){a.b;}}";
        assertTrue(((ExprStmt)getStmt(program,0)).getExpr()  instanceof VarExpr);
    }

    /**
     * Test Parentheses Expressions
     * @throws Exception
     */
    @Test
    public void parentheseExpressionTest() throws Exception{
        String program = "class a{int a(){int a = (a)(true);}}";
        assertTrue(((ExprStmt)getStmt(program,0)).getExpr()  instanceof Expr);
    }

    /**
     * Tests the case of a field
     */
    @Test
    public void fieldTest() throws Exception{
        Class_ mainClass = getClass("class Main{int a;}");
        Field field = (Field)mainClass.getMemberList().get(0);
        assertEquals("a", field.getName());
        assertEquals("int", field.getType());
    }

    /**
     * Tests the case of an initialized field
     */
    @Test
    public void initializedFieldTest() throws Exception{
        Class_ mainClass = getClass("class Main{int a = 0;}");
        Field field = (Field)mainClass.getMemberList().get(0);
        assertEquals("a", field.getName());
        assertEquals("int", field.getType());
        ConstIntExpr constInt = (ConstIntExpr)field.getInit();
        assertEquals(0, constInt.getIntConstant());
    }


    /**
     * Tests the unaryDecrExpr
     * @throws Exception
     */
    @Test
    public void unaryPostDecrExprTest() throws Exception{
        Method method = getMethod("class Main{void test(){test--;}}");
        ExprStmt stmt = (ExprStmt)method.getStmtList().get(0);
        Expr expr = stmt.getExpr();
        assertTrue(expr instanceof UnaryDecrExpr);
    }

    /**
     * Tests the UnaryIncrExpr
     * @throws Exception
     */
    @Test
    public void unaryPostIncrExprTest() throws Exception{
        Method method = getMethod("class Main{void test(){test++;}}");
        ExprStmt stmt = (ExprStmt)method.getStmtList().get(0);
        Expr expr = stmt.getExpr();
        assertTrue(expr instanceof UnaryIncrExpr);
    }

    /**
     * Tests the unaryDecrExpr for Pre Unary ops
     * @throws Exception
     */
    @Test
    public void unaryPreDecrExprTest() throws Exception{
        Method method = getMethod("class Main{void test(){--test;}}");
        ExprStmt stmt = (ExprStmt)method.getStmtList().get(0);
        Expr expr = stmt.getExpr();
        assertTrue(expr instanceof UnaryDecrExpr);
    }

    /**
     * Tests the UnaryIncrExpr for Post Unary Ops
     * @throws Exception
     */
    @Test
    public void unaryPreIncrExprTest() throws Exception{
        Method method = getMethod("class Main{void test(){++test;}}");
        ExprStmt stmt = (ExprStmt)method.getStmtList().get(0);
        Expr expr = stmt.getExpr();
        assertTrue(expr instanceof UnaryIncrExpr);
    }

    /**
     * Tests the UnaryIncrExpr for Post Unary Ops
     * @throws Exception
     */
    @Test
    public void unaryNotExpr() throws Exception{
        Method method = getMethod("class Main{void test(){boolean a = !true;}}");
        DeclStmt statement = (DeclStmt)method.getStmtList().get(0);
        Expr expr = statement.getInit();
        assertTrue(expr instanceof UnaryNotExpr);
    }

    /**
     * Tests the UnaryNegateExpression
     * @throws Exception
     */
    @Test
    public void unaryNegExpr() throws Exception{
        Method method = getMethod("class Main{void test(){int test = -1; }}");
        DeclStmt statement = (DeclStmt)method.getStmtList().get(0);
        Expr expr = statement.getInit();
        assertTrue(expr instanceof UnaryNegExpr);
    }

    /**
     * test the binary logic and expression
     * @throws Exception
     */
    @Test
    public void binaryLogicAndExpr() throws Exception{
        Method method = getMethod("class Main{void test(){boolean test = true && true;}}");
        DeclStmt statement = (DeclStmt)method.getStmtList().get(0);
        Expr expr = statement.getInit();
        assertTrue(expr instanceof BinaryLogicAndExpr);
    }

    /**
     * test the binary logic or expression
     * @throws Exception
     */
    @Test
    public void binaryLogicOrExpr() throws Exception{
        Method method = getMethod("class Main{void test(){boolean test = true || true;}}");
        DeclStmt statement = (DeclStmt)method.getStmtList().get(0);
        Expr expr = statement.getInit();
        assertTrue(expr instanceof BinaryLogicOrExpr);
    }

    /**
     * test the binary Comp Expr
     * @throws Exception
     */
    @Test
    public void binaryEqualsExpr() throws Exception{
        BinaryCompExpr binaryCompExpr = getBinaryCompExpr("class Main{void test(){boolean test = true == true;}}");
        assertEquals("==",binaryCompExpr.getOpName());
    }

    /**
     * test the binary less than
     * @throws Exception
     */
    @Test
    public void binaryLessThanExpr() throws Exception{
        BinaryCompExpr binaryCompExpr = getBinaryCompExpr("class Main{void test(){boolean test = true < true;}}");
        assertEquals("<",binaryCompExpr.getOpName());
    }

    /**
     * test the binary greater than
     * @throws Exception
     */
    @Test
    public void binaryGreaterThanExpr() throws Exception{
        BinaryCompExpr binaryCompExpr = getBinaryCompExpr("class Main{void test(){boolean test = true > true;}}");
        assertEquals(">",binaryCompExpr.getOpName());
    }

    /**
     * test the binary greater or equal to expression
     * @throws Exception
     */
    @Test
    public void binaryLessThanOrEqualToExpr() throws Exception{
        BinaryCompExpr binaryCompExpr = getBinaryCompExpr("class Main{void test(){boolean test = true <= true;}}");
        assertEquals("<=",binaryCompExpr.getOpName());
    }

    /**
     * test the binary greater or equal to than
     * @throws Exception
     */
    @Test
    public void binaryGreaterThanOrEqualToExpr() throws Exception{
        BinaryCompExpr binaryCompExpr = getBinaryCompExpr("class Main{void test(){boolean test = true >= true;}}");
        assertEquals(">=",binaryCompExpr.getOpName());
    }


    /**
     * test the binary Not Equal Expr
     * @throws Exception
     */
    @Test
    public void BinaryNotEqualsExpr() throws Exception{
        BinaryCompExpr binaryCompExpr = getBinaryCompExpr("class Main{void test(){boolean test = true != true;}}");
        assertEquals("!=",binaryCompExpr.getOpName());
    }


    /**
     * Returns the binary Comparator expression for a declaration in a method
     * @param s
     * @return
     * @throws Exception
     */
    public BinaryCompExpr getBinaryCompExpr(String s) throws Exception{
        Method method = getMethod(s);
        DeclStmt statement = (DeclStmt)method.getStmtList().get(0);
        Expr expr = statement.getInit();
        return (BinaryCompExpr) expr;
    }

    /**
     * Tests the Add Expression
     */
    @Test
    public void testAddExpr() throws Exception{
        BinaryArithExpr binaryArithExpr = getBinaryArithExpr("class Main{void test(){int test = 1 + 1;}}");
        assertEquals("+",binaryArithExpr.getOpName());
        assertEquals("1", ((ConstIntExpr)binaryArithExpr.getLeftExpr()).getConstant());
        assertEquals("1", ((ConstIntExpr)binaryArithExpr.getRightExpr()).getConstant());
    }

    /**
     * Tests the Minus Expression
     */
    @Test
    public void testMinusExpr() throws Exception{
        BinaryArithExpr binaryArithExpr = getBinaryArithExpr("class Main{void test(){int test = 1 - 2;}}");
        assertEquals("-",binaryArithExpr.getOpName());
        assertEquals("1", ((ConstIntExpr)binaryArithExpr.getLeftExpr()).getConstant());
        assertEquals("2", ((ConstIntExpr)binaryArithExpr.getRightExpr()).getConstant());
    }

    /**
     * Tests the Times Expression
     */
    @Test
    public void testTimesExpr() throws Exception{
        BinaryArithExpr binaryArithExpr = getBinaryArithExpr("class Main{void test(){int test = 1 * 4;}}");
        assertEquals("*",binaryArithExpr.getOpName());
        assertEquals("1", ((ConstIntExpr)binaryArithExpr.getLeftExpr()).getConstant());
        assertEquals("4", ((ConstIntExpr)binaryArithExpr.getRightExpr()).getConstant());
    }

    /**
     * Tests the Times Expression
     */
    @Test
    public void testDivideExpr() throws Exception{
        BinaryArithExpr binaryArithExpr = getBinaryArithExpr("class Main{void test(){int test = 1 / 4;}}");
        assertEquals("/",binaryArithExpr.getOpName());
        assertEquals("1", ((ConstIntExpr)binaryArithExpr.getLeftExpr()).getConstant());
        assertEquals("4", ((ConstIntExpr)binaryArithExpr.getRightExpr()).getConstant());
    }

    /**
     * Tests the Times Expression
     */
    @Test
    public void testModExpr() throws Exception{
        BinaryArithExpr binaryArithExpr = getBinaryArithExpr("class Main{void test(){int test = 1 % 4;}}");
        assertEquals("%",binaryArithExpr.getOpName());
        assertEquals("int",binaryArithExpr.getOpType());
        assertEquals("1", ((ConstIntExpr)binaryArithExpr.getLeftExpr()).getConstant());
        assertEquals("4", ((ConstIntExpr)binaryArithExpr.getRightExpr()).getConstant());
    }

    /**
     * Test the const int expr
     * @throws Exception
     */
    @Test
    public void testConstIntExpr() throws Exception{
        DeclStmt stmt = (DeclStmt)getStmt("class Main{void test(){int test = 1;}}",0);
        ConstIntExpr myIntExpr = (ConstIntExpr)stmt.getInit();
        assertEquals("1",myIntExpr.getConstant());
    }

    /**
     * Test the const String expr
     * @throws Exception
     */
    @Test
    public void testConstStringExpr() throws Exception{
        DeclStmt stmt = (DeclStmt)getStmt("class Main{void test(){int test = \"string\";}}",0);
        ConstStringExpr myStringExpr = (ConstStringExpr)stmt.getInit();
        assertEquals("string",myStringExpr.getConstant());
    }

    /**
     * Test the const boolean expr
     * @throws Exception
     */
    @Test
    public void testConstBooleanExpr() throws Exception{
        DeclStmt stmt = (DeclStmt)getStmt("class Main{void test(){int test = true;}}",0);
        ConstBooleanExpr myBooleanExpr = (ConstBooleanExpr)stmt.getInit();
        assertEquals("TRUE", myBooleanExpr.getConstant());
    }

    /**
     * Test the Cast Expression
     */
    @Test
    public void testCastExpression() throws Exception{
        DeclStmt stmt = (DeclStmt)getStmt("class Main{void test(){int test = (int)(true);}}",0);
        CastExpr castExpr = (CastExpr)stmt.getInit();
        assertEquals("int", castExpr.getType());
        assertEquals("TRUE", ((ConstBooleanExpr)castExpr.getExpr()).getConstant());
    }

    /**
     * Test the instanceof Expression
     */
    @Test
    public void testInstanceOfExpression() throws Exception{
        DeclStmt stmt = (DeclStmt)getStmt("class Main{void test(){boolean test = a instanceof prog;}}",0);
        InstanceofExpr instanceofExpr = (InstanceofExpr)stmt.getInit();
        assertEquals("prog", instanceofExpr.getType());
    }

    /**
     * Test the NewExpr Expression
     */
    @Test
    public void testNewExpression() throws Exception{
        DeclStmt stmt = (DeclStmt)getStmt("class Main{void test(){boolean test = new JohnnyBoy();}}",0);
        NewExpr newExpr = (NewExpr)stmt.getInit();
        assertEquals("JohnnyBoy", newExpr.getType());
    }

    /**
     * Tests the Dispatch Expression
     * @throws Exception
     */
    @Test
    public void testDispatchExpr() throws Exception{
        ExprStmt stmt = (ExprStmt)getStmt("class Main{void test(){a.b();}}",0);
        DispatchExpr dispatchExpr = (DispatchExpr) stmt.getExpr();
        assertEquals("b",dispatchExpr.getMethodName());
        assertEquals("ID",((VarExpr)dispatchExpr.getRefExpr()).getName());
    }

    /**
     * Returns the Binary Arithmetic Expression for the declaration
     * @param s
     * @return
     * @throws Exception
     */
    public BinaryArithExpr getBinaryArithExpr(String s) throws Exception{
        Method method = getMethod(s);
        DeclStmt statement = (DeclStmt)method.getStmtList().get(0);
        Expr expr = statement.getInit();
        return (BinaryArithExpr) expr;
    }


    /**
     * Gets the first statement
     * @param s the java program string
     * @param index
     * @return
     */
    public Stmt getStmt(String s, int index)throws Exception{
        return (Stmt)getMethod(s).getStmtList().get(index);
    }

    public Method getMethod(String s)throws Exception{
        Class_ myClass = getClass(s);
        return (Method)myClass.getMemberList().get(0);
    }

    public Class_ getClass(String s) throws Exception{
        Parser parser = new Parser(new Lexer(new StringReader(s)));
        Symbol result = parser.parse();
        ClassList classes = ((Program) result.value).getClassList();
        return (Class_) classes.get(0);
    }
}
