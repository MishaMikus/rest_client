package rest.client;

import rest.model.RequestModel;
import rest.model.ResponseModel;

import java.util.HashMap;
import java.util.Map;

public interface RestClient {
    Map<String, String> cookies = new HashMap<>();

    /**
     * =========== Request Builder :
     * CONTENT_TYPE
     * HEADERS
     * BODY
     * MULTIPART
     * PARAMS
     * COOKIES
     * AUTH
     * REQUEST_LOG
     * FOLLOW_REDIRECTS
     * ===========SEND======>>>====GET=RESPONSE===========
     * RESPONSE BY METHOD BY PATH
     * TIME
     * RESPONSE
     * RESPONSE_LOG
     * RESPONSE_LOG_IF_ERROR
     * RETURN
     */
    ResponseModel call(RequestModel responseModel);

}
