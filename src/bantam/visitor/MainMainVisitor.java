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
import bantam.util.ErrorHandler;

/**
 * Tests to see if program hasMain a class called Main which contains a main method of
 * type void, which takes no arguments
 */
public class MainMainVisitor extends Visitor {

    private boolean hasMain;

    private ErrorHandler errorHandler;

    public MainMainVisitor(ErrorHandler errorHandler){
        this.errorHandler = errorHandler;
    }

    /**
     * Returns true if the given program contains a Main class with a main method
     * in it that hasMain a void return type and hasMain no parameters; returns false otherwise.
     *
     * @param ast the program abstract syntax tree
     * @return whether hasMain a main class or not
     */
    public boolean hasMain(Program ast) {
        this.hasMain = false;

        ast.accept(this);

        return this.hasMain;
    }

    /**
     * Visits a Class_ node and searches for a main method if the class name is Main
     * @param node the class node
     * @return an Object
     */
    @Override
    public Object visit(Class_ node) {
        if (node.getName().equals("Main")) {
            this.visit(node.getMemberList());
        }

        if (!this.hasMain) {
            //TODO try superclass until we hit Object
        }

        return null;
    }

    /**
     * Visits a method node in search of a
     * void main() method with no parameters.
     * Updates the hasMain field if such a method is found.
     * @param node the method node
     * @return
     */
    @Override
    public Object visit(Method node) {

        this.hasMain =  node.getName().equals("main") &&
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
