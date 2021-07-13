package tedi.backend.controllers;


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tedi.backend.model.Booking;
import tedi.backend.model.Department;
import tedi.backend.model.User;
import tedi.backend.repositories.BookingRepository;
import tedi.backend.repositories.DepartmentRepository;
import tedi.backend.repositories.UserRepository;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static tedi.backend.controllers.Util.*;

@RestController
public class BookingController {

    BookingRepository bookingRepository;
    UserRepository userRepository;
    DepartmentRepository departmentRepository;

    BookingController(DepartmentRepository departmentRepository, UserRepository userRepository,BookingRepository bookingRepository ) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }


    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('TENANT')")
    @PostMapping("/departments/{depId}/bookings")
    public ResponseEntity<?> book(@RequestBody Booking booking, @PathVariable Long depId) {
        Date currentDate = new Date();

        if(setTimeToZero(booking.getStartDate()).before(setTimeToZero(currentDate)) || setTimeToZero(booking.getEndDate()).before(setTimeToZero(currentDate))){

            return ResponseEntity
                    .badRequest()
                    .body("{\"timestamp\": " + "\"" + new Date().toString()+ "\","
                            + "\"status\": 400, "
                            + "\"error\": \"Bad Request\", "
                            + "\"message\": \"Invalid dates!\", "
                            + "\"path\": \"/departments/" + depId.toString() +"/bookings\"}"
                    );
        }

        if (bookingRepository.BookingExists(depId,booking.getStartDate(),booking.getEndDate())){
            return ResponseEntity
                    .badRequest()
                    .body("{\"timestamp\": " + "\"" + new Date().toString()+ "\","
                            + "\"status\": 400, "
                            + "\"error\": \"Bad Request\", "
                            + "\"message\": \"No available dates!\", "
                            + "\"path\": \"/departments/" + depId.toString() +"/bookings\"}"
                    );
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User tenant = userRepository.findByUsername(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername());
        Department department = (Department) Util.checkOptional(departmentRepository.findById(depId));
        booking.setTenant(tenant);
        booking.setDepartment(department);

        bookingRepository.save(booking);

        return ResponseEntity.ok().body(booking);
    }

    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('TENANT')")
    @DeleteMapping("/bookings/{bookingId}")
    public ResponseEntity deleteBooking(@PathVariable Long bookingId) {
        Set<Booking> bookingSet;
        Booking booking = (Booking) Util.checkOptional(bookingRepository.findById(bookingId));
        Department department = (Department)  Util.checkOptional(departmentRepository.findById(booking.getDepartment().getId()));
        User tenant = (User) Util.checkOptional(userRepository.findById(booking.getTenant().getId()));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername());

        if ( user.getId() != tenant.getId() ) {
            return ResponseEntity
                    .badRequest()
                    .body("{\"timestamp\": " + "\"" + new Date().toString() + "\","
                            + "\"status\": 403, "
                            + "\"error\": \"Forbidden\", "
                            + "\"message\": \"Not owning the booking!\", "
                            + "\"path\": \"/bookings/" + bookingId.toString() +"\"}"
                    );
        }

        
        Date currentDate = new Date();

        if( setTimeToZero(booking.getEndDate()).before(setTimeToZero(currentDate))){
            return ResponseEntity
                    .badRequest()
                    .body("{\"timestamp\": " + "\"" + new Date().toString()+ "\","
                            + "\"status\": 400, "
                            + "\"error\": \"Bad Request\", "
                            + "\"message\": \"Cannot delete!\", "
                            + "\"path\": \"/bookings/" + bookingId.toString() +"\"}"
                    );
        }


        bookingSet = new HashSet<Booking>();
        for (Booking bookingtmp : department.getBookings()) {
            if (bookingtmp.getId() != bookingId)
                bookingSet.add(bookingtmp);
        }

        department.setBookings(bookingSet);
        departmentRepository.save(department);
        booking.setDepartment(null);



        bookingSet = new HashSet<Booking>();
        for (Booking bookingtmp : tenant.getBookings()) {
            if (bookingtmp.getId() != bookingId)
                bookingSet.add(bookingtmp);
        }
        tenant.setBookings(bookingSet);
        userRepository.save(tenant);
        booking.setTenant(null);


        bookingRepository.delete(booking);


        return ResponseEntity.ok("\"Successfully deleted\"");
    }

    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('TENANT')")
    @GetMapping("/bookings")
    public Set<Booking> getBookings(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User tenant = userRepository.findByUsername(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername());
        return tenant.getBookings();
    }

    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('TENANT')")
    @GetMapping("/bookings/{id}")
    public ResponseEntity<?> getBooking(@PathVariable Long id ){
        Booking booking = (Booking) Util.checkOptional(bookingRepository.findById(id));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername());

        if ( user.getId() != booking.getTenant().getId() ) {
            return ResponseEntity
                    .badRequest()
                    .body("{\"timestamp\": " + "\"" + new Date().toString() + "\","
                            + "\"status\": 403, "
                            + "\"error\": \"Forbidden\", "
                            + "\"message\": \"Not owning the booking!\", "
                            + "\"path\": \"/bookings/" + id.toString() +"\"}"
                    );
        }


        return ResponseEntity.ok(booking);
    }



    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('TENANT')")
    @GetMapping("/departments/{depId}/bookings")
    public ResponseEntity<?> BookingExists(@RequestParam String startDate,@RequestParam String endDate, @PathVariable Long depId) throws ParseException {

        Date stDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
        Date enDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);


        if (!bookingRepository.BookingExists(depId, stDate, enDate)) {
            return ResponseEntity
                    .badRequest()
                    .body("{\"timestamp\": " + "\"" + new Date().toString() + "\","
                            + "\"status\": 400, "
                            + "\"error\": \"Bad Request\", "
                            + "\"message\": \"Booking not exist!\", "
                            + "\"path\": \"/departments/" + depId.toString() + "/bookings\"}"
                    );
        }

        return ResponseEntity.ok("\"Booking exists!\"");
    }

    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/bookings/export" , produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<Booking> getBookingsForExport() {

        List<Booking> bookings = bookingRepository.findAll();

        return bookings;
    }



}
