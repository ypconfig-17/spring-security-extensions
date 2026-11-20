package io.github.ypconfig17.common.http;

import io.github.ypconfig17.common.util.JacksonUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @author yangpan
 */
public class HttpResponseUtils {

    public static final String APPLICATION_JSON_VALUE = "application/json";
    public static final String TEXT_HTML_VALUE = "text/html";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CHARACTE_ENCODING = "UTF-8";


    public static void responseJson(HttpServletResponse response,Object value) throws IOException {
        response.setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE);  //设置响应头的编码
        response.setCharacterEncoding(CHARACTE_ENCODING);
        response.getWriter().write(JacksonUtils.toJson(value));
    }

    public static void response(HttpServletResponse response,String value) throws IOException {
        response.setHeader(CONTENT_TYPE, TEXT_HTML_VALUE);  //设置响应头的编码
        response.setCharacterEncoding(CHARACTE_ENCODING);
        response.getWriter().write(value);

    }
}
