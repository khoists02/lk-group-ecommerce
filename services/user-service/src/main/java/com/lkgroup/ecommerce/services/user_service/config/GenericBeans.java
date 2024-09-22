package com.lkgroup.ecommerce.services.user_service.config;

import com.google.protobuf.util.JsonFormat;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.protobuf.ProtobufModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufJsonFormatHttpMessageConverter;

import java.awt.image.BufferedImage;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class GenericBeans {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.registerModule(new ProtobufModule());
        mapper.getConfiguration()
                .setSkipNullEnabled(false)
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true);

        mapper.addConverter(new AbstractConverter<ZonedDateTime, String>() {
            @Override
            protected String convert(ZonedDateTime zonedDateTime) {
                if (zonedDateTime == null)
                    return "";
                return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            }
        });
        mapper.addConverter(new AbstractConverter<String, ZonedDateTime>() {
            @Override
            protected ZonedDateTime convert(String value) {
                if (value == null)
                    return null;
                return ZonedDateTime.parse(value);
            }
        });

        return mapper;
    }

    /*
    * Ignore unknow fields set in protobuf.
    * */
    @Bean
    public ProtobufJsonFormatHttpMessageConverter protobufJsonFormatHttpMessageConverter() {
        return new ProtobufJsonFormatHttpMessageConverter(JsonFormat.parser().ignoringUnknownFields(), JsonFormat.printer().alwaysPrintFieldsWithNoPresence());
    }

    @Bean
    public HttpMessageConverter<BufferedImage> createImageHttpMessageConverter() {
        return new BufferedImageHttpMessageConverter();
    }

//    @Bean
//    public RenderedInstanceInputStreamHttpMessageConverter renderedInstanceInputStreamHttpMessageConverter()
//    {
//        return new RenderedInstanceInputStreamHttpMessageConverter();
//    }

    /*
     * This must be a Bean as this is a memory monster, ~120MiB just to parse User Agents!
     */
//    @Bean
//    @ConditionalOnProperty(value = "advapacs.yauaa.delegate", havingValue = "embedded", matchIfMissing = true)
//    public UserAgentAnalyzer userAgentAnalyzer() {
//        return UserAgentAnalyzer.newBuilder()
//                .withCache(5000)
//                .withField("DeviceName")
//                .withField("AgentName")
//                .delayInitialization()
//                .build();
//    }
//
//    @Bean
//    public HapiContext hapiContext() {
//        return new DefaultHapiContext(ValidationContextFactory.noValidation());
//    }

}
