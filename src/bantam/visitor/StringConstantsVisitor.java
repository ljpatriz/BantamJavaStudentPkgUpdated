package bantam.visitor;

import bantam.ast.ConstStringExpr;
import bantam.ast.Formal;
import bantam.ast.FormalList;
import bantam.ast.Program;

import java.util.HashMap;
import java.util.Map;

/*
 * File: ParserTest.java
 * Author: Jacob, Nick, Larry, Luis, CP
 * Date: 2/23/17
 */
public class StringConstantsVisitor extends Visitor{

    /**
     * This map contains all the Strings and their assigned names
     */
    private Map<String, String> stringConstantMap;

    /**
     * This Builds the String Constants for the names
     */
    private StringBuilder nameBuilder = new StringBuilder("StringConst_");

    /**
     * Returns a map containing Map whose keys are the String constants from the program
     * and whose values are names for the String constants.
     * @param ast Abstract Syntax Tree
     * @return
     */
    public Map<String, String> getStringConstants(Program ast){
        stringConstantMap = new HashMap<>();
        this.visit(ast);
        return stringConstantMap;
    }

    /**
     * Override method for the string constant. Adds this string to the map.
     * @param constStringExpr
     * @return
     */
    public Object visit(ConstStringExpr constStringExpr){
        this.nameBuilder.setLength(12);

        this.nameBuilder.append(stringConstantMap.entrySet().size());

        stringConstantMap.put(constStringExpr.getConstant(),
                this.nameBuilder.toString());

        return null;
    }

    /**
     * Optimization function as formals cannot be string Constants
     * Does nothing
     * @param node
     * @return
     */
    public Object visit(FormalList node){
        return null;
    }

}
