package helmosdeep.domains;

/**
 * 
 */
public class Tile {

	private Coordinates coord;
	private ETileType tileType;
	private Unit unit;

	public Tile(Coordinates coord, ETileType tileType, Unit unit) {
		this.coord = coord;
		this.tileType = tileType;
		this.unit = unit;
	}

	public Coordinates getCoord() {
		return this.coord;
	}

	public ETileType getTilType() {
		return this.tileType;
	}
	
	public Unit getUnit() {
		return this.unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

}
