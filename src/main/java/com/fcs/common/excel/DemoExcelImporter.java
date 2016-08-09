package com.fcs.common.excel;

import com.fcs.common.Strings;
import com.fcs.common.excel.importer.AbstractExcelImporter;
import com.fcs.common.excel.importer.vo.ExcelRecord;
import com.fcs.common.excel.importer.vo.ImportContext;
import com.fcs.common.excel.util.ExcelUtil;

import java.util.Date;
import java.util.List;



public class DemoExcelImporter extends AbstractExcelImporter<DemoExcelImporter.Demo> {

	@Override
	protected void initContext(ImportContext context) {
		context.put("date", new Date());
	}

	@Override
	protected String[] getExpectHeaders(ImportContext context) {
		return new String[] { "用户名", "密码", "年龄" };
	}

	@Override
	protected Demo parseRecord(ExcelRecord record, ImportContext context) {
		System.out.println("now = " + context.get("date"));
		String username = record.get("用户名");
		String password = record.get("密码");
		String age = record.get("年龄");
		if (Strings.isEmpty(username)) {
			record.putError("用户名", "【用户名】不能为空");
		}
		if (Strings.isEmpty(password)) {
			record.putError("密码", "【密码】不能为空");
		}
		if (Strings.isEmpty(age)) {
			record.putError("年龄", "【年龄】不能为空");
		}
		if (!ExcelUtil.isInteger(age)) {
			record.putError("年龄", "【年龄】不合法");
		}

		if (record.isError()) {
			return null;
		}

		int a = Integer.parseInt(age);

		Demo demo = new Demo();
		demo.setUsername(username);
		demo.setPassword(password);
		demo.setAge(a);
		return demo;
	}

	@Override
	protected int resolveEntity(List<Demo> list) {
		// persist or other
		return 0;
	}

	class Demo {

		String username;

		String password;

		int age;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}
	}

}
