package bantam.visitor;

import bantam.ast.Class_;
import bantam.ast.Program;

/**
 * Tests to see if program has a class called Main
 */
public class MainMainVisitor extends Visitor {

    private String currentClass = "";

    /**
     * Returns true if the given program contains a Main class with a main method
     * in it that has a void return type and has no parameters; returns false otherwise.
     *
     * @param ast the program abstract syntax tree
     * @return whether has a main class or not
     */
    public boolean hasMain(Program ast) {
        this.visit(ast);
        return this.currentClass.equals("Main");
    }

    /**
     * Visits a Class_ node and updates the currentClass string if the
     * currentClass is not "Main"
     * @param node the class node
     * @return an Object
     */
    @Override
    public Object visit(Class_ node) {
        super.visit(node);
        if (!this.currentClass.equals("Main"))
            this.currentClass = node.getName();

        return null;
    }
}
