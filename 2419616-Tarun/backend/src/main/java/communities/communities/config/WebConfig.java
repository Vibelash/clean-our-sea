package communities.communities.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**").allowedOrigins("*").allowedMethods("GET","POST","PUT","DELETE","OPTIONS");
    }

    // serve the static front-end files from the sibling "front-end" folder so that
    // the pages can be accessed via http://localhost:8080/communities.html etc.  This
    // ensures the chat page is loaded from the same origin as the API and avoids
    // the "Unknown" title / network errors when the HTML is opened directly via
    // file://.
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/", "file:../front-end/");
    }
}
