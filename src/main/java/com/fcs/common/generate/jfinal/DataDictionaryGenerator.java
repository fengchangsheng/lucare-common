package com.fcs.common.generate.jfinal;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucare.Feng on 2016/9/4.
 * 数据字典生成器
 */
public class DataDictionaryGenerator {

    protected DataSource dataSource;
    protected String dataDictionaryOutputDir;
    protected String dataDictionaryFileName = "_DataDictionary.txt";

    public DataDictionaryGenerator(DataSource dataSource, String dataDictionaryOutputDir) {
        this.dataSource = dataSource;
        this.dataDictionaryOutputDir = dataDictionaryOutputDir;
    }

    public void setDataDictionaryOutputDir(String dataDictionaryOutputDir) {
        if (StrKit.notBlank(dataDictionaryOutputDir))
            this.dataDictionaryOutputDir = dataDictionaryOutputDir;
    }

    public void setDataDictionaryFileName(String dataDictionaryFileName) {
        if (StrKit.notBlank(dataDictionaryFileName))
            this.dataDictionaryFileName = dataDictionaryFileName;
    }

    public void generate(List<TableMeta> tableMetas) {
        System.out.println("Generate DataDictionary file...");
        rebuildColumnMetas(tableMetas);

        StringBuilder ret = new StringBuilder();
        for (TableMeta tableMeta : tableMetas) {
            generateTable(tableMeta, ret);
        }
        wirtToFile(ret);
    }

    protected void generateTable(TableMeta tableMeta, StringBuilder ret) {
        ret.append("Table: ").append(tableMeta.name);
        if (StrKit.notBlank(tableMeta.remarks)) {
            ret.append("\tRemark: ").append(tableMeta.remarks);
        }
        ret.append("\n");

        String sparateLine = genSeparateLine(tableMeta);
        ret.append(sparateLine);
        genTableHead(tableMeta, ret);
        ret.append(sparateLine);
        for (ColumnMeta columnMeta : tableMeta.columnMetas) {
            genColumn(tableMeta, columnMeta, ret);
        }
        ret.append(sparateLine);
        ret.append("\n");
    }

    /*
	-----------+---------+------+-----+---------+----------------
	 Field     | Type    | Null | Key | Default | Remarks
	-----------+---------+------+-----+---------+----------------
	 id		   | int(11) | NO	| PRI | NULL	| remarks here
	*/
    protected void genCell(int columnMaxLen, String preChar, String value, String fillChar, String postChar, StringBuilder ret) {
        ret.append(preChar);
        ret.append(value);
        for (int i=0, n=columnMaxLen-value.length() + 1; i<n; i++)
            ret.append(fillChar);	// 值后的填充字符，值为 " "、"-"
        ret.append(postChar);
    }

    protected String genSeparateLine(TableMeta tableMeta) {
        StringBuilder ret = new StringBuilder();
        genCell(tableMeta.colNameMaxLen,"-", "---", "-", "+",  ret);
        genCell(tableMeta.colTypeMaxLen, " ", "Type", " ", "|", ret);
        genCell("Null".length(), " ", "Null",  " ","|", ret);
        genCell("Key".length(), " ", "Key",  " ","|", ret);
        genCell(tableMeta.colDefaultValueMaxLen, " ", "Default", " ", "|", ret);
        genCell("Remarks".length(), " ", "Remarks", " ", "", ret);
        ret.append("\n");
        return ret.toString();
    }

    protected void genTableHead(TableMeta tm, StringBuilder ret) {
        genCell(tm.colNameMaxLen, " ", "Field", " ", "|",  ret);
        genCell(tm.colTypeMaxLen, " ", "Type", " ", "|", ret);
        genCell("Null".length(), " ", "Null",  " ","|", ret);
        genCell("Key".length(), " ", "Key",  " ","|", ret);
        genCell(tm.colDefaultValueMaxLen, " ", "Default", " ", "|", ret);
        genCell("Remarks".length(), " ", "Remarks", " ", "", ret);
        ret.append("\n");
    }

