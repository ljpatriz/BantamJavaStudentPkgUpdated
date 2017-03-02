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

    public ClassHierarchyVisitor(){
        classMap = new Hashtable<>();
    }

    public ClassTreeNode buildClassTree(Program program, ErrorHandler errHandler){
        classTreeRootNode = buildBuiltinTree();
        this.visit(program);
        buildInheritanceTree();
        if(hasCycles()){
            //TODO create a specific error class for this
            errHandler.register(2, "The class inheritance tree has is not well formed.");
            //TODO change to include line number
        }
        return classTreeRootNode;
    }

    private void hasCycles(){
        Hashtable<String, ClassTreeNode> tempMap = new Hashtable<>();
        while(classTreeRootNode.getChildrenList().hasNext()) {
            ClassTreeNode currentChild = classTreeRootNode.getChildrenList().next();
            if(tempMap.contains(currentChild)){
                //cyclical error
                //ErrorStatement
            }
            else{
                tempMap.put(currentChild.getName(), currentChild);
                hasCycles(tempMap,currentChild);
            }

        }
    }

    private void hasCycles(Hashtable<String, ClassTreeNode> tempMap, ClassTreeNode currentNode ){
        while(currentNode.getChildrenList().hasNext()) {
            ClassTreeNode currentChild = currentNode.getChildrenList().next();
            if(tempMap.contains(currentChild)){
                //cyclical error
                //ErrorStatement
            }
            else{
                tempMap.put(currentChild.getName(), currentChild);
                hasCycles(tempMap,currentChild);
            }

        }
    }


    /**
     * Function for building all the tree of default classes
     * @return
     */
    private ClassTreeNode buildBuiltinTree(){
        //classTreeRootNode = new ClassTreeNode();
        //TODO find out how to do this
        //return classTreeRootNode;
        return null;
    }

    /**
     * Function for visiting the class Node
     * @param classNode
     * @return
     */
    public Object visit(Class_ classNode){
        ClassTreeNode classTreeNode = new ClassTreeNode(classNode,true,false,classMap);

        if(classMap.containsKey(classNode.getName())){
            throw new RuntimeException("Class Tree has duplicate class names");
        }
        
        else{
            classMap.put(classNode.getName(),classTreeNode);
        }

        //TODO add stuff for adding, making method tables
        return null;
    }

    /**
     * Builds the inheritance tree using the classMap that was already generated
     */
    private void buildInheritanceTree(){
        for(Entry<String, ClassTreeNode> entry:classMap.entrySet()){
            ClassTreeNode classTreeNode = entry.getValue();
            String parent = classTreeNode.getASTNode().getParent();
            ClassTreeNode parentTreeNode = classMap.get(parent);
            parentTreeNode.addChild(classTreeNode);
            classTreeNode.setParent(parentTreeNode);
        }
    }
}
