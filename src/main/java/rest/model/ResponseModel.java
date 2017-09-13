package rest.model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ResponseModel {
    private Long start;
    private String body;
    private String statusLine;
    private Integer statusCode;
    private Long responseTime;
    private Date headerDate;
    private Map<String, String> cookiesMap = new HashMap<>();
    private Map<String, String> headerMap = new HashMap<>();

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public void setStatusLine(String statusLine) {
        this.statusLine = statusLine;
    }

    public Map<String, String> getCookiesMap() {
        return cookiesMap;
    }

    public void setCookiesMap(Map<String, String> cookiesMap) {
        this.cookiesMap = cookiesMap;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }


    public Long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Long responseTime) {
        this.responseTime = responseTime;
    }

    Date getHeaderDate() {
        return headerDate;
    }

    void setHeaderDate(Date headerDate) {
        this.headerDate = headerDate;
    }

    @Override
    public String toString() {
        return "ResponseModel{" +
                "body='" + body + '\'' +
                ", statusCode=" + statusCode +
                ", cookiesMap=" + cookiesMap +
                ", headerMap=" + headerMap +
                '}';
    }

    public JSONObject getBodyAsJson() {
        JSONObject res = null;
        try {
            res = new JSONObject(body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public JSONArray getBodyAsJsonArray() {
        JSONArray res = null;
        try {
            res = new JSONArray(body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    //Content-Type=text/html;

    public String getPrettyPrintBody() {

        if ("text/html".equals(headerMap.get("Content-Type"))) {
            return getPrettyPrintBodyHTML();
        }

        if ("text/xml".equals(headerMap.get("Content-Type"))) {
            return getPrettyPrintBodyXML();
        }

        if ("application/json".equals(headerMap.get("Content-Type"))) {
            return body.trim().startsWith("[") ? getBodyAsJsonArray().toString() : getBodyAsJson().toString();
        }

        return body;
    }

    private String getPrettyPrintBodyHTML() {
        return Jsoup.parse(body, "", Parser.htmlParser()).toString();
    }

    public String getPrettyPrintBodyXML() {
        InputSource src = new InputSource(new StringReader(body));
        Node document = null;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(src).getDocumentElement();
        } catch (SAXException | ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }
        Boolean keepDeclaration = Boolean.valueOf(body.startsWith("<?xml"));
        System.setProperty(DOMImplementationRegistry.PROPERTY,
                "com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");

        DOMImplementationRegistry registry = null;
        try {
            registry = DOMImplementationRegistry.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        DOMImplementationLS impl = (DOMImplementationLS) registry
                .getDOMImplementation("LS");
        LSSerializer writer = impl.createLSSerializer();

        writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
        writer.getDomConfig().setParameter("xml-declaration", keepDeclaration);
        return writer.writeToString(document);
    }

    String getStatusLine() {
        return statusLine;
    }

}
