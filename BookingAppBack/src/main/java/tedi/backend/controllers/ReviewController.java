package tedi.backend.controllers;


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tedi.backend.model.Department;
import tedi.backend.model.Review;
import tedi.backend.model.User;
import tedi.backend.repositories.BookingRepository;
import tedi.backend.repositories.DepartmentRepository;
import tedi.backend.repositories.ReviewRepository;
import tedi.backend.repositories.UserRepository;

import java.util.Date;
import java.util.List;

@RestController
public class ReviewController {

    ReviewRepository reviewRepository;
    UserRepository userRepository;
    DepartmentRepository departmentRepository;
    BookingRepository bookingRepository;

    ReviewController(DepartmentRepository departmentRepository, UserRepository userRepository,ReviewRepository reviewRepository,BookingRepository bookingRepository) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
    }


    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('TENANT')")
    @PostMapping("/departments/{depId}/reviews")
    public ResponseEntity<?> reviewDepartment(@RequestBody Review review, @PathVariable Long depId) {

        if( review.getText() != null || !review.getText().equals("")) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User tenant = userRepository.findByUsername(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername());
            Department department = (Department) Util.checkOptional(departmentRepository.findById(depId));
            if(!this.bookingRepository.hasBooked(depId,tenant.getId())){
                return ResponseEntity.badRequest().body("{\"timestamp\": " + "\"" + new Date().toString()+ "\","
                        + "\"status\": 400, "
                        + "\"error\": \"Bad Request\", "
                        + "\"message\": \"Cannot review without booking!\", "
                        + "\"path\": \"/departments/" + depId.toString() +"/reviews\"}"
                );
            }

            if(this.reviewRepository.hasReviewedDep(depId,tenant.getId())){
                return ResponseEntity.badRequest().body("{\"timestamp\": " + "\"" + new Date().toString()+ "\","
                        + "\"status\": 400, "
                        + "\"error\": \"Bad Request\", "
                        + "\"message\": \"Cannot review twice!\", "
                        + "\"path\": \"/departments/" + depId.toString() +"/reviews\"}"
                );
            }


            review.setFromUser(tenant);
            review.setForDepartment(department);

            reviewRepository.save(review);

            return ResponseEntity.ok("\"Successfully sent!\"");
        }

        return ResponseEntity.badRequest().body("{\"timestamp\": " + "\"" + new Date().toString()+ "\","
                + "\"status\": 400, "
                + "\"error\": \"Bad Request\", "
                + "\"message\": \"Empty review!\", "
                + "\"path\": \"/departments/" + depId.toString() +"/reviews\"}"
        );
    }

    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('TENANT')")
    @PostMapping("/users/{hostId}/reviews")
    public ResponseEntity<?> reviewHost(@RequestBody Review review, @PathVariable Long hostId) {

        if( review.getText() != null || !review.getText().equals("")) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User tenant = userRepository.findByUsername(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername());
            User host = (User) Util.checkOptional(userRepository.findById(hostId));
            if(!this.bookingRepository.hasBookedAtLeastOne(tenant.getId(),host.getId())){
                return ResponseEntity.badRequest().body("{\"timestamp\": " + "\"" + new Date().toString()+ "\","
                        + "\"status\": 400, "
                        + "\"error\": \"Bad Request\", "
                        + "\"message\": \"Cannot review without booking!\", "
                        + "\"path\": \"/departments/" + hostId.toString() +"/reviews\"}"
                );
            }

            if(this.reviewRepository.hasReviewedHost(tenant.getId(),hostId)){
                return ResponseEntity.badRequest().body("{\"timestamp\": " + "\"" + new Date().toString()+ "\","
                        + "\"status\": 400, "
                        + "\"error\": \"Bad Request\", "
                        + "\"message\": \"Cannot review twice!\", "
                        + "\"path\": \"/departments/" + hostId.toString() +"/reviews\"}"
                );
            }



            review.setFromUser(tenant);
            review.setForUser(host);

            reviewRepository.save(review);

            return ResponseEntity.ok("\"Successfully sent!\"");
        }

        return ResponseEntity.badRequest().body("{\"timestamp\": " + "\"" + new Date().toString()+ "\","
                + "\"status\": 400, "
                + "\"error\": \"Bad Request\", "
                + "\"message\": \"Empty review!\", "
                + "\"path\": \"/departments/" + hostId.toString() +"/reviews\"}"
        );
    }



    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/departmentreviews/export" , produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<Review> getDepartmentReviewsForExport() {

        List<Review> reviews = reviewRepository.getDepartmentReviews();

        return reviews;
    }

    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/hostreviews/export" , produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<Review> getHostReviewsForExport() {

        List<Review> reviews = reviewRepository.getHostReviews();

        return reviews;
    }


}
