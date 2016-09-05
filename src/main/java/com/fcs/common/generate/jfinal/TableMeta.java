package com.fcs.common.generate.jfinal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucare.Feng on 2016/9/2.
 */
public class TableMeta {

    public String name;
    public String remarks;
    public String primaryKey;
    public List<ColumnMeta> columnMetas = new ArrayList<>();
    public boolean isImportDate = false;

    public String baseModelName;
    public String baseModelContent;

    public String modelName;
    public String modelContent;

    public int colNameMaxLen = "Field".length();
    public int colTypeMaxLen = "Type".length();
    public int colDefaultValueMaxLen = "Default".length();



}
