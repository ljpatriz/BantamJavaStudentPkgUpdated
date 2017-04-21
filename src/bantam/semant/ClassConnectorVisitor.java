/*
*   File: ClassConnectorVisitor.java
*   Names: Ana Sofia Solis Canales, He He, Josh Hews, Erin Lavoie
*   Class: CS461
*   Project: 3
*   Date: 3/12/17
*/


package bantam.semant;

import bantam.ast.Class_;
import bantam.ast.Program;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;
import bantam.visitor.Visitor;

import java.util.Hashtable;
import java.util.Set;

/**
 * Visitor that creates the inheritance structure for the Program
 */
public class ClassConnectorVisitor extends Visitor {

    /**
     * Table of classes in program
     */
    private Hashtable<String, ClassTreeNode> classMap;

    /**
     * Error Checker checks for semantic errors in program
     */
    private SemanticErrorReporter errorReporter;

    /**
     * Initializes a ClassConnector Visitor
     *
     * @param classMap      table of classes in program
     * @param err           handler for registering errors
     * @param reservedWords set of reserved words in language
     */
    public ClassConnectorVisitor(Hashtable<String, ClassTreeNode> classMap, ErrorHandler err,
                                 Set<String> reservedWords) {
        this.classMap = classMap;

        this.errorReporter = new SemanticErrorReporter(this.classMap, err, reservedWords);

    }

    /**
     * Parses the program //TODO is this description OK?
     *
     * @param ast program to be parsed
     */
    public void connectClasses(Program ast) {
        ast.accept(this);
    }

    /**
     * Connects the visited class to its parent in the ClassMap
     *
     * @param class_ Class_ that is visited
     * @return null
     */
    @Override
    public Object visit(Class_ class_) {

        this.errorReporter.setCurrFile(class_.getFilename());

        String parentName = class_.getParent();

        // check that parent exists
        this.errorReporter.reportClassNonexistence(parentName, class_.getLineNum());

        // get child and parent CTN's
        ClassTreeNode child = this.classMap.get(class_.getName());
        ClassTreeNode parent = this.classMap.get(parentName);

        child.setParent(parent);

        return null;

    }

}
