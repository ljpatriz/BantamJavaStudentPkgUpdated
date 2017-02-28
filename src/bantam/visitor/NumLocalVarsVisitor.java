/*
 * File: NumLocalVarsVisitor.java
 * Author: Jacob, Nick, Larry, Luis, CP
 * Date: 2/23/17
 */

package bantam.visitor;

import bantam.ast.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Visitor class to count the number of local variables in a parse tree, and create a Map
 * from identifiers to counts, where the identifiers are formatted like
 * [class name].[method name]
 */
public class NumLocalVarsVisitor extends Visitor {

    /** Map described above */
    private Map<String,Integer> numLocalVars;

    /** Holds the name of the currently traversed class. */
    private String currentClass;
    /** Holds the name of the currently traversed method. */
    private String currentMethod;
    /** Used to build the current identifier. */
    private StringBuilder currentID = new StringBuilder();

    /**
     * Returns the map from method identifiers to the Integer count of their local
     * variables.
     * @param ast   the abstract syntax tree node to begin the traversal
     * @return  the map from method ids to the Integer count of their local variables
     */
    public Map<String,Integer> getNumLocalVars(Program ast) {
        this.numLocalVars = new HashMap<>();
        ast.accept(this);
        return this.numLocalVars;
    }

    /**
     * Gets the current Class.Method identifier as a string.
     * @return the current Class.Method identifier as a string.
     */
    public String getCurrentID() {
        this.currentID.setLength(0);
        this.currentID.append(this.currentClass);
        this.currentID.append(".");
        this.currentID.append(this.currentMethod);

        return this.currentID.toString();
    }

    /**
     * Visits a Class_ node and updates the currentClass string
     * @param node the class node
     * @return  null
     */
    @Override
    public Object visit(Class_ node) {
        this.currentClass = node.getName();

        super.visit(node);
        return null;
    }

    /**
     * Visits a Method node and updates the currentClass string
     * @param node the method node
     * @return  null
     */
    @Override
    public Object visit(Method node) {
        this.currentMethod = node.getName();
        this.numLocalVars.put(this.getCurrentID(), node.getFormalList().getSize());

        node.getStmtList().accept(this);
        return null;
    }

    /**
     * Visits a DeclStmt node and increments the corresponding counter in the map.
     * @param node the declaration statement node
     * @return  null
     */
    @Override
    public Object visit(DeclStmt node) {
        String key = this.getCurrentID();
        this.numLocalVars.put(key, this.numLocalVars.get(key)+1);

        // the child expression is worthless
        return null;
    }

    /**
     * Stop traversal if the node is a Field.
     * @param node the field node
     * @return  null
     */
    @Override
    public Object visit(Field node) {
        return null;
    }

    /**
     * Stop traversal if the node is a Formal.
     * @param node the formal node
     * @return  null
     */
    @Override
    public Object visit(Formal node) {
        return null;
    }

    /**
     * Stops traversal if the node is an Expr.
     * @param node the expression node
     * @return   null
     */
    @Override
    public Object visit(Expr node) {
        return null;
    }
}
