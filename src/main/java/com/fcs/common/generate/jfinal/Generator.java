package com.fcs.common.generate.jfinal;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by Lucare.Feng on 2016/9/4.
 */
public class Generator {

    protected MetaBuilder metaBuilder;
    protected ModelGenerator modelGenerator;
    protected DataDictionaryGenerator dataDictionaryGenerator;
    protected boolean generateDataDictionary = false;
    protected boolean generateAnnotation = false;

    public Generator(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException("dataSource can not be null.");
        }

        this.metaBuilder = new MetaBuilder(dataSource, generateAnnotation);
        this.modelGenerator = null;
        this.dataDictionaryGenerator = null;
    }

    public Generator(DataSource dataSource, ModelGenerator modelGenerator) {
        if (dataSource == null) {
            throw new IllegalArgumentException("dataSource can not be null.");
        }

        if (modelGenerator == null) {
            throw new IllegalArgumentException("modelGenerator can not be null");
        }

        this.metaBuilder = new MetaBuilder(dataSource, generateAnnotation);
        this.modelGenerator = modelGenerator;
        this.dataDictionaryGenerator = new DataDictionaryGenerator(dataSource, modelGenerator.modelOutputDir);
    }

    /**
     * 设置需要被移除的表名前缀,仅用于生成 modelName 与 baseModelName
     * @param removeTableNamePrefixes
     */
    public void setRemovedTableNamePrefixes(String... removeTableNamePrefixes) {
        metaBuilder.setRemoveTableNamePrefixes(removeTableNamePrefixes);
    }

    /**
     * 添加不需要处理的数据表
     * @param excludedTables
     */
    public void addExcludedTable(String... excludedTables) {
        metaBuilder.addExcludedTable(excludedTables);
    }

    /**
     * 设置是否生成数据字典 Dictionary 文件  默认不生成
     * @param generateDataDictionary
     */
    public void setGenerateDataDictionary(boolean generateDataDictionary) {
        this.generateDataDictionary = generateDataDictionary;
    }

    /**
     * 设置数据字典 DataDictionary 文件输出目录, 默认与 ModelOutputDir 相同
     * @param dataDictionaryOutDir
     */
    public void setDataDictionaryOutDir(String dataDictionaryOutDir) {
        if (this.dataDictionaryGenerator != null) {
            this.dataDictionaryGenerator.setDataDictionaryOutputDir(dataDictionaryOutDir);
        }
    }

    /**
     * 设置数据字典 DataDictionary 文件输出目录,默认值为"_DataDictionary.txt"
     * @param dataDictionaryFileName
     */
    public void setDataDictionaryFileName(String dataDictionaryFileName) {
        if (dataDictionaryGenerator != null) {
            dataDictionaryGenerator.setDataDictionaryFileName(dataDictionaryFileName);
        }
    }

    /**
     * 设置是否生成字段注解  默认不生成
     *
     * @param generateAnnotation
     */
    public void setGenerateAnnotation(boolean generateAnnotation) {
        this.generateAnnotation = generateAnnotation;
    }

    public void generate() {
        long start = System.currentTimeMillis();
        List<TableMeta> tableMetas = metaBuilder.build();
        if (tableMetas.size() == 0) {
            System.out.println("TableMeta 数量为 0,不生成任何文件");
            return;
        }

        if (modelGenerator != null) {
            modelGenerator.generate(tableMetas);
        }

        if (dataDictionaryGenerator != null && generateDataDictionary) {
            dataDictionaryGenerator.generate(tableMetas);
        }

        long useTime = (System.currentTimeMillis() - start) / 1000;
        System.out.println("Generate complete in " + useTime + "seconds.");
    }
}
