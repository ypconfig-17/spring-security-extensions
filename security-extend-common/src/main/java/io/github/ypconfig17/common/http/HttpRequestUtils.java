package io.github.ypconfig17.common.http;


import io.github.ypconfig17.common.util.JacksonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yangpan
 */
public class HttpRequestUtils {



    public static Map<String, String> getParameterMap(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = request.getParameter(paramName);
            paramMap.put(paramName, paramValue);
        }
        return paramMap;
    }

    /**
     * 从serveletReuqest 中读取json格式数据
     * @param request
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T>  T readJson(HttpServletRequest request,Class<T>  clazz) throws IOException {
        String json = getRequestJsonString(request);
        return JacksonUtils.toObj(json, clazz);
    }

    /**
     * 从serveletReuqest 中读取json格式数据
     * @param request
     * @param typeReference
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T>  T readJson(HttpServletRequest request, TypeReference<T> typeReference) throws IOException {
        String json = getRequestJsonString(request);
        return JacksonUtils.toObj(json, typeReference);
    }
    /***
     * 获取 request 中 json 字符串的内容
     *
     * @param request
     * @return :
     * @throws IOException
     */
    private static String getRequestJsonString(HttpServletRequest request)
            throws IOException {
        String submitMehtod = request.getMethod();
        // GET
        if (submitMehtod.equals("GET")) {
            return new String(request.getQueryString().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8).replaceAll("%22", "\"");
            // POST
        } else {
            return getRequestPostStr(request);
        }
    }

    /**
     * @param request
     * @return
     * @throws IOException
     */
    private  static byte[] getRequestPostBytes(HttpServletRequest request)
            throws IOException {
        int contentLength = request.getContentLength();
        if(contentLength<0){
            return null;
        }
        byte[] buffer = new byte[contentLength];
        for (int i = 0; i < contentLength;) {

            int readlen = request.getInputStream().read(buffer, i,contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return buffer;
    }

    /**
     * 描述:获取 post 请求内容
     * @param request
     * @return
     * @throws IOException
     */
    private static String getRequestPostStr(HttpServletRequest request)
            throws IOException {
        byte[] buffer = getRequestPostBytes(request);
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = "UTF-8";
        }
        return new String(buffer, charEncoding);
    }




}
