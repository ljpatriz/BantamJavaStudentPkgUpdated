package bantam.visitor;

import bantam.ast.BreakStmt;
import bantam.ast.ForStmt;
import bantam.ast.Program;
import bantam.ast.WhileStmt;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;

import java.util.Hashtable;
import java.util.Objects;


public class BreakVisitor extends SemanticVisitor{
    private boolean currentlyInLoop;

    public BreakVisitor(Hashtable<String, ClassTreeNode> classMap,
                        ErrorHandler errorHandler){
        super(classMap, errorHandler);
    }

    @Override
    public void check(Program ast) {
        ast.accept(this);
        this.afterVisit();
    }

    @Override
    public void afterVisit(){
        this.currentlyInLoop = false;
    }

    public Object visit(WhileStmt node){
        boolean previousState = this.currentlyInLoop;
        this.currentlyInLoop = true;
        super.visit(node);
        this.currentlyInLoop = previousState;
        return null;
    }

    public Object visit(ForStmt node){
        boolean previousState = this.currentlyInLoop;
        this.currentlyInLoop = true;
        super.visit(node);
        this.currentlyInLoop = previousState;
        return null;
    }


    public Object visit(BreakStmt node){
        super.visit(node);
        if(!this.currentlyInLoop){
            this.registerError(node, "Break statement outside of valid loop context.");
        }
        return null;
    }


}
