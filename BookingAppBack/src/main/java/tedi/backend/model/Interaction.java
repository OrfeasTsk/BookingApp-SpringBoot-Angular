package tedi.backend.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
public class Interaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer counter;

    @ManyToOne
    @JsonIgnoreProperties(value = {"interactions","reviewsForUsers","reviewsFromUsers","bookings","messagesForUsers","messagesFromUsers","profilePhoto","departments","recommendedDepartments"},allowSetters = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;

    @ManyToOne
    @JsonIgnoreProperties(value = {"interactions","reviews","bookings","photos","recommendedTo","host"},allowSetters = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Department department;

    public Interaction(){}

    public Interaction(Department department , User user){
        this.user = user;
        this.department = department;
        this.counter = 1;
    }

}
