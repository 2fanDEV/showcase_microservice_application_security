package de.security.microservice.userservice.configuration.WebMvc.Interceptor;

import de.security.microservice.userservice.configuration.Service.FeignHelperService;
import de.security.microservice.userservice.feign.AuthServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Intercept each request and extract the bearer token from the request header
 * and insert it into the FeignHelperService so we can insert it into the
 * header of the FeignRequests
 * https://www.baeldung.com/spring-mvc-handlerinterceptor
 * https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/HandlerInterceptor.html
 */
@Configuration
public class CustomInterceptor implements HandlerInterceptor {

    Logger logger = LoggerFactory.getLogger(CustomInterceptor.class);

    @Autowired
    private AuthServiceFeign authServiceFeign;

    @Autowired
    private FeignHelperService feignHelperService;

    /**
     * extract the authorization header (token) out of the request
     * and insert it into the feignHelperServicec
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if(token != null) {
            logger.info("FEIGNHELPERSERVICE INSERT TOKEN");
            logger.info("inserted_token:" + token.replace("Bearer ", ""));
        }
        feignHelperService.setAuthorizationHeader(token);
        return true;
    }

    /**
     * do nothing
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {

    }


    /**
     * after request is finished, delete the token out of the service
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        logger.info("DELETE TOKEN");
        this.feignHelperService.setAuthorizationHeader("");
    }
}
