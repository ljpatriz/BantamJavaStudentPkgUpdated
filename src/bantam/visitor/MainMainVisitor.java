/*
 * File: MainMainVisitor.java
 * Author: Jacob, Nick, Larry, Luis, CP
 * Date: 2/28/17
 */

package bantam.visitor;

import bantam.ast.Class_;
import bantam.ast.Field;
import bantam.ast.Method;
import bantam.ast.Program;

/**
 * Tests to see if program has a class called Main which contains a main method of
 * type void, which takes no arguments
 */
public class MainMainVisitor extends Visitor {

    private boolean has;

    /**
     * Returns true if the given program contains a Main class with a main method
     * in it that has a void return type and has no parameters; returns false otherwise.
     *
     * @param ast the program abstract syntax tree
     * @return whether has a main class or not
     */
    public boolean hasMain(Program ast) {
        this.has = false;

        ast.accept(this);

        return this.has;
    }

    /**
     * Visits a Class_ node and searches for a main method if the class name is Main
     * @param node the class node
     * @return an Object
     */
    @Override
    public Object visit(Class_ node) {
        if (node.getName().equals("Main")) {
            super.visit(node);
        }

        return null;
    }

    /**
     * Visits a method node in search of a
     * void main() method with no parameters.
     * Updates the has field if such a method is found.
     * @param node the method node
     * @return
     */
    @Override
    public Object visit(Method node) {

        this.has =  node.getName().equals("main") &&
                    node.getReturnType().equals("void") &&
                    node.getFormalList().getSize() == 0;

        return null;
    }

    /**
     * Prevents descending any further than a Field from a Member in the grammar
     * @param node the field node
     * @return
     */
    @Override
    public Object visit(Field node) {
        return null;
    }
}
