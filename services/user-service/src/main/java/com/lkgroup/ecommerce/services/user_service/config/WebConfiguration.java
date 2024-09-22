package com.lkgroup.ecommerce.services.user_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.util.MimeType;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    /**
     * We need to add application/json before any wildcard type if provided, otherwise, the default match for the
     * wildcard will be protobuf which displays an error if a UserAgent tries to display it
     * @param configurer
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.strategies(List.of(
                new HeaderContentNegotiationStrategy(){
                    @Override
                    public List<MediaType> resolveMediaTypes(NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
                        List<MediaType> types = new ArrayList<>(super.resolveMediaTypes(request));
                        if(types.stream().anyMatch(MimeType::isWildcardType))
                        {
                            if(types.stream().filter(m -> !m.isWildcardType()).noneMatch(mediaType -> mediaType.isCompatibleWith(MediaType.APPLICATION_JSON)))
                            {
                                types.add(MediaType.APPLICATION_JSON);
                                MediaType.sortBySpecificityAndQuality(types);
                            }
                        }
                        return types;
                    }
                }
        ));
    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.removeIf(converter ->
                MappingJackson2XmlHttpMessageConverter.class.isAssignableFrom(converter.getClass()));
    }
}
