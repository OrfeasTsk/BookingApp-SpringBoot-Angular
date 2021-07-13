package tedi.backend.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@Data
@XmlRootElement
@Entity
public class Booking {
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @NotNull
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @NotNull
    private Integer numberOfAdults;
    @NotNull
    private Integer numberOfChildren;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"bookings","reviewsForUsers","reviewsFromUsers","messagesForUsers","messagesFromUsers","profilePhoto","departments","interactions","recommendedDepartments"},allowSetters = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User tenant;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"bookings","reviews","photos","interactions","recommendedTo"},allowSetters = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Department department;


}



