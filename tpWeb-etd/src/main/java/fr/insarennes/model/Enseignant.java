package fr.insarennes.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@Entity(name="ENSEIGNANT")
@NamedQueries({
		@NamedQuery(name="SelectEnseignants", query="SELECT e FROM ENSEIGNANT e"),
		@NamedQuery(name="SelectEnseignant", query="SELECT e FROM ENSEIGNANT e WHERE e.id=:id"),
		@NamedQuery(name="SelectEnseignantsByName", query="SELECT e FROM ENSEIGNANT e WHERE e.name=:name"),
		//@NamedQuery(name="DeleteEnseignantsByName", query="REMOVE e FROM ENSEIGNANT WHERE e.name=:name"),
})
@XmlRootElement
public class Enseignant extends CalendarElement {
    @Column(nullable=false)
	private String name;

	public Enseignant() {
		super();
		name = "enseignant";
	}

	public Enseignant(final String n) {
		super();
		name = Objects.requireNonNull(n);
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public boolean equals(final Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		Enseignant ens = (Enseignant) o;

		return name.equals(ens.name);

	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return "Enseignant{id=" + getId() + ", name='" + name + '\'' + '}';
	}
}
