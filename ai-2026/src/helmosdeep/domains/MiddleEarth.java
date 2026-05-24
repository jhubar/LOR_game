package helmosdeep.domains;

import java.util.ArrayList;

/**
 * Représente la carte du jeu.
 */
public class MiddleEarth {

	private final int height;
	private final int width;
	private final Tile[][] tiles;
	private Army humans;
	private Army mordor;

	/**
	 * Initialise une carte et les 2 armées du jeu à partir de sa description textuelle.
	 */
	public MiddleEarth(String mapDescription) {
		String[] lines = mapDescription.split("\\R");
		String[] coordStr = lines[0].split(":");
		this.height = Integer.parseInt(coordStr[0]);
		this.width = Integer.parseInt(coordStr[1]);
		this.tiles = new Tile[this.height][this.width];

		for (int row = 0; row < this.height; row++) {
			for (int col = 0; col < this.width; col++) {
				Coordinates gameCoordinate = new Coordinates(row, col);
				char terrainChar = lines[row + 1].charAt(col);
				char unitChar = lines[row + this.height + 2].charAt(col);
				this.setTile(gameCoordinate, new Tile(gameCoordinate,
						ETileType.getTileTypeWithLetter(terrainChar),
						Unit.getUnitWithLetter(unitChar, gameCoordinate)));
			}
		}
		this.initializeArmies();
	}

	private void initializeArmies() {
		Army humansArmy = new Army("Hommes", new ArrayList<>());
		Army mordorArmy = new Army("Mordor", new ArrayList<>());

		for (int i = 0; i < this.tiles.length; i++) {
			for (int j = 0; j < this.tiles[i].length; j++) {
				Unit currentUnit = this.tiles[i][j].getUnit();
				if (currentUnit == null) {
					continue;
				}
				if (currentUnit.getArmyName().equals("Hommes")) {
					humansArmy.addUnitToArmy(currentUnit);
				} else {
					mordorArmy.addUnitToArmy(currentUnit);
				}
			}
		}
		this.humans = humansArmy;
		this.mordor = mordorArmy;
	}

	public Army getHumans() {
		return this.humans;
	}

	public Army getMordor() {
		return this.mordor;
	}

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}

	public void setTile(Coordinates coord, Tile tile) {
		this.tiles[coord.getX()][coord.getY()] = tile;
	}

	public Tile getTileWithCoord(Coordinates coord) {
		return this.tiles[coord.getX()][coord.getY()];
	}

	public boolean isValidCoordinate(Coordinates coord) {
		return coord.getX() >= 0 && coord.getX() < this.height
				&& coord.getY() >= 0 && coord.getY() < this.width;
	}

	/**
	 * Coût pour entrer sur {@code dest} (règle unité lourde incluse).
	 */
	public int getMovementCost(Coordinates dest, EUnitType unitType) {
		if (unitType == EUnitType.LOURDE) {
			return 1;
		}
		return getTileWithCoord(dest).getTilType().getCoutMvt();
	}

	/**
	 * Une case est traversable si elle est vide ou si c'est le point de départ.
	 */
	public boolean isPassable(Coordinates coord, Coordinates from) {
		if (!isValidCoordinate(coord)) {
			return false;
		}
		if (coord.equals(from)) {
			return true;
		}
		return getTileWithCoord(coord).getUnit() == null;
	}

	public void moveUnit(Unit unit, Coordinates destination) {
		Coordinates source = unit.getCoord();
		Tile sourceTile = getTileWithCoord(source);
		Tile destinationTile = getTileWithCoord(destination);

		sourceTile.setUnit(null);
		destinationTile.setUnit(unit);
		unit.setCoord(destination);
	}

	public void removeUnit(Unit unit) {
		if (unit == null) {
			return;
		}
		Coordinates coord = unit.getCoord();
		getTileWithCoord(coord).setUnit(null);
		unit.die();
		if (unit.getArmyName().equals("Hommes")) {
			this.humans.removeUnit(unit);
		} else {
			this.mordor.removeUnit(unit);
		}
	}

	public Army getArmyFor(Unit unit) {
		if (unit.getArmyName().equals("Hommes")) {
			return this.humans;
		}
		return this.mordor;
	}

	public ArrayList<Tile> getAllCOodrinates() {
		ArrayList<Tile> flatCoordinatesTiles = new ArrayList<>();
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length; j++) {
				flatCoordinatesTiles.add(tiles[i][j]);
			}
		}
		return flatCoordinatesTiles;
	}
}
