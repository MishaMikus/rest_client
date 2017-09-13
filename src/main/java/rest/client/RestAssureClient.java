package rest.client;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.SystemDefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;
import rest.model.RequestModel;
import rest.model.ResponseModel;
import utils.PropertyUtils;

import java.io.*;
import java.util.*;

import static io.restassured.RestAssured.given;

public class RestAssureClient implements RestClient {

    public final Logger LOGGER = Logger.getLogger(this.getClass());


    protected String endLogMessage(RequestModel requestModel, ResponseModel responseModel, String message) {

        return "[" + requestModel.getMethod() + "]\t" +
                "[code : " + responseModel.getStatusCode() + "]\t" +
                "[" + message + "]\t" +
                "[" + requestModel.getURLWithQuery() + "]\t" +
                "[responseTime : " + responseModel.getResponseTime() + "]";
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies.clear();
        this.cookies.putAll(cookies);
    }

    public void putCookie(String key, String value) {
        cookies.put(key, value);
    }

    @Override
    public ResponseModel call(RequestModel requestModel) {
        setConfig();

        ResponseModel responseModel = new ResponseModel();

        //switch off save connection
        RestAssured.useRelaxedHTTPSValidation();

        //init GIVEN
        RequestSpecification requestSpecification = given();

        //urlEncodingEnabled
        if (requestModel.getUrlEncodingEnabled() != null) {
            requestSpecification = requestSpecification.urlEncodingEnabled(requestModel.getUrlEncodingEnabled());
        }

        //CONTENT_TYPE
        if (requestModel.getContentType() != null) {
            requestSpecification = requestSpecification.contentType(requestModel.getContentType());
        }

        //HEADERS
        if (requestModel.getHeaders().values().size() > 0) {
            requestSpecification = requestSpecification.headers(requestModel.getHeaders());
        }

        //BODY
        if (requestModel.getBody() != null) {
            requestSpecification = requestSpecification.body(requestModel.getBody());
        }

        //MULTIPART
        if (requestModel.getMultipartFile() != null) {
            requestSpecification = requestSpecification.multiPart(requestModel.getMultipartFile());
        }

        //PARAMS
        if (requestModel.getParams().values().size() > 0) {
            requestSpecification = requestSpecification.params(requestModel.getParams());
        }

        //COOKIES
        if (requestModel.getUseCookie() != null && requestModel.getUseCookie()) {
            requestSpecification = requestSpecification.cookies(cookies);
        }

        //AUTH
        if (requestModel.getBaseUserName() != null && requestModel.getBaseUserPassword() != null) {
            requestSpecification = requestSpecification.auth().basic(requestModel.getBaseUserName(), requestModel.getBaseUserPassword());
        }

        //REQUEST_LOG
        if (requestModel.getRequestLog() != null && requestModel.getRequestLog()) {
            requestSpecification = requestSpecification.log().all();
        }

        //FOLLOW_REDIRECTS
        if (requestModel.getFollowRedirects() != null) {
            requestSpecification.redirects().follow(requestModel.getFollowRedirects());
        }

        //===========SEND======>>>====GET=RESPONSE===========

        //RESPONSE BY METHOD BY PATH
        responseModel.setStart(new Date().getTime());
        Response response = getResponseByMethod(requestSpecification, requestModel.getMethod(), requestModel.getURLWithQuery());
        if (response != null) {
            //TIME
            responseModel.setResponseTime(response.time());

            //RESPONSE
            ValidatableResponse validatableResponse = response.then();

            //RESPONSE_LOG
            validatableResponse.log();
            if (requestModel.getResponseLog() != null && requestModel.getResponseLog()) {
                validatableResponse = validatableResponse.log().all();
            } else {
                //RESPONSE_LOG_IF_ERROR
                if (requestModel.getResponseIfErrorLog() != null && requestModel.getResponseIfErrorLog()) {
                    validatableResponse = validatableResponse.log().ifError();
                }
            }

            //RETURN
            if (validatableResponse != null) {
                responseModel = transform(responseModel, validatableResponse.extract().response());
                cookies.putAll(responseModel.getCookiesMap());
            }
        } else {
            LOGGER.warn("RESPONSE is null");
        }
        storeLog(endLogMessageTabbed(requestModel, responseModel, ""));
        return responseModel;
    }

    private String endLogMessageTabbed(RequestModel requestModel, ResponseModel responseModel, String s) {
        List<String> row = new ArrayList<>();
        row.add(responseModel.getStart() + "");
        row.add(requestModel.getMethod());
        row.add(responseModel.getStatusCode() + "");
        row.add(responseModel.getResponseTime() + "");
        row.add(requestModel.getURLWithQuery());
        return tabString(row);
    }

    private String tabString(List<String> row) {
        StringBuilder res = new StringBuilder();
        for (String text : row) {
            res.append(text).append("\t");
        }
        return res.toString().trim();
    }

    private static final File LOG_FILE = new File("http.log");

    private void storeLog(String text) {
        //create logFile if doesn't exists
        if (!LOG_FILE.exists()) {
            try {
                if (LOG_FILE.createNewFile()) {
                    LOGGER.info("file '" + LOG_FILE.getAbsolutePath() + "' created");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        //append log
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(LOG_FILE, true), "UTF-8")))) {
            out.println(text);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setConfig() {
        HttpClientConfig clientConfig = RestAssured.config().getHttpClientConfig();
        clientConfig = clientConfig.httpClientFactory(() -> {
            HttpClient rv = new SystemDefaultHttpClient();
            HttpParams httpParams = rv.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, Integer.parseInt(PropertyUtils.getProperty("restassured.client.connection.timeout.second")) * 1000); //Wait 5s for a connection
            HttpConnectionParams.setSoTimeout(httpParams, Integer.parseInt(PropertyUtils.getProperty("restassured.client.session.timeout.second")) * 1000); // Default session is 60s
            return rv;
        });

        //This is necessary to ensure, that the client is reused.
        clientConfig = clientConfig.reuseHttpClientInstance();

        RestAssured.config = RestAssured.config().httpClient(clientConfig);

    }

    private Response getResponseByMethod(RequestSpecification requestSpecification, String method, String url) {
        requestSpecification = requestSpecification.when();
        Response response = null;
        if (method == null) {
            LOGGER.warn("requestModel HTTP method cannot be null");
        } else {
            try {
                response = requestSpecification.request(method, url);
            } catch (Exception e) {
                LOGGER.warn("CONNECTION ISSUE\n");
                e.printStackTrace();
            }
        }
        return response;
    }

    private ResponseModel transform(ResponseModel responseModel, Response response) {
        for (io.restassured.http.Header header : response.headers()) {
            if (responseModel.getHeaderMap() == null) {
                responseModel.setHeaderMap(new HashMap<>());
            }
            responseModel.getHeaderMap().put(header.getName(), header.getValue());
        }

        responseModel.setBody(response.getBody().asString());
        responseModel.setStatusCode(response.statusCode());
        responseModel.setCookiesMap(response.cookies());
        responseModel.setStatusLine(response.statusLine());
        return responseModel;
    }


}
