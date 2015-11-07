package cn.elvea.config;

import cn.elvea.core.spring.mvc.CustomArgumentResolver;
import cn.elvea.web.filter.SitemeshFilter;
import cn.elvea.web.multipart.FileUploadMultipartResolver;
import cn.elvea.web.servlet.CaptchaServlet;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.List;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new CustomArgumentResolver());
    }

    /**
     * 视图设置
     */
    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    /**
     * 扩展已至此上传进度监控
     */
    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new FileUploadMultipartResolver();
        resolver.setMaxUploadSize(1000000000);
        return resolver;
    }


    @Bean(name = "sitemeshFilter")
    public FilterRegistrationBean sitemeshFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new SitemeshFilter());
        bean.addUrlPatterns("/*");
        bean.setOrder(1111);
        return bean;
    }

    @Bean(name = "captchaServlet")
    public ServletRegistrationBean captchaServlet() {
        ServletRegistrationBean bean = new ServletRegistrationBean();
        bean.setServlet(new CaptchaServlet());
        bean.addUrlMappings("/captcha");
        return bean;
    }
}
