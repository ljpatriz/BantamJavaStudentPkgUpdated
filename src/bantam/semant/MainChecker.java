/*
*   File: EnvironmentBuilderVisitor.java
*   Names: Ana Sofia Solis Canales, He He, Josh Hews, Erin Lavoie
*   Class: CS461
*   Project: 3
*   Date: 3/12/17
*/


package bantam.semant;

import bantam.ast.Method;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;
import bantam.util.SymbolTable;

import java.util.Hashtable;

/**
 * A Class that checks if a program has a main method with void return type
 * and taking no parameters in a Main class
 */
public class MainChecker {

    /**
     * Table of classes in program
     */
    Hashtable<String, ClassTreeNode> classmap;

    /**
     * Error Handler that can be used to register an error
     */
    ErrorHandler errorHandler;

    /**
     * Constructor.
     *
     * @param classmap     the classmap passed by the caller
     * @param errorHandler the error handler passed by the caller
     */
    public MainChecker(Hashtable<String, ClassTreeNode> classmap, ErrorHandler errorHandler) {
        this.classmap = classmap;
        this.errorHandler = errorHandler;
    }

    /**
     * Checks if there is a class called Main
     *
     * @return true if there is, false if there is not
     */
    private boolean hasMainClass() {
        if (this.classmap.containsKey("Main")) {
            return true;
        }

        errorHandler.register(errorHandler.SEMANT_ERROR, "Class Main not found.");
        return false;
    }

    /**
     * Checks if there is a main Method with return type void
     * and no parameters in the given class
     *
     * @param node the given class
     * @return true if there is a valid main method, false if there is not
     */
    private boolean hasMainMethod(ClassTreeNode node) {
        SymbolTable table = node.getMethodSymbolTable();

        if (table.lookup("main") != null) {
            if (((Method)table.lookup("main")).getReturnType().equals("void")){
                if (((Method)table.lookup("main")).getFormalList().getSize() == 0){
                    return true;
                }
            }
        }
        errorHandler.register(errorHandler.SEMANT_ERROR, "Method void main() not found in Class Main.");
        return false;

    }

    /**
     * Checks if there is a main method in Main class
     *
     * @return true if there is, false if there is not
     */
    public boolean hasMainMethodInMainClass() {

        if (!this.hasMainClass()) return false;

        ClassTreeNode main = this.classmap.get("Main");

        return hasMainMethod(main);

    }

}
