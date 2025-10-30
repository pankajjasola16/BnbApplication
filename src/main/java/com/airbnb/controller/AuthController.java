package com.airbnb.controller;

import com.airbnb.entity.AppUser;
import com.airbnb.exception.UserExists;
import com.airbnb.payload.JWTToken;
import com.airbnb.payload.LoginDto;
import com.airbnb.repository.AppUserRepository;
import com.airbnb.service.AppUserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private AppUserRepository appUserRepository;
    private AppUserServiceImpl appUserService;

    public AuthController(AppUserRepository appUserRepository, AppUserServiceImpl appUserService) {
        this.appUserRepository = appUserRepository;
        this.appUserService = appUserService;
    }

    //                           ADMIN   SIGNUP

    // ADMIN Signup do not give --> there should be one project id that is --> hard coded
    // manually you will have to make an entry --> because an AADMIN can create -->
    // property manager --> username passsword (under ADMIN there will be lot of Property
    // manager) --> so property manager will check(or verify) whether photo or details are
    // proper --> ADMIN has created property manager Account (ADMIN has authority)
    // So now --> How ADMIN will signup
    // so MANUAALY Create the Account of ADMIN in databse (WE have --> thyson as an ADMIN)
    // BUT How to write Encrypted password --> see in --> Class A --> we have encrypted the
    // password --> testing --> so copy that and paste into database
    // ADMIN SIGNUP is predefined in our software

    //                     Property Manager
    // Now ADMIN can create --> property manager password
    // property manager signup --> only ADMIN can do --> means i will protect that URL
    // so flow is --> login as an ADMIN --> get the JWT Token --> and only with ADMIN Token
    // i can create property manager Signup Detail

    // http://localhost:8080/api/v1/auth
    // this URL is secured because of Spring Security, but we have opened it in
    //  SecurityConfig.java by using @Bean
    // below URL should Sign Up as a --> user Role --> so after adding a role field in AppUser.java
    // change the below URL --> now before going to createUser() method set the --> Role --> as
    // we set the password --> so set Role before creating user because anybody who signup
    // they do not know what is their Role
    // http://localhost:8080/api/v1/auth/createuser
    // whoever will sign up he will be called --> user --> so he can only search hotel & book hotel
    // And he can Log in only as a --> user
    @PostMapping("/createuser")
    public ResponseEntity<AppUser> createUser(
            @RequestBody AppUser user
    ){
        Optional<AppUser> opEmail = appUserRepository.findByEmail(user.getEmail());
        // Above we are finding user in database and if user already exist:
        // create package --> exception --> to throw exception --> if user already exist
        // RunTime Exception --> here all the exception are of type Run Time not Compile time
        // because if it is Compile time --> you need to surround it inside --> try-catch

        if(opEmail.isPresent()){
            throw new UserExists("Email id Exists");
        }

        Optional<AppUser> opUsername = appUserRepository.findByUsername(user.getUsername());

        if(opUsername.isPresent()){
            throw new UserExists("username Exists");
        }

        // before saving the record encrypt the password
        // for learning see class --> A --> how to encrypt password
        // whoever will signup he will be called --> user
        user.setRole("ROLE_USER");
              // if we want to set the role from the JSON below
//        user.setRole("ROLE_" + user.getRole().toUpperCase());
        AppUser savedUser = appUserService.createUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    // Signup as a Property Owner (ROLE_OWNER) --> he can upload a photo of hotel for booking
    @PostMapping("/createpropertyowner")
    public ResponseEntity<AppUser> createPropertyOwner(
            @RequestBody AppUser user
    ){
        Optional<AppUser> opEmail = appUserRepository.findByEmail(user.getEmail());

        if(opEmail.isPresent()){
            throw new UserExists("Email id Exists");
        }

        Optional<AppUser> opUsername = appUserRepository.findByUsername(user.getUsername());

        if(opUsername.isPresent()){
            throw new UserExists("username Exists");
        }

        user.setRole("ROLE_OWNER");
        AppUser savedUser = appUserService.createUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    // Below Signup URL --> can be accessed only by --> ADMIN
    // And when you call this proprty manager URL --> tHIS fellow will be Signed in as
    // manager --> ROLE_MANAGER
    // how to access this URL --> so first login as an ADMIN --> get the Token of ADMIN -->
    // and make this URL works --> and if i login as a USER or anything else --> they should
    // not work --> see the coding in --> JWTFilter.java  --> in ---> Authorities -->
    // inside --> new UsernamePasswordAuthenticationToken(appUser,null,null);
    // THIS THIRD PARAMETER --> WHICH IS COLLECTION --> Is of type --> SimpleGrantedAuthority
    // and that we have to develop --> which is built in feature(class) --> so supplied this data
    // as a SimpleGrantedAuthority object and store tyhe object in collection --> and this
    // collection has only one data(not multiple data) --> it is a Special Collection
    // so here we will give --> Colections.singleton() --> means whatever collection i will
    // give in it --> should be a collection --> which has only one value in it --> we cannot
    // give a Collection with multiple value (so only collection of only one object)
    // and this Collection should have what object --.>So this Collection should have an object
    // of the Type --> new SimpleGrantedAuthority()

    // http://localhost:8080/api/v1/auth/createpropertymanager
    @PostMapping("/createpropertymanager")
    public ResponseEntity<AppUser> createPropertyManager(
            @RequestBody AppUser user
    ){
        Optional<AppUser> opEmail = appUserRepository.findByEmail(user.getEmail());

        if(opEmail.isPresent()){
            throw new UserExists("Email id Exists");
        }

        Optional<AppUser> opUsername = appUserRepository.findByUsername(user.getUsername());

        if(opUsername.isPresent()){
            throw new UserExists("username Exists");
        }

        user.setRole("ROLE_MANAGER");
        AppUser savedUser = appUserService.createUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    // Login
    // URL: http://localhost:8080/api/v1/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> signIn(
            @RequestBody LoginDto loginDto
    ){
        String token = appUserService.verifyLogin(loginDto);
        JWTToken jwtToken = new JWTToken();

        if (token!=null){
            jwtToken.setTokenType("JWT");
            jwtToken.setToken(token);
            return new ResponseEntity<>(jwtToken, HttpStatus.OK);
            // Now token will be come back as JSON and this token can be extract from JSON
        }else
            return new ResponseEntity<>("Invalid username/password", HttpStatus.UNAUTHORIZED);

    }
}







