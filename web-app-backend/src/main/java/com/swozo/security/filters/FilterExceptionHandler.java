package com.swozo.security.filters;

import com.swozo.security.exceptions.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.DefaultCorsProcessor;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class FilterExceptionHandler extends OncePerRequestFilter {
    private final Logger logger = LoggerFactory.getLogger(FilterExceptionHandler.class);

    private final ApplicationContext context;

    @Autowired
    public FilterExceptionHandler(ApplicationContext context) {
        this.context = context;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            filterChain.doFilter(request, response);
            return;
        } catch (UnauthorizedException ex) {
            logger.info("Unauthorized", ex);

            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getOutputStream().print("..."); // probably wont need any extra info on frontend
        } catch (Exception ex) {
            logger.warn("Unexpected filter exception", ex);

            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }

        addCorsHeaders(request, response);
    }

    private void addCorsHeaders(HttpServletRequest request, HttpServletResponse response) {
        try {
            // if exception (such as UnauthorizedException thrown by AuthFilter) is thrown
            // filter chain doesn't add cors headers by default, and we get cors errors in the browser
            new DefaultCorsProcessor().processRequest(
                    context.getBean(HandlerMappingIntrospector.class).getCorsConfiguration(request),
                    request,
                    response
            );
        } catch (Exception exception) {
            logger.error("Failed to add cors headers", exception);
        }
    }
}
