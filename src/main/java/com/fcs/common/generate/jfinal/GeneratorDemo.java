package com.fcs.common.generate.jfinal;

import com.alibaba.druid.pool.DruidDataSource;
import javax.sql.DataSource;


/**
 * GeneratorDemo
 */
public class GeneratorDemo {
	
	public static DataSource getDataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");

		dataSource.setUsername("root");
		dataSource.setPassword("root");
		dataSource.setUrl("jdbc:mysql://112.74.217.22:3306/train2");
		dataSource.setInitialSize(5);
		dataSource.setMinIdle(1);
		dataSource.setMaxActive(10); // 启用监控统计功能  dataSource.setFilters("stat");// for mysql  dataSource.setPoolPreparedStatements(false);

		return dataSource;
	}
	
	public static void main(String[] args) {
		// base model 所使用的包名
//		String baseModelPackageName = "com.demo.common.model.base";
		// base model 文件保存路径
//		String baseModelOutputDir = PathKit.getWebRootPath() + "/../src/com/demo/common/model/base";
		
		// model 所使用的包名 (MappingKit 默认使用的包名)
		String modelPackageName = "com.fcs.common.model";
		// model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
		String modelOutputDir = PathKit.getWebRootPath() + "/src/main/java/com/fcs/common/model";
		
		// 创建生成器
		Generator gernerator = new Generator(getDataSource(), modelPackageName, modelOutputDir);
		// 添加不需要生成的表名
		gernerator.addExcludedTable("adv");
		// 设置是否生成字典文件
		gernerator.setGenerateDataDictionary(false);
		// 设置需要被移除的表名前缀用于生成modelName。例如表名 "osc_user"，移除前缀 "osc_"后生成的model名为 "User"而非 OscUser
		gernerator.setRemovedTableNamePrefixes("t_");
		// 生成
		gernerator.generate();
	}
}