    protected void genColumn(TableMeta tableMeta, ColumnMeta columnMeta, StringBuilder ret) {
        genCell(tableMeta.colNameMaxLen, " ", columnMeta.name, " ", "|",  ret);
        genCell(tableMeta.colTypeMaxLen, " ", columnMeta.type, " ", "|", ret);
        genCell("Null".length(), " ", columnMeta.isNullable, " ", "|", ret);
        genCell("Key".length(), " ", columnMeta.isPrimaryKey, " ", "|", ret);
        genCell(tableMeta.colDefaultValueMaxLen, " ", columnMeta.defaultValue, " ", "|", ret);
        genCell("Remarks".length(), " ", columnMeta.remarks, " ", "", ret);
        ret.append("\n");
    }

    protected void rebuildColumnMetas(List<TableMeta> tableMetas) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            DatabaseMetaData dbMeta = conn.getMetaData();
            for (TableMeta tableMeta : tableMetas) {
                tableMeta.columnMetas = new ArrayList<ColumnMeta>();
                ResultSet rs = dbMeta.getColumns(conn.getCatalog(), null, tableMeta.name, null);
                while (rs.next()) {
                    ColumnMeta columnMeta = new ColumnMeta();
                    columnMeta.name = rs.getString("COLUMN_NAME");
                    columnMeta.type = rs.getString("TYPE_NAME");

                    if (columnMeta.type == null) {
                        columnMeta.type = "";
                    }

                    int columnSize = rs.getInt("COLUMN_SIZE");
                    if (columnSize > 0) {
                        columnMeta.type = columnMeta.type + "(" + columnSize;
                        int decimalDigits = rs.getInt("DECIMAL_DIGITS");
                        if (decimalDigits > 0) {
                            columnMeta.type = columnMeta.type + "," + decimalDigits;
                        }
                        columnMeta.type = columnMeta.type + ")";
                    }

                    columnMeta.isNullable = rs.getString("IS_NULLABLE");
                    if (columnMeta.isNullable == null) {
                        columnMeta.isNullable = "";
                    }

                    columnMeta.isPrimaryKey = "   ";
                    String[] keys = tableMeta.primaryKey.split(",");
                    for (String key : keys) {
                        if (key.equalsIgnoreCase(columnMeta.name)) {
                            columnMeta.isPrimaryKey = "PRI";
                            break;
                        }
                    }

                    columnMeta.defaultValue = rs.getString("COLUMN_DEF");	// 默认值
                    if (columnMeta.defaultValue == null)
                        columnMeta.defaultValue = "";

                    columnMeta.remarks = rs.getString("REMARKS");			// 备注
                    if (columnMeta.remarks == null)
                        columnMeta.remarks = "";

                    if (tableMeta.colNameMaxLen < columnMeta.name.length())
                        tableMeta.colNameMaxLen = columnMeta.name.length();
                    if (tableMeta.colTypeMaxLen < columnMeta.type.length())
                        tableMeta.colTypeMaxLen = columnMeta.type.length();
                    if (tableMeta.colDefaultValueMaxLen < columnMeta.defaultValue.length())
                        tableMeta.colDefaultValueMaxLen = columnMeta.defaultValue.length();

                    tableMeta.columnMetas.add(columnMeta);
                }
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
        }

    }

    /**
     * _DataDictionary.txt 覆盖写入
     */
    protected void wirtToFile(StringBuilder ret) {
        FileWriter fw = null;
        try {
            File dir = new File(dataDictionaryOutputDir);
            if (!dir.exists())
                dir.mkdirs();

            String target = dataDictionaryOutputDir + File.separator + dataDictionaryFileName;
            fw = new FileWriter(target);
            fw.write(ret.toString());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (fw != null){
                try {
                    fw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }
}
