package com.example.jwtserver.filter;

import javax.servlet.*;
import java.io.IOException;

public class MyFilter1 implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("필터 1");



        // 필터 끝나지 않게 계속 doFilter 호출
        filterChain.doFilter(servletRequest, servletResponse);
    }

}
