package fr.insarennes.model;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity(name="CM")
@XmlRootElement
public class CM extends Cours {
	public CM() {
		super();
	}

	public CM(final Matiere m, final LocalDateTime h, final Enseignant e, final Duration d) {
		super(m, h, e, d);
	}
}
