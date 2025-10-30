package com.airbnb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

// we are encrypting the password before saving it but URL are secured so here in
// configuration file we will give permission to all URL --> do not secure URL
// here i can decide (configure) what i want to achieve from Spring Security

// this class loaded first into the Spring Boot memory automatically

// which URL which user can access
// so this is the file where i will grant the permission that this user can access this URL
// and this user role cannot access this URL
@Configuration
public class SecurityConfig {

    // new BCryptPasswordEncoder() --> it is returning the object of PasswordEncoder


//    @Bean
//    public PasswordEncoder getPasswordEncoder(){
//        return new BCryptPasswordEncoder();
//    }


    // now i want my Spring Security Framework to communicate with Spring IOC and tell:
    // let this URL be an --> open URL  --> Do not secure the URL

    // so in order to Configure it with Spring Security --> Annotate this method with --> @Bean
    // and it will automatically create one object and in that object --> now i will do
    // configuration wherein that configuration Spring Boot will study and accordingly do the
    // required things for me in the project
    // so what is that object -->     see below
    // HttpSecurity http --> this object is now automatically created because of Bean Annotation
    // and the Bean Annotation takes this http object and this object --> it will give it to
    // Spring IOC

    private JWTFilter jwtFilter;

    public SecurityConfig(JWTFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
            // HttpSecurity http --> this object is now automatically created because of Bean Annotation
            // and the Bean Annotation takes this http object and this object --> it will give it to
            // Spring IOC

    ) throws Exception {

        // now do the first Configuration

        // Below this line of code which earlier was restricting me(giving 403 error forbidden)
        // to fire a request from third party software --. thisā helps me stop that
        // below we are disabling csrf() --> so that any third party software can access the URL

        // h(cd)2   --> formula
        http.csrf().disable().cors().disable();

        // This line disables CSRF protection (making the app vulnerable to CSRF attacks) and
        // CORS restrictions (allowing requests from any origin, which can be risky for public
        // APIs).

        // Enabling CSRF and CORS is safer because it protects against CSRF attacks and
        // unauthorized cross-origin requests. Disabling them reduces security but may be
        // needed for public APIs.

        // below we are doing configuration and @Bean Annotation is giving http object to
        //Spring IOC and telling --> whatever i have written in that object --> you read
        // that object and accordingly do my work

        // haap  --> shortcut way to remember the below code
//        http.authorizeHttpRequests().anyRequest().permitAll();

        // above line means keep all the http request open --> permitAll()
        // above-->  http object has all the information and this information says keep all the URL OPEN
        // and this http object is given to Spring IOC by @Bean Annotation
//        return http.build();

        // this will help us to build the object (so instead of using new keyword the object
        // is now built (http.build()) with this information --> http.authorizeHttpRequests())


        // http.build() --> This object @Bean --> i will give it to Spring Boot
        // Spring Boot will read that object and it will understand --> i am keeping all the
        // URL open as of now.

        // below URL IS --> permitAll() --> means accessible by all
        // but any other request happens to be --> Authenticated --> that cannot be accessed
        // without you --> login
        // so only this request --> "/api/v1/auth/**" --> is permitAll and
        // other request are Authenticated() --> .anyRequest().authenticated();

        // below line means run --> AuthorizationFilter first even before the --> jwtFilter
        // after login the request should go to Filter
        // the URL which are permitAll()  --> For them Filter class should not come into picture
        // because if Filter class will not run --> then only open URL will work
        // but if Filter class run then open URL logic will not work
        // so below code is for --> only when i want to run Filter class but not for evrery request
        http.addFilterBefore(jwtFilter, AuthorizationFilter.class);
        // AuthorizationFilter.class --> this class will check which URL is permitAll
        // for the time being permit all URL BELOW --> for testing fast: below code
//        http.authorizeHttpRequests().anyRequest().permitAll();
        http.authorizeHttpRequests()
//        http.authorizeHttpRequests()
//                // below one is for signup for user and owner --> open URL --> BOOKING url will also open
//                .requestMatchers("/api/v1/auth/createuser","/api/v1/auth/createpropertyowner","/api/v1/auth/login")
//                .permitAll()
////                .requestMatchers("/api/v1/dummy/**")
////                .permitAll()
//

//                .requestMatchers("/addRoom").permitAll()

                // below permission for booking controller -->then we can book without JWT
                // Token--> means if --> Auth Type is not--> Bearer Token --> we can do the booking
                // but if we will comment below code --> then it will fall under --> anyRequest().authenticated();
                // means --> without JWT Token we cannot access this CONTROLLER

//                .requestMatchers("/api/v1/booking/createBooking").permitAll()

                // below we have given permission for AuthController
                .requestMatchers("/api/v1/auth/createuser").permitAll()
                .requestMatchers("/api/v1/auth/login").permitAll()
                // below we have given permission for PropertyController
                .requestMatchers("/api/v1/property/propertyresult").permitAll()
                // anyone can rate
                .requestMatchers("/api/v1/reviews/rate").permitAll()
                .requestMatchers("/api/v1/otp/**").permitAll()  // 👈 allow OTP endpoints
                // below one --> only owner can add property
                .requestMatchers("/api/v1/property/addProperty").hasAnyRole("OWNER","ADMIN","MANAGER")
//                .requestMatchers("/api/v1/auth/createpropertymanager").hasRole("ADMIN")
                .requestMatchers("/api/v1/images/**").hasAnyRole("OWNER","ADMIN","MANAGER")
                // only below roles can add country
                .requestMatchers("/api/v1/country/addCountry").hasAnyRole("OWNER","ADMIN", "MANAGER")
                .requestMatchers("/rooms/**").permitAll()
                .requestMatchers("/api/v1/booking/checkAvailabilityAndCreatePayment").permitAll()
                .requestMatchers("/addRoom").hasAnyRole("OWNER","ADMIN", "MANAGER")
                .requestMatchers("/api/v1/auth/createpropertymanager").hasRole("ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/api/v1/property/deleteProperty").hasAnyRole("OWNER", "ADMIN")


                // permission for swagger below
                .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                ).permitAll()

