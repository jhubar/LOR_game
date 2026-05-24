package helmosdeep.domains;

/**
 * Tests combat sans JUnit — lancer : {@code java -cp bin helmosdeep.domains.CombatSelfTest}
 */
public final class CombatSelfTest {

	private CombatSelfTest() {
	}

	public static void main(String[] args) {
		testTieGoesToAttacker();
		testWeakerAttackerDies();
		testGeneralCannotAttack();
		System.out.println("\nTous les tests combat sont passés.");
	}

	private static void testTieGoesToAttacker() {
		MiddleEarth map = isolatedMap();
		Unit orcs = unitAt(map, 0, 0);
		Unit gondor = unitAt(map, 0, 1);
		CombatReport report = orcs.resolveAttack(gondor, map, 2, 2);
		check(report != null && report.isAttackerWon(), "égalité → attaquant gagne");
		check(report.getAttackerPower().total() == 4 && report.getDefenderPower().total() == 4,
				"totaux à 4");
		System.out.println("OK   : égalité → attaquant gagne");
	}

	private static void testWeakerAttackerDies() {
		MiddleEarth map = isolatedMap();
		Unit orcs = unitAt(map, 0, 0);
		Unit gondor = unitAt(map, 0, 1);
		CombatReport report = orcs.resolveAttack(gondor, map, 1, 6);
		check(report != null && !report.isAttackerWon(), "attaquant plus faible perd");
		check(!orcs.isAlive(), "orcs mort");
		System.out.println("OK   : attaquant plus faible éliminé");
	}

	private static void testGeneralCannotAttack() {
		MiddleEarth map = new MiddleEarth("""
				2:2
				LL
				LL
				--
				SO
				..
				""");
		Unit sauron = unitAt(map, 0, 0);
		Unit orcs = unitAt(map, 0, 1);
		check(!sauron.canAttack(), "général ne peut pas attaquer");
		check(sauron.resolveAttack(orcs, map, 6, 1) == null, "resolveAttack null pour général");
		System.out.println("OK   : général inactif en attaque");
	}

	private static MiddleEarth isolatedMap() {
		return new MiddleEarth("""
				2:2
				LL
				LL
				--
				OG
				..
				""");
	}

	private static Unit unitAt(MiddleEarth map, int x, int y) {
		return map.getTileWithCoord(new Coordinates(x, y)).getUnit();
	}

	private static void check(boolean condition, String message) {
		if (!condition) {
			throw new AssertionError(message);
		}
	}
}
