/*
*   File: EnvironmentBuilderVisitor.java
*   Names: Ana Sofia Solis Canales, He He, Josh Hews, Erin Lavoie
*   Class: CS461
*   Project: 3
*   Date: 3/12/17
*/

package bantam.semant;

import bantam.ast.Class_;
import bantam.ast.Field;
import bantam.ast.Method;
import bantam.ast.Program;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;
import bantam.visitor.Visitor;

import java.util.Hashtable;
import java.util.Set;

/**
 * Visitor that creates the environment for each class of a Program
 */
public class EnvironmentBuilderVisitor extends Visitor {

    /**
     * Table of classes in program
     */
    private Hashtable<String, ClassTreeNode> classMap;

    /**
     * Current Node being visited
     */
    private ClassTreeNode curNode;

    /**
     * Program file
     */
    private String curFile;

    /**
     * Reports Errors to the Error Handler
     */
    private SemanticErrorReporter errorReporter;


    /**
     * Constructor
     * @param classMap Hashtable<String, ClassTreeNode> of program classes
     * @param err ErrorHandler for Semantic Errors
     * @param reservedWords Set<String> of words reserved for special purposes in the program
     */
    public EnvironmentBuilderVisitor(Hashtable<String, ClassTreeNode> classMap, ErrorHandler err, Set<String> reservedWords) {
        this.classMap = classMap;
        this.errorReporter = new SemanticErrorReporter(this.classMap, err, reservedWords);

    }

    /**
     * Builds the environment
     * @param program Program to which the environment would be built
     */
    public void buildEnvironment(Program program) {
        //Visit Built-Ins
        this.classMap.get("Object").getASTNode().accept(this);
        this.classMap.get("Sys").getASTNode().accept(this);
        this.classMap.get("TextIO").getASTNode().accept(this);
        this.classMap.get("String").getASTNode().accept(this);

        //visit program
        program.accept(this);
    }

    /**
     * Visits the class_
     * @param class_ Class_ to visit
     * @return Visit to continue traversing
     */
    @Override
    public Object visit(Class_ class_) {

        //lookup current node in classMap
        ClassTreeNode temp = this.classMap.get(class_.getName());

        //get file name
        this.curFile = temp.getASTNode().getFilename();

        //initialize current Node
        this.curNode = temp;

        //enter Var Symbol Table Scope
        this.curNode.getVarSymbolTable().enterScope();

        //enter Method Symbol table Scope
        this.curNode.getMethodSymbolTable().enterScope();

        //setting the error checker
        this.errorReporter.setCurrClass(this.curNode);
        this.errorReporter.setCurrFile(this.curFile);

        //Visit class.
        return super.visit(class_);
    }

    /**
     * Visits the Field
     * Adds fields to the class_ SymbolTable
     * @param field Field to visit
     * @return null to prevent further traversal
     */
    @Override
    public Object visit(Field field) {

        //check if there is a a Name Conflict in the Field
        this.errorReporter.reportFieldNameErrors(field.getName(), field.getLineNum());

        //Add field to the Symbol Table
        this.curNode.getVarSymbolTable().add(field.getName(), field.getType());

        return null;
    }

    /**
     * Visits the method
     * Adds the method to the class_ Symbol Table
     * @param method Method to visit
     * @return null to prevent further traversal
     */
    @Override
    public Object visit(Method method) {

        this.errorReporter.reportMethodDeclarationErrors(method);

        this.curNode.getMethodSymbolTable().add(method.getName(), method);

        return null;
    }
}
