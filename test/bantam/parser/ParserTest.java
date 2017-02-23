package bantam.parser;

import bantam.ast.*;
import java_cup.runtime.Symbol;
import bantam.lexer.Lexer;
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
public class ParserTest
{
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
        Method method = getMethod("class Main{void test(){int test = 1; test--;}}");
        assertTrue(method.getStmtList().get(1) instanceof UnaryDecrExpr);
    }

    /**
     * Tests the UnaryIncrExpr
     * @throws Exception
     */
    @Test
    public void unaryPostIncrExprTest() throws Exception{
        Method method = getMethod("class Main{void test(){int test = 1; test++;}}");
        assertTrue(method.getStmtList().get(1) instanceof UnaryIncrExpr);
    }

    /**
     * Tests the unaryDecrExpr for Pre Unary ops
     * @throws Exception
     */
    @Test
    public void unaryPreDecrExprTest() throws Exception{
        Method method = getMethod("class Main{void test(){int test = 1; --test;}}");
        assertTrue(method.getStmtList().get(1) instanceof UnaryDecrExpr);
    }

    /**
     * Tests the UnaryIncrExpr for Post Unary Ops
     * @throws Exception
     */
    @Test
    public void unaryPreIncrExprTest() throws Exception{
        Method method = getMethod("class Main{void test(){int test = 1; ++test;}}");
        assertTrue(method.getStmtList().get(1) instanceof UnaryIncrExpr);
    }

    /**
     * Tests the UnaryIncrExpr for Post Unary Ops
     * @throws Exception
     */
    @Test
    public void unaryNotExpr() throws Exception{
        Method method = getMethod("class Main{void test(){boolean test != true;}}");
        assertTrue(method.getStmtList().get(1) instanceof UnaryIncrExpr);
    }

    /**
     * Tests the UnaryIncrExpr for Post Unary Ops
     * @throws Exception
     */
    @Test
    public void unaryNegExpr() throws Exception{
        Method method = getMethod("class Main{void test(){int test = -1; }}");
        assertTrue(method.getStmtList().get(1) instanceof UnaryIncrExpr);
    }

    /**
     * Gets the first statement
     * @param s the java program string
     * @param index
     * @return
     */
    @Test
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