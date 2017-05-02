package fr.insarennes;

import fr.insarennes.model.*;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;


public class TestJPACalendar {
	private EntityManagerFactory emf;
	private EntityManager em;
	private EntityTransaction tr;

	private Agenda agenda;
	private Enseignant ens;
	private ArrayList<Cours> cours;
	private ArrayList<Matiere> matieres;


	@Before
	public void setUp() throws Exception {
		emf = Persistence.createEntityManagerFactory("agendapp");
		em = emf.createEntityManager();
		tr = em.getTransaction();

		createTable();
	}

	private void createTable() throws SQLException {
		agenda = new Agenda();
		agenda.setName("agenda1");

		matieres = new ArrayList<Matiere>();
		matieres.add(new Matiere("Web", 3));
		ens = new Enseignant("Blouin");
		cours = new ArrayList<Cours>();

		cours.add(new TD(matieres.get(0), LocalDateTime.of(2017, 04, 1, 8, 00), ens, Duration.ofHours(2)));
		cours.add(new CM(matieres.get(0), LocalDateTime.of(2017, 04, 2, 10, 00), ens, Duration.ofHours(2)));

		em.getTransaction().begin();
		em.persist(agenda);
		em.persist(ens);
		em.persist(cours.get(0));
		em.persist(cours.get(1));
		em.persist(matieres.get(0));
//        Use em.persist to put an object into the database
//        em.persist(myObjectToPutIntoTheDatabase);

		em.getTransaction().commit();
		printTables();
	}


	public void printTables() {
		System.out.println(em.getMetamodel().getEntities().stream().map(e -> {
			Table t = e.getJavaType().getAnnotation(Table.class);
			return (t == null ? e.getName() : t.name()) + (e.getSupertype() == null ? "" : " -> " +
				e.getSupertype().getJavaType().getSimpleName()) + e.getAttributes().stream().map(a -> a.getName() + ":" +
				a.getJavaType().getSimpleName()).collect(Collectors.joining(", ", "[", "]"));
		}).collect(Collectors.joining("\n", "****************\nTables:\n", "\n****************")));
	}

	@After
	public void tearDown() throws Exception {
		em.clear();
		em.close();
		emf.close();
	}

	@Test
	public void testSelectAgenda() {
		tr.begin();
		List<Agenda> cc = em.createQuery("SELECT a FROM AGENDA a", Agenda.class).getResultList();
		tr.commit();

		assertEquals(1, cc.size());
		Agenda a = cc.get(0);
		assertNotNull(a);
		assertEquals("agenda1", a.getName());
		assertEquals(agenda.getId(), a.getId());
	}

	@Test
	public void testSelectEnseignant() {
		tr.begin();
		List<Enseignant> cc = em.createQuery("SELECT e FROM ENSEIGNANT e", Enseignant.class).getResultList();
		tr.commit();

		assertEquals(1, cc.size());
		Enseignant e = cc.get(0);
		assertNotNull(e);
		assertEquals("Blouin", e.getName());
		assertEquals(ens.getId(), e.getId());
		assertEquals(ens.hashCode(), e.hashCode());
		assertTrue(ens.equals(e));
	}

    @Test
    public void testSelectCours() {
        tr.begin();
        List<Cours> cc = em.createQuery("SELECT c FROM COURS c", Cours.class).getResultList();
        tr.commit();

        assertEquals(2, cc.size());
        Cours c = cc.get(0);
        assertNotNull(c);
        assertEquals("Web", c.getMatiere().getName());
        assertEquals(c.getId(), cours.get(1).getId());
        c = cc.get(1);
        assertNotNull(c);
        assertEquals("Web", c.getMatiere().getName());
        assertEquals(c.getId(), cours.get(0).getId());

        assertThat(cc, containsInAnyOrder(cours.get(0), cours.get(1)));
    }

    @Test
    public void testNamedQueries() {
        //tr.begin();
        TypedQuery<Enseignant> q1 = em.createNamedQuery("SelectEnseignant", Enseignant.class);
        TypedQuery<Enseignant> q2 = em.createNamedQuery("SelectEnseignants", Enseignant.class);
        //tr.commit();

        //////////////// TEST Q1 /////////////////

        List<Enseignant> cc = new ArrayList<Enseignant>();
        cc.add(q1.setParameter("id", ens.getId()).getSingleResult());

        assertEquals(1, cc.size());
        Enseignant e = cc.get(0);
        assertNotNull(e);
        assertEquals("Blouin", e.getName());
        assertEquals(ens.getId(), e.getId());
        assertEquals(ens.hashCode(), e.hashCode());
        assertTrue(ens.equals(e));

        ////////////// TEST Q2 //////////////////
        cc = q2.getResultList();

        assertEquals(1, cc.size());
        e = cc.get(0);
        assertNotNull(e);
        assertEquals("Blouin", e.getName());
        assertEquals(ens.getId(), e.getId());
        assertEquals(ens.hashCode(), e.hashCode());
        assertTrue(ens.equals(e));
    }


}
