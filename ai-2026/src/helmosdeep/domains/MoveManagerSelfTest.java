package helmosdeep.domains;

/**
 * Tests sans JUnit — lancer avec :
 * {@code java -cp bin helmosdeep.domains.MoveManagerSelfTest}
 */
public final class MoveManagerSelfTest {

	private MoveManagerSelfTest() {
	}

	public static void main(String[] args) {
		int failed = 0;
		failed += assertEquals("reste sur place", 0,
				cost(""" 
						3:3
						LLL
						LLL
						LLL
						---
						S..
						...
						...
						""", 0, 0, 0, 0, EUnitType.GENERAL, 3));

		failed += assertEquals("voisin plaine", 1,
				cost("""
						3:3
						LLL
						LLL
						LLL
						---
						S..
						...
						...
						""", 0, 0, 0, 1, EUnitType.GENERAL, 3));

		failed += assertEquals("case occupée", -1,
				cost("""
						3:3
						LLL
						LLL
						LLL
						---
						SO.
						...
						...
						""", 0, 0, 0, 1, EUnitType.GENERAL, 3));

		failed += assertEquals("unité lourde sur montagne", 1,
				cost("""
						2:2
						MM
						MM
						--
						T.
						..
						""", 0, 0, 0, 1, EUnitType.LOURDE, 2));

		failed += assertEquals("budget insuffisant", -1,
				cost("""
						3:3
						LLL
						LLL
						LLL
						---
						S..
						...
						...
						""", 0, 0, 2, 2, EUnitType.GENERAL, 1));

		Path path = path("""
				3:3
				LLL
				LLL
				LLL
				---
				.S.
				...
				...
				""", 0, 1, 2, 1, EUnitType.GENERAL, 3);
		if (path.getTotalCost() < 0 || path.getSteps().size() < 2) {
			System.out.println("ÉCHEC : findPath doit relier (0,1) à (2,1)");
			failed++;
		} else {
			System.out.println("OK   : findPath → " + path);
		}

		if (failed == 0) {
			System.out.println();
			System.out.println("Tous les tests sont passés.");
		} else {
			System.out.println();
			System.out.println(failed + " test(s) en échec.");
			System.exit(1);
		}
	}

	private static int cost(String map, int sx, int sy, int dx, int dy, EUnitType type, int pm) {
		MiddleEarth middleEarth = new MiddleEarth(map);
		MoveManager manager = new MoveManager();
		return manager.lowerCost(new Coordinates(sx, sy), new Coordinates(dx, dy), pm, middleEarth, type);
	}

	private static Path path(String map, int sx, int sy, int dx, int dy, EUnitType type, int pm) {
		MiddleEarth middleEarth = new MiddleEarth(map);
		MoveManager manager = new MoveManager();
		return manager.findPath(new Coordinates(sx, sy), new Coordinates(dx, dy), pm, middleEarth, type);
	}

	private static int assertEquals(String name, int expected, int actual) {
		if (expected == actual) {
			System.out.println("OK   : " + name);
			return 0;
		}
		System.out.println("ÉCHEC : " + name + " — attendu " + expected + ", obtenu " + actual);
		return 1;
	}
}
