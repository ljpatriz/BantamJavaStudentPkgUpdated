package bantam.semant;

import bantam.ast.Program;
import bantam.lexer.Lexer;
import org.junit.Test;
import bantam.parser.Parser;
import bantam.util.ErrorHandler;

import java.io.StringReader;

import static org.junit.Assert.assertEquals;
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
    private String testDirectory = "/Users/larrypatrizio/Downloads/" +
            "BantamJavaStudentPkgUpdated/testfiles/SemanticAnalyzerTestFiles/";

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

    private boolean testThrown(String filename) throws Exception{
        boolean thrown = false;
        Parser parser = new Parser(new Lexer(new StringReader(
                readFile(testDirectory+filename))));
        Program program = (Program) parser.parse().value;
        SemanticAnalyzer analyzer = new SemanticAnalyzer(program, true);
        try {
            analyzer.analyze();
        } catch (RuntimeException e) {
            thrown = true;
            assertEquals("Bantam semantic analyzer found errors.", e.getMessage());
            for (ErrorHandler.Error err : analyzer.getErrorHandler().getErrorList()) {
                System.out.println(err);
            }
        }
        return thrown;
    }

    private void testCleanFile(String filename) throws Exception{
        assertTrue(!testThrown(filename));
    }

    private void testErrorFile(String filename) throws Exception{
        assertTrue(testThrown(filename));
    }

    /** Tests the case of a Main class with only a main.  This is legal
     * because a Bantam Java program must have a Main class with a main
     * method. */
    @Test
    public void testEmptyMainClass() throws Exception {
        testCleanFile("testEmptyMainClass.btm");
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
        testCleanFile("testRepeatedClassName.btm");
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
        testCleanFile("testDeclStmt.btm");
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
        testCleanFile("testAssignExpr.btm");
    }

    /**
     * Tests the case of an illegal AssignExpr.
     */
    @Test
    public void testAssignExprError() throws Exception{
        testErrorFile("testAssignExprError.btm");
    }

    /**
     * Tests the case of a legal Field.
     */
    @Test
    public void testField() throws Exception{
        testCleanFile("testField.btm");
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
        testCleanFile("testWhileStmt.btm");
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
        testCleanFile("testIfStmt.btm");
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
        testCleanFile("testReturnStmt.btm");
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
        testCleanFile("testDispatchExpr.btm");
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
        testCleanFile("testNewExpr.btm");
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
        testCleanFile("testCastExpr.btm");
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
        testCleanFile("testBinaryArithExpr.btm");
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
        testCleanFile("testBinaryCompEqExpr.btm");
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
        testCleanFile("testBinaryCompNeExpr.btm");
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
        testCleanFile("testBinaryCompExpr.btm");
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
        testCleanFile("testBinaryLogicExpr.btm");
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
    @Test
    public void testUnaryNegExpr() throws Exception{
        testCleanFile("testUnaryNegExpr.btm");
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
        testCleanFile("testUnaryNotExpr.btm");
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
        testCleanFile("testUnaryIncrExpr.btm");
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
        testCleanFile("testUnaryDecrExpr.btm");
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
        testCleanFile("testVarExpr.btm");
    }

    /**
     * Tests the case of an illegal UnaryDecrExpr.
     */
    @Test
    public void testVarExprError() throws Exception{
        testErrorFile("testVarExprError.btm");
    }

}