package fr.insarennes;

import fr.insarennes.model.Cours;
import fr.insarennes.model.Enseignant;
import fr.insarennes.model.Matiere;
import fr.insarennes.model.TD;
import fr.insarennes.resource.CalendarResource;
import fr.insarennes.utils.MyExceptionMapper;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.After;
import org.junit.Test;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static com.sun.xml.internal.ws.dump.LoggingDumpTube.Position.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class TestCalendarResource extends JerseyTest {
	// @Inject permits to get the CalendarResource singleton created by Jersey (somehow line 29).
	@Inject private CalendarResource calResource;

	@Override
	protected Application configure() {
		enable(TestProperties.LOG_TRAFFIC);
		enable(TestProperties.DUMP_ENTITY);
		// You must register the service you want to test
		// register(this) is just used to allow this class to access the CalendarResource instance.
		return new ResourceConfig(CalendarResource.class).register(this).register(MyExceptionMapper.class);
	}

	@Override
	@After // The @After annotation permits to tag methods to be executed after each test. This method is usually called tearDown.
	public void tearDown() throws Exception {
		super.tearDown();
		// It is necessary to flush the database between each test to avoid side-effects.
		calResource.flush();
	}

    @Test
    public void testPostEnseignantOK() {
        // Creation of a teacher.
        Enseignant ensWithoutID = new Enseignant("Cellier");
        // Asks the addition of the teacher object to the server.
        // target(...) is provided by the JerseyTest class to ease the writting of the tests
        // the URI "calendar/ens" first identifies the service ("calendar") to which the request will be sent.
        // "ens" permits the identification of the server operation that will process the request.
        // post(...) corresponds to the HTTP verb POST.
        // To POST an object to the server, this object must be serialised into a standard format: XML and JSON
        // Jersey provides operations (Entity.xml(...)) and processes to automatically serialised objects.
        // To do so (for both XML and Json), the object's class must be tagged with the annotation @XmlRootElement (see Enseignant.java)
        // A Response object is returned by the server.
        Response responseAfterPost = target("calendar/ens").request().post(Entity.xml(ensWithoutID));
        // This Response object provides a status that can be checked (see the HTTP header status picture in the subject).
        assertEquals(Response.Status.OK.getStatusCode(), responseAfterPost.getStatus());
        // The Response object may also embed an object that can be read (give the expected class as parameter).
        Enseignant ensWithID = responseAfterPost.readEntity(Enseignant.class);
        // The two Enseignant instances must be equals.
        assertEquals(ensWithoutID, ensWithID);
        // But their ID will differ since the instance returned by the server has been serialised in the database and thus
        // received a unique ID (see the JPA practice session).
        assertNotSame(ensWithoutID.getId(), ensWithID.getId());
    }

    @Test
    public void testGetEnseignantOk() {
        final String name = "Cellier";

        target("calendar/ens").request().post(Entity.xml(new Enseignant(name)));
        Response res = target("calendar/ens/"+name).request().get();
        assertEquals(Response.Status.OK.getStatusCode(), res.getStatus());
        Enseignant e = res.readEntity(Enseignant.class);
        assertEquals(e.getName(), name);
    }

    @Test
    public void testDeleteEnseignantOk() {
        final String name = "Cellier";

        target("calendar/ens").request().post(Entity.xml(new Enseignant(name)));
        Response res = target("calendar/ens/"+name).request().get();
        assertEquals(Response.Status.OK.getStatusCode(), res.getStatus());
        Enseignant e = res.readEntity(Enseignant.class);
        assertEquals(e.getName(), name);

        Response res2 = target("calendar/ens/"+name).request().delete();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), res2.getStatus());

        Response res3 = target("calendar/ens/"+name).request().get();
        assertNotSame(Response.Status.OK.getStatusCode(), res3.getStatus());
    }

    //TODO
    @Test
    public void testPutMatiereOk() {
        final String name = "BDD & WEB";
        int id;

        target("calendar/ens").request().post(Entity.xml(new Enseignant(name)));
        Response res = target("calendar/ens/"+name).request().get();
        assertEquals(Response.Status.OK.getStatusCode(), res.getStatus());
        Enseignant e = res.readEntity(Enseignant.class);
        assertEquals(e.getName(), name);

        Response res2 = target("calendar/ens/"+name).request().delete();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), res2.getStatus());

        Response res3 = target("calendar/ens/"+name).request().get();
        assertNotSame(Response.Status.OK.getStatusCode(), res3.getStatus());
    }



    @Test
    public void testPostCoursOK() {
        Enseignant e1 = new Enseignant("Cellier");
        Response r1 = target("calendar/ens").request().post(Entity.xml(e1));
        Enseignant e2 = r1.readEntity(Enseignant.class);

        Matiere m1 = new Matiere("BddWeb", 4);
        Response r2 = target("calendar/matiere").request().post(Entity.xml(m1));
        Matiere m2 = r2.readEntity(Matiere.class);

        TD c = new TD(m2, LocalDateTime.now(), e2, Duration.ofHours(2));

        Response responseAfterPost = target("calendar/cours").request().post(Entity.xml(c));
        assertEquals(Response.Status.OK.getStatusCode(), responseAfterPost.getStatus());
        TD newc = responseAfterPost.readEntity(TD.class);
        assertEquals(c, newc);
        assertNotSame(c.getId(), newc.getId());
    }



    @Test
    public void testGetIdOK() {
        Enseignant e1 = new Enseignant("Cellier");
        Response r1 = target("calendar/ens").request().post(Entity.xml(e1));
        Enseignant e2 = r1.readEntity(Enseignant.class);

        Matiere m1 = new Matiere("Web", 4);
        Response r2 = target("calendar/matiere").request().post(Entity.xml(m1));
        Matiere m2 = r2.readEntity(Matiere.class);

        TD c = new TD(m2, LocalDateTime.now(), e2, Duration.ofHours(2));

        Response responseAfterPost = target("calendar/cours").request().post(Entity.xml(c));
        System.out.println(m2.getId());
        Response res = target("calendar/getIdUse/"+m2.getId()).request().get();
        assertEquals(res.getStatus(), 200);
        ArrayList<Cours> cc = res.readEntity(new GenericType<ArrayList<Cours>>(){});

        assertEquals(cc.size(), 1);
    }

	// In your tests, do not create teachers, topics, and courses that already exist (in the constructor of the CalendarResource).
}