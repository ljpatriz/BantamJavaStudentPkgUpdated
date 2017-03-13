/**
 * File: ParserTest.java
 * Author: Jacob, Nick, Larry, Luis, CP
 * Date: 2/23/17
 */

package bantam.visitor;

import bantam.ast.Class_;
import bantam.ast.Program;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;

import java.util.Hashtable;

/**
 * Tests to make sure that classes are not created with the same name as previously
 * created classes.
 */
public class ClassMapVisitor extends SemanticVisitor {

    public ClassMapVisitor(Hashtable<String, ClassTreeNode> classMap, ErrorHandler errorHandler) {
        super(classMap, errorHandler);
    }

    /**
     * Adds all the nodes to the classmap
     * @param ast
     */
    @Override
    public void check(Program ast) {
        ast.accept(this);
    }

    /**
     * Creates the classtree node and puts it in the classmap
     * @param node the class node
     * @return
     */
    @Override
    public Object visit(Class_ node) {
        ClassTreeNode classTreeNode = new ClassTreeNode(node, false, true, getClassMap());
        if(getClassMap().containsKey(node.getName()))
            registerError(node, "Class with the same name already exists");
        this.getClassMap().put(node.getName(), classTreeNode);
        return super.visit(node); //good spot for optimization
    }
}
