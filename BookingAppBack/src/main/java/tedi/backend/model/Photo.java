package tedi.backend.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Entity
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 25)
    private String name;

    private String type;

    @Column(length = 200000)
    private byte[] photoBytes;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonIgnoreProperties("photos")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Department department;


    @OneToOne(mappedBy = "profilePhoto")
    @JsonIgnoreProperties("profilePhoto")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;


    public Photo(Long id, String name, String type, byte[] photoBytes){
        this.id = id;
        this.name = name;
        this.type = type;
        this.photoBytes = photoBytes;
    }

    public Photo(String name, String type, byte[] photoBytes){
        this.name = name;
        this.type = type;
        this.photoBytes = photoBytes;
    }


    public Photo(){}


}
