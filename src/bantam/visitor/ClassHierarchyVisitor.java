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
<<<<<<< HEAD
    private ErrorHandler errorHandler;
=======
    private ErrorHandler errorHandler = new ErrorHandler();
>>>>>>> cbc8edfd1317f424fb4d816af1071b55c260fe36

    public ClassHierarchyVisitor(){
        classMap = new Hashtable<>();
    }

    public ClassTreeNode buildClassTree(Program program, ErrorHandler errHandler, ClassTreeNode classTreeRootNode){
        this.classTreeRootNode = classTreeRootNode;
        this.visit(program);
<<<<<<< HEAD
        if(hasCycles()){
            errHandler.register(2, "The class inheritance tree has is not well formed.");
            //TODO change to include line number
        }
        this.errorHandler = errHandler;
=======
        buildInheritanceTree();
        hasCycles();
>>>>>>> cbc8edfd1317f424fb4d816af1071b55c260fe36
        return classTreeRootNode;
    }

    private void hasCycles(){
        Hashtable<ClassTreeNode, String> traversedClasses = new Hashtable<>();
        hasCycles(traversedClasses,classTreeRootNode);
    }

    private void hasCycles(Hashtable<ClassTreeNode, String> traversedClasses, ClassTreeNode currentNode ){
        while(currentNode.getChildrenList().hasNext()) {
            ClassTreeNode currentChild = currentNode.getChildrenList().next();
            if(traversedClasses.contains(currentChild)){
                //cyclical error
                errorHandler.register(2, currentChild.getASTNode().getFilename(),
                        currentChild.getASTNode().getLineNum(), "The class inheritance tree is not well formed.");
                //TODO more specific error regarding which class is causing problems
            }
            else{
                traversedClasses.put(currentChild, currentChild.getName());
                hasCycles(traversedClasses,currentChild);
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
<<<<<<< HEAD
           errorHandler.register(2, classNode.getFilename(), classNode.getLineNum(), "Class with the same name already exists");
=======
            errorHandler.register(2, classNode.getFilename(),
                    classNode.getLineNum(), "The class already exists.");
>>>>>>> cbc8edfd1317f424fb4d816af1071b55c260fe36
        }
        else{
            classMap.put(classNode.getName(),classTreeNode);
        }
        //TODO add stuff for adding, making method tables
        return null;
    }
}
