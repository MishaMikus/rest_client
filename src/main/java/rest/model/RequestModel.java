package rest.model;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RequestModel {
    private Boolean urlEncodingEnabled;
    private String path;
    private String contentType;
    private String protocol;
    private String host;
    private Object body;
    private File multipartFile;
    private Map<String, Object> headers = new HashMap<>();
    private Map<String, Object> params = new HashMap<>();
    private String baseUserName;
    private String baseUserPassword;
    private String method;
    private Boolean requestLog=false;
    private Boolean responseLog=false;
    private Boolean useCookie = true;//default
    private Boolean responseIfErrorLog=false;
    private Boolean followRedirects;

    public Boolean getUrlEncodingEnabled() {
        return urlEncodingEnabled;
    }

    public void setUrlEncodingEnabled(Boolean urlEncodingEnabled) {
        this.urlEncodingEnabled = urlEncodingEnabled;
    }

    public File getMultipartFile() {
        return multipartFile;
    }

    public void setMultipartFile(File multipartFile) {
        this.multipartFile = multipartFile;
    }

    String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPath(String path) {
        this.path = path;
    }

    void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    void setBaseUserName(String baseUserName) {
        this.baseUserName = baseUserName;
    }

    void setBaseUserPassword(String baseUserPassword) {
        this.baseUserPassword = baseUserPassword;
    }

    void setRequestLog(boolean requestLog) {
        this.requestLog = requestLog;
    }

    void setResponseLog(Boolean responseLog) {
        this.responseLog = responseLog;
    }

    public String getPath() {
        return path;
    }

    public String getContentType() {
        return contentType;
    }

    public Object getBody() {
        return body;
    }

    public String getBaseUserName() {
        return baseUserName;
    }

    public String getBaseUserPassword() {
        return baseUserPassword;
    }

    public Boolean getRequestLog() {
        return requestLog;
    }

    public Boolean getResponseLog() {
        return responseLog;
    }

    void setUseCookie(Boolean useCookie) {
        this.useCookie = useCookie;
    }

    public Boolean getUseCookie() {
        return useCookie;
    }

    public void putHeader(String key, Object value) {
        headers.put(key, value);
    }

    public void putParam(String key, Object value) {
        params.put(key, value);
    }

    void setResponseIfErrorLog(Boolean responseIfErrorLog) {
        this.responseIfErrorLog = responseIfErrorLog;
    }

    public Boolean getResponseIfErrorLog() {
        return responseIfErrorLog;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = new HashMap<>(headers);
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    void setRequestLog(Boolean requestLog) {
        this.requestLog = requestLog;
    }

    private MapQuery mapQuery = new MapQuery();

    public String getURLWithQuery() {
        return getURL() + (params.size() == 0 ? "" : "?" + mapQuery.urlEncodeUTF8(params));
    }

    public String getURL() {
        return protocol + host + path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "RequestModel{" +
                "path='" + path + '\'' +
                ", contentType='" + contentType + '\'' +
                ", protocol='" + protocol + '\'' +
                ", host='" + host + '\'' +
                ", body=" + body +
                ", headers=" + headers +
                ", params=" + params +
                ", baseUserName='" + baseUserName + '\'' +
                ", baseUserPassword='" + baseUserPassword + '\'' +
                ", method='" + method + '\'' +
                ", requestLog=" + requestLog +
                ", responseLog=" + responseLog +
                ", useCookie=" + useCookie +
                ", responseIfErrorLog=" + responseIfErrorLog +
                '}';
    }

    void addParam(String key, String value) {
        params.put(key, value);
    }

    public Boolean getFollowRedirects() {
        return followRedirects;
    }

    void setFollowRedirects(Boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    public void setAllLog(boolean logOn) {
        requestLog = logOn;
        responseIfErrorLog = logOn;
        responseLog = logOn;
    }

    public Boolean getAllLog() {
        return requestLog&&responseIfErrorLog&&responseLog;
    }

    public void deleteHeader(String s) {
        headers.remove("Content-Type");
    }
}
