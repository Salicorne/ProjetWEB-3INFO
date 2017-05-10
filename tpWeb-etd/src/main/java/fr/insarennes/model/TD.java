package fr.insarennes.model;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity(name="TD")
@XmlRootElement
public class TD extends Cours {
	public TD() {
		super();
	}

	public TD(final Matiere m, final LocalDateTime h, final Enseignant e, final Duration d) {
		super(m, h, e, d);
	}
}
