package bantam.visitor;

import bantam.ast.Class_;
import bantam.ast.Program;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Created by Jacob on 01/03/17.
 */
public class ClassHierarchyVisitor extends Visitor {

    private ClassTreeNode classTreeRootNode;
    private Hashtable<String, ClassTreeNode> classMap;
    private ErrorHandler errorHandler = new ErrorHandler();

    public ClassHierarchyVisitor(){
        classMap = new Hashtable<>();
    }

    public ClassTreeNode buildClassTree(Program program, ErrorHandler errHandler, ClassTreeNode classTreeRootNode){
        this.classTreeRootNode = classTreeRootNode;
        this.errorHandler = errHandler;
        this.visit(program);
        hasCycles();
        return classTreeRootNode;
    }

    private void hasCycles(){
        Set<ClassTreeNode> traversed = new HashSet<>();
        hasCycles(traversed, classTreeRootNode);
    }

    private void hasCycles(Set<ClassTreeNode> traversed, ClassTreeNode currentNode){
        while(currentNode.getChildrenList().hasNext()) {
            ClassTreeNode currentChild = currentNode.getChildrenList().next();
            if(traversed.contains(currentChild)){
                //cyclical error
                errorHandler.register(2, currentChild.getASTNode().getFilename(),
                        currentChild.getASTNode().getLineNum(),
                        "The class inheritance tree is not well formed.");
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

        }
        //TODO add stuff for adding, making method tables
        return null;
    }
}
