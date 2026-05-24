package helmosdeep.domains;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UnitCombatTest {

	private static final String MAP_3X3 = """
			3:3
			LLL
			FFF
			MMM
			---
			SO.
			W..
			.GA
			""";

	private MiddleEarth map;

	@BeforeEach
	void setUp() {
		map = new MiddleEarth(MAP_3X3);
	}

	@Test
	@DisplayName("Égalité de puissance : l'attaquant gagne")
	void tie_goes_to_attacker() {
		MiddleEarth isolated = new MiddleEarth("""
				2:2
				LL
				LL
				--
				OG
				..
				""");
		Unit orcs = isolated.getTileWithCoord(new Coordinates(0, 0)).getUnit();
		Unit gondor = isolated.getTileWithCoord(new Coordinates(0, 1)).getUnit();

		CombatReport report = orcs.resolveAttack(gondor, isolated, 2, 2);
		assertNotNull(report);
		assertTrue(report.isAttackerWon());
		assertEquals(4, report.getAttackerPower().total());
		assertEquals(4, report.getDefenderPower().total());
	}

	@Test
	@DisplayName("Attaquant strictement plus faible : éliminé")
	void weaker_attacker_is_removed() {
		MiddleEarth isolated = new MiddleEarth("""
				2:2
				LL
				LL
				--
				OG
				..
				""");
		Unit orcs = isolated.getTileWithCoord(new Coordinates(0, 0)).getUnit();
		Unit gondor = isolated.getTileWithCoord(new Coordinates(0, 1)).getUnit();

		CombatReport report = orcs.resolveAttack(gondor, isolated, 1, 6);
		assertNotNull(report);
		assertFalse(report.isAttackerWon());
		assertFalse(orcs.isAlive());
		assertTrue(gondor.isAlive());
	}

	@Test
	@DisplayName("Unité légère : 1 PM après attaque réussie")
	void light_unit_gets_one_pm_after_win() {
		Unit wargs = map.getTileWithCoord(new Coordinates(1, 0)).getUnit();
		Unit orcs = map.getTileWithCoord(new Coordinates(0, 1)).getUnit();

		CombatReport report = wargs.resolveAttack(orcs, map, 6, 1);
		assertNotNull(report);
		assertTrue(report.isAttackerWon());
		assertEquals(1, wargs.getMvtRestants());
	}

	@Test
	@DisplayName("Général ne peut pas attaquer")
	void general_cannot_attack() {
		Unit sauron = map.getTileWithCoord(new Coordinates(0, 0)).getUnit();
		Unit orcs = map.getTileWithCoord(new Coordinates(0, 1)).getUnit();

		assertFalse(sauron.canAttack());
		assertNull(sauron.resolveAttack(orcs, map, 6, 1));
	}

	@Test
	@DisplayName("formatSituationLine inclut le détail force+alliés+dé")
	void combat_report_formats_breakdown() {
		AttackPowerBreakdown attacker = new AttackPowerBreakdown(2, 1, 4);
		AttackPowerBreakdown defender = new AttackPowerBreakdown(1, 0, 3);
		CombatReport report = new CombatReport("Orcs", "Wargs", attacker, defender, true);

		String line = report.formatSituationLine();
		assertTrue(line.contains("2+1+4"));
		assertTrue(line.contains("1+0+3"));
	}
}
