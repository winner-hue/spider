package com.pj.spider.plugin;

import com.pj.spider.entity.*;
import com.pj.spider.util.CommonUtil;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pj.UrlParse;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 提取类基类
 */
public class ExtractBase extends Spider {
    // html dom文档
    private Document dom = null;
    // xpath 初始化
    private XPath xPath = XPathFactory.newInstance().newXPath();

    public ExtractBase(Task task) {
        super(task);
    }

    /**
     * 处理提取任务
     *
     * @param map
     * @return
     */
    public ResponseData processTask(HashMap map) {
        ResponseData responseData = new ResponseData();
        responseData.setStatusCode(200);
        HtmlCleaner hc = new HtmlCleaner();
        BaseStruct structData = new BaseStruct();
        TagNode tn = hc.clean(this.task.getPageSource());
        try {
            dom = new DomSerializer(new CleanerProperties()).createDOM(tn);
        } catch (Exception e) {
            responseData.setStatusCode(400);
            responseData.setErrorContent(e.getMessage());
            return responseData;
        }
        extract(structData, map);
        responseData.setBaseStruct(structData);
        return responseData;
    }

    /**
     * 判断url是否匹配列表格式
     *
     * @param match
     * @param url
     * @return
     */
    protected boolean isListMatch(String match, String url) {
        String[] matchList = match.split("@@@");
        boolean flag = false;
        for (String s : matchList) {
            String[] matchArray = s.split("###");
            if (matchArray[0].trim().equals("re")) {
                for (int j = 1; j < matchArray.length; j++) {
                    boolean isMatch = CommonUtil.isMatch(matchArray[j].trim(), url);
                    if (isMatch) {
                        flag = true;
                        break;
                    }
                }
            }
            if (matchArray[0].trim().equals("equal")) {
                for (int j = 1; j < matchArray.length; j++) {
                    if (matchArray[j].equals(url)) {
                        flag = true;
                        break;
                    }
                }
            }
        }
        return flag;
    }

