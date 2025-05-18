package com.airbnb.service;

import com.airbnb.entity.AppUser;
import com.airbnb.payload.LoginDto;
import com.airbnb.repository.AppUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

// making this for securing password
@Service
public class AppUserServiceImpl {

    // see BnbApplication.java or SecurityConfig.java  --> to learn about below two lines of code

    // if i will use @Autowired --> Spring IOC does not know which object to create
    //which means the object creation to be done for passwordEncoder that information is
    // missing in IOC
    // now i have to go to Spring Boot and tell if you do not know which object to create
    // i will tell you which object to create --> so How to tell?:
    // in Configuration class we will tell --> which object to create for PasswordEncoder
    // an extra object to be created --> which Spring IOC does not know

    // if we will run without creating object in configuration class (BnbApplication.java)
    // it crash and give message:  see below (required a bean)
    // message 1: Parameter 0 of constructor in com.airbnb.service.AppUserServiceImpl
    // required a bean of type 'org.springframework.security.crypto.password.PasswordEncoder' that could not be found.
    // message 2: Consider defining a bean of type 'org.springframework.security.crypto.password.PasswordEncoder' in your configuration.

    //@Autowired --> do not use --> see details above below and in SecurityConfig.java

//    private PasswordEncoder passwordEncoder;

    // this.passwordEncoder = passwordEncoder; --> this interface comes from Spring Security framework
    // not from Spring Boot Framework so it is an another library from where this particular concept is coming
    // if it was Spring library itself --> then Spring Boot will know (repository) --> which
    // object i have to create
    // Spring IOC does not know which object to create so create a bean in configuration
    // so create a bean in configuration class and it will tell to Spring Boot which
    // object to crerate
    // so here we will not use --> @Autowired

    private AppUserRepository appUserRepository;

    private JWTService jwtService;

    public AppUserServiceImpl(AppUserRepository appUserRepository, JWTService jwtService) {
//        this.passwordEncoder = passwordEncoder;
        this.appUserRepository = appUserRepository;
        this.jwtService = jwtService;
    }

    // URL: http://localhost:8080/api/v1/auth
    public AppUser createUser(
            AppUser user
    ){
        // secure password
        // encrypt this --> user.getPassword() --> by 10 round --> BCrypt.gensalt(10)
        // the number of rounds should be atleast --> 4
        // below code will not work because --> URL is secured --> because of Spring Security
        // so by using  @Bean Annotation and csrf() disabled in SecurityConfig.java --> we will open the URL

        String hashpw = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(4));
        user.setPassword(hashpw);
        return appUserRepository.save(user);
    }

    // Login
    // URL: http://localhost:8080/api/v1/auth/login
    public String verifyLogin(LoginDto loginDto) {

        // usrname checking if we supply only username
//        Optional<AppUser> opUser = appUserRepository.findByUsername(loginDto.getUsername());
        // below both are --> getUsername() --> because in JSON we are supplying email
        // and username in the same entity called : "username"

        // usrname checking --> if we supply username OR password
        Optional<AppUser> opUser = appUserRepository.findByEmailOrUsername(loginDto.getUsername(), loginDto.getUsername());

        if (opUser.isPresent()) {
            AppUser appUser = opUser.get();
            // password checking
            // below we are comparing password
            if(BCrypt.checkpw(loginDto.getPassword(), appUser.getPassword())){

                // after checking the BCrypt method we will return boolean value now not
                // first expected password then actual password

                // BCrypt.checkpw() --> above it takes two things:
                // appUser.getPassword() --> password coming from database which is encrypted
                // loginDto.getPassword() --> password coming from user
                // so this method automatically does the decryption of password

//                String token = jwtService.generateToken(appUser);

                return jwtService.generateToken(appUser);

                // token --> that includes a custom claim (USER_NAME), an
                // expiration date, an issuer, and is signed with a specified algorithm

            }
        }

        return null;

    }

}
