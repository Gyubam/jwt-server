package com.example.jwtserver.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.jwtserver.config.auth.PrincipalDetails;
import com.example.jwtserver.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

// 스프링 시큐리티에서 UsernamePasswordAuthenticationFilter가 있음
// login 요청해서 username, password 전송하면(post)
// UsernamePasswordAuthenticationFilter 동작

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    // /login 요청 시 로그인 시도를 위해서 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        // 1. username, password 받음

        // 2. 정상인지 로그인 시도

        // 3. authenticationManager로 로그인 시도, PrincipalDetailsService가 호출

        // 4. loadUserByUsername 함수 실행

        // 5. principalDetails 세션에 담고 (권한 관리를 위해서)

        // 6. JWT토큰을 만들어서 응답해주면 됨

        try {
//            BufferedReader br = request.getReader();
//            String input = null;
//            while((input = br.readLine()) != null) {
//                System.out.println("input = " + input);
//            }
            ObjectMapper om = new ObjectMapper();
            User user = om.readValue(request.getInputStream(), User.class);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

            // principalDetailsService의 loadUserByUsername() 함수가 실행 후 정상이면 authentication 반환
            // DB에 있는 username과 password가 일치한다
            Authentication authentication =
                    authenticationManager.authenticate(authenticationToken);

            // -> 로그인이 되었다는 뜻
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            System.out.println("로그인 완료 : " + principalDetails.getUser().getUsername());

            // authentication 객체가 session 영역에 저장됨
            // 리턴의 이유: 권한 관리를 security가 대신 해주기때문에
            // jwt 토큰 사용하면서 세션 만들 이유는 X
            return authentication;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // attemptAuthentication 실행 후 인증이 정상적으로 되었으면 실행
    // jwt 토큰 만들어서 request 요청 사용자에게 jwt 토큰 응답
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication 실행");

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        // RSA 방식은 아님, Hash 암호 방식
        String jwtToken = JWT.create()
                .withSubject("cos토큰")
                .withExpiresAt(new Date(System.currentTimeMillis() + (60000) * 10))
                .withClaim("id", principalDetails.getUser().getId())
                .withClaim("username", principalDetails.getUser().getUsername())
                .sign(Algorithm.HMAC512("cos"));


        response.addHeader("Authorization", "Bearer " + jwtToken);
    }
}
