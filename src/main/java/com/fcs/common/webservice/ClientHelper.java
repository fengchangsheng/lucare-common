package com.fcs.common.webservice;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义WebService客户端  基于SOAP协议
 * Created by fengcs on 2018/9/13.
 */
public class ClientHelper {

    private static final Logger LOGGER = Logger.getLogger(ClientHelper.class);
    private static final String XML_PREFIX = "<![CDATA[";
    private static final String XML_SUFFIX = "]]>";
    /**
     * 创建WS连接
     */
    public static HttpURLConnection openConnection(String url) throws IOException {
        URL oURL = new URL(url);
        HttpURLConnection con = (HttpURLConnection) oURL.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-type", "text/xml; charset=utf-8");
        con.setDoOutput(true);
        return con;
    }

    /**
     * 关闭WS连接
     */
    public static void closeConnection(HttpURLConnection connection) {
        if (connection != null) {
            connection.disconnect();
        }
    }

    /**
     * 请求输出流
     */
    public static void requestServer(HttpURLConnection connection, String xmlStr) throws IOException {
        OutputStream reqStream = connection.getOutputStream();
        reqStream.write(getRequestStr(xmlStr).toString().getBytes("UTF-8"));
        reqStream.flush();
        reqStream.close();
    }

    /**
     * 返回结果流
     */
    public static StringBuilder responseServer(HttpURLConnection connection) throws IOException {
        InputStream inputStream = connection.getInputStream();
        Reader reader = new InputStreamReader(inputStream, "UTF-8");
        BufferedReader br = new BufferedReader(reader);
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        inputStream.close();
        return sb;
    }

    /**
     * 请求WS协议串组装
     */
    private static StringBuffer getRequestStr(String xmlStr) {
        StringBuffer body = new StringBuffer();
        body.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.aisino.com/\">");
        body.append("<soapenv:Header/>");
        body.append("<soapenv:Body>");
        body.append("<web:AisinoZZSFPZHGLPTWS>");
        body.append("<xmlContent>").append(xmlStr);
        body.append("</xmlContent>");
        body.append("</web:AisinoZZSFPZHGLPTWS>");
        body.append("</soapenv:Body>");
        body.append("</soapenv:Envelope>");
        return body;
    }

    /**
     * 结果集组装
     */
    private static Map parseXml(StringBuilder data) {
        Document document = null;
        Map rstMap = new HashMap(0);
        try {
            document = DocumentHelper.parseText(data.toString());
            Element rootElement = document.getRootElement();
            Element bodyElement = (Element) rootElement.elements().get(0);
            Element eElement = bodyElement.element("AisinoZZSFPZHGLPTWSResponse").element("return");
            return new XMLToMap().getXML(eElement.getText());
        } catch (DocumentException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return rstMap;
    }

    private static String parseXmlString(StringBuilder data) {
        Document document;
        try {
            document = DocumentHelper.parseText(data.toString());
            Element rootElement = document.getRootElement();
            Element bodyElement = (Element) rootElement.elements().get(0);
            Element eElement = bodyElement.element("AisinoZZSFPZHGLPTWSResponse").element("return");
            return eElement.getText();
        } catch (DocumentException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    public static String sendMessage(String url, String xmlStr) {
        xmlStr = XML_PREFIX + xmlStr + XML_SUFFIX;
        HttpURLConnection httpConn = null;
        StringBuilder sb;
        try {
            httpConn = openConnection(url);
            requestServer(httpConn, xmlStr);
            sb = responseServer(httpConn);
            return parseXmlString(sb);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            closeConnection(httpConn);
        }
        return null;
    }

}
