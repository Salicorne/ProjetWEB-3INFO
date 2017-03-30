package fr.insarennes.model;

import javax.persistence.Entity;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity(name="TD")
public class TD extends Cours {
	public TD() {
		super();
	}

	public TD(final Matiere m, final LocalDateTime h, final Enseignant e, final Duration d) {
		super(m, h, e, d);
	}
}
