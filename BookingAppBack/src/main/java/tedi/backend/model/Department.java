package tedi.backend.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.hibernate.annotations.FetchMode.SELECT;


@Data
@XmlRootElement
@Entity
public class Department {

    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 50)
    private String name;

    @NotNull
    private Float costPerDay;
    @NotNull
    private Float costPerPerson;
    @NotNull
    private Integer minBookingDays;
    @NotNull
    private Integer maxPeople;
    @NotNull
    @Enumerated(EnumType.STRING)
    private DType type;
    @NotNull
    private String city;
    @NotNull
    private String address;
    @NotNull
    private Float longitude;
    @NotNull
    private Float latitude;
    @NotNull
    private String country;
    @NotNull
    private Integer numberOfBedrooms;
    @NotNull
    private Integer numberOfBeds;
    @NotNull
    private Integer numberOfBaths;
    private Integer numberOfReviews;
    private Float area;
    @Column(columnDefinition="TEXT")
    private String description;
    @Column(columnDefinition="TEXT")
    private String transport;
    private Boolean hasInternet;
    private Boolean hasAirCondition;
    private Boolean hasHeat;
    private Boolean hasKitchen;
    private Boolean hasTv;
    private Boolean hasParking;
    private Boolean hasElevator;
    private Boolean hasLivingRoom;
    private Boolean smokingAllowed;
    private Boolean petsAllowed;
    private Boolean eventsAllowed;
    @Temporal(TemporalType.DATE)
    private Date  startDate;
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"departments","messagesFromUsers","messagesForUsers","reviewsForUsers","bookings","interactions","recommendedDepartments"},allowSetters = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User host;

    @OneToMany(fetch = FetchType.EAGER, mappedBy="department")
    @JsonIgnoreProperties("department")
    private Set<Booking> bookings = new HashSet<Booking>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy="forDepartment")
    @JsonIgnoreProperties("forDepartment")
    private Set<Review> reviews = new HashSet<Review>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "department")
    @JsonIgnoreProperties("department")
    private Set<Photo> photos;


    @OneToMany(cascade = CascadeType.ALL ,fetch = FetchType.EAGER, mappedBy = "aboutDepartment")
    @JsonIgnoreProperties({"aboutDepartment"})
    private Set<Message> messages;

    @OneToMany(fetch = FetchType.EAGER, mappedBy="department")
    @Fetch(value = FetchMode.SELECT)
    @JsonIgnoreProperties("department")
    private Set<Interaction> interactions = new HashSet<Interaction>();

    @ManyToMany(mappedBy="recommendedDepartments",fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = {"recommendedDepartments","departments","interactions"},allowSetters = true)
    @Fetch(value= FetchMode.SELECT)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<User> recommendedTo = new HashSet<User>();


    public Department(){}

   public Department(Long id,Float costPerDay,Integer minBookingDays,DType type,Integer numberOfBedrooms,Integer numberOfBeds,Integer numberOfBaths,Integer numberOfReviews,Float area,String description,
                    Float averageRating,Boolean hasInternet,Boolean hasAirCondition,Boolean hasHeat,Boolean hasKitchen,Boolean hasTv, Boolean hasParking, Boolean hasElevator, Boolean hasLivingRoom,
                      Boolean smokingAllowed, Boolean petsAllowed, Boolean eventsAllowed){

        this.id = id;
        this.costPerDay = costPerDay;
        this.minBookingDays = minBookingDays;
        this.type = type;
        this.numberOfBedrooms = numberOfBedrooms;
        this.numberOfBeds = numberOfBeds;
        this.numberOfBaths = numberOfBaths;
        this.numberOfReviews = numberOfReviews;
        this.area = area;
        this.description = description ;
        this.hasInternet = hasInternet;
        this.hasAirCondition = hasAirCondition;
        this.hasHeat = hasHeat;
        this.hasKitchen = hasKitchen;
        this.hasTv = hasTv;
        this.hasParking = hasParking;
        this.hasElevator = hasElevator;
        this.hasLivingRoom = hasLivingRoom;
        this.smokingAllowed = smokingAllowed;
        this.petsAllowed = petsAllowed;
        this.eventsAllowed = eventsAllowed;


    }




}
