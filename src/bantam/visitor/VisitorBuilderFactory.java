package bantam.visitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ncameron on 3/1/2017.
 */
public class VisitorBuilderFactory {

    public static void main (String[] whatHaveIDONE) throws IOException {

        StringBuilder sb = new StringBuilder();
        sb.append("package bantam.visitor;\n\n");
        sb.append("import bantam.ast.*;\nimport java.util.function.Function;\n\n");
        sb.append("public class VisitorBuilder {\n\n");

        List<String> fieldStrings = new ArrayList<>();
        List<String> setterStrings = new ArrayList<>();
        List<String> overrideStrings = new ArrayList<>();

        Class visitorClass = Visitor.class;
        for (java.lang.reflect.Method m : visitorClass.getMethods()) {
            Class[] paramTypes = m.getParameterTypes();
            if (paramTypes.length == 1
                    && paramTypes[0] != long.class
                    && paramTypes[0] != Object.class) {

                Class paramType = paramTypes[0];
                fieldStrings.add(generateField(paramType));
                setterStrings.add(generateSetter(paramType));
                overrideStrings.add(generateOverride(paramType));
            }
        }

        for (String field : fieldStrings) {
            sb.append(field);
            sb.append("\n");
        }

        sb.append("\n\n");
        sb.append("\tpublic Visitor build() {\n");
        sb.append("\t\treturn new Visitor() {\n");
        for(String override : overrideStrings) {
            sb.append("\t\t");
            sb.append(override);
            sb.append("\n\n");
        }

        sb.append("\t};\n}\n\n");

        for (String setter : setterStrings) {
            sb.append("\t");
            sb.append(setter);
            sb.append("\n\n");
        }

        sb.append("}");

        File file = new File("."+File.separator+"src"+File.separator+"bantam"+
                File.separator+"visitor"+File.separator+"VisitorBuilder.java");
        if (!file.exists())
            file.createNewFile();
        OutputStream f = new FileOutputStream(file, false);
        f.write(sb.toString().getBytes());

        //System.out.println(sb.toString());

        f.close();

    }

    private static String generateField(Class clazz) {
        StringBuilder sb = new StringBuilder();
        sb.append("\tprivate Function<");
        sb.append(clazz.getName());
        sb.append(", Object> visit");
        sb.append(clazz.getName());
        sb.append(";");
        return sb.toString().replaceAll("bantam\\.ast\\.", "");
    }

    private static String generateSetter(Class clazz) {
        StringBuilder sb = new StringBuilder();
        sb.append("public void setVisit");
        sb.append(clazz.getName());
        sb.append("(Function<");
        sb.append(clazz.getName());
        sb.append(", Object> visitFunction) {\n");
        sb.append("\t\tthis.visit");
        sb.append(clazz.getName());
        sb.append(" = visitFunction;\n\t}");
        return sb.toString().replaceAll("bantam\\.ast\\.", "");
    }

    private static String generateOverride(Class clazz) {
        StringBuilder sb = new StringBuilder();
        sb.append("\t@Override\n\t\t\tpublic Object visit(");
        sb.append(clazz.getName());
        sb.append(" node) {\n\t\t\t\treturn null == visit");
        sb.append(clazz.getName());
        sb.append(" ? super.visit(node) : visit");
        sb.append(clazz.getName());
        sb.append(".apply(node);\n\t\t\t}");
        return sb.toString().replaceAll("bantam\\.ast\\.", "");
    }

}