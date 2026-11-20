package io.github.ypconfig17.security.extend.web.authentication;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yangpan
 */



public  class ResponseAuthenticationHandler {

    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    public void setMappingJackson2HttpMessageConverter(MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter) {
        this.mappingJackson2HttpMessageConverter = mappingJackson2HttpMessageConverter;
    }

    public  void response(HttpServletResponse response , Object object) throws IOException {
        ServletServerHttpResponse servletServerHttpResponse = new ServletServerHttpResponse(response);
        mappingJackson2HttpMessageConverter.write(object, MediaType.APPLICATION_JSON,servletServerHttpResponse);
    }

}
