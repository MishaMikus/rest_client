package rest.model;

import org.json.JSONArray;
import org.json.JSONObject;

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
    private Map<String, String> cookiesMap=new HashMap<>();
    private Map<String, String> headerMap=new HashMap<>();

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

    String getStatusLine() {
        return statusLine;
    }

}
