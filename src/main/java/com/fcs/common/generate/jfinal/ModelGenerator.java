package com.fcs.common.generate.jfinal;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by Lucare.Feng on 2016/9/4.
 */
public abstract class ModelGenerator {

    protected String packageTemplate = "package %s;%n%n";
    protected String importTemplate = "import %s;%n%n";
    protected String classComments = "/**\n" +
            " * Generated by Util.\n" +
            " */\n" +
            "@SuppressWarnings(\"serial\")\n";
    protected String classDefineTemplate = "public class %s {%n";
    protected String modelPackageName;
    protected String baseModelPackageName;
    protected String modelOutputDir;
    protected boolean generateAnnotation = false;

    public ModelGenerator(String modelPackageName, String modelOutputDir) {
        if (StrKit.isBlank(modelPackageName))
            throw new IllegalArgumentException("modelPackageName can not be blank.");

        if (modelPackageName.contains("/") || modelPackageName.contains("\\"))
            throw new IllegalArgumentException("modelPackageName error :"+modelPackageName);

//        if (StrKit.isBlank(baseModelPackageName))
//            throw new IllegalArgumentException("baseModelPackageName can not be blank.");
//
//        if (baseModelPackageName.contains("/") || baseModelPackageName.contains("\\"))
//            throw new IllegalArgumentException("baseModelPackageName error : " + baseModelPackageName);

        if (StrKit.isBlank(modelOutputDir))
            throw new IllegalArgumentException("modelOutPutDir can not be blank.");

        this.modelPackageName = modelPackageName;
//        this.baseModelPackageName = baseModelPackageName;
        this.modelOutputDir = modelOutputDir;
    }

    public void generate(List<TableMeta> tableMetas) {
        System.out.println("Generate model...");
        int i = 1;
        for (TableMeta tableMeta : tableMetas) {
            genModelContent(tableMeta);
            System.out.println("Generating " + i++ + " table....");
        }
        wirtToFile(tableMetas);
    }

    protected void genModelContent(TableMeta tableMeta) {
        StringBuilder ret = new StringBuilder();
        genPackage(ret);
        genImport(tableMeta, ret);
        genClassDefine(tableMeta, ret);
        genAttrs(tableMeta, ret);
        genGetAndSet(tableMeta, ret);
        ret.append(String.format("}%n"));
        tableMeta.modelContent = ret.toString();
    }

    protected void genPackage(StringBuilder ret) {
        ret.append(String.format(packageTemplate, modelPackageName));
    }

    protected void genImport(TableMeta tableMeta, StringBuilder ret) {
        if (tableMeta.isImportDate) {
            ret.append(String.format(importTemplate, "java.util.Date"));
        }

        if (generateAnnotation) {
            genAnnotationImport(ret);
        }
    }

    protected void genClassDefine(TableMeta tableMeta, StringBuilder ret) {
        ret.append(classComments);
        genClassAnnotion(tableMeta, ret);
        ret.append(String.format(classDefineTemplate, tableMeta.modelName));
    }

    protected void genAttrs(TableMeta tableMeta, StringBuilder ret) {
        for (ColumnMeta columnMeta : tableMeta.columnMetas) {
            if (generateAnnotation) {
                genAnnotation(columnMeta, ret);
            }
            ret.append("\tprivate " + columnMeta.javaType + " " + columnMeta.attrName + ";\r\n");
        }
    }

    protected void genGetAndSet(TableMeta tableMeta, StringBuilder ret) {
        for (ColumnMeta columnMeta : tableMeta.columnMetas) {
            ret.append("\n\tpublic void set" + ColumnMeta.initcap(columnMeta.attrName) + "(" + columnMeta.javaType + " " +
                    columnMeta.attrName + "){\r\n");
            ret.append("\t\tthis." + columnMeta.attrName + " = " + columnMeta.attrName + ";\r\n");
            ret.append("\t}\r\n");
            ret.append("\n\tpublic " + columnMeta.javaType + " get" + ColumnMeta.initcap(columnMeta.attrName) + "(){\r\n");
            ret.append("\t\treturn " + columnMeta.attrName + ";\r\n");
            ret.append("\t}\r\n");
        }

    }

    protected void wirtToFile(List<TableMeta> tableMetas) {
        try {
            for (TableMeta tableMeta : tableMetas) {
                wirtToFile(tableMeta);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    protected void wirtToFile(TableMeta tableMeta) throws IOException{
        File dir = new File(modelOutputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String target = modelOutputDir + File.separator + tableMeta.modelName + ".java";

        File file = new File(target);
        if (file.exists()) {
            return;  //若model存在则不覆盖
        }

        FileWriter fw = new FileWriter(file);
        try {
            fw.write(tableMeta.modelContent);
        } finally {
            fw.close();
        }

    }

    public void setGenerateAnnotation(boolean generateAnnotation) {
        this.generateAnnotation = generateAnnotation;
    }

    abstract void genClassAnnotion(TableMeta tableMeta, StringBuilder ret);

    abstract void genAnnotationImport(StringBuilder ret);

    abstract void genAnnotation(ColumnMeta columnMeta,StringBuilder ret);

}
