package com.fcs.common.generate.jfinal;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Lucare.Feng on 2016/9/3.
 */
public class MetaBuilder {

    protected DataSource dataSouce;
    //    protected Dialect dialect = new MysqlDialect();
    protected Set<String> excludedTables = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

    protected Connection conn = null;
    protected DatabaseMetaData dbMeta = null;

    protected String[] removeTableNamePrefixes = null;

    protected TypeMapping typeMapping = new TypeMapping();

    public MetaBuilder(DataSource dataSouce) {
        if (dataSouce == null) {
            throw new IllegalArgumentException("dataSource can not be null.");

        }
        this.dataSouce = dataSouce;
    }

    public void addExcludedTable(String... excludedTables) {
        if (excludedTables != null) {
            for (String table : excludedTables) {
                this.excludedTables.add(table);
            }
        }
    }

    /**
     * 设置需要被移除的表名前缀，仅用于生成modelName 与 baseModelName
     * 例如表名  "osc_account". 移除前缀"osc_"变为account
     * @param removeTableNamePrefixes
     */
    public void setRemoveTableNamePrefixes(String... removeTableNamePrefixes) {
        this.removeTableNamePrefixes = removeTableNamePrefixes;
    }

    public void setTypeMapping(TypeMapping typeMapping) {
        if (typeMapping != null) {
            this.typeMapping = typeMapping;
        }
    }

    public List<TableMeta> build() {
        System.out.println("Build TableMeta...");
        try {
            conn = dataSouce.getConnection();
            dbMeta = conn.getMetaData();

            List<TableMeta> ret = new ArrayList<>();
            buildTableNames(ret);
            for (TableMeta tableMeta : ret) {
                buildPrimaryKey(tableMeta);
                buildColumnMetas(tableMeta);
            }

            return ret;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    new RuntimeException(e);
                }

            }
        }
    }

    protected boolean isSkipTable(String tableName) {
        return false;
    }

    protected String buildModelName(String tableName) {
        if (removeTableNamePrefixes != null) {
            for (String prefix : removeTableNamePrefixes) {
                if (tableName.startsWith(prefix)) {
                    tableName = tableName.replaceFirst(prefix, "");
                    break;
                }
            }
        }

        /**
         * TODO dialect
         */

        return StrKit.firstCharToUpperCase(StrKit.toCamelCase(tableName));
    }

    protected String buildBaseModelName(String modelName) {
        return "Base" + modelName;
    }

    protected ResultSet getTablesResultSet() throws SQLException{
//        String schemaPattern = dialect instanceof OracleDialect ? dbMeta.getUserName() : null;
        return dbMeta.getTables(conn.getCatalog(), null, null, new String[]{"TABLE", "VIEW"});
    }

    protected void buildTableNames(List<TableMeta> ret) throws SQLException {
        ResultSet rs = getTablesResultSet();
        while (rs.next()) {
            String tableName = rs.getString("TABLE_NAME");

            if (excludedTables.contains(tableName)) {
                System.out.println("Skip table :" + tableName);
                continue;
            }

            if (isSkipTable(tableName)) {
                System.out.println("Skip table :" + tableName);
                continue;
            }

            TableMeta tableMeta = new TableMeta();
            tableMeta.name = tableName;
            tableMeta.remarks = rs.getString("REMARKS");

            tableMeta.modelName = buildModelName(tableName);
            tableMeta.baseModelName = buildBaseModelName(tableMeta.modelName);
            ret.add(tableMeta);
        }
        rs.close();
    }

    protected void buildPrimaryKey(TableMeta tableMeta) throws SQLException{
        ResultSet rs = dbMeta.getPrimaryKeys(conn.getCatalog(), null, tableMeta.name);

        String primaryKey = "";
        int index = 0;
        while (rs.next()) {
            if (index++ > 0)
                primaryKey += ",";
            primaryKey += rs.getString("COLUMN_NAME");
        }
        tableMeta.primaryKey = primaryKey;
        rs.close();
    }

    protected void buildColumnMetas(TableMeta tableMeta) throws SQLException {
        String sql = "select * from `" + tableMeta.name + "` where 1 = 2";
        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery(sql);
        ResultSetMetaData rsmd = rs.getMetaData();

        for (int i = 1; i<=rsmd.getColumnCount(); i++) {
            ColumnMeta cm = new ColumnMeta();
            cm.name = rsmd.getColumnName(i);

            String colClassName = rsmd.getColumnClassName(i);
            if ("java.sql.Timestamp".equals(colClassName) || "java.sql.Time".equals(colClassName) ||
                    "java.sql.Date".equals(colClassName)){
                tableMeta.isImportDate = true;
            }
            String typeStr = typeMapping.getType(colClassName);
            if (typeStr != null) {
                cm.javaType = typeStr;
            } else {
                int type = rsmd.getColumnType(i);
                if (type == Types.BINARY || type == Types.VARBINARY || type == Types.BLOB) {
                    cm.javaType = "byte[]";
                } else if (type == Types.CLOB || type == Types.NCLOB) {
                    cm.javaType = "java.lang.String";
                } else{
                    cm.javaType = "java.lang.String";
                }
            }

            // 构造字段对应的属性名 attrName
            cm.attrName = buildAttrName(cm.name);
            tableMeta.columnMetas.add(cm);
        }

        rs.close();
        stm.close();
    }

    /**
     * 构造 colName 所对应的 attrName，mysql 数据库建议使用小写字段名或者驼峰字段名
     * Oralce 反射将得到大写字段名，所以不建议使用驼峰命名，建议使用下划线分隔单词命名法
     */
    protected String buildAttrName(String colName) {
//        if (dialect instanceof OracleDialect) {
//            colName = colName.toLowerCase();
//        }
        return StrKit.toCamelCase(colName);
    }
}