                // permit all access to static files like add-countrey.html, CSS, JS, etc.
//                .requestMatchers("/", "/add-country.html", "/css/**", "/js/**", "/images/**").permitAll()
                // permit all add-city, add-country, add-property in one line to open html pages only not for roles
                .requestMatchers(
                        "/",
                        "/add-country.html",
                        "/add-city.html",
                        "/add-property.html",
                        "/your-added-property.html",
                        "/booking.html",
                        "/booking-confirmation.html",
                        "/search-results.html",
                        "/all-properties.html",
                        "/Hotel-dashboard.html",
                        "/Login.html",
                        "/signup.html",
                        "/add-room.html",
                        "/payment.html",
                        "/otp-login.html",
                        "/create-manager.html",
                        "/my-bookings.html",
                        "/jobportal.html",
                        "/booking.js",
                        "/booking-confirmation.js",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/favicon.ico"
                ).permitAll()



                .anyRequest().authenticated();

//   above all will not check roles untill you mention like --> hasAnyRole("OWNER","ADMIN", "MANAGER")

//        If you want to allow all HTML files inside static folder without listing every single file, you could do:

//                .requestMatchers("/**/*.html", "/css/**", "/js/**", "/images/**").permitAll()

//        This permits all .html files under your static folder.

        // Above .anyRequest().authenticated(); --> any request apart from  --> "/api/v1/auth/**"
        // will work only when we send the request with --> JWT Token
        // Above -->  .requestMatchers("/api/v1/auth/**") --> this is login url which is
        // permitAll()
        // any URL Authenticated --> .anyRequest().authenticated(); --> should work after Authorization
        // and any URL which are open should run before Authorization or for Authorization
        // for means --> login page

        // and which URL is not permitted --> so above 2-3 lines of code where we are permitting
        // them or Authenticated these are Authorization code

        // if we will write below two lines of code and give the the
        // URL --> http://localhost:8080/api/v1/dummy/getMessage then it will run without
        // jwt token --> BECAUSE IT IS PERMIT ALL --> But if we want to secure the URL
        // WE need to make it --> .anyRequest().authenticated(); --> then first filter method
        // will run --> means Authentication will happen first --> then verify

        //.requestMatchers("/api/v1/dummy/**")
          // .permitAll()

        // so based on username --> which we are getting in JWTFilter.java(System.out.println(username);)
        // we will get the record from database --> see JWTFilter.java

        return http.build();
    }
}

//     @Bean   Annotation

// whenever we use @Autowired --> at time the objects are not created --> because your
// Spring IOC does not know which object to create

//         with practical ex:     why to use @Bean Annotation
// in Spring security i am using passwordEncoder with @Autowired for that the object will
// not be created --> so now which object to create --> i have to configure that -> and
// i will do that by using --> @Bean Annotation in my configuration class

// now IOC has information which object to create

//                    JWTFilter class:

// Only after  Authentication and Authorization completed  we get the  Token
// After you get the Token  your JwtFilter should work
// JwtFilter should not run first  (not before Authentication and Authorization)
// So add one line in the code  in Configuration file:
//        http.addFilterBefore(jwtFilter, AuthorizationFilter.class)

//                     @Configuration

// @Configuration: Marks the class as a configuration class, meaning it contains beans and configuration
// settings for the application context. Spring Boot will load this class during startup and manage it as
// part of the application context.

// @Bean: Indicates that a method produces a bean (an object managed by Spring IoC) that will be registered
// in the application context. For example:

// securityFilterChain(HttpSecurity http) creates and configures a SecurityFilterChain bean to manage
// HTTP security settings.
// getPasswordEncoder() (if uncommented) would create a PasswordEncoder bean using BCryptPasswordEncoder.
// These annotations help Spring manage and provide the necessary objects and configurations.

                  // What does return http.build(); return?

// It returns a SecurityFilterChain object.
// This object contains the entire security configuration defined in the HttpSecurity object, such as
// authentication, authorization, filters, and other security-related settings.
// Spring Security uses this chain to process incoming HTTP requests and enforce the configured security
// rules.



