//package com.boilerplate.boilerplate.global.filter;
//
//import jakarta.servlet.Filter;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Collection;
//import org.springframework.stereotype.Component;
//
//@Component
//public class SameSiteCookieFilter implements Filter {
//
//    @Override
//    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
//        throws IOException, ServletException {
//        HttpServletResponse response = (HttpServletResponse) res;
//
//        chain.doFilter(req, res);
//
//        Collection<String> headers = response.getHeaders("Set-Cookie");
//        boolean firstHeader = true;
//        for (String header : headers) {
//            if (header.contains("SameSite")) {
//                continue; // 이미 포함된 경우는 무시
//            }
//            String newHeader = header + "; SameSite=None";
//            if (firstHeader) {
//                response.setHeader("Set-Cookie", newHeader);
//                firstHeader = false;
//            } else {
//                response.addHeader("Set-Cookie", newHeader);
//            }
//        }
//    }
//}