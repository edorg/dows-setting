package org.dows.setting.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class TraceFilter implements Filter {


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
            HttpServletRequest req = (HttpServletRequest) request;
            String traceId = req.getHeader("X-Trace-Id");
            if (traceId != null && !traceId.isBlank()) {
                TraceContext.set(traceId);
            } else {
                TraceContext.getOrCreate();
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            TraceContext.clear();
        }
    }
}