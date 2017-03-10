package bantam.visitor;

import bantam.ast.*;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;

import java.util.Hashtable;

/**
 * This class is responsible for ensuring there is no method overloading going on in the
 * program. It will register an error if a method is overloaded.
 */
public class MethodOverrideVisitor extends SemanticVisitor {

    /**
     * Creates a MethodOverrideVisitor.
     * @param classMap  map of class names to CTNs
     * @param errHandler    errorHandler
     */
    public MethodOverrideVisitor(Hashtable<String, ClassTreeNode> classMap,
                                 ErrorHandler errHandler) {
        super(classMap, errHandler);
    }

//    /**
//     * Visit the program node
//     * @param ast
//     */
//    @Override
//    public void check(Program ast) {
//        ast.accept(this);
//    }

    public void check(Program ast) {}

//    /**
//     * Visit a class node
//     * @param node the class node
//     * @return
//     */
//    public Object visit(Class_ node) {
//        this.setCurrentClassName(node.getName());
//        return super.visit(node);
//    }
//
//    /**
//     * Visit a member list node and skip fields!!
//     * @param node the member list node
//     * @return
//     */
//    public Object visit(MemberList node) {
//        for (ASTNode child : node) {
//            if (! (child instanceof Field))
//                child.accept(this);
//        }
//        return null;
//    }

    /**
     * Visit a method node
     * @param currentMethod the method node whom is visited
     * @return
     */
    public Object visit(Method currentMethod) {
        ClassTreeNode currentClass = this.getClassMap().get(this.getCurrentClassName());
        ClassTreeNode parentClass = currentClass.getParent();

        if (parentClass == null) {
            return null;    // everything is fine
        }

        Method parentMethod = (Method) parentClass.getMethodSymbolTable()
                .lookup(currentMethod.getName());

        if (parentMethod == null) {
            return null;    // everything is fine
        }

        // Parent method actually exists, we're in trouble

        // check return types
        if (!currentMethod.getReturnType().equals(parentMethod.getReturnType())) {
            this.registerError(currentMethod, "Method: " + currentClass.getName() + "." +
                    currentMethod.getName() + " has different return type than the method" +
                    " it overrides.");
        }

        // check parameters
        if (currentMethod.getFormalList().getSize() !=
                parentMethod.getFormalList().getSize()) {
            this.registerError(currentMethod,"Method: " + currentClass.getName() + "." +
                    currentMethod.getName() + "has a different number of arguments than" +
                    " the method it overrides.");
        }

        for (int i = 0; i < currentMethod.getFormalList().getSize(); i++) {
            if (((Formal)currentMethod.getFormalList().get(i)).getType() !=
                    ((Formal)currentMethod.getFormalList().get(i)).getType()) {
                this.registerError(currentMethod, "Method: " + currentClass.getName() +
                        "." + currentMethod.getName() + " has a different param type at" +
                        " index" + i);
            }
        }

        return null;    // everything is fine
    }

}
