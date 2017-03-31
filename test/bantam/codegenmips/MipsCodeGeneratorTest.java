/**
 * File: MipsCodeGeneratorTest.java
 * Author: Jacob, Nick, Larry
 * Date: 3/31/17
 */

package bantam.codegenmips;

import bantam.ast.Program;
import bantam.lexer.Lexer;
import bantam.parser.Parser;
import bantam.semant.SemanticAnalyzer;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

/*
 * File: MipsCodeGeneratorTest.java
 * Author: djskrien
 * Date: 1/8/17
 */
public class MipsCodeGeneratorTest
{

    /**
     * String of the directory path to the semantic analyzer test files
     */
    private String testDirectory = System.getProperty("user.dir") +
            "/testfiles/";

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


    @Test
    public void testONE() throws Exception {
        String fileContents = "empty.btm";
        Parser parser = new Parser(new Lexer(new StringReader(
                readFile(testDirectory+fileContents))));
        Program program = (Program) parser.parse().value;
        SemanticAnalyzer analyzer = new SemanticAnalyzer(program, true);
        ClassTreeNode root = analyzer.analyze();
        MipsCodeGenerator mipsCodeGenerator = new MipsCodeGenerator(root,"MIPS_TEST_$.asm",false,false,false);
        mipsCodeGenerator.generate();

    }
}