package bantam.visitor;

import bantam.ast.Class_;
import bantam.ast.Program;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;

import java.util.*;
import java.util.Map.Entry;

/**
 * Created by Jacob on 01/03/17.
 */
public class ClassHierarchyVisitor extends Visitor {

    private ClassTreeNode classTreeRootNode;
    private Hashtable<String, ClassTreeNode> classMap;
    private ErrorHandler errorHandler;


    public ClassHierarchyVisitor(ErrorHandler errorHandler, Hashtable<String, ClassTreeNode> classMap){
        this.errorHandler = errorHandler;
        this.classMap = classMap;
    }

    public ClassTreeNode buildClassTree(Program program, ClassTreeNode classTreeRootNode){
        this.classTreeRootNode = classTreeRootNode;
        this.visit(program);
        hasCycles();
        return classTreeRootNode;
    }

    private void hasCycles(){
        Set<ClassTreeNode> traversed = new HashSet<>();
        hasCycles(traversed, classTreeRootNode);
    }

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

        ClassTreeNode classTreeNode = new ClassTreeNode(classNode, false, true, classMap);
        if(classMap.containsKey(classNode.getName())){

            errorHandler.register(2, classNode.getFilename(),
                    classNode.getLineNum(),
                    "Class with the same name already exists");

        }
        else{
            classMap.put(classNode.getName(),classTreeNode);
            classTreeNode.setParent(classMap.get(classNode.getParent()));

        }
        //TODO add stuff for adding, making method tables
        return null;
    }
}
