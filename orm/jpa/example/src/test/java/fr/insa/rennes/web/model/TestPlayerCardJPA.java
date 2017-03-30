package fr.insa.rennes.web.model;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestPlayerCardJPA extends JPATest {
	Player player;
	PlayerCard pc;
	Picture pic1;

	@Override
	void fillDatabase() {
		pic1 = new Picture("pic/gernot.jpg");

		// pic1 must not be persisted since it is not an entity.

		player = new Player("P1");
		em.persist(player);

		LocalDate date = LocalDate.of(2015, Month.JANUARY, 23);

		pc = new PlayerCard(player, pic1, date);
		em.persist(pc);
	}


	@Test
	public void testSelectP1() {
		tr.begin();

		List<PlayerCard> players = em.createQuery("SELECT pc FROM PlayerCard pc INNER JOIN Player p ON pc.player=p AND p.name='P1'", PlayerCard.class).getResultList();

		tr.commit();

		assertEquals(1, players.size());
		assertEquals(pc, players.get(0));
	}
}