    /**
     * 判断url是否匹配详情url格式
     *
     * @param match
     * @param url
     * @return
     */
    protected boolean isDetailMatch(String match, String url) {
        String[] detailSplit = match.split("###");
        for (String s : detailSplit) {
            boolean match1 = CommonUtil.isMatch(s.trim(), url);
            if (match1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 解析，提取文档内容
     *
     * @param node
     * @param object
     * @return
     */
    private String getString(Node node, Object object) {
        if (object == null || object.equals("")) {
            return null;
        }
        try {
            String path = (String) object;
            String[] pathList = path.split("@@@");
            NodeList nodes = (NodeList) xPath.evaluate(pathList[0], node, XPathConstants.NODESET);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node item = nodes.item(i);
                if (item != null) {
                    String textContent = item.getTextContent().trim();
                    builder.append(textContent);
                }
            }
            if (path.length() > 1) {
                try {
                    return CommonUtil.match(pathList[1], builder.toString())[1];
                } catch (Exception e) {
                    return builder.toString();
                }
            }
            return builder.toString();
        } catch (XPathExpressionException e) {
            return null;
        }
    }

    protected String getListTitle(Node node, Object listTitle) {
        return getString(node, listTitle);
    }

    protected String getAuthor(Node node, Object listAuthor) {
        return getString(node, listAuthor);
    }

    protected String getPostTime(Node node, Object listPostTime) {
        return getString(node, listPostTime);
    }

    protected String getDetailUrl(Node node, Object detailUrl) {
        return getString(node, detailUrl);
    }

    protected String getListSource(Node node, Object listSource) {
        return getString(node, listSource);
    }

    /**
     * 解析列表，提取其中可能对详情页面字段游泳的字段值
     *
     * @param map
     * @return
     */
    protected List<UrlStructData> getUrlStructData(HashMap map) {
        List<UrlStructData> urlStructData = new ArrayList<>();
        Object listNode = map.get("listNode");
        Object listTitle = map.get("listTitle");
        Object listAuthor = map.get("listAuthor");
        Object listPostTime = map.get("listPostTime");
        Object listSource = map.get("listSource");
        Object detailUrl = map.get("detailUrl");
        if (listNode != null) {
            try {
                Object nodes = xPath.evaluate((String) listNode, dom, XPathConstants.NODESET);
                if (nodes instanceof NodeList) {
                    NodeList nodeList = (NodeList) nodes;
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Node item = nodeList.item(i);
                        UrlStructData data = new UrlStructData();
                        data.setTitle(getListTitle(item, listTitle));
                        data.setAuthor(getAuthor(item, listAuthor));
                        data.setPostTime(getPostTime(item, listPostTime));
                        data.setUrl(getDetailUrl(item, detailUrl));
                        data.setSource(getListSource(item, listSource));
                        data.isDetail = true;
                        urlStructData.add(data);
                    }
                }
            } catch (Exception e) {
                return urlStructData;
            }
        }
        return urlStructData;
    }

    /**
     * 获取列表页的所有下一页连接
     *
     * @return
     */
    protected List<String> getListUrl(HashMap map) {
        List<String> urls = new ArrayList<>();
        Object listUrls = map.get("listUrls");
        int page = 0;
        try {
            page = Integer.parseInt(getString(dom, listUrls));
            for (int i = 1; i < page + 1; i++) {
                String url = this.task.getTaskUrl().replace("page=\\d+", "page=" + i);
                urls.add(url);
            }
            return urls;
        } catch (Exception ignore) {
        }
        return null;
    }

    /**
     * 构造新的url
     *
     * @param baseUrl
     * @param url
     * @return
     */
    protected String urlJoin(String baseUrl, String url) {
        return UrlParse.urlJoin(baseUrl, url);
    }

    /**
     * 获取页面中所有的url，并将匹配详情url格式的连接下发
     *
     * @param detailMatch
     * @param baseUrl
     * @return
     */
    protected List<UrlStructData> getAllUrls(Object detailMatch, String baseUrl) {
        List<UrlStructData> data = new ArrayList<>();
        try {
            NodeList nodes = (NodeList) xPath.evaluate(".//a/@href", dom, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                Node item = nodes.item(i);
                if (item != null) {
                    String tempUrl = item.getTextContent();
                    String url = urlJoin(baseUrl, tempUrl);
                    UrlStructData structData = new UrlStructData();
                    boolean isMatch = isDetailMatch((String) detailMatch, url);
                    if (isMatch) {
                        structData.isDetail = true;
                        structData.setUrl(url);
                        data.add(structData);
                    }
                }
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return data;
    }

    protected String getDetailTitle(Node node, Object detailTitle) {
        return getString(node, detailTitle);
    }

    protected String getDetailAuthor(Node node, Object detailAuthor) {
        return getString(node, detailAuthor);
    }

    protected String getDetailPostTime(Node node, Object detailPostTime) {
        return getString(node, detailPostTime);
    }

    protected String getDetailSource(Node node, Object detailSource) {
        return getString(node, detailSource);
    }

    protected String getDetailContent(Node node, Object detailContent) {
        return getString(node, detailContent);
    }

    /**
     * 处理列表页面不包含详情页面的字段
     *
     * @param structData
     * @param map
     */
    protected void processOtherField(StructData structData, HashMap map) {
        Object detailContent = map.get("detailContent");
        structData.setContent(getDetailContent(dom, detailContent));
    }

    /**
     * 提取详情页面的字段值
     *
     * @param map
     * @return
     */
    protected List<StructData> getStructData(HashMap map) {
        List<StructData> structData = new ArrayList<>();
        Object isUseListData = map.get("isUseListData");
        if (isUseListData != null && isUseListData.equals("1")) {
            UrlStructData urlStructData = this.task.getUrlStructData();
            StructData data = new StructData();
            data.setAuthor(urlStructData.getAuthor());
            data.setPostTime(urlStructData.getPostTime());
            data.setSource(urlStructData.getSource());
            data.setTitle(urlStructData.getTitle());
            processOtherField(data, map);
            structData.add(data);
            return structData;
        }
        Object detailNode = map.get("detailNode");
        Object detailTitle = map.get("detailTitle");
        Object detailContent = map.get("detailContent");
        Object detailAuthor = map.get("detailAuthor");
        Object detailPostTime = map.get("detailPostTime");
        Object detailSource = map.get("detailSource");
        if (detailNode != null) {
            try {
                Object nodes = xPath.evaluate((String) detailNode, dom, XPathConstants.NODESET);
                if (nodes instanceof NodeList) {
                    NodeList nodeList = (NodeList) nodes;
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Node item = nodeList.item(i);
                        StructData data = new StructData();
                        data.setTitle(getDetailTitle(item, detailTitle));
                        data.setAuthor(getDetailAuthor(item, detailAuthor));
                        data.setPostTime(getDetailPostTime(item, detailPostTime));
                        data.setSource(getDetailSource(item, detailSource));
                        data.setContent(getDetailContent(item, detailContent));
                        structData.add(data);
                    }
                }
            } catch (Exception e) {
                return structData;
            }
        } else {
            StructData data = new StructData();
            data.setTitle(getDetailTitle(dom, detailTitle));
            data.setAuthor(getDetailAuthor(dom, detailAuthor));
            data.setPostTime(getDetailPostTime(dom, detailPostTime));
            data.setSource(getDetailSource(dom, detailSource));
            data.setContent(getDetailContent(dom, detailContent));
            structData.add(data);
        }
        return structData;
    }

    /**
     * 子提取， 封装方法
     *
     * @param structData
     * @param detailMatch
     * @param url
     * @param map
     */
    protected void extractSub(BaseStruct structData, Object detailMatch, String url, HashMap map) {
        boolean isDetailMatch = isDetailMatch((String) detailMatch, url);
        if (isDetailMatch) {
            structData.setStructData(getStructData(map));
        } else {
            structData.setUrlStructData(getAllUrls(detailMatch, url));
        }
    }

    /**
     * 提取页面内容
     *
     * @param structData
     * @param map
     */
    protected void extract(BaseStruct structData, HashMap map) {
        // 列表
        Object listMatch = map.get("listMatch");
        // 正文
        Object detailMatch = map.get("detailMatch");
        String url = this.task.getTaskUrl();
        if (listMatch != null) {
            boolean isListMatch = isListMatch((String) listMatch, url);
            if (isListMatch) {
                structData.setListUrls(getListUrl(map));
                structData.setUrlStructData(getUrlStructData(map));
            } else {
                extractSub(structData, detailMatch, url, map);
            }
        } else {
            extractSub(structData, detailMatch, url, map);
        }
    }
}
