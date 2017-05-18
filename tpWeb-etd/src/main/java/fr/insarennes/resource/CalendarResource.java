package fr.insarennes.resource;

import fr.insarennes.model.*;
import io.swagger.annotations.Api;
import java.net.HttpURLConnection;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
                    Enseignant ens2 = new Enseignant("Cellier");
                    Matiere mat = new Matiere("Web", 3);
                    Matiere mat2 = new Matiere("Bdd", 3);

                    em.persist(ens);
                    em.persist(ens2);
                    em.persist(mat);
                    em.persist(mat2);

                    CM c1 = new CM(mat2, LocalDate.of(2015, Month.JANUARY, 2).atTime(8, 0), ens, Duration.ofHours(2));
                    CM c2 = new CM(mat, LocalDate.of(2015, Month.JANUARY, 2).atTime(10, 0), ens, Duration.ofHours(2));
                    TD c3 = new TD(mat, LocalDate.of(2015, Month.JANUARY, 2).atTime(13, 30), ens, Duration.ofHours(2));
                    TD c4 = new TD(mat2, LocalDate.of(2015, Month.JANUARY, 2).atTime(15, 30), ens, Duration.ofHours(1));
                    agenda.addCours(c1);
                    agenda.addCours(c2);
                    agenda.addCours(c3);
                    agenda.addCours(c4);
                    em.persist(c1);
                    em.persist(c2);
                    em.persist(c3);
                    em.persist(c4);
					tr.commit();

					LOGGER.log(Level.INFO, "Added during the creation of the calendar resource:");
					LOGGER.log(Level.INFO, "a Enseignant: " + ens);
					LOGGER.log(Level.INFO, "a Matiere: " + mat);
					LOGGER.log(Level.INFO, "a CM: " + c1);
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
    @POST
    @Path("matiere/")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.APPLICATION_JSON)
    public Matiere postMatiere(final Matiere mat) {
        final EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.persist(mat);
            tr.commit();
            return mat;
        }catch(final Throwable ex) {
            if(tr.isActive()) {
                tr.rollback();
            }
            LOGGER.log(Level.SEVERE, "Crash on adding a Matiere : " + mat, ex);
            // A Web exception is then thrown.
            throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).build());
        }
    }

    @GET
    @Path("matiere/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Matiere getMatiere(@PathParam("name") final String name) {
        final EntityTransaction tr = em.getTransaction();
        try {
            TypedQuery<Matiere> query = em.createNamedQuery("SelectMatieresByName", Matiere.class);
            return query.setParameter("name", name).getResultList().get(0);
        }catch(final Throwable ex) {
            LOGGER.log(Level.SEVERE, "Crash reading Matiere: " + name, ex);
            throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).build());
        }
    }

    @PUT
    @Path("matiere/{id}/{newname}")
    public void putMatiere(@PathParam("id") final int id, @PathParam("newname") final String newname) {
        final EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            Matiere mat = em.find(Matiere.class, id);
            mat.setName(newname);
            //em.persist(mat);  /!\ not to do !
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

    @DELETE
    @Path("matiere/{id}")
    public void deleteMatiere(@PathParam("id") final int id) {
        final EntityTransaction tr = em.getTransaction();
        try {
            // begin starts a transaction:
            tr.begin();
            //int idToDelete = this.getMatiere(name).getId();
            int idToDelete = id;
            Matiere m = em.find(Matiere.class, idToDelete);
            em.remove(m);
            tr.commit();
            //query.setParameter("name", name).executeUpdate();
        }catch(final Throwable ex) {
            // If an exception occurs after a begin and before the commit, the transaction has to be rollbacked.
            if(tr.isActive()) {
                tr.rollback();
            }
            LOGGER.log(Level.SEVERE, "Crash deleting Matiere: " + id, ex);
            // A Web exception is then thrown.
            throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).build());
        }
    }
    //</editor-fold>

    //<editor-fold desc="Prefix cours">
    @POST
    @Path("cours/")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.APPLICATION_JSON)
    public Cours postCours(final Cours c) {
        final EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.persist(c);
            agenda.addCours(c);
            tr.commit();
            return c;
        }catch(final Throwable ex) {
            if(tr.isActive()) {
                tr.rollback();
            }
            LOGGER.log(Level.SEVERE, "Crash on adding a Cours : " + c, ex);
            // A Web exception is then thrown.
            throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).build());
        }
    }

    //</editor-fold>

    @GET
    @Path("getIdUse/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIdUse(@PathParam("id") final int id) {
        try {
            ArrayList<Cours> cours = this.agenda.getIdUse(id);
            return Response.status(Response.Status.OK).entity(cours.toArray(new Cours[cours.size()])).build();
        }catch(final Throwable ex) {
            LOGGER.log(Level.SEVERE, "Crash on reading Agenda !", ex);
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