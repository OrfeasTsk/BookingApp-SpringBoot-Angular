package tedi.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@Data
@XmlRootElement
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition="TEXT")
    private String text;

    @Range(min = 0 , max = 5)
    private Integer stars;


    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"reviewsForUsers","reviewsFromUsers","bookings","messagesForUsers","messagesFromUsers","profilePhoto","departments","interactions","recommendedDepartments"},allowSetters = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User fromUser;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"reviewsFromUsers","reviewsForUsers","bookings","messagesForUsers","messagesFromUsers","profilePhoto","departments","interactions","recommendedDepartments"},allowSetters = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User forUser;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"reviews","bookings","photos","interactions","recommendedTo"},allowSetters = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Department forDepartment;

}
