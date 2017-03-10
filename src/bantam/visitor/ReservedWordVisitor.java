package bantam.visitor;

import bantam.ast.*;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;

import java.util.Hashtable;

/**
 * Created by Jacob on 10/03/17.
 */
public class ReservedWordVisitor extends SemanticVisitor {

    public ReservedWordVisitor(Hashtable<String, ClassTreeNode> classMap, ErrorHandler errorHandler) {
        super(classMap, errorHandler);
    }

    /**
     * Checks to make sure that the Program does not improperly use reserved words
     * @param ast the program node
     * @return
     */
    @Override
    public void check(Program ast) {
        ast.accept(this);
    }

    /**
     * Checks to make sure that the Class_ node does not used reserved words
     * @param node the class node
     * @return
     */
    @Override
    public Object visit(Class_ node) {
        if(isReservedWord(node.getName()))
            registerError(node, node.getName()+"is not a legal identifier");
        return super.visit(node);
    }

    /**
     * Checks to make sure that the Field node does not used reserved words
     * @param node the Field node
     * @return
     */
    @Override
    public Object visit(Field node) {
        if(isReservedWord(node.getName()))
            registerError(node, node.getName()+"is not a legal identifier");
        return super.visit(node);
    }

    /**
     * Checks to make sure that the Method node does not used reserved words
     * @param node the Method node
     * @return
     */
    @Override
    public Object visit(Method node) {
        if(isReservedWord(node.getName()))
            registerError(node, node.getName()+"is not a legal identifier");
        return super.visit(node);
    }

    /**
     * Checks to make sure that the Formal does not used reserved words
     * @param node the new expression node
     * @return
     */
    @Override
    public Object visit(Formal node) {
        if(isReservedWord(node.getName()))
            registerError(node, node.getName()+"is not a legal identifier");

        if(isReservedWord(node.getType())){
            if(!node.getType().equals(this.INT)|| !node.getType().equals(this.BOOLEAN)){
                registerError(node, node.getType()+"is not a legal type");
            }
        }

        return super.visit(node);
    }

    /**
     * Checks to make sure that the DeclStmt does not used reserved words
     * @param node the DeclStmt node
     * @return
     */
    @Override
    public Object visit(DeclStmt node) {
        if(isReservedWord(node.getName()))
            registerError(node, node.getName()+"is not a legal identifier");
        if(isReservedWord(node.getType())){
            if(!node.getType().equals(this.INT)|| !node.getType().equals(this.BOOLEAN)){
                registerError(node, node.getType()+"is not a legal type");
            }
        }
        return super.visit(node);
    }

    /**
     * Checks to make sure that the CastExpr does not used reserved words
     * @param node the new expression node
     * @return
     */
    @Override
    public Object visit(CastExpr node) {
        if(isReservedWord(node.getType())){
            if(!node.getType().equals(this.INT)|| !node.getType().equals(this.BOOLEAN)){
                registerError(node, node.getType()+"is not a legal identifier");
            }
        }
        return super.visit(node);
    }

    /**
     * Checks to make sure that the NewExpr does not used reserved words
     * @param node the new expression node
     * @return
     */
    @Override
    public Object visit(NewExpr node) {
        if(isReservedWord(node.getType())){
            if(!node.getType().equals(this.INT)|| !node.getType().equals(this.BOOLEAN)){
                registerError(node, node.getType()+"is not a legal identifier");
            }
        }
        return super.visit(node);
    }

    /**
     * Checks to make sure that the VarExpr does not used reserved words
     * @param node the new expression node
     * @return
     */
    @Override
    public Object visit(VarExpr node) {
        if(isReservedWord(node.getName())){
            registerError(node, node.getName()+"is not a legal identifier");
        }
        return super.visit(node);
    }

    /**
     * Checks to make sure that the ArrayExpr does not used reserved words
     * @param node the new expression node
     * @return
     */
    @Override
    public Object visit(ArrayExpr node) {
        if(isReservedWord(node.getName())){
            registerError(node, node.getName()+"is not a legal identifier");
        }
        return super.visit(node);
    }
}
