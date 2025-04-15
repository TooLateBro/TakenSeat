package com.taken_seat.gateway_service.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .version("v1.0") //버전
                .title("이선좌 API") //이름
                .description("이선좌 관련 API"); //설명
        return new OpenAPI()
                .info(info);
    }
}