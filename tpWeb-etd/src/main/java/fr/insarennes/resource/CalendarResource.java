package fr.insarennes.resource;

import fr.insarennes.model.Agenda;
import fr.insarennes.model.Enseignant;
import java.net.HttpURLConnection;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Singleton // Q: with and without, see 3.4 https://jersey.java.net/documentation/latest/jaxrs-resources.html
@Path("calendar")
public class CalendarResource {
	private final EntityManagerFactory emf;
	private final EntityManager em;
	private final Agenda agenda;

	public CalendarResource() {
		super();
		agenda = new Agenda();
		emf = Persistence.createEntityManagerFactory("agendapp");
		em = emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(agenda);
		em.getTransaction().commit();

		// You can add here calendar elements to add by default in the database of the application.
		// For instance:
		//		try {
		//			// Each time you add an object into the database or modify an object already added into the database,
		//			// You must surround your code with a em.getTransaction().begin() that identifies the beginning of a transaction
		//			// and a em.getTransaction().commit() at the end of the transaction to validate it.
		//			// In case of crashes, you have to surround the code with a try/catch block, where the catch rollbacks the
		//			// transaction using em.getTransaction().rollback()
		//			em.getTransaction().begin();
		//			em.persist(agenda);
		//
		//			Enseignant ens = new Enseignant("Blouin");
		//			Matiere mat = new Matiere("Web", 3);
		//
		//			em.persist(ens);
		//			em.persist(mat);
		//
		//			TD td = new TD(mat, LocalDate.of(2015, Month.JANUARY, 2).atTime(8, 0), ens, Duration.ofHours(2));
		//			agenda.addCours(td);
		//			em.persist(td);
		//			em.getTransaction().commit();
		//
		//			System.out.println("LOG for calendar resource: ");
		//			System.out.println(ens);
		//			System.out.println(mat);
		//			System.out.println(td);
		//		}catch(final Throwable ex) {
		//			ex.printStackTrace();
		//			em.getTransaction().rollback();
		//		}
	}


	public void flush() {
		em.clear();
		em.close();
		emf.close();
	}

	@Override
	protected void finalize() throws Throwable {
		flush();
		super.finalize();
	}

	//curl -H "Content-Type: application/json" -d '{"name":"blouin"}' -X POST "http://localhost:8080/api/calendar/ens"
	// To know the XML format, look at the returned XML message.
	@POST
	@Path("ens/")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces(MediaType.APPLICATION_JSON)
	public Enseignant postEnseignant(Enseignant ens) {
		try {
			// begin starts a transaction:
			// https://en.wikibooks.org/wiki/Java_Persistence/Transactions
			em.getTransaction().begin();
			em.persist(ens);
			em.getTransaction().commit();
			return ens;
		}catch(Throwable ex) {
			// If an exception occurs, the transaction has to be rollbacked.
			em.getTransaction().rollback();
			// A Web exception is then thrown.
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).build());
		}
	}


	// DO NOT USE begin(), commit() or rollback() for the @GET verb.

	// When modifying an object (@PUT verb) DO NOT USE em.persits(obj) again since the object has been already added to the database during its @POST

	// Do not use @Consumes when the parameters are primitive types (String, etc.)

	// When adding a course (@POST a course), do not forget to add it to the agenda as well:
	// em.persist(c);
	// agenda.addCours(c);

	// When getting the list of courses for a given week, do not use a SQL command but agenda.getCours();

	// When getting the list of courses for a given week, the Cours class already has a function matchesID(int) that checkes whether the given ID is used by the course.
}
