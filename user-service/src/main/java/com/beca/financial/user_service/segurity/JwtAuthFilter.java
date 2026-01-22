package com.beca.financial.user_service.segurity;


import com.beca.financial.user_service.JwtService.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter  extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
       String autHeader = request.getHeader("Authorization");
       if (autHeader == null || !autHeader.startsWith("Bearer ")) {
           filterChain.doFilter(request, response);
           return;
       }

       String token = autHeader.substring(7);
       if (!jwtService.isTokenValid(token)) {
           filterChain.doFilter(request, response);
           return;
       }

       String email = jwtService.extractSubject(token);

       if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
           UserDetails userDetails = userDetailsService.loadUserByUsername(email);

           @Nullable Object UserDetails = null;
           UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(UserDetails, null, userDetails.getAuthorities());
           auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
           SecurityContextHolder.getContext().setAuthentication(auth);
       }

       filterChain.doFilter(request, response);

    }
}
