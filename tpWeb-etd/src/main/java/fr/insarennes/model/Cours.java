package fr.insarennes.model;

import fr.insarennes.utils.DurationXmlAdapter;
import fr.insarennes.utils.LocalDateTimeXmlAdapter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity(name="COURS")
@NamedQueries({
    @NamedQuery(name="SelectCours", query="SELECT c FROM COURS c"),
    @NamedQuery(name="SelectCoursById", query="SELECT c FROM COURS c WHERE c.id=:id"),
})
@XmlSeeAlso({CM.class,TD.class})
@XmlRootElement
public abstract class Cours extends CalendarElement {
	protected Matiere matiere;
	protected LocalDateTime horaire;
	protected Enseignant ens;
	protected Duration duration;
	@ManyToOne
	protected Agenda agenda;

	public Cours() {
		super();
	}

	public Cours(final Matiere m, final LocalDateTime h, final Enseignant e, final Duration d) {
		super();
		matiere = Objects.requireNonNull(m);
		horaire = Objects.requireNonNull(h);
		ens = Objects.requireNonNull(e);
		duration = Objects.requireNonNull(d);
	}

	public boolean matchesID(final int i) {
		return i == getId() || i == matiere.getId() || i == ens.getId();
	}

	@XmlTransient // The agenda of a cours must not be considered during a REST serialisation.
	public Agenda getAgenda() {
		return agenda;
	}

	public void setAgenda(final Agenda agenda) {
		this.agenda = agenda;
	}

	public Matiere getMatiere() {
		return matiere;
	}

	public void setMatiere(final Matiere m) {
		matiere = m;
	}

	public LocalDateTime getHoraire() {
		return horaire;
	}

    @XmlJavaTypeAdapter(LocalDateTimeXmlAdapter.class)
	public void setHoraire(final LocalDateTime h) {
		horaire = h;
	}

	public Enseignant getEns() {
		return ens;
	}

	public void setEns(final Enseignant e) {
		ens = e;
	}

	public Duration getDuration() {
		return duration;
	}

    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
	public void setDuration(final Duration d) {
		duration = d;
	}

	@Override
	public boolean equals(final Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		Cours c = (Cours) o;

		return getDuration().equals(c.getDuration()) && getMatiere().equals(c.getMatiere()) &&
			getHoraire().equals(c.getHoraire()) && getEns().equals(c.getEns());

	}

	@Override
	public int hashCode() {
		int result = getHoraire().hashCode();
		result = 31 * result + getDuration().hashCode();
		if(getMatiere() != null) result = 31 * result + getMatiere().hashCode();
		;
		if(getEns() != null) result = 31 * result + getEns().hashCode();
		return result;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" + "id=" + getId() + ", duration=" + duration + ", matiere=" + matiere +
			", horaire=" + horaire + ", ens=" + ens + '}';
	}
}
