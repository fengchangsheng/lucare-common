package com.fcs.common.excel.importer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import com.fcs.common.Strings;
import com.fcs.common.excel.importer.vo.ExcelData;
import com.fcs.common.excel.importer.vo.ExcelParserException;
import com.fcs.common.excel.importer.vo.ExcelRecord;
import com.fcs.common.excel.importer.vo.ImportContext;
import com.fcs.common.excel.util.ExcelUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 导入基础类
 * 
 * @author yanxin
 * 
 */
public abstract class AbstractExcelImporter<T> implements IExcelImporter {
	private static final Logger logger = LoggerFactory.getLogger(AbstractExcelImporter.class.getSimpleName());
	
	/**
	 * 传递异常的标签
	 * */
	protected static final String EXCEPTIONREMARK = "其他异常";
	
	/**
	 * 初始化context
	 * @param context
	 */
	protected abstract void initContext(ImportContext context);

	/**
	 * 需要子类提供预期的头部数组
	 * 
	 * @return
	 */
	protected abstract String[] getExpectHeaders(ImportContext context);

	/**
	 * 定义sheet下标
	 * 
	 * @return
	 */
	protected int getSheetIndex() {
		return 0;
	}

	/**
	 * 获取开始解析的行号
	 * @return
	 */
	protected int getHeaderRowIndex() {
		return 0;
	}

	/**
	 * 解析具体的记录
	 * 
	 * @param record
	 * @return false - 代表有问题
	 */
	protected abstract T parseRecord(ExcelRecord record, ImportContext context);

	/**
	 * 
	 * @param list
	 * @return -成功条数
	 */
	protected abstract int resolveEntity(List<T> list);

	/**
	 * 由exceldata 解析出具体对象列表
	 * 
	 * @param excelData
	 * @param context
	 * @return
	 */
	protected List<T> parseRecords(ExcelData excelData, ImportContext context) {
		Iterator<ExcelRecord> it = excelData.getData().iterator();
		List<T> list = new ArrayList<>();
		while (it.hasNext()) {
			ExcelRecord record = it.next();
			try {
				T t = parseRecord(record, context);
				if (!record.isError() && t != null) {
					list.add(t);
					it.remove();//如果成功解析出，清掉原数据
				}
			}
			catch(Exception ex) {
				ex.printStackTrace();//捕获解析的异常
				logger.info(ex.getMessage());
				record.putError(EXCEPTIONREMARK, ex.getMessage());
			}			
		}
		return list;
	}

	/**
	 * 导入入口
	 * 
	 * @param ips
	 * @param context
	 * @return
	 * @throws ExcelParserException
	 * @throws IOException
	 */
	@Override
	public boolean doImport(InputStream ips, ImportContext context) throws ExcelParserException, IOException {
		//解析excel
		ExcelData excelData = resolve(ips, getSheetIndex(), getHeaderRowIndex(), context);

		// init context
		initContext(context);

		//解析记录
		List<T> list = parseRecords(excelData, context);
		
		//处理记录
		int prepareSize = list.size();
		int success = resolveEntity(list); //成功的记录数

		int fail = prepareSize - success + excelData.getData().size(); //还剩下的记录为错误记录

		// 生成导入结果消息
		StringBuilder resultMsg = new StringBuilder("成功导入 [ ").append(success).append(" ] 条记录, 失败 [ ").append(fail).append(" ] 条记录");
		context.setResultMsg(resultMsg.toString());

		if (fail == 0) {
			return true;
		}

		//处理错误的数据
		handleErrorRecords(excelData.getHeaders(), excelData.getData(), context);

		return false;
	}
	@Override
	public boolean doImport(byte[] bytes, ImportContext context) throws ExcelParserException, IOException {
		InputStream inputStream = new ByteArrayInputStream(bytes);
		return doImport(inputStream, context);
	}

