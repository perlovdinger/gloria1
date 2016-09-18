package com.volvo.gloria.web.uiservices.filter;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

public class CharacterEncodingResponseFilter implements ContainerResponseFilter {
 
    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        MediaType contentType = response.getMediaType();
        if (MediaType.APPLICATION_JSON_TYPE.equals(contentType)) {
            response.getHttpHeaders().putSingle("Content-Type", MediaType.APPLICATION_JSON_TYPE + ";charset=UTF-8");
        }
        
        return response;
    }
}
