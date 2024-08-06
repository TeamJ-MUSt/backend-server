package TeamJ.MUSt.web;

import TeamJ.MUSt.web.interceptor.LoggingInterceptor;
import TeamJ.MUSt.web.interceptor.LoginCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final ApiQueryCounter apiQueryCounter;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(0)
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/logout", "/members/new");

        registry.addInterceptor(new LoggingInterceptor(apiQueryCounter))
                .order(1)
                .addPathPatterns("/**");

    }
}
