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
    private StringBuilder nameBuilder = new StringBuilder("StringConst_");

    public Map<String, String> getStringConstants(Program ast){
        stringConstantMap = new HashMap<>();
        this.visit(ast);
        return stringConstantMap;
    }

    public Object visit(ConstStringExpr constStringExpr){

        this.nameBuilder.setLength(11);
        this.nameBuilder.append(stringConstantMap.entrySet().size());
        stringConstantMap.put(this.nameBuilder.toString(),
                constStringExpr.getConstant());

        return null;
    }

    public Object visit(Formal formal){
        return null;
    }

}
