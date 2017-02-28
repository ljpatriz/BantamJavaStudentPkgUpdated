package bantam.visitor;

import bantam.ast.Class_;
import bantam.ast.Field;
import bantam.ast.Method;
import bantam.ast.Program;

/**
 * Tests to see if program has a class called Main
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
        this.visit(ast);
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
        if (!node.getName().equals("main"))
            return null;
        if (!node.getReturnType().equals("void"))
            return null;
        if (node.getFormalList().getSize() != 0)
            return null;

        this.has = true;
        // do not descend further for efficient traversal
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
