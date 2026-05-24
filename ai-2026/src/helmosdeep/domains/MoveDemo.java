package helmosdeep.domains;

import helmosdeep.util.TxtFileReader;

/**
 * Démo console des déplacements — lancer depuis le dossier ai-2026 :
 * <pre>
 *   ./run-move-demo.sh
 *   ./run-move-demo.sh resources/maps/level-1.txt
 * </pre>
 */
public final class MoveDemo {

	private static final String DEMO_MAP = """
			3:3
			LLL
			LFL
			LML
			---
			S..
			...
			..A
			""";

	private MoveDemo() {
	}

	public static void main(String[] args) {
		String mapContent = args.length > 0 ? TxtFileReader.getContent(args[0]) : DEMO_MAP;
		String mapLabel = args.length > 0 ? args[0] : "(carte démo intégrée)";

		MiddleEarth map = new MiddleEarth(mapContent);
		MoveManager moveManager = new MoveManager();

		System.out.println("=== Helmosdeep — démo déplacements ===");
		System.out.println("Carte : " + mapLabel + " (" + map.getHeight() + "x" + map.getWidth() + ")");
		printMap(map);

		Coordinates sauron = findUnit(map, "Sauron");
		Coordinates aragorn = findUnit(map, "Aragorn");

		if (sauron == null) {
			System.out.println("Aucune unité 'Sauron' sur cette carte.");
			return;
		}

		int pm = EUnitType.GENERAL.getMvt();
		EUnitType type = EUnitType.GENERAL;

		System.out.println();
		System.out.println("Sauron en " + sauron + " — PM = " + pm);
		System.out.println("Cases atteignables (branches trop coûteuses = coupées) :");
		var reachable = moveManager.reachableCosts(sauron, pm, map, type);
		reachable.entrySet().stream()
				.sorted((a, b) -> {
					int cmp = Integer.compare(a.getValue(), b.getValue());
					return cmp != 0 ? cmp : a.getKey().toString().compareTo(b.getKey().toString());
				})
				.forEach(e -> System.out.println("  " + e.getKey() + " → coût " + e.getValue()));

		if (aragorn != null) {
			System.out.println();
			int costToAragorn = moveManager.lowerCost(sauron, aragorn, pm, map, type);
			Path path = moveManager.findPath(sauron, aragorn, pm, map, type);
			System.out.println("Vers Aragorn en " + aragorn + " : coût minimal = " + costToAragorn);
			System.out.println("Chemin : " + path);
		} else {
			System.out.println();
			System.out.println("Exemple vers (2,2) :");
			Path path = moveManager.findPath(sauron, new Coordinates(2, 2), pm, map, type);
			System.out.println("  " + path);
		}
	}

	private static Coordinates findUnit(MiddleEarth map, String name) {
		for (Tile tile : map.getAllCOodrinates()) {
			Unit unit = tile.getUnit();
			if (unit != null && unit.getName().equalsIgnoreCase(name)) {
				return tile.getCoord();
			}
		}
		return null;
	}

	private static void printMap(MiddleEarth map) {
		for (int row = 0; row < map.getHeight(); row++) {
			StringBuilder line = new StringBuilder();
			for (int col = 0; col < map.getWidth(); col++) {
				Tile tile = map.getTileWithCoord(new Coordinates(row, col));
				char terrain = switch (tile.getTilType()) {
				case LAND -> 'L';
				case FOREST -> 'F';
				case MOUNTAIN -> 'M';
				};
				Unit unit = tile.getUnit();
				char unitChar = unit == null ? '.' : unit.getName().charAt(0);
				line.append('[').append(terrain).append('/').append(unitChar).append("] ");
			}
			System.out.println(line);
		}
	}
}
