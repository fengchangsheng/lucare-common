package com.fcs.common.jna;

import com.alibaba.fastjson.JSONObject;
import com.sun.jna.Library;
import com.sun.jna.Native;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by fengcs on 2018/12/29.
 */
public class BaiWangProxyServlet extends HttpServlet {

    private static Logger logger = LoggerFactory.getLogger(BaiWangProxyServlet.class);
    private static String targetUrl = "";

    @Override
    public void init() throws ServletException {
        targetUrl = getInitParameter("targetUrl");
        logger.debug("=============== tax software dir : " + targetUrl);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("=============== enter get =================");
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String param = request.getParameter("webXmlStr");
        String uri = request.getRequestURI();
        logger.debug(uri);
        logger.debug(">>>================== param :======================");
        logger.debug(param);
        String result = invokeDll(param).trim();
        logger.debug("<<<================== result :=====================");
        logger.debug(result);
        ServletOutputStream outputStream = null;
        try {
//            response.setCharacterEncoding("UTF-8");
            setResponseCrossDomain(response);
            JSONObject jsonObject = XmlUtils.convertXmlToJson(result);
            outputStream = response.getOutputStream();
            String jsonResult;
            if (jsonObject == null || jsonObject.getJSONObject("body") == null) {
                jsonResult = XmlUtils.buildErrorMsg("服务异常，请稍后重试！").toString();
            } else {
                jsonResult = jsonObject.getJSONObject("body").toString();
            }
            logger.debug("================== return msg ===================");
            logger.debug(jsonResult);
            byte[] resultBytes = jsonResult.getBytes("UTF-8");
            outputStream.write(resultBytes);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    protected String invokeDll(String xmlStr) {
        byte[] b = new byte[1024 * 10];
        try {
            JNAOperateDll.instanceDll.PostAndRecvEx((xmlStr + "\0") .getBytes("gbk"), b);
//            JNAOperateDll.instanceDll.PostAndRecvEx((xmlStr) .getBytes("gbk"), b);
            return new String(b, "gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void setResponseCrossDomain(HttpServletResponse response) {
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET");
        response.setHeader("Access-Control-Max-Age", "1800");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

    public interface JNAOperateDll extends Library {

        JNAOperateDll instanceDll = (JNAOperateDll) Native.loadLibrary(targetUrl, JNAOperateDll.class);

        void PostAndRecvEx(byte[] sInputInfo, byte[] sOutputInfo);

    }

}
