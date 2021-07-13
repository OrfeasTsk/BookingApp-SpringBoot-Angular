package tedi.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(max = 50)
    private String firstName;
    @NotBlank
    @Size(max = 50)
    private String lastName;
    @NotBlank
    @Email
    @Size(max = 50)
    private String email;
    @Size(max = 15)
    private String phone;
    @NotBlank
    @Size(max = 50)
    private String username;
    @NotBlank
    private String password;
    @Transient
    private String passwordConfirm;

    private Boolean accepted;

    @ManyToMany(cascade = CascadeType.PERSIST , fetch = FetchType.EAGER)
    @JsonIgnoreProperties("users")
    private Set<Role> roles = new HashSet<Role>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy="host")
    @JsonIgnoreProperties(value = "host",allowSetters = true)
    @Fetch(value= FetchMode.SELECT)
    private Set<Department> departments = new HashSet<Department>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy="tenant")
    @JsonIgnoreProperties("tenant")
    private Set<Booking> bookings = new HashSet<Booking>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy="fromUser")
    @JsonIgnoreProperties("fromUser")
    private Set<Review> reviewsForUsers = new HashSet<Review>();


    @OneToMany(fetch = FetchType.EAGER, mappedBy="forUser")
    @JsonIgnoreProperties("forUser")
    private Set<Review> reviewsFromUsers = new HashSet<Review>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy="fromUser")
    @JsonIgnoreProperties("fromUser")
    private Set<Message> messagesForUsers = new HashSet<Message>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy="forUser")
    @JsonIgnoreProperties("forUser")
    private Set<Message> messagesFromUsers = new HashSet<Message>();

    @OneToOne(cascade = CascadeType.ALL , fetch = FetchType.EAGER)
    @JsonIgnoreProperties("user")
    private Photo profilePhoto;

    @OneToMany(fetch = FetchType.EAGER, mappedBy="user")
    @Fetch(value = FetchMode.SELECT)
    @JsonIgnoreProperties("user")
    private Set<Interaction> interactions = new HashSet<Interaction>();

    @ManyToMany(cascade = CascadeType.PERSIST,fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = {"recommendedTo","interactions"} , allowSetters = true)
    @Fetch(value= FetchMode.SELECT)
    private Set<Department> recommendedDepartments = new HashSet<Department>();




    public User() {}

    public User(String fname, String lname, String username,String email,String phone,String password,Set<Role> roles) {
        this.firstName = fname;
        this.lastName = lname;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.roles=roles;
    }

}
