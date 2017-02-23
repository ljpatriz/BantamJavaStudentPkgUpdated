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

    @Test
    /**
     * Tests the case of a method
     */
    public void emptyMethodTest() throws Exception{
        Parser parser = new Parser(new Lexer(new StringReader("class Main{ int myInt(){ }}")));
        Symbol result = parser.parse();
        ClassList classes = ((Program) result.value).getClassList();
        Class_ mainClass = (Class_) classes.get(0);
        Method method = (Method)mainClass.getMemberList().get(0);
        assertEquals("myInt", method.getName());
    }

    @Test
    /**
     * Tests the case of a field
     */
    public void fieldTest() throws Exception{
        Parser parser = new Parser(new Lexer(new StringReader("class Main{int a;}")));
        Symbol result = parser.parse();
        ClassList classes = ((Program) result.value).getClassList();
        Class_ mainClass = (Class_) classes.get(0);
        Field field = (Field)mainClass.getMemberList().get(0);
        assertEquals("a", field.getName());
        assertEquals("int", field.getType());
    }

    /**
     * Tests the case of an initialized field
     */
    @Test
    public void initializedFieldTest() throws Exception{
        Parser parser = new Parser(new Lexer(new StringReader("class Main{int a = 0;}")));
        Symbol result = parser.parse();
        ClassList classes = ((Program) result.value).getClassList();
        Class_ mainClass = (Class_) classes.get(0);
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
    public void unaryDecrExprTest() throws Exception{
        Parser parser = new Parser(new Lexer(new StringReader("class Main{void test(){int test = 1; test--;}}")));
        Symbol result = parser.parse();
        ClassList classes = ((Program) result.value).getClassList();
        Class_ mainClass = (Class_) classes.get(0);
        Method method = (Method)mainClass.getMemberList().get(0);
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
}