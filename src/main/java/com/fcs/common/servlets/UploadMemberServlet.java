package com.fcs.common.servlets;

import com.fcs.common.Strings;
import com.fcs.common.beans.ApiResult;
import com.fcs.common.excel.importer.AbstractExcelImporter;
import com.fcs.common.excel.importer.vo.ImportContext;
import com.fcs.common.helper.CookieHelper;
import com.fcs.common.util.CommonUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Lucare.Feng on 2016/8/4.
 */
public class UploadMemberServlet extends HttpServlet {
    private static Logger logger = LoggerFactory.getLogger(UploadMemberServlet.class);
    private static Set<String> extSet = new HashSet<String>(4, 1.0f);

    @Override
    public void init(ServletConfig config) throws ServletException {
        extSet.add("xls");
        extSet.add("xlsx");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(req,resp);
    }

    protected void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        ApiResult<String> result = new ApiResult<String>();
//        String sid = CookieHelper.getCookieValue("sid", request);
//        TSession tSession = TSessionManager.get(sid);
//        Object object = tSession.getPropertie("orgId");
//        int orgId = Integer.parseInt(object.toString());
//        int userId = TSessionManager.decodeUserid(sid);
//        if (userId == 0) {
//            result.error("no login");
//        } else {
//            try {
//                InputStream is = getInputStream(request, result);
//                if (is != null) {
//                    try {
//                        AbstractExcelImporter importer = new MemberImporter();
//                        importer.setHeaderRowIndex(1);
//                        ImportContext context = new ImportContext();
//                        String lang = request.getParameter("lang");
//                        String proIdStr = request.getParameter("projectId");
//                        if (Strings.isEmpty(lang)) {
//                            context.put("lang",lang);
//                        }
//                        if (Strings.isNotEmpty(proIdStr)) {
//                            context.put("projectId", Integer.parseInt(proIdStr));
//                        }
//                        context.put("user_id", userId);
//                        context.put("org_id", orgId);
//
//
//                        boolean flag = importer.doImport(is, context);
//
//                        if (flag) {
//                            result.ok(context.getResultMsg());
//                        } else {
//                            result.error(context.getErrorMsg());
//                        }
//                    } catch (Exception ex) {
//                        result.error(ex.getMessage());
//                    } finally {
//                        is.close();
//                    }
//                }
//            } catch (Exception ex) {
//                logger.error("excel upload with ex:", ex);
//                result.error(ex.getMessage());
//            }
//        }
//
//        String output = JSON.toJSONString(result);
//
//        response.setContentType("application/json; charset=UTF-8");
//        response.getWriter().write(output);
//        response.flushBuffer();


    }

    private static InputStream getInputStream(HttpServletRequest request, ApiResult<String> result) throws IOException {
        List<FileItem> itemList = getFileItemList(request);
        if (CommonUtils.isEmpty(itemList)) {
            result.error("501");//上传文件失败
            return null;
        }

        FileItem item = getFileItem(itemList);
        if (item == null) {
            result.error("501");//上传文件失败
            return null;
        }

        String fileName = item.getName();
        String fileExt = StringUtils.substringAfterLast(fileName, ".").toLowerCase();
        if (!extSet.contains(fileExt)) {
            result.error("502");//不是允许的文件类型(xls,xlsx格式)
            return null;
        }
        return item.getInputStream();
    }

    private static FileItem getFileItem(List<FileItem> items) {
        FileItem item = null;
        Iterator<FileItem> itr = items.iterator();
        while (itr.hasNext()) {
            FileItem tItem = itr.next();
            if (!tItem.isFormField()) {
                item = tItem;
                break;
            }
        }
        return item;
    }

    private static List<FileItem> getFileItemList(HttpServletRequest request) {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("UTF-8");

        List<FileItem> itemList = null;
        try {
            itemList = upload.parseRequest(request);
        } catch (Throwable ex) {
            logger.error("getFileItemList with ex:", ex);
        }
        return itemList;
    }

}

