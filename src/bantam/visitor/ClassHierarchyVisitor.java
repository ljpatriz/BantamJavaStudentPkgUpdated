package bantam.visitor;

import bantam.ast.*;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;

import java.util.*;
import java.util.Map.Entry;

/**
 * Visitor to set up the inheritance hierarchy.
 */
public class ClassHierarchyVisitor extends Visitor {

    // root node of the class tree
    private ClassTreeNode classTreeRootNode;
    // class map from names to nodes
    private Hashtable<String, ClassTreeNode> classMap;
    // error handler ?
    private ErrorHandler errorHandler;
    // current node
    private ClassTreeNode currentClassTreeNode;
    private MethodOverrideVisitor methodOverrideVisitor;

    /**
     * Creates a new instance with the given error handler and class map.
     * @param errorHandler
     * @param classMap
     */
    public ClassHierarchyVisitor(ErrorHandler errorHandler, Hashtable<String, ClassTreeNode> classMap){
        this.errorHandler = errorHandler;
        this.classMap = classMap;
        this.methodOverrideVisitor = new MethodOverrideVisitor(classMap, errorHandler);
    }

    /**
     * Builds the class tree for the given program and root class tree node.
     * @param program
     * @param classTreeRootNode
     * @return  the root node
     */
    public ClassTreeNode buildClassTree(Program program, ClassTreeNode classTreeRootNode){
        this.classTreeRootNode = classTreeRootNode;
        this.visit(program);
        hasCycles();
        return classTreeRootNode;
    }

    /**
     * If the tree is not well-formed, object to the error reporter
     */
    private void hasCycles(){
        Set<ClassTreeNode> traversed = new HashSet<>();
        hasCycles(traversed, classTreeRootNode);
    }

    /**
     * If the tree is not well-formed, object to the error reporter
     * @param traversed the set of ClassTreeNodes whom have already been traversed
     * @param currentNode   the current node whom'st is undergoing traversal
     */
    private void hasCycles(Set<ClassTreeNode> traversed, ClassTreeNode currentNode){
        Iterator<ClassTreeNode> childrenList = currentNode.getChildrenList();
        while(childrenList.hasNext()) {
            ClassTreeNode currentChild = childrenList.next();
            if(traversed.contains(currentChild)){
                //cyclical error
                errorHandler.register(2, currentChild.getASTNode().getFilename(),
                        currentChild.getASTNode().getLineNum(),
                        "The class inheritance tree is not well formed: " + currentChild.getName()
                                + " inherits from itself");
                currentNode = currentChild;
                continue;
                //TODO more specific error regarding which class is causing problems
            }
            else{
                traversed.add(currentChild);
                hasCycles(traversed,currentChild);
            }

        }
    }

    /**
     * Function for visiting the class Node
     * @param classNode
     * @return
     */
    public Object visit(Class_ classNode){
        this.methodOverrideVisitor.setCurrentClassName(classNode.getName());
        ClassTreeNode classTreeNode = new ClassTreeNode(classNode, false, true, classMap);
        if(classMap.containsKey(classNode.getName())){

            errorHandler.register(2, classNode.getFilename(),
                    classNode.getLineNum(),
                    "Class with the same name already exists");
        }
        else{
            classMap.put(classNode.getName(),classTreeNode);
            classTreeNode.setParent(classMap.get(classNode.getParent()));

            classTreeNode.getMethodSymbolTable().enterScope();
            classTreeNode.getMethodSymbolTable().setParent(classTreeNode.getParent().getMethodSymbolTable());

            classTreeNode.getVarSymbolTable().enterScope();
            classTreeNode.getVarSymbolTable().setParent(classTreeNode.getParent().getVarSymbolTable());
            currentClassTreeNode = classTreeNode;
        }
        super.visit(classNode);
        new MethodOverrideVisitor(classMap, errorHandler).visit(classNode);
        return null;
    }

    /**
     * @param methodNode
     * @return
     */
    public Object visit(Method methodNode){
        if (currentClassTreeNode.getMethodSymbolTable().lookup(methodNode.getName()) !=
                null) {
            errorHandler.register(2, "Method overloading is verboten.");
        }
        currentClassTreeNode.getMethodSymbolTable().add(methodNode.getName(),methodNode);
        return null;
    }

    /**
     * Visit a field node
     * @param field
     * @return
     */
    public Object visit(Field field){
        currentClassTreeNode.getVarSymbolTable().add(field.getName(),field.getType());
        return null;
    }
}
