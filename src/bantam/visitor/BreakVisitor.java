/**
 * File: ParserTest.java
 * Author: Jacob, Nick, Larry, Luis, CP
 * Date: 2/23/17
 */
package bantam.visitor;

import bantam.ast.BreakStmt;
import bantam.ast.ForStmt;
import bantam.ast.Program;
import bantam.ast.WhileStmt;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;

import java.util.Hashtable;
import java.util.Objects;

/**
 * Break visitor vists the ast tree and checks that all the break statements are properly done
 */
public class BreakVisitor extends SemanticVisitor{

    /**
     * flag for currently in loop
     */
    private boolean currentlyInLoop;

    /**
     * Default constructor
     * @param classMap
     * @param errorHandler
     */
    public BreakVisitor(Hashtable<String, ClassTreeNode> classMap,
                        ErrorHandler errorHandler){
        super(classMap, errorHandler);
    }

    @Override
    /**
     * Checks to make sure that the program has proper break statment
     */
    public void check(Program ast) {
        ast.accept(this);
        this.currentlyInLoop = false;
    }

    /**
     * visits the while statement, sets flag
     * @param node the while statement node
     * @return
     */
    public Object visit(WhileStmt node){
        boolean previousState = this.currentlyInLoop;
        this.currentlyInLoop = true;
        super.visit(node);
        this.currentlyInLoop = previousState;
        return null;
    }

    /**
     * Visits the for statement, sets flag
     * @param node the for statement node
     * @return
     */
    public Object visit(ForStmt node){
        boolean previousState = this.currentlyInLoop;
        this.currentlyInLoop = true;
        super.visit(node);
        this.currentlyInLoop = previousState;
        return null;
    }

    /**
     * checks that the break statement is in a legal location i.e. in a loop
     * @param node the break statement node
     * @return
     */
    public Object visit(BreakStmt node){
        super.visit(node);
        if(!this.currentlyInLoop){
            this.registerError(node, "Break statement outside of valid loop context.");
        }
        return null;
    }
}
