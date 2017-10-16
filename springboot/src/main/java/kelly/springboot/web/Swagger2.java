package kelly.springboot.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


//Spring来加载该类配置
@Configuration
//启用Swagger2
@EnableSwagger2
public class Swagger2 {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                //用来创建该Api的基本信息
                .apiInfo(apiInfo())
                //select()函数返回一个ApiSelectorBuilder实例用来控制哪些接口暴露给Swagger来展现
                .select()
                //本例采用指定扫描的包路径来定义，Swagger会扫描该包下所有Controller定义的API
                .apis(RequestHandlerSelectors.basePackage("com.creditease.data.springboot.web.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Spring Boot中使用Swagger2构建RESTful APIs")
                .description("测试地址：http://localhost:8088/swagger-ui.html")
                .termsOfServiceUrl("https://www.yirendai.com/")
                .contact("宜人贷")
                .version("1.0")
                .build();
    }

}