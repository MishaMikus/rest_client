package rest.client;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import rest.model.RequestModel;
import rest.model.ResponseModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ApacheRestClient implements RestClient {

    @Override
    public ResponseModel call(RequestModel requestModel) {
        HttpEntityEnclosingRequestBase request = null;
        DefaultHttpClient httpClient = null;
        try {
            //METHOD
            request = new HttpEntityEnclosingRequestBase() {
                @Override
                public String getMethod() {
                    return requestModel.getMethod().toUpperCase();
                }
            };

            //URL
            URI uri=new URI(requestModel.getURLWithQuery());
            System.out.println(uri);
            request.setURI(uri);

            //HOST

            //HEADERS, CONTENT_TYPE
            request.setHeaders(makeHeaders(requestModel));

            //BODY
            if (requestModel.getBody() != null) {
                try {
                    request.setEntity(new StringEntity(requestModel.getBody().toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }


            //MULTIPART
            if (requestModel.getMultipartFile() != null) {
                request.setEntity(MultipartEntityBuilder.create()
                        .addPart("upload-file", new FileBody(requestModel.getMultipartFile()))
                        .build());
            }

            //PARAMS
            request.setParams(makeParams(requestModel));

            //COOKIES
            httpClient = new DefaultHttpClient();
            if (requestModel.getUseCookie()) {
                httpClient.setCookieStore(makeCookieStore());
            }


            //AUTH
            baseAuth(request, requestModel);

            //REQUEST_LOG
            if (requestModel.getRequestLog()) {
                System.out.println(requestModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        //FOLLOW_REDIRECTS
        //TODO

        //===========SEND======>>>====GET=RESPONSE===========
        HttpResponse response = null;
        ResponseModel responseModel = null;
        Date start = new Date();
        try {
            response = httpClient.execute(request);
            responseModel = new ResponseModel();


            //RESPONSE STATUS LINE
            responseModel.setStatusLine(response.getStatusLine().toString());

            responseModel.setBody(getBody(response));

            //RESPONSE CODE
            responseModel.setStatusCode(response.getStatusLine().getStatusCode());

            //RESPONSE TIME
            responseModel.setStart(start.getTime());
            responseModel.setResponseTime(new Date().getTime() - start.getTime());

            //RESPONSE COOKIES
            responseModel.setCookiesMap(parseCookies(httpClient));
            if (requestModel.getUseCookie()) {
                cookies.putAll(responseModel.getCookiesMap());
            }

            //RESPONSE HEADERS
            responseModel.setHeaderMap(headerMap(response));


            //RESPONSE LOG
            if (requestModel.getResponseLog()) {
                System.out.println(responseModel);
            }

            //RESPONSE LOG IF ERROR
            if (requestModel.getResponseIfErrorLog() && responseModel.getStatusCode() >= 400) {
                System.err.println(responseModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //RETURN
        return responseModel;
    }

    private Map<String, String> headerMap(HttpResponse response) {
        Map<String, String> headerMap = new HashMap<>();
        if (response.getAllHeaders() != null) {
            for (Header header : response.getAllHeaders()) {
                headerMap.put(header.getName(), header.getValue());
            }
        }
        return headerMap;
    }

    private Map<String, String> parseCookies(DefaultHttpClient httpClient) {
        Map<String, String> cookiesMap = new HashMap<>();

        for (Cookie cookie : httpClient.getCookieStore().getCookies()) {
            cookiesMap.put(cookie.getName(), cookie.getValue());
        }
        return cookiesMap;
    }

    private String getBody(HttpResponse response) {
        BufferedReader rd = null;
        try {
            rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder result = new StringBuilder();
        String line;
        try {
            if (rd != null) {
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private void baseAuth(HttpEntityEnclosingRequestBase request, RequestModel requestModel) {
        request.addHeader(new BasicHeader("Authorization", "Basic " + base64(requestModel.getBaseUserName() + ":" + requestModel.getBaseUserPassword())));
    }

    private String base64(String str) {
        return new String(Base64.encodeBase64(str.getBytes()));
    }

    private CookieStore makeCookieStore() {
        CookieStore cookieStore = new BasicCookieStore();
        for (Map.Entry cookie : cookies.entrySet()) {
            cookieStore.addCookie(new BasicClientCookie(cookie.getKey().toString(), cookie.getValue().toString()));
        }

        return cookieStore;
    }

    private HttpParams makeParams(RequestModel requestModel) {
        HttpParams httpParams = new BasicHttpParams();
        for (Map.Entry param : requestModel.getParams().entrySet()) {
            httpParams.setParameter((String) param.getKey(), param.getValue());
        }
        return httpParams;
    }

    private Header[] makeHeaders(RequestModel requestModel) {
        //put contentType to Header
        if (requestModel.getContentType() != null) {
            requestModel.getHeaders().put("Content-Type", requestModel.getContentType());
        }
        final int headerSize = requestModel.getHeaders().size();
        Header[] headers = new Header[headerSize];
        int i = 0;
        for (Map.Entry headerEntry : requestModel.getHeaders().entrySet()) {
            headers[i++] = new BasicHeader(headerEntry.getKey().toString(), headerEntry.getValue().toString());
        }
        return headers;
    }

}
