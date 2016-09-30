package com.fcs.common.generate.jfinal;

/**
 * Created by Lucare.Feng on 2016/9/30.
 */
public class MyModelGenerator extends ModelGenerator {

    public MyModelGenerator(String modelPackageName, String modelOutputDir, boolean generateAnnotation) {
        super(modelPackageName, modelOutputDir, generateAnnotation);
    }

    @Override
    void genAnnotationImport(StringBuilder ret) {
        ret.append("import org.mission.dbclient.annotation.CTMapping;\n" +
                "import org.mission.dbclient.annotation.FCMapping;\r\n");
    }

    @Override
    void genAnnotation(ColumnMeta columnMeta, StringBuilder ret) {
        ret.append("\n\t@FCMapping(column = \""+columnMeta.attrName+"\", comment = \""+columnMeta.remarks+"\")\r\n");

    }
}
