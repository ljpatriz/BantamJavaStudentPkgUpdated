
/*
*   File: BreakStmtChecker.java
*   Names: Ana Sofia Solis Canales, He He, Josh Hews, Erin Lavoie
*   Class: CS461
*   Project: 3
*   Date: 3/12/17
*/

package bantam.semant;

import bantam.ast.*;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;
import bantam.visitor.Visitor;

import java.util.Hashtable;

/**
 * Visitor that checks if the break statements is outside loops
 */
public class BreakStmtCheckerVisitor extends Visitor {

    /**
     * Table of classes in Pro
     */
    private Hashtable<String, ClassTreeNode> classMap;

    /**
     * The error handler that will be used to register errors
     */
    private ErrorHandler err;

    /**
     * Current class tree node being vissted
     */
    private ClassTreeNode currClass;

    /**
     * File of the current program
     */
    private String currFile;


    /**
     * Constructor
     * @param classMap classMap passed by the caller
     * @param err error handler passed by the caller
     */
    public BreakStmtCheckerVisitor(Hashtable<String, ClassTreeNode> classMap, ErrorHandler err) {
        this.classMap = classMap;
        this.err = err;
    }

    /**
     * Checks that if there is illegal break statements in given program
     * @param ast the given program
     */
    public void checkIllegalBreaks(Program ast) {
        ast.getClassList().accept(this);
    }

    /**
     * Visit class list
     * @param classList the class list
     * @return result
     */
    @Override
    public Object visit(ClassList classList) {
        classList.forEach(c -> c.accept(this));
        return null;
    }

    /**
     * Visit the class
     * @param class_ the class to visit
     * @return the result of the visit
     */
    @Override
    public Object visit(Class_ class_) {
        this.currClass = this.classMap.get(class_.getName());
        this.currFile = class_.getFilename();
        class_.getMemberList().forEach(m -> m.accept(this));
        return null;
    }

    /**
     * Visit the method
     * @param method a method to visit
     * @return the result of the visit
     */
    @Override
    public Object visit(Method method) {
        method.getStmtList().accept(this);
        return null;
    }

    /**
     * Visits the statement list and checks for the illegal break statements
     * @param stmtList the statement list to visit
     * @return the result of the visit
     */
    @Override
    public Object visit(StmtList stmtList) {
        stmtList.forEach(
                c -> checkBreakStatement((Stmt) c)
        );
        return super.visit(stmtList);
    }

    /**
     * Visits a if statement and checks for the illegal break statements
     * @param ifStmt the if statement to visit
     * @return the result of the visit
     */
    @Override
    public Object visit(IfStmt ifStmt) {

        Stmt thenStmt = ifStmt.getThenStmt();
        Stmt elseStmt = ifStmt.getElseStmt();

        checkBreakStatement(thenStmt);

        if (elseStmt != null) {
            checkBreakStatement(elseStmt);
        }
        return null;
    }


    /**
     * Visits a block statement
     * @param blockStmt the block statement to visit
     * @return the result of visit
     */
    @Override
    public Object visit(BlockStmt blockStmt) {
        blockStmt.getStmtList().accept(this);
        return null;
    }

    /**
     * Visits a for statement and does nothing
     * @param node the for statement node
     * @return the result of the visit
     */
    @Override
    public Object visit(ForStmt node) {
        return null;
    }

    /**
     * Visits a while statement and does nothing
     * @param node the while statement node
     * @return the result of the visit
     */
    @Override
    public Object visit(WhileStmt node) {
        return null;
    }

    /**
     * Checks if there is an illegal break statements inside the given statement
     * @param stmt the given statement
     */
    private void checkBreakStatement (Stmt stmt){
        if (stmt instanceof BreakStmt) {
            err.register(err.SEMANT_ERROR, this.currFile, stmt.getLineNum(), "Invalid Break Statement");

        } else {
            stmt.accept(this);
        }
    }
}
