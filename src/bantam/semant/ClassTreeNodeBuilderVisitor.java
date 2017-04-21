/*
*   File: ClassTreeNodeBuilderVisitor.java
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
 * Visitor that created the ClassTreeNodes for all classes in a program
 */
public class ClassTreeNodeBuilderVisitor extends Visitor {

    /**
     * Table of classes in program
     */
    private Hashtable<String, ClassTreeNode> classMap;

    /**
     * Registers the different errors to the ErrorHandler
     */
    private SemanticErrorReporter errorReporter;

    /**
     * Builds the ClassTreeNodes from the program
     *
     * @param classMap      table of classes in program
     * @param err           handler for registering errors
     * @param reservedWords set of words reserved in language
     */
    public ClassTreeNodeBuilderVisitor(Hashtable<String, ClassTreeNode> classMap, ErrorHandler err,
                                       Set<String> reservedWords) {
        this.classMap = classMap;

        this.errorReporter = new SemanticErrorReporter(this.classMap, err, reservedWords);

    }

    /**
     * Builds the ClassTreeNodes from the program
     *
     * @param ast Abstract syntax tree of program
     */
    public void buildClasses(Program ast) {
        //Visit Program!
        super.visit(ast);
    }

    /**
     * Visits Class_
     * creates a corresponding ClassTreeNode
     *
     * @param class_ Class_ that is being visited
     * @return null
     */
    @Override
    public Object visit(Class_ class_) {

        this.errorReporter.setCurrFile(class_.getFilename());

        // Create new classNode
        ClassTreeNode classNode = new ClassTreeNode(class_, false, true, this.classMap);

        // TODO do we want to do these things here?
        classNode.getVarSymbolTable().enterScope();
        classNode.getMethodSymbolTable().enterScope();

        // Check for naming conflict
        this.errorReporter.reportClassNameErrors(class_.getName(), class_.getLineNum());

        // Add class to classMap
        this.classMap.put(class_.getName(), classNode);

        return null;
    }

}
