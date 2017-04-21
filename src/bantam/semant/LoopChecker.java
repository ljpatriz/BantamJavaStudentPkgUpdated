/*
*   File: LoopChecker.java
*   Names: Ana Sofia Solis Canales, He He, Josh Hews, Erin Lavoie
*   Class: CS461
*   Project: 3
*   Date: 3/12/17
*/

package bantam.semant;

import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;

import java.util.HashSet;
import java.util.Hashtable;

/**
 * Checks that there are no cyclic relationships in a program
 */

public class LoopChecker {

    /**
     * Table of classes in program
     */
    private Hashtable<String, ClassTreeNode> classMap;

    /**
     * Error Handler for registering errors
     */
    private ErrorHandler err;

    private HashSet<String> visited;


    /**
     * Initiates a LoopChecker
     *
     * @param classMap table of classes in program
     * @param err      handler for registering errors
     */
    public LoopChecker(Hashtable<String, ClassTreeNode> classMap, ErrorHandler err) {
        this.classMap = classMap;
        this.err = err;
        this.visited = new HashSet<>();
    }

    /**
     * Launches the recursive checkLoop(..) to check for loops
     */
    public void checkLoop() {

        //Check for all the Object children
        ClassTreeNode object = this.classMap.get("Object");
        checkLoop(object);

        //If the loop is done, check that all class in the classMap have been visited
        for (String s : this.classMap.keySet()) {
            //if it has not been visited, check the loop.
            if (!this.visited.contains(s)) checkLoop(this.classMap.get(s));
        }

    }

    /**
     * Checks for an inheritance loop recursively.
     *
     * @param node node to visit/check
     */
    public void checkLoop(ClassTreeNode node) {

        String nodeName = node.getName();

        // if node has been visited then there is a loop
        if (this.visited.contains(nodeName)) {

            //Register an Error
            this.err.register(this.err.SEMANT_ERROR, "Cyclic Inheritance Involving " + nodeName + ".");

        } else {
            //visit all the children in the class.
            this.visited.add(node.getName());
            node.getChildrenList().forEachRemaining(child -> checkLoop(child));
        }
    }

}
