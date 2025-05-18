package com.airbnb.config;

import com.airbnb.entity.AppUser;
import com.airbnb.repository.AppUserRepository;
import com.airbnb.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

// if this class (JWTFilter.java) will be commented --> then Authorization class(in SecurityConfig.java)
// will be given the priority --> Authorization we have done in --> SecurityConfig.java
// But if JWTFilter and Authorization filter are there --> then JWTFilter always run
// first -> and then Authorization filter by default --> and now we need to alter the sequence
// so add this --> http.addFilterBefore(jwtFilter, AuthorizationFilter.class); --> in
// SecurityConfig.java (alter means add Authorization filter before JWTFilter)
// and when JWTFilter runs we have the -->  filterChain.doFilter(request,response); -->
// which further does some internal filtering mechanism --> to now decide --> wheather
// the URL access to be granted or denied

// after verification of Token from this class request will go to Spring security
// and tell --> The Token is valid --> Spring security grant the permission

// this class will run when we supply request with JWT Token (because these request
// are Authenticated --. see in SecurityConfig.java)
// @Component --> now the details of the below class will be with Spring Boot
@Component
public class JWTFilter extends OncePerRequestFilter {

    private JWTService jwtService;
    private AppUserRepository appUserRepository;

    public JWTFilter(JWTService jwtService, AppUserRepository appUserRepository) {
        this.jwtService = jwtService;
        this.appUserRepository = appUserRepository;
    }

    // call getUserName() method from here
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // request --> the URL when you are submitting in the Brwoser --> that URL will
        // automatically come to this method in request object
        // and request object is having a URL information
        // from this object we have to extract the token but where is the Token
        // Token is under Authorization (see in POSTMAN inside Header then click hidden)

        // more detail:
        // doFilter method  which we are overriding it  which is coming from  OncePerRequestFilter
        // HttpServletRequest request  --> this request object has URL information and from this
        // URL we have to extract the Token --> and the Token is present in the Header of
        // the request under authorization (in POSTMAN)

        // Retrieve JWT token from the Authorization header
        // request  in the POSTMAN  http://localhost:8080/api/v1/auth


        String token = request.getHeader("Authorization");
        // request.getHeader("Authorization"); --> inside request there is url --> and inside
        // Header there is Authorization
        // token is present in the header of the request
//        System.out.println(token);
        // in this token --> the word --> Bearer is coming --> so remove that
        if (token!=null && token.startsWith("Bearer")){
            String tokenVal = token.substring(8, token.length()-1);
            String username = jwtService.getUserName(tokenVal);
//            System.out.println(username);
//            System.out.println(tokenVal);

            Optional<AppUser> opUser = appUserRepository.findByUsername(username);

//            AppUser appUser = opUser.get();
//            System.out.println(appUser.getName());
//            System.out.println(appUser.getEmail());
//            System.out.println(appUser.getUsername());
//            System.out.println(appUser.getPassword());
            if(opUser.isPresent()){
                 AppUser appUser = opUser.get();
                 // above we have got the exact user object and having the database record

                // Role based Authentication

                // search below code in Chat Gpt
                // this constructor is developed by Spring Boot and Spring Security
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(appUser,null, Collections.singleton(new SimpleGrantedAuthority(appUser.getRole())));

                // Above third parameter is Authority --> and it is a Collection --> so here we cannot
                // give --> appUser.getRole()
                // THIS THIRD PARAMETER --> WHICH IS COLLECTION --> Is of type --> SimpleGrantedAuthority
                // and that we have to develop --> which is built in feature(class) --> so supplied this data
                // as a SimpleGrantedAuthority object and store the object in collection --> and this
                // collection has only one data(not multiple data) --> it is a Special Collection
                // so here we will give --> Colections.singleton() --> means whatever collection i will
                // give in it --> should be a collection --> which has only one value in it --> we cannot
                // give a Collection with multiple value (so only collection of only one object)
                // and this Collection should have what object --.>So this Collection should have an object
                // of the Type --> new SimpleGrantedAuthority() --> (see details below)

                // auth --> this has a user details now (it will also contain role and Authority)
                // and this user details i get from --> Token --> Token comes from --> POSTMAN
                // FROM that Token username taken --> from that username --> user details fetched
                // from the database --> that user details --> You need to send this in this
                //  object --> UsernamePasswordAuthenticationToken auth
                // once you set this user details in this --> auth --> now you are
                // telling to this user details --> you grant the permission for this:
                // request --> auth.setDetails(new WebAuthenticationDetails(request)); see below code
                // this request is also being send in --> auth
                // first time in --> auth --> i am sending --> user details:
                // UsernamePasswordAuthenticationToken auth  and second time in
                // auth --> i am sending --> request


                // Above is built in class that comes in Spring Security
                // in this class we will supply 3 things to its Constructor so first one is:
                // 1: principal --> means which user is accessing(OR Demanding) this URL
                // 2: null --> Credentials (not needed here as it's already authenticated)., --> if it is not null we will have to give user password
                // 3: null(because role part is not there like ADMIN, USER etc or Authority or Authorization)
                // here appUser --> is a Principal --> means which is user or consist of username

                // formula to remember --> as(a) we(w) request for job we never get job
                auth.setDetails(new WebAuthenticationDetails(request));

                // Above It sets additional request details (like IP address and session ID) in
                // the authentication object.

                // now once you set this Token --> this particular request for this Token --> Allow that

                // at last we tell to Spring Security that --> you further check and
                // grant the permission:

                // formula --> sgs
                SecurityContextHolder.getContext().setAuthentication(auth);

                // now Spring Security understand that --> this Token was valid --> this
                // Token is --> this user and --> this user is requesting --> this URL
                // ultimately we are giving this object --> auth --> to Spring Security
                // now Spring security will further investigate and see --> this URL -->
                // should be given or not --> so this is like taking permission from the
                // Spring Security

             }
            // and here once token is valid we have to inform Spring Security --> to grant
            // access to this URL (which is Authenticated )
        }

        // request.getHeader("Authorization"); --> inside request there is url --> and inside
        // Header there is Authorization

        filterChain.doFilter(request,response);

        // Above line means --> once your JWTFilter runs there is further chain of filter
        // which should run internally in your Spring Security Framework --> which will take care
        // of permitting the access for that URL --> OR Denying the access for that URL
        // above we have only two filters --> but apart from that there are several internal filter
        // means there are chain of filter --> from the chain of filter once this complete request
        // passes through --> then it takes care of wheather to grant the permission or not

        //                    new SimpleGrantedAuthority()

//        It encapsulates a single authority (e.g., role), such as "ROLE_ADMIN" or "ROLE_USER",
//        which Spring uses for authorization decisions.

//        Example:

        // SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMIN");

//        This means the user has the "ROLE_ADMIN" authority. It is typically used in collections
//        like Collections.singleton() for configuring user roles.
    }
}
