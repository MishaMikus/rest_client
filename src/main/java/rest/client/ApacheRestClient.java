package rest.client;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
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
import java.util.Map;

import static org.apache.http.HttpHeaders.USER_AGENT;

public class ApacheRestClient implements RestClient {

    @Override
    public ResponseModel call(RequestModel requestModel) {
        String url = "http://www.google.com/search?q=developer";

        //METHOD
        HttpEntityEnclosingRequestBase request = new HttpEntityEnclosingRequestBase() {
            @Override
            public String getMethod() {
                return requestModel.getMethod().toUpperCase();
            }
        };

//*HEADERS, CONTENT_TYPE
        request.setHeaders(makeHeaders(requestModel));

//*BODY
        try {
            request.setEntity(new StringEntity(requestModel.getBody().toString()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


//*MULTIPART
        request.setEntity(MultipartEntityBuilder.create()
                .addPart("upload-file", new FileBody(requestModel.getMultipartFile()))
                .build());

//*PARAMS
        request.setParams(makeParams(requestModel));

//*COOKIES
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.setCookieStore(makeCookieStore(requestModel));


//*AUTH

//                * REQUEST_LOG
//                * FOLLOW_REDIRECTS
//                * ===========SEND======>>>====GET=RESPONSE===========
//     * RESPONSE BY METHOD BY PATH
//                * TIME
//                * RESPONSE
//                * RESPONSE_LOG
//                * RESPONSE_LOG_IF_ERROR
//                * RETURN


        // add request header
        request.addHeader("User-Agent", USER_AGENT);

        HttpResponse response = null;
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " +
                response.getStatusLine().getStatusCode());

        BufferedReader rd = null;
        try {
            rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuffer result = new StringBuffer();
        String line;
        try {
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(result.toString());
        return null;
    }

    private CookieStore makeCookieStore(RequestModel requestModel) {
        CookieStore cookieStore = new BasicCookieStore();
        for(Map.Entry cookie:cookies.entrySet()){
        cookieStore.addCookie(new BasicClientCookie(cookie.getKey().toString(),cookie.getValue().toString()));}

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
