package tedi.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition="TEXT")
    private String text;

    private Boolean isQuestion;


    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"messagesForUsers","messagesFromUsers","reviewsForUsers","reviewsFromUsers","bookings","departments","profilePhoto","interactions","recommendedDepartments"},allowSetters = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User fromUser;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"messagesFromUsers","messagesForUsers","reviewsForUsers","reviewsFromUsers","bookings","departments","profilePhoto","interactions","recommendedDepartments"},allowSetters = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User forUser;



    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"messages","bookings","host","photos","interactions","recommendedTo"},allowSetters = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Department aboutDepartment;

    @OneToOne(cascade = CascadeType.REMOVE,mappedBy = "reply")
    @JsonIgnoreProperties("reply")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Message question;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("question")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Message reply;



}

