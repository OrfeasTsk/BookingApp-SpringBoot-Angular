package tedi.backend.controllers;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import tedi.backend.model.*;
import tedi.backend.repositories.RoleRepository;
import tedi.backend.repositories.UserRepository;
import org.springframework.web.bind.annotation.*;
import tedi.backend.security.SecurityConstants;


import static tedi.backend.controllers.Util.*;

@RestController
class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;


    UserController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }


    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<User> all() {
        List<User> users  =  userRepository.findAll();
        for( User user: users){
            user.setProfilePhoto(null);
            for(Department department: user.getDepartments()){
                department.setPhotos(null);
            }
        }

        return users;
    }


    @CrossOrigin(origins = "*")
    @PostMapping(value = "/signup", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> signup(@RequestPart("object") User user,@RequestPart("imageFile") MultipartFile file) throws IOException {


        if (user.getUsername() != null)
           if (userRepository.UsernameExists(user.getUsername())) {
                return ResponseEntity
                        .badRequest()
                        .body("{\"timestamp\": " + "\"" + new Date().toString()+ "\","
                                        + "\"status\": 400, "
                                        + "\"error\": \"Bad Request\", "
                                        + "\"message\": \"Username is already taken!\", "
                                        + "\"path\": \"/signup\"}"
                                );
            }

        if (user.getEmail() != null && !"".equals(user.getEmail()))
            if (userRepository.EmailExists(user.getEmail())) {
                return ResponseEntity
                        .badRequest()
                        .body( "{\"timestamp\": " + "\"" + new Date().toString()+ "\","
                                        + "\"status\": 400, "
                                        + "\"error\": \"Bad Request\", "
                                        + "\"message\": \"Email is already in use!\", "
                                        + "\"path\": \"/signup\"}"
                                );
            }


        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            return ResponseEntity
                    .badRequest()
                    .body("{\"timestamp\": " + "\"" + new Date().toString()+ "\","
                                    + "\"status\": 400, "
                                    + "\"error\": \"Bad Request\", "
                                    + "\"message\": \"Passwords do not match!\", "
                                    + "\"path\": \"/signup\"}"
                            );
        }

        user.setPassword(encoder.encode(user.getPassword()));

        Set<Role> roles = user.getRoles();
        Set<Role> existingRoles = new HashSet<>();

        if (roles == null) {
            Role tenantRole = roleRepository.findByName(RoleNames.ROLE_TENANT);
            existingRoles.add(tenantRole);
        }
        else
            for(Role role : roles)
                switch(role.getName().name()){
                    case "ROLE_HOST":
                        Role hostRole = roleRepository.findByName(RoleNames.ROLE_HOST);
                        existingRoles.add(hostRole);
                        break;
                    default:
                        Role tenantRole = roleRepository.findByName(RoleNames.ROLE_TENANT);
                        existingRoles.add(tenantRole);
                        break;
                }


        user.setRoles(existingRoles);
        Photo photo = new Photo(file.getOriginalFilename() ,file.getContentType() ,compressBytes(file.getBytes()));
        user.setProfilePhoto(photo);

        userRepository.save(user);

        String token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()));
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        responseHeaders.set("Content-Type","application/json");
        user.setPassword(null);
        return ResponseEntity.ok().headers(responseHeaders).body(user);
    }


    @CrossOrigin(origins = "*")
    @GetMapping("/users/{id}")
    public User seeProfile(@PathVariable Long id){
        return userRepository.findById(id).map(( usertmp )->{
            Photo photo = usertmp.getProfilePhoto();
            if(photo != null){
                Photo newPhoto = new Photo(photo.getId(),photo.getName(),photo.getType(),decompressBytes(photo.getPhotoBytes()));
                usertmp.setProfilePhoto(newPhoto);
            }
            for(Department department: usertmp.getDepartments()){
                department.setPhotos(null);
            }
            return usertmp;
        }).orElseThrow(() -> new UserNotFoundException(id));

    }

    @CrossOrigin(origins = "*")
    @PutMapping("/users/{id}/passwordchange")
    public ResponseEntity changePassword(@PathVariable Long id , @RequestBody PasswordDetails pwdDetails) {
        if (!pwdDetails.getNewPassword().equals(pwdDetails.getPasswordConfirm())) {
            return ResponseEntity
                    .badRequest()
                    .body("{\"timestamp\": " + "\"" + new Date().toString()+ "\","
                            + "\"status\": 400, "
                            + "\"error\": \"Bad Request\", "
                            + "\"message\": \"Passwords do not match!\", "
                            + "\"path\": \"/users/"+ id.toString() +"/passwordchange\"}"
                    );
        }
        User user = (User) checkOptional(userRepository.findById(id));
        if(!encoder.matches(pwdDetails.getCurrPassword(),user.getPassword())){
            return ResponseEntity
                    .badRequest()
                    .body("{\"timestamp\": " + "\"" + new Date().toString()+ "\","
                            + "\"status\": 400, "
                            + "\"error\": \"Bad Request\", "
                            + "\"message\": \"Wrong password!\", "
                            + "\"path\": \"/users/"+ id.toString() +"/passwordchange\"}"
                    );
        }

        user.setPassword(encoder.encode(pwdDetails.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("\"Password Changed!\"");

    }

    @CrossOrigin(origins = "*")
    @PutMapping("/users/{id}/edit")
    public ResponseEntity editUser(@RequestBody User user, @PathVariable Long id) {
        String token = null;
        User existingUser = (User) checkOptional(userRepository.findById(id));

        if (user.getUsername() != null && !user.getUsername().equals("")) {
            if (!user.getUsername().equals(existingUser.getUsername()) && userRepository.UsernameExists(user.getUsername())) {
                return ResponseEntity
                        .badRequest()
                        .body("{\"timestamp\": " + "\"" + new Date().toString() + "\","
                                + "\"status\": 400, "
                                + "\"error\": \"Bad Request\", "
                                + "\"message\": \"Username is already taken!\", "
                                + "\"path\": \"/users/"+ id.toString() +"\"}"
                        );
            }

            if(!user.getUsername().equals(existingUser.getUsername())) {
                token = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                        .sign(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()));
                token = SecurityConstants.TOKEN_PREFIX + token;
            }
            existingUser.setUsername(user.getUsername());

        }
        if (user.getEmail() != null && !"".equals(user.getEmail())) {
            if (!user.getEmail().equals(existingUser.getEmail()) && userRepository.EmailExists(user.getEmail())) {
                return ResponseEntity
                        .badRequest()
                        .body("{\"timestamp\": " + "\"" + new Date().toString() + "\","
                                + "\"status\": 400, "
                                + "\"error\": \"Bad Request\", "
                                + "\"message\": \"Email is already in use!\", "
                                + "\"path\": \"/users/"+ id.toString() +"\"}"
                        );
            }
            existingUser.setEmail(user.getEmail());
        }
        if (user.getFirstName() != null && !user.getFirstName().equals(""))
            existingUser.setFirstName(user.getFirstName());
        if (user.getLastName() != null && !user.getLastName().equals(""))
            existingUser.setLastName(user.getLastName());
        if (user.getPhone() != null && !user.getPhone().equals(""))
            existingUser.setPhone(user.getPhone());

        userRepository.save(existingUser);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(SecurityConstants.HEADER_STRING, token);
        return ResponseEntity.ok().headers(responseHeaders).body("\"Successful edit!\"");
    }


    @CrossOrigin(origins = "*")
    @PutMapping(value = "/users/{id}/profilephoto" , consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity changeMainPhoto(@PathVariable Long id, @RequestPart("imageFile") MultipartFile file) throws IOException {

        if(!file.getOriginalFilename().equals("profilePhoto"))
            return ResponseEntity
                    .badRequest()
                    .body("{\"timestamp\": " + "\"" + new Date().toString() + "\","
                            + "\"status\": 400, "
                            + "\"error\": \"Bad Request\", "
                            + "\"message\": \"Invalid type!\", "
                            + "\"path\": \"/users/"+ id.toString() +"/profilephoto\"}"
                    );

        User user = (User) checkOptional(userRepository.findById(id));

        Photo photo = user.getProfilePhoto();
        if(photo != null) {
            photo.setType(file.getContentType());
            photo.setPhotoBytes(compressBytes(file.getBytes()));
        }
        else{
            Photo newphoto = new Photo(file.getOriginalFilename() ,file.getContentType() ,compressBytes(file.getBytes()));
            user.setProfilePhoto(newphoto);
        }

        userRepository.save(user);

        return ResponseEntity.ok("\"Profile photo changed!\"");

    }


    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/accept/users/{id}")
    public ResponseEntity accepthost(@PathVariable Long id) {

        User existingUser = (User) checkOptional(userRepository.findById(id));
        Boolean flag = false;

        for(Role role: existingUser.getRoles())
            if(role.getName().equals(RoleNames.ROLE_HOST))
                flag = true;

        if(existingUser.getAccepted() == null && flag )
            existingUser.setAccepted(true);
        else
            return ResponseEntity
                    .badRequest()
                    .body("{\"timestamp\": " + "\"" + new Date().toString() + "\","
                            + "\"status\": 400, "
                            + "\"error\": \"Bad Request\", "
                            + "\"message\": \"Cannot accept!\", "
                            + "\"path\": \"/admin/accept/users/"+ id.toString() +"\"}"
                    );

        userRepository.save(existingUser);

        return ResponseEntity.ok("\"Host Accepted!\"");
    }



}
