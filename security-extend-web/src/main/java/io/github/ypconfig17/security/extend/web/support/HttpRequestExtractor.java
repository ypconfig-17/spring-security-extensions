package io.github.ypconfig17.security.extend.web.support;

import io.github.ypconfig17.common.http.HttpRequestUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * @author yangpan
 */
public class HttpRequestExtractor {



    public static Map<String,String> extractParamters(HttpServletRequest request) throws IOException {
        Map<String,String> requestParamters ;
        String contentType = request.getContentType();
        if (StringUtils.hasText(contentType)){
            if (MediaType.APPLICATION_JSON.equals(MediaType.parseMediaType(contentType))){
                requestParamters = HttpRequestUtils.readJson(request, new TypeReference<Map<String, String>>() {});
            }else  if (MediaType.APPLICATION_FORM_URLENCODED.equals(MediaType.parseMediaType(contentType))){
                requestParamters = HttpRequestUtils.getParameterMap(request);
            }else  if (MediaType.MULTIPART_FORM_DATA.equals(MediaType.parseMediaType(contentType))){
                requestParamters = HttpRequestUtils.getParameterMap(request);
            }else {
                throw new AuthenticationServiceException("不支持的媒体类型: " + contentType);
            }

        }else {
            requestParamters = HttpRequestUtils.getParameterMap(request);
        }
        return requestParamters;
    }
}
