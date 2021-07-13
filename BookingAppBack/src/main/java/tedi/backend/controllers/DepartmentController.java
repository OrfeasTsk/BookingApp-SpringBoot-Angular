package tedi.backend.controllers;



import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tedi.backend.model.*;
import tedi.backend.repositories.DepartmentRepository;
import tedi.backend.repositories.InteractionRepository;
import tedi.backend.repositories.PhotoRepository;
import tedi.backend.repositories.UserRepository;

import java.io.IOException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;



import static tedi.backend.controllers.Util.compressBytes;
import static tedi.backend.controllers.Util.decompressBytes;


@RestController
public class DepartmentController {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final InteractionRepository interactionRepository;

    DepartmentController(DepartmentRepository departmentRepository, UserRepository userRepository, PhotoRepository photoRepository, InteractionRepository interactionRepository) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.photoRepository = photoRepository;
        this.interactionRepository = interactionRepository;
    }

    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('HOST')")
    @PostMapping(value = "/departments/departmentregister",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> newDepartment(@RequestPart("object") Department department,@RequestPart("imageFile") MultipartFile[] files) {
        if (department.getName() != null)
            if (departmentRepository.departmentNameExists(department.getName())) {
                return ResponseEntity
                        .badRequest()
                        .body("{\"timestamp\": " + "\"" + new Date().toString()+ "\","
                                + "\"status\": 400, "
                                + "\"error\": \"Bad Request\", "
                                + "\"message\": \"Department name already exists!\", "
                                + "\"path\": \"/departments/departmentregister\"}"
                        );
            }


        Set<Photo> photos = new HashSet<Photo>();
        Arrays.asList(files).forEach(file -> {
            try {
                Photo photo = new Photo(file.getOriginalFilename() ,file.getContentType() ,compressBytes(file.getBytes()));
                photo.setDepartment(department);
                photos.add(photo);

            } catch (IOException e) {
                e.getMessage();
            }

        });
        department.setPhotos(photos);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername());
        department.setHost(user);
        department.setNumberOfReviews(0);
        //departmentRepository.save(department);
        for(Photo photo: photos){
            photoRepository.save(photo);
        }

        return ResponseEntity.ok("\"Successful registration!\"");
    }


    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('HOST')")
    @PutMapping("/departments/{id}")
    public ResponseEntity<?> editDepartment(@PathVariable Long id,@RequestBody Department department) {
        Department existingDepartment =(Department) Util.checkOptional(departmentRepository.findById(id));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername());
        if ( user.getId() != existingDepartment.getHost().getId() ) {
            return ResponseEntity
                    .badRequest()
                    .body("{\"timestamp\": " + "\"" + new Date().toString() + "\","
                            + "\"status\": 403, "
                            + "\"error\": \"Forbidden\", "
                            + "\"message\": \"Not owning the department!\", "
                            + "\"path\": \"/departments/" + id.toString() +"\"}"
                    );
        }


        if (department.getName() != null && !department.getName().equals("") ) {
            if (!department.getName().equals(existingDepartment.getName()) && departmentRepository.departmentNameExists(department.getName())) {
                return ResponseEntity
                        .badRequest()
                        .body("{\"timestamp\": " + "\"" + new Date().toString() + "\","
                                + "\"status\": 400, "
                                + "\"error\": \"Bad Request\", "
                                + "\"message\": \"Department name already exists! Edit failed.\", "
                                + "\"path\": \"/departments/" + id.toString() +"\"}"
                        );
            }
            existingDepartment.setName(department.getName());
        }
        if (department.getAddress() != null && !department.getAddress().equals(""))
            existingDepartment.setAddress(department.getAddress());
        if (department.getArea() != null)
            existingDepartment.setArea(department.getArea());
        if (department.getCostPerDay() != null)
            existingDepartment.setCostPerDay(department.getCostPerDay());
        if (department.getCostPerPerson() != null)
            existingDepartment.setCostPerPerson(department.getCostPerPerson());
        if (department.getMinBookingDays() != null)
            existingDepartment.setMinBookingDays(department.getMinBookingDays());
        if (department.getMaxPeople() != null)
            existingDepartment.setMaxPeople(department.getMaxPeople());
        if (department.getType() != null)
            existingDepartment.setType(department.getType());
        if (department.getCity() != null && !department.getCity().equals(""))
            existingDepartment.setCity(department.getCity());
        if (department.getCountry() != null && !department.getCountry().equals(""))
            existingDepartment.setCountry(department.getCountry());
        if (department.getNumberOfBeds() != null)
            existingDepartment.setNumberOfBeds(department.getNumberOfBeds());
        if (department.getNumberOfBedrooms() != null)
            existingDepartment.setNumberOfBedrooms(department.getNumberOfBedrooms());
        if (department.getNumberOfBaths() != null)
            existingDepartment.setNumberOfBaths(department.getNumberOfBaths());
        if (department.getDescription() != null && !department.getDescription().equals(""))
            existingDepartment.setDescription(department.getDescription());
        if (department.getTransport() != null && !department.getTransport().equals(""))
            existingDepartment.setTransport(department.getTransport());
        if (department.getHasInternet() != null)
            existingDepartment.setHasInternet(department.getHasInternet());
        if (department.getHasHeat() != null)
            existingDepartment.setHasHeat(department.getHasHeat());
        if (department.getHasAirCondition() != null)
            existingDepartment.setHasAirCondition(department.getHasAirCondition());
        if (department.getHasKitchen() != null)
            existingDepartment.setHasKitchen(department.getHasKitchen());
        if (department.getHasTv() != null)
            existingDepartment.setHasTv(department.getHasTv());
        if (department.getHasParking() != null)
            existingDepartment.setHasParking(department.getHasParking());
        if (department.getHasElevator() != null)
            existingDepartment.setHasElevator(department.getHasElevator());
        if (department.getHasLivingRoom() != null)
            existingDepartment.setHasLivingRoom(department.getHasLivingRoom());
        if (department.getSmokingAllowed() != null)
            existingDepartment.setSmokingAllowed(department.getSmokingAllowed());
        if (department.getPetsAllowed() != null)
            existingDepartment.setPetsAllowed(department.getPetsAllowed());
        if (department.getEventsAllowed() != null)
            existingDepartment.setEventsAllowed(department.getEventsAllowed());
        if (department.getLongitude() != null)
            existingDepartment.setLongitude(department.getLongitude());
        if (department.getLatitude() != null)
            existingDepartment.setLatitude(department.getLatitude());
        if (department.getStartDate() != null)
            existingDepartment.setStartDate(department.getStartDate());
        if (department.getEndDate() != null)
            existingDepartment.setEndDate(department.getEndDate());



        departmentRepository.save(existingDepartment);

        return ResponseEntity.ok("\"Successful edit!\"");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/departments/{id}")
    public Department getDepartment(@PathVariable Long id){

        Department department = (Department) Util.checkOptional(departmentRepository.findById(id));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        if(!auth.getPrincipal().equals("anonymousUser"))
            user = userRepository.findByUsername(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername());


        if(user != null && department.getHost().getId() != user.getId())
            for(Role role : user.getRoles())
                if(role.getName().equals(RoleNames.ROLE_TENANT)) {
                    Interaction interaction = interactionRepository.getInteraction(department.getId(), user.getId());
                    if (interaction == null)
                        interactionRepository.save(new Interaction(department, user));
                    else {
                        interaction.setCounter(interaction.getCounter() + 1);
                        interactionRepository.save(interaction);
                    }
                }

        Set<Photo> newPhotos = new HashSet<Photo>();
        for(Photo photo: department.getPhotos()){
            newPhotos.add(new Photo(photo.getId(),photo.getName(),photo.getType(),decompressBytes(photo.getPhotoBytes())));
        }
        department.setPhotos(newPhotos);

        User host = department.getHost();
        Photo hostphoto = host.getProfilePhoto();
        if(hostphoto != null){
            Photo newHostPhoto = new Photo(hostphoto.getId(),hostphoto.getName(),hostphoto.getType(),decompressBytes(hostphoto.getPhotoBytes()));
            host.setProfilePhoto(newHostPhoto);
        }
        department.setHost(host);

        return department;
    }



    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('HOST')")
    @DeleteMapping("/departments/{depId}/photos/{photoId}")
    public ResponseEntity deletePhoto(@PathVariable Long depId, @PathVariable Long photoId) {
        Photo photo = (Photo) Util.checkOptional(photoRepository.findById(photoId));
        Department department = (Department) Util.checkOptional(departmentRepository.findById(depId));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername());
        if ( user.getId() != department.getHost().getId() ) {
            return ResponseEntity
                    .badRequest()
                    .body("{\"timestamp\": " + "\"" + new Date().toString() + "\","
                            + "\"status\": 403, "
                            + "\"error\": \"Forbidden\", "
                            + "\"message\": \"Not owning the department!\", "
                            + "\"path\": \"/departments/" + depId.toString() + "/photos/" + photoId.toString() +"\"}"
                    );
        }


        Set<Photo> photoSet = new HashSet<Photo>();
        for (Photo phototmp : department.getPhotos()) {
            if (phototmp.getId() != photoId)
                photoSet.add(phototmp);
        }
        department.setPhotos(photoSet);
        departmentRepository.save(department);
        photo.setDepartment(null);
        photoRepository.deleteById(photoId);

        return ResponseEntity.ok("\"Successfully deleted\"");

    }

    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('HOST')")
    @PutMapping(value = "/departments/{id}/photos" , consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity addPhotos(@PathVariable Long id, @RequestPart("imageFile") MultipartFile[] files) {
        Department department = (Department) Util.checkOptional(departmentRepository.findById(id));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername());
        if ( user.getId() != department.getHost().getId() ) {
            return ResponseEntity
                    .badRequest()
                    .body("{\"timestamp\": " + "\"" + new Date().toString() + "\","
                            + "\"status\": 403, "
                            + "\"error\": \"Forbidden\", "
                            + "\"message\": \"Not owning the department!\", "
                            + "\"path\": \"/departments/" + id.toString() + "/photos\"}"
                    );
        }

        Set<Photo> photos = new HashSet<Photo>();
        Arrays.asList(files).forEach(file -> {
            try {
                Photo photo = new Photo(file.getOriginalFilename() ,file.getContentType() ,compressBytes(file.getBytes()));
                photo.setDepartment(department);
                photos.add(photo);


            } catch (IOException e) {
                e.getMessage();
            }

        });
        Set<Photo> allphotos = department.getPhotos();
        allphotos.addAll(photos);
        department.setPhotos(allphotos);
        //departmentRepository.save(department);
        for(Photo photo: allphotos){
            photoRepository.save(photo);
        }

        return ResponseEntity.ok("\"Successfully added\"");
    }


    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('HOST')")
    @PutMapping(value = "/departments/{id}/photos/mainphoto" , consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity changeMainPhoto(@PathVariable Long id, @RequestPart("imageFile") MultipartFile file) throws IOException {
        Boolean flag = false;

        if(!file.getOriginalFilename().equals("mainPhoto"))
            return ResponseEntity
                    .badRequest()
                    .body("{\"timestamp\": " + "\"" + new Date().toString() + "\","
                            + "\"status\": 400, "
                            + "\"error\": \"Bad Request\", "
                            + "\"message\": \"Invalid type!\", "
                            + "\"path\": \"/departments/" + id.toString() + "/photos/mainphoto\"}"
                    );

        Department department = (Department) Util.checkOptional(departmentRepository.findById(id));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername());
        if ( user.getId() != department.getHost().getId() ) {
            return ResponseEntity
                    .badRequest()
                    .body("{\"timestamp\": " + "\"" + new Date().toString() + "\","
                            + "\"status\": 403, "
                            + "\"error\": \"Forbidden\", "
                            + "\"message\": \"Not owning the department!\", "
                            + "\"path\": \"/departments/" + id.toString() + "/photos/mainphoto\"}"
                    );
        }

        for(Photo photo: department.getPhotos()){
            if(photo.getName().equals("mainPhoto")){
                photo.setType(file.getContentType());
                photo.setPhotoBytes(compressBytes(file.getBytes()));
                flag = true;
                photoRepository.save(photo);
            }
        }

       if(flag == false){
            Set<Photo> allphotos = department.getPhotos();
            Photo photo = new Photo(file.getOriginalFilename() ,file.getContentType() ,compressBytes(file.getBytes()));
            photo.setDepartment(department);
            allphotos.add(photo);
            department.setPhotos(allphotos);
            for(Photo tmpphoto: allphotos){
               photoRepository.save(tmpphoto);
            }
        }

       return ResponseEntity.ok("\"Photo changed!\"");

    }




    @CrossOrigin(origins = "*")
    @GetMapping("/departments/search")
    public Set<Department> search(@RequestParam(required = false) DType roomType,@RequestParam(required = false) Float minCost ,@RequestParam(required = false) Float maxCost,@RequestParam(required = false) Boolean smokingAllowed,@RequestParam(required = false) Boolean petsAllowed,@RequestParam(required = false) Boolean eventsAllowed, @RequestParam(required = false) Boolean hasInternet,@RequestParam(required = false) Boolean hasLivingRoom, @RequestParam(required = false) Boolean hasAirCondition, @RequestParam(required = false) Boolean hasHeat, @RequestParam(required = false) Boolean hasKitchen,
                                            @RequestParam(required = false) Boolean hasTv, @RequestParam(required = false) Boolean hasParking, @RequestParam(required = false) Boolean hasElevator,@RequestParam String startDate,@RequestParam String endDate,@RequestParam String location,@RequestParam Integer numberOfPeople, @RequestParam(required = false) Long hostId) throws ParseException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        if(!auth.getPrincipal().equals("anonymousUser"))
            user = userRepository.findByUsername(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername());

        Set<Department> departmentSet = new HashSet<Department>();
        Date stDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
        Date enDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);

        List<Department> departments = departmentRepository.find(roomType,minCost ,maxCost, smokingAllowed,petsAllowed,eventsAllowed ,hasInternet, hasLivingRoom,hasAirCondition,hasHeat,hasKitchen,
                hasTv,hasParking,hasElevator,stDate, enDate,location, numberOfPeople, hostId);

        for(Department department: departments){

            if(user != null)
                for(Role role : user.getRoles())
                    if(role.getName().equals(RoleNames.ROLE_TENANT)) {
                        Interaction interaction = interactionRepository.getInteraction(department.getId(), user.getId());
                        if (interaction == null)
                            interactionRepository.save(new Interaction(department, user));
                        else {
                            interaction.setCounter(interaction.getCounter() + 1);
                            interactionRepository.save(interaction);
                        }
                    }

            Set<Photo> newPhotos = new HashSet<Photo>();
            for(Photo photo: department.getPhotos()){
                newPhotos.add(new Photo(photo.getId(),photo.getName(),photo.getType(),decompressBytes(photo.getPhotoBytes())));
            }
            department.setPhotos(newPhotos);
            departmentSet.add(department);

        }

        return departmentSet;
    }



    @CrossOrigin(origins = "*")
    @GetMapping("/departments/finder")
    public List<String> locationFinder(@RequestParam String location){

        return departmentRepository.locationFinder(location);
    }


    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('HOST')")
    @GetMapping("/host/departments")
    public Set<Department> getDepartments() {

        Set<Department> departmentSet = new HashSet<Department>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User host = userRepository.findByUsername(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername());
        for(Department department: host.getDepartments()){
                Set<Photo> newPhotos = new HashSet<Photo>();
                for(Photo photo: department.getPhotos()){
                    newPhotos.add(new Photo(photo.getId(),photo.getName(),photo.getType(),decompressBytes(photo.getPhotoBytes())));
                }
                department.setPhotos(newPhotos);
            departmentSet.add(department);
        }

        return departmentSet;
    }

    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/departments/export" , produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<Department> getDepartmentsForExport() {

        List<Department> departments = departmentRepository.findAll();
        for(Department department: departments)
            department.setPhotos(null);

        return departments;
    }


    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('TENANT')")
    @GetMapping("/departments/recommended")
    public Set<Department> getRecommended(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername());
        return  user.getRecommendedDepartments();
    }


}
