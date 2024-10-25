package tn.esprit.spring.entities;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder // Add this annotation to enable the builder pattern

@FieldDefaults(level=AccessLevel.PRIVATE)
@Entity
public class Piste implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long numPiste;
	String namePiste;
	@Enumerated(EnumType.STRING)
	Color color;
	int length;
	int slope;

	@ManyToMany(mappedBy= "pistes")
	Set<Skier> skiers;
	
}