	/**
	 * 处理存在错误的记录
	 * 
	 * @param headers
	 * @param errorRecords
	 * @param context
	 */
	protected void handleErrorRecords(String[] headers, List<ExcelRecord> errorRecords, ImportContext context) {
		// 生成错误数据
		StringBuilder errorMsg = new StringBuilder();
		for (int i = 0; i < errorRecords.size(); i++) {
			ExcelRecord record = errorRecords.get(i);
			for (int j = 0; j < headers.length; j++) {
				String key = headers[j];
				String msg = record.getError(key);
				if (Strings.isNotEmpty(msg)) {
					errorMsg.append("第").append(record.getRowIndex()+1).append("行，").append(msg).append("\r\n");
				}
			}
			String msg = record.getError(EXCEPTIONREMARK);
			if (Strings.isNotEmpty(msg)) {
				errorMsg.append("第").append(record.getRowIndex()+1).append("行，").append(msg).append("\r\n");
			}
		}
		context.setErrorMsg(errorMsg.toString());
	}

	/**
	 * 解析头部
	 * 
	 * @param sheet
	 * @param headerRowIndex
	 * @return
	 */
	protected String[] resolveHeaders(Sheet sheet, int headerRowIndex, ImportContext context) throws ExcelParserException {
		// 获取抬头行
		String[] headers = ExcelUtil.readRow(sheet, headerRowIndex);
		if (headers == null) {
			throw new ExcelParserException("解析excel出错，[请确认第(" + (headerRowIndex + 1) + ")行是否为抬头行,且该行不能为空行]");
		}
		return headers;
	}

	/**
	 * 校验头部
	 * 
	 * @param headers
	 * @throws ExcelParserException
	 */
	private void validateHeaders(String[] headers, ImportContext context) throws ExcelParserException {
		// 校验预期的头部与实际的头部是否吻合
		String[] expectedHeaders = getExpectHeaders(context);
		if (expectedHeaders == null || expectedHeaders.length == 0) {
			throw new ExcelParserException("解析excel出错，[预期的抬头信息缺失，请联系管理员]");
		}
		Set<String> headerSet = new HashSet<String>(Arrays.asList(headers));
		StringBuilder headerBuilder = new StringBuilder();
		StringBuilder missingBuilder = new StringBuilder();
		for (String expectedHeader : expectedHeaders) {
			headerBuilder.append(" ").append(expectedHeader);
			if (!headerSet.contains(expectedHeader)) {
				missingBuilder.append(" ").append(expectedHeader);
			}
		}
		if (!missingBuilder.toString().isEmpty()) {
			throw new ExcelParserException("解析excel出错，[预期的抬头信息为(" + headerBuilder + ")，当前缺失抬头(" + missingBuilder + ")]");
		}
	}

	/**
	 * 
	 * @param buffer
	 *            -当前excel数组
	 * @param sheetIndex
	 *            -当前需解析的sheet号
	 * @param headerRowIndex
	 *            -当前抬头所在的行号 - 默认从抬头开始往下解析
	 * @return
	 */
	protected ExcelData resolve(InputStream inputStream, int sheetIndex, int headerRowIndex, ImportContext context) throws ExcelParserException {
		try {
			//获取workbook
			Workbook workbook = ExcelUtil.createWorkBook(inputStream);
			// 获取相应的sheet
			Sheet sheet = workbook.getSheetAt(sheetIndex);
			if (sheet == null) {
				throw new ExcelParserException("解析excel出错，[请确认Sheet号(" + sheetIndex + ")是否存在]");
			}
			// 获取头部
			String[] headers = resolveHeaders(sheet, headerRowIndex, context);

			// 校验预期的头部与实际的头部是否吻合
			validateHeaders(headers, context);

			// 获取总行数
			int rows = sheet.getPhysicalNumberOfRows();
			if (rows <= headerRowIndex + 1) {
				throw new ExcelParserException("解析excel出错，[请确认第(" + (headerRowIndex + 1) + ")行是否为抬头行，且该行之后的行不能全为空行]");
			}
			// 解析body
			List<ExcelRecord> records = new ArrayList<ExcelRecord>();
			for (int i = headerRowIndex + 1; i < rows; i++) {
				Row r = sheet.getRow(i);
				if (!ExcelUtil.isBlankRow(r)) {
					ExcelRecord record = new ExcelRecord(i);
					for (int j = 0; j < headers.length; j++) {
						String value = ExcelUtil.getStringCellValue(r.getCell(j));
						record.put(headers[j], value);
					}
					records.add(record);
				}
			}

			if (records.isEmpty()) {
				throw new ExcelParserException("解析excel出错，[请确认第(" + (headerRowIndex + 1) + ")行是否为抬头行，且该行之后的行不能全为空行]");
			}

			return new ExcelData(headers, records);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
