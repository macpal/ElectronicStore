package com.monks.electronic.store.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtHelper jwtHelper;
    private UserDetailsService userDetailsService;

    private Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtHelper jwtHelper, UserDetailsService userDetailsService) {
        this.jwtHelper = jwtHelper;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //Authorization
        String requestHeader = request.getHeader("Authorization");
        logger.info(" Header: {}", requestHeader);

        //Bearer 342123256363saeraagfdgs
        String username=null;
        String token=null;
        if(requestHeader!=null && requestHeader.startsWith("Bearer")) {
            //looking good
            token = requestHeader.substring(7); //Return except Bearer
            try {
                username=this.jwtHelper.getUsernameFromToken(token);
            } catch (IllegalArgumentException e) {
                logger.info("Illegal argument while fetching username !!");
                e.printStackTrace();
            } catch (ExpiredJwtException e) {
                logger.info("Given Jwt token is expired !!");
                e.printStackTrace();
            } catch (MalformedJwtException e) {
                logger.info("Some changes done in token !! Invalid token !!");
                e.printStackTrace();
            }
        } else {
            logger.info("Invalid Header Value !!");
        }
//       /*check if username is not empty and no user in use by checking getContext() */
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            /* Fetch user detail from username*/
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            Boolean validateToken = this.jwtHelper.validateToken(token, userDetails);
            if(validateToken) {
                /* Set authentication for user*/
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                /* Validation Failed*/
                logger.info("Validation Failed !!");
            }

        }
        filterChain.doFilter(request, response);
    }
}
