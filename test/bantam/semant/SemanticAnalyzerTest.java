package bantam.semant;

import bantam.ast.Program;
import bantam.lexer.Lexer;
import org.junit.Test;
import bantam.parser.Parser;
import bantam.util.ErrorHandler;

import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
/*
 * File: SemanticAnalyzerTest.java
 * Author: djskrien
 * Date: 2/13/17
 */
public class SemanticAnalyzerTest
{
    /**
     * String of the directory path to the semantic analyzer test files
     */
    private String testDirectory = System.getProperty("user.dir") +
                    "/testfiles/SemanticAnalyzerTestFiles/";

    /**
     * Receives a filename and directory (constructed by using the above
     * private testDirectory variable) and then creates a string of the
     * entire file. This was taken off of StackOverFlow at the following
     * website address:
     * http://stackoverflow.com/questions/22019296/java-read-write-a-file-and-more
     *
     * @param fileName - string filename (includes the full directory)
     * @return - returns a string of the entire file
     * @throws IOException - IOException that can occur during string building
     */
    private String readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }

    /**
     * Receives a string of the file contents and then runs the semantic analyzer
     * on it. It will catch any errors thrown by the analyzer and will then also
     * print out the errors. It will return a boolean (false for no errors; true
     * for errors found) for the JUnit test asserts.
     *
     * @param fileContents - the file contents in string form
     * @return - returns a boolean indicating whether errors where found
     * @throws Exception - RuntimeException thrown by the analyzer
     */
    private boolean testThrown(String fileContents) throws Exception {
        boolean thrown = false;
        Parser parser = new Parser(new Lexer(new StringReader(
                readFile(testDirectory+fileContents))));
        Program program = (Program) parser.parse().value;
        SemanticAnalyzer analyzer = new SemanticAnalyzer(program, true);
        try {
            analyzer.analyze();
        } catch (RuntimeException e) {
            thrown = true;
            e.printStackTrace();
            for (ErrorHandler.Error err : analyzer.getErrorHandler().getErrorList()) {
                System.out.println(err);

            }
            assertEquals("Bantam semantic analyzer found errors.", e.getMessage());
        }
        return thrown;
    }

    /**
     * Receives a string of the file contents and then passes that onto testThrown.
     * It will then run an assertFalse test on the resulting boolean, false is the
     * desired result which indicates no errors found.
     *
     * @param filename - the file contents in string form
     * @throws Exception - RuntimeException thrown by the analyzer
     */
    private void expectNoError(String filename) throws Exception{
        assertFalse(testThrown(filename));
    }

    /**
     * Receives a string of the file contents and then passes that onto testThrown.
     * It will then run an assertTrue test on the resulting boolean, true is the
     * desired result which indicates errors were found.
     *
     * @param filename - the file contents in string form
     * @throws Exception - RuntimeException thrown by the analyzer
     */
    private void testErrorFile(String filename) throws Exception{
        assertTrue(testThrown(filename));
    }

    /**
     * Tests method creation
     */
    @Test
    public void testMethodCreation() throws Exception {
        expectNoError("testMethodCreation.btm");
    }

    /** Tests the case of a Main class with only a main.  This is legal
     * because a Bantam Java program must have a Main class with a main
     * method. */
    @Test
    public void testEmptyMainClass() throws Exception {
        expectNoError("testEmptyMainClass.btm");
    }

    /** Tests the case of a Main class with no members.  This is illegal
     * because a Bantam Java program must have a Main class with a main
     * method. */
    @Test
    public void testEmptyMainClassError() throws Exception {
        testErrorFile("testEmptyMainClassError.btm");
    }

    /**
     * Tests the case of no repeated class names. This is legal.
     */
    @Test
    public void testRepeatedClassName() throws Exception {
        expectNoError("testRepeatedClassName.btm");
    }

    /**
     * Tests the case of a no repeated class name. This is illegal.
     */
    @Test
    public void testRepeatedClassNameError() throws Exception {
        testErrorFile("testRepeatedClassNameError.btm");
    }

    /**
     * Tests the case of a legal DeclStmt.
     */
    @Test
    public void testDeclStmt() throws Exception{
        expectNoError("testDeclStmt.btm");
    }

    /**
     * Tests the case of an illegal DeclStmt.
     */
    @Test
    public void testDeclStmtError() throws Exception{
        testErrorFile("testDeclStmtError.btm");
    }

    /**
     * Tests the case of a legal AssignExpr.
     */
    @Test
    public void testAssignExpr() throws Exception{
        expectNoError("testAssignExpr.btm");
    }

    /**
     * Tests the case of an illegal AssignExpr.
     */
    @Test
    public void testAssignExprError() throws Exception{
        testErrorFile("testAssignExprError.btm");
    }

    /**
     * Tests the case of a valid ArrayExpr
     */
    @Test
    public void testArrayExpr() throws Exception{
        expectNoError("testArrayExpr.btm");
    }

    /**
     * Tests the case of an illegal ArrayExpr
     */
    @Test
    public void testArrayExprError() throws Exception{
        testErrorFile("testArrayExprError.btm");
    }


    /**
     * Tests the case of a valid ArrayAssignExpr
     */
    @Test
    public void testArrayAssignExpr() throws Exception{
        expectNoError("testArrayAssignExpr.btm");
    }

    /**
     * Tests the case of an illegal ArrayAssignExpr
     */
    @Test
    public void testArrayAssignExprError() throws Exception{
        testErrorFile("testArrayAssignExprError.btm");
    }

    /**
     * Tests the case of a legal Field.
     */
    @Test
    public void testField() throws Exception{
        expectNoError("testField.btm");
    }

    /**
     * Tests the case of an illegal Field.
     */
    @Test
    public void testFieldError() throws Exception{
        testErrorFile("testFieldError.btm");
    }

    /**
     * Tests the case of a legal WhileStmt.
     */
    @Test
    public void testWhileStmt() throws Exception{
        expectNoError("testWhileStmt.btm");
    }

    /**
     * Tests the case of an illegal WhileStmt.
     */
    @Test
    public void testWhileStmtError() throws Exception{
        testErrorFile("testWhileStmtError.btm");
    }

    /**
     * Tests the case of a legal IfStmt.
     */
    @Test
    public void testIfStmt() throws Exception{
        expectNoError("testIfStmt.btm");
    }

    /**
     * Tests the case of an illegal IfStmt.
     */
    @Test
    public void testIfStmtError() throws Exception{
        testErrorFile("testIfStmtError.btm");
    }

    /**
     * Tests the case of a legal ReturnStmt.
     */
    @Test
    public void testReturnStmt() throws Exception{
        expectNoError("testReturnStmt.btm");
    }

    /**
     * Tests the case of an illegal ReturnStmt.
     */
    @Test
    public void testReturnStmtError() throws Exception{
        testErrorFile("testReturnStmtError.btm");
    }

    /**
     * Tests the case of a legal DispatchExpr.
     */
    @Test
    public void testDispatchExpr() throws Exception{
        expectNoError("testDispatchExpr.btm");
    }

    /**
     * Tests the case of an illegal DispatchExpr.
     */
    @Test
    public void testDispatchExprError() throws Exception{
        testErrorFile("testDispatchExprError.btm");
    }

    /**
     * Tests the case of a legal NewExpr.
     */
    @Test
    public void testNewExpr() throws Exception{
        expectNoError("testNewExpr.btm");
    }

    /**
     * Tests the case of an illegal NewExpr.
     */
    @Test
    public void testNewExprError() throws Exception{
        testErrorFile("testNewExprError.btm");
    }

    /**
     * Tests the case of a legal CastExpr.
     */
    @Test
    public void testCastExpr() throws Exception{
        expectNoError("testCastExpr.btm");
    }

    /**
     * Tests the case of an illegal CastExpr.
     */
    @Test
    public void testCastExprError() throws Exception{
        testErrorFile("testCastExprError.btm");
    }

    /**
     * Tests the case of a legal BinaryArithExpr.
     */
    @Test
    public void testBinaryArithExpr() throws Exception{
        expectNoError("testBinaryArithExpr.btm");
    }

    /**
     * Tests the case of an illegal BinaryArithExpr.
     */
    @Test
    public void testBinaryArithExprError() throws Exception{
        testErrorFile("testBinaryArithExprError.btm");
    }

    /**
     * Tests the case of a legal BinaryCompEqExpr.
     */
    @Test
    public void testBinaryCompEqExpr() throws Exception{
        expectNoError("testBinaryCompEqExpr.btm");
    }

    /**
     * Tests the case of an illegal BinaryCompEqExpr.
     */
    @Test
    public void testBinaryCompEqExprError() throws Exception{
        testErrorFile("testBinaryCompEqExprError.btm");
    }

    /**
     * Tests the case of a legal BinaryCompNeExpr.
     */
    @Test
    public void testBinaryCompNeExpr() throws Exception{
        expectNoError("testBinaryCompNeExpr.btm");
    }

    /**
     * Tests the case of an illegal BinaryCompNeExpr.
     */
    @Test
    public void testBinaryCompNeExprError() throws Exception{
        testErrorFile("testBinaryCompNeExprError.btm");
    }

    /**
     * Tests the case of a legal BinaryCompExpr.
     */
    @Test
    public void testBinaryCompExpr() throws Exception{
        expectNoError("testBinaryCompExpr.btm");
    }

    /**
     * Tests the case of an illegal BinaryCompExpr.
     */
    @Test
    public void testBinaryCompExprError() throws Exception{
        testErrorFile("testBinaryCompExprError.btm");
    }

    /**
     * Tests the case of a legal BinaryLogicExpr.
     */
    @Test
    public void testBinaryLogicExpr() throws Exception{
        expectNoError("testBinaryLogicExpr.btm");
    }

    /**
     * Tests the case of an illegal BinaryLogicExpr.
     */
    @Test
    public void testBinaryLogicExprError() throws Exception{
        testErrorFile("testBinaryLogicExprError.btm");
    }

    /**
     * Tests the case of a legal UnaryNegExpr
     */

    @Test
    public void testUnaryNegExpr() throws Exception{
        expectNoError("testUnaryNegExpr.btm");
    }

    /**
     * Tests the case of an illegal UnaryNegExpr.
     */
    @Test
    public void testUnaryNegExprError() throws Exception{
        testErrorFile("testUnaryNegExprError.btm");
    }

    /**
     * Tests the case of a legal UnaryNotExpr.
     */
    @Test
    public void testUnaryNotExpr() throws Exception{
        expectNoError("testUnaryNotExpr.btm");
    }

    /**
     * Tests the case of an illegal UnaryNotExpr.
     */
    @Test
    public void testUnaryNotExprError() throws Exception{
        testErrorFile("testUnaryNotExprError.btm");
    }

    /**
     * Tests the case of a legal UnaryIncrExpr.
     */
    @Test
    public void testUnaryIncrExpr() throws Exception{
        expectNoError("testUnaryIncrExpr.btm");
    }

    /**
     * Tests the case of an illegal UnaryIncrExpr.
     */
    @Test
    public void testUnaryIncrExprError() throws Exception{
        testErrorFile("testUnaryIncrExprError.btm");
    }

    /**
     * Tests the case of a legal UnaryDecrExpr.
     */
    @Test
    public void testUnaryDecrExpr() throws Exception{
        expectNoError("testUnaryDecrExpr.btm");
    }

    /**
     * Tests the case of an illegal UnaryDecrExpr.
     */
    @Test
    public void testUnaryDecrExprError() throws Exception{
        testErrorFile("testUnaryDecrExprError.btm");
    }

    /**
     * Tests the case of a legal VarExpr.
     */
    @Test
    public void testVarExpr() throws Exception{
        expectNoError("testVarExpr.btm");
    }

    /**
     * Tests the case of an illegal UnaryDecrExpr.
     */
    @Test
    public void testVarExprError() throws Exception{
        testErrorFile("testVarExprError.btm");
    }

    /**
     * Tests the case of legal BreakStmt's
     */
    @Test
    public void testBreakStmt() throws Exception{
        expectNoError("testBreakStmt.btm");
    }

    /**
     * Tests the case of legal BreakStmt's
     */
    @Test
    public void testBreakStmtError() throws Exception{
        testErrorFile("testBreakStmtError.btm");
    }

    /**
     * Test inheritance
     */
    @Test
    public void testInheritance() throws Exception{
        expectNoError("testInheritence.btm");
    }

    /**
     * Test inheritance
     */
    @Test
    public void testInheritanceError() throws Exception{
        testErrorFile("testInheritenceError.btm");
    }


    /**
     * Test super
     */
    @Test
    public void testSuper() throws Exception{
        expectNoError("testSuper.btm");
    }

    /**
     * Test super illegal
     */
    @Test
    public void testSuperError() throws Exception{
        testErrorFile("testSuperError.btm");
    }

    /**
     * Test this
     */
    @Test
    public void testThis() throws Exception{
        expectNoError("testThis.btm");
    }

    /**
     * Test this illegal
     */
    @Test
    public void testThisError() throws Exception{
        testErrorFile("testThisError.btm");
    }


}