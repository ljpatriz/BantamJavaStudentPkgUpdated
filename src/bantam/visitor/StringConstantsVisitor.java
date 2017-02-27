package bantam.visitor;

import bantam.ast.ConstStringExpr;
import bantam.ast.Formal;
import bantam.ast.Program;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jacob on 27/02/17.
 */
public class StringConstantsVisitor extends Visitor{

    private Map<String, String> stringConstantMap;

    public Map<String, String> getStringConstants(Program ast){
        stringConstantMap = new HashMap<String,String>();
        this.visit(ast);
        return stringConstantMap;
    }

    public Object visit(ConstStringExpr constStringExpr){
        String name = "StringConst_"+ stringConstantMap.entrySet().size();
        stringConstantMap.put(name, constStringExpr.getConstant());
        return null;
    }

    public Object visit(Formal formal){
        return null;
    }

}
