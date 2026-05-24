package helmosdeep.domains;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests unitaires du déplacement — lancer depuis ai-2026 :
 * <pre>
 *   javac -d bin -cp "resources/libs/*:src:tests" $(find src tests -name "*.java")
 *   java -jar resources/libs/junit-platform-console-standalone.jar --class-path bin --scan-class-path
 * </pre>
 * Ou lancer la classe MoveDemo avec {@code java -cp bin helmosdeep.domains.MoveDemo}
 */
class MoveManagerTest {

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
	private MoveManager moveManager;

	@BeforeEach
	void setUp() {
		map = new MiddleEarth(MAP_3X3);
		moveManager = new MoveManager();
	}

	@Test
	@DisplayName("Reste sur place : coût 0")
	void staying_on_same_tile_costs_zero() {
		Coordinates sauron = new Coordinates(0, 0);
		assertEquals(0, moveManager.lowerCost(sauron, sauron, 3, map, EUnitType.GENERAL));
	}

	@Test
	@DisplayName("Case voisine plaine : coût 1")
	void adjacent_plain_costs_one() {
		Coordinates sauron = new Coordinates(0, 0);
		Coordinates neighbour = new Coordinates(0, 1);
		assertEquals(1, moveManager.lowerCost(sauron, neighbour, 3, map, EUnitType.GENERAL));
	}

	@Test
	@DisplayName("Case occupée par une autre unité : inaccessible")
	void occupied_tile_is_not_reachable() {
		Coordinates sauron = new Coordinates(0, 0);
		Coordinates orcs = new Coordinates(0, 1);
		assertEquals(-1, moveManager.lowerCost(sauron, orcs, 3, map, EUnitType.GENERAL));
	}

	@Test
	@DisplayName("Budget insuffisant : -1")
	void insufficient_movement_points_returns_minus_one() {
		Coordinates sauron = new Coordinates(0, 0);
		Coordinates far = new Coordinates(2, 2);
		assertEquals(-1, moveManager.lowerCost(sauron, far, 1, map, EUnitType.GENERAL));
	}

	@Test
	@DisplayName("Unité lourde : montagne coûte 1")
	void heavy_unit_ignores_terrain_cost() {
		MiddleEarth mountainMap = new MiddleEarth("""
				2:2
				MM
				MM
				--
				T.
				..
				""");
		Coordinates troll = new Coordinates(0, 0);
		Coordinates mountainNeighbour = new Coordinates(0, 1);
		assertEquals(1, moveManager.lowerCost(troll, mountainNeighbour, 2, mountainMap, EUnitType.LOURDE));
		assertEquals(3, moveManager.lowerCost(troll, mountainNeighbour, 2, mountainMap, EUnitType.MOYENNE));
	}

	@Test
	@DisplayName("findPath reconstruit un chemin valide")
	void find_path_returns_steps() {
		Coordinates start = new Coordinates(1, 2);
		Coordinates dest = new Coordinates(2, 2);
		Path path = moveManager.findPath(start, dest, 4, map, EUnitType.LEGERE);
		assertTrue(path.getTotalCost() >= 0);
		assertEquals(start, path.getSteps().get(0));
		assertEquals(dest, path.getSteps().get(path.getSteps().size() - 1));
	}
}
