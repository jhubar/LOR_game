package helmosdeep.domains;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Army {

	String name;
	List<Unit> units;
//	Map<Coordinates, Unit> units;
	int killCount;

	public Army(String name, List<Unit> units) {
		this.name = name;
		this.units = units;
		this.killCount = 0;
	}

	public int getNbOfAllies(Collection<Coordinates> neighbours) {
		int cpt = 0;

		List<Coordinates> unitCoords = new ArrayList<Coordinates>();
		for (Unit unit : this.units) {
			unitCoords.add(unit.getCoord());
		}
		for (Coordinates coord : unitCoords) {
			if (neighbours.contains(coord)) {
				cpt++;
			}
		}
		return cpt;
	}

	public void addUnitToArmy(Unit unit) {
		this.units.add(unit);
	}

	public void removeUnit(Unit unit) {
		this.units.remove(unit);
	}

	public void recordKill() {
		this.killCount++;
	}

	public int getUnitCount() {
		return this.units.size();
	}

	public int getKillCount() {
		return this.killCount;
	}

	public void setName(String name) {
		this.name = name;
	}

}
