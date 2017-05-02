package fr.insarennes.resource;

import fr.insarennes.model.Agenda;
import fr.insarennes.model.Enseignant;
import fr.insarennes.model.Matiere;
import fr.insarennes.model.TD;
import io.swagger.annotations.Api;
import java.net.HttpURLConnection;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Singleton;
import javax.persistence.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Singleton // Q: with and without, see 3.4 https://jersey.java.net/documentation/latest/jaxrs-resources.html
@Path("calendar")
@Api(value = "calendar")
public class CalendarResource {
	private static final Logger LOGGER = Logger.getAnonymousLogger();

	// Static blocks are used to parametrized static objects of the class.
	static {
		// Define the level of importance the Logger has to consider.
		// The logged messages with an importance lower than the one defined here will be ignored.
		LOGGER.setLevel(Level.ALL);
	}

	private final EntityManagerFactory emf;
	private final EntityManager em;
	private final Agenda agenda;

	public CalendarResource() {
		super();
		agenda = new Agenda();
		emf = Persistence.createEntityManagerFactory("agendapp");
		em = emf.createEntityManager();

		final EntityTransaction tr = em.getTransaction();

		tr.begin();
		em.persist(agenda);
		tr.commit();

		// You can add here calendar elements to add by default in the database of the application.
		// For instance:
				try {
					// Each time you add an object into the database or modify an object already added into the database,
					// You must surround your code with a em.getTransaction().begin() that identifies the beginning of a transaction
					// and a em.getTransaction().commit() at the end of the transaction to validate it.
					// In case of crashes, you have to surround the code with a try/catch block, where the catch rollbacks the
					// transaction using em.getTransaction().rollback()
					tr.begin();
					em.persist(agenda);

					Enseignant ens = new Enseignant("Blouin");
					Matiere mat = new Matiere("Web", 3);

					em.persist(ens);
					em.persist(mat);

					TD td = new TD(mat, LocalDate.of(2015, Month.JANUARY, 2).atTime(8, 0), ens, Duration.ofHours(2));
					agenda.addCours(td);
					em.persist(td);
					tr.commit();

					LOGGER.log(Level.INFO, "Added during the creation of the calendar resource:");
					LOGGER.log(Level.INFO, "a Enseignant: " + ens);
					LOGGER.log(Level.INFO, "a Matiere: " + mat);
					LOGGER.log(Level.INFO, "a TD: " + td);
				}catch(final Throwable ex) {
					LOGGER.log(Level.SEVERE, "Crash during the creation of initial data", ex);
					if(tr.isActive()) {
						tr.rollback();
					}
				}
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

    //<editor-fold desc="Prefix ens">
    //curl -H "Content-Type: application/json" -d '{"name":"blouin"}' -X POST "http://localhost:8080/api/calendar/ens"
	// To know the XML format, look at the returned XML message.
	@POST
	@Path("ens/")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces(MediaType.APPLICATION_JSON)
	public Enseignant postEnseignant(final Enseignant ens) {
		final EntityTransaction tr = em.getTransaction();
		try {
			// begin starts a transaction:
			// https://en.wikibooks.org/wiki/Java_Persistence/Transactions
			tr.begin();
			em.persist(ens);
			tr.commit();
			return ens;
			//return Response.status(Response.Status.OK).entity(ens).build();
		}catch(final Throwable ex) {
			// If an exception occurs after a begin and before the commit, the transaction has to be rollbacked.
			if(tr.isActive()) {
				tr.rollback();
			}
			// Loggers are widely used to log information about the execution of a program.
			// The classical use is a static final Logger for each class or for the whole application.
			// Here, the first parameter is the level of importance of the message.
			// The second parameter is the message, and the third one is the exception.
			// Various useful methods compose a Logger.
			// By default, when a message is logged it is printed in the console.
			LOGGER.log(Level.SEVERE, "Crash on adding a Enseignant: " + ens, ex);
			// A Web exception is then thrown.
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).build());
		}
	}



    @GET
    @Path("ens/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Enseignant getEnseignant(@PathParam("name") final String name) {
        final EntityTransaction tr = em.getTransaction();
        try {
            // begin starts a transaction:
            // https://en.wikibooks.org/wiki/Java_Persistence/Transactions
            TypedQuery<Enseignant> query = em.createNamedQuery("SelectEnseignantsByName", Enseignant.class);
            return query.setParameter("name", name).getResultList().get(0);
        }catch(final Throwable ex) {
            LOGGER.log(Level.SEVERE, "Crash reading Enseignant: " + name, ex);
            throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).build());
        }
    }



    @DELETE
    @Path("ens/{name}")
    public void deleteEnseignant(@PathParam("name") final String name) {
        final EntityTransaction tr = em.getTransaction();
        try {
            // begin starts a transaction:
            tr.begin();
            int idToDelete = this.getEnseignant(name).getId();
            Enseignant e = em.find(Enseignant.class, idToDelete);
            em.remove(e);
            tr.commit();
            //query.setParameter("name", name).executeUpdate();
        }catch(final Throwable ex) {
            // If an exception occurs after a begin and before the commit, the transaction has to be rollbacked.
            if(tr.isActive()) {
                tr.rollback();
            }
            // Loggers are widely used to log information about the execution of a program.
            // The classical use is a static final Logger for each class or for the whole application.
            // Here, the first parameter is the level of importance of the message.
            // The second parameter is the message, and the third one is the exception.
            // Various useful methods compose a Logger.
            // By default, when a message is logged it is printed in the console.
            LOGGER.log(Level.SEVERE, "Crash deleting Enseignant: " + name, ex);
            // A Web exception is then thrown.
            throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).build());
        }
    }
    //</editor-fold>

    //<editor-fold desc="Prefix matiere">
    @PUT
    @Path("matiere/{id}/{newname}")
    public void putMatiere(@PathParam("id") final int id, @PathParam("newname") final String newname) {
        final EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            Matiere mat = em.find(Matiere.class, id);
            mat.setName(newname);
            em.persist(mat);
            tr.commit();
        }catch(final Throwable ex) {
            // If an exception occurs after a begin and before the commit, the transaction has to be rollbacked.
            if(tr.isActive()) {
                tr.rollback();
            }
            // Loggers are widely used to log information about the execution of a program.
            // The classical use is a static final Logger for each class or for the whole application.
            // Here, the first parameter is the level of importance of the message.
            // The second parameter is the message, and the third one is the exception.
            // Various useful methods compose a Logger.
            // By default, when a message is logged it is printed in the console.
            LOGGER.log(Level.SEVERE, "Crash updating Matiere: " + id + " to " + newname, ex);
            // A Web exception is then thrown.
            throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).build());
        }
    }
    //</editor-fold>

	// DO NOT USE begin(), commit() or rollback() for the @GET verb.

	// When modifying an object (@PUT verb) DO NOT USE em.persits(obj) again since the object has been already added to the database during its @POST

	// Do not use @Consumes when the parameters are primitive types (String, etc.)

	// When adding a course (@POST a course), do not forget to add it to the agenda as well:
	// em.persist(c);
	// agenda.addCours(c);

	// When getting the list of courses for a given week, do not use a SQL command but agenda.getCours();

	// When getting the list of courses for a given week, the Cours class already has a function matchesID(int) that checkes whether the given ID is used by the course.
}