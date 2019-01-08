package com.fcs.common.jna;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.xml.XmlMapper;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by fengcs on 2018/12/29.
 */
public class XmlUtils {

    private static final Logger logger = LoggerFactory.getLogger(XmlUtils.class);

    public static JSONObject convertXmlToJson(String xml) {
        XmlMapper xmlMapper = new XmlMapper();
        JSONObject param = null;
        try {
            param = xmlMapper.readValue(xml, JSONObject.class);
        } catch (IOException e) {
            logger.error("IO异常", e);
            return buildErrorMsg("IO异常");
        }
        return param;
    }

    public static JSONObject xml2Json(String xmlStr) {
        if (StringUtils.isEmpty(xmlStr)) {
            return null;
        }
        try {
            xmlStr = xmlStr.replaceAll("\\\n", "");
            //        byte[] xml = xmlStr.getBytes("UTF-8");
            byte[] xml = xmlStr.getBytes("GBK");
//            JSONObject json = new JSONObject();
            InputStream is = new ByteArrayInputStream(xml);
            SAXBuilder sb = new SAXBuilder();
            Document doc = sb.build(is);
            Element root = doc.getRootElement();
//            json.put(root.getName(), iterateElement(root));
//            System.out.println(root.getName());
//            return json;
            return iterateElement(root);
        } catch (JDOMException e) {
            logger.error("解析DOM节点失败", e);
            return buildErrorMsg("解析DOM节点失败");
        } catch (IOException e) {
            logger.error("IO异常", e);
            return buildErrorMsg("IO异常");
        }
    }

    public static JSONObject buildErrorMsg(String errorMsg) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("returncode", "-1");
        jsonObject.put("returnmsg", errorMsg);
        return jsonObject;
    }

    private static JSONObject iterateElement(Element element) {
        List<Element> node = element.getChildren();
        JSONObject obj = new JSONObject();
        List list = null;
        for (Element child : node) {
            list = new LinkedList();
            String text = child.getTextTrim();
            if (StringUtils.isBlank(text)) {
                if (child.getChildren().size() == 0) {
                    continue;
                }
                if (obj.containsKey(child.getName())) {
                    list = (List) obj.get(child.getName());
                }
                //遍历child的子节点
                list.add(iterateElement(child));
                obj.put(child.getName(), list);
            } else {
                if (obj.containsKey(child.getName())) {
                    Object value = obj.get(child.getName());
                    try {
                        list = (List) value;
                    } catch (ClassCastException e) {
                        list.add(value);
                    }
                }
                //child无子节点时直接设置text
                if (child.getChildren().size() == 0) {
                    obj.put(child.getName(), text);
                } else {
                    list.add(text);
                    obj.put(child.getName(), list);
                }
            }
        }
        return obj;
    }



}
