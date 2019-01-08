package com.fcs.common.webservice;

import org.dom4j.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class XMLToMap {

    public Map<String, String> getXML(String requestXml) {
        Map<String, String> map = new HashMap<String, String>();
        // 将字符串转为XML
        Document doc;
        try {
            doc = DocumentHelper.parseText(requestXml);
            // 获取根节点
            Element rootElm = doc.getRootElement();//从root根节点获取请求报文
            XMLToMap xmlIntoMap = new XMLToMap();
            map = xmlIntoMap.parseXML(rootElm, new HashMap<String, String>());
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return map;
    }

    private Map<String, String> parseXML(Element ele, Map<String, String> map) {
        for (Iterator<?> i = ele.elementIterator(); i.hasNext(); ) {
            Element node = (Element) i.next();
            //System.out.println("parseXML node name:" + node.getName());
            if (node.attributes() != null && node.attributes().size() > 0) {
                for (Iterator<?> j = node.attributeIterator(); j.hasNext(); ) {
                    Attribute item = (Attribute) j.next();
                    map.put(item.getName(), item.getValue().trim());
                }
            }
            if (node.getText().length() > 0) {
                map.put(node.getName(), node.getText().trim());
            }
            if (node.elementIterator().hasNext()) {
                parseXML(node, map);
            }
        }
        return map;
    }

}