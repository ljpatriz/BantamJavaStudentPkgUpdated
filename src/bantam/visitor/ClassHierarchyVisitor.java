package bantam.visitor;

import bantam.ast.Class_;
import bantam.ast.Program;
import bantam.util.ClassTreeNode;
import bantam.util.ErrorHandler;

import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by Jacob on 01/03/17.
 */
public class ClassHierarchyVisitor extends Visitor {

    private ClassTreeNode classTreeRootNode;
    private Hashtable<String, ClassTreeNode> classMap;
    private ErrorHandler errorHandler;

    public ClassHierarchyVisitor(){
        classMap = new Hashtable<>();
    }

    public ClassTreeNode buildClassTree(Program program, ErrorHandler errHandler, ClassTreeNode classTreeRootNode){
        this.classTreeRootNode = classTreeRootNode;
        this.visit(program);
        if(hasCycles()){
            errHandler.register(2, "The class inheritance tree has is not well formed.");
            //TODO change to include line number
        }
        this.errorHandler = errHandler;
        return classTreeRootNode;
    }

    private boolean hasCycles() {
        //TODO don't just return false
        return false;
    }

    /**
     * Function for visiting the class Node
     * @param classNode
     * @return
     */
    public Object visit(Class_ classNode){
        ClassTreeNode classTreeNode = new ClassTreeNode(classNode, false, true, classMap);
        if(classMap.containsKey(classNode.getName())){
           errorHandler.register(2, classNode.getFilename(), classNode.getLineNum(), "Class with the same name already exists");
        }
        else{
            classMap.put(classNode.getName(),classTreeNode);
        }
        //TODO add stuff for adding, making method tables
        return null;
    }
}
