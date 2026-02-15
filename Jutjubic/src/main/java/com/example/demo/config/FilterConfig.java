package com.example.demo.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public VisitorActivityFilter visitorActivityFilter(MeterRegistry meterRegistry) {
        return new VisitorActivityFilter(meterRegistry);
    }

    @Bean
    public FilterRegistrationBean<VisitorActivityFilter> visitorActivityFilterRegistration(
            VisitorActivityFilter filter
    ) {
        FilterRegistrationBean<VisitorActivityFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(filter);
        reg.addUrlPatterns("/*");
        reg.setOrder(1);
        return reg;
    }
}
