package helmosdeep.domains;

import java.util.ArrayList;
import java.util.List;

/**
 * Partie en cours : carte, tours et unité sélectionnée.
 */
public class HelmosDeepGame {

	private final MiddleEarth middleEarth;
	private boolean mordorTurn;
	private Unit selectedUnit;
	private CombatReport lastCombatReport;
	private final List<String> combatHighlights = new ArrayList<>();

	public HelmosDeepGame(String gameDescription) {
		this.middleEarth = new MiddleEarth(gameDescription);
		this.mordorTurn = true;
	}

	public MiddleEarth getMiddleEarth() {
		return this.middleEarth;
	}

	public boolean isMordorTurn() {
		return this.mordorTurn;
	}

	public String getMatchLabel() {
		return "Le Mordor vs les Hommes";
	}

	public String getTurnLabel() {
		return this.mordorTurn ? "Au tour du Mordor" : "Au tour des Hommes";
	}

	public Unit getSelectedUnit() {
		return this.selectedUnit;
	}

	public void selectUnit(Unit unit) {
		this.selectedUnit = unit;
	}

	public void clearSelection() {
		this.selectedUnit = null;
	}

	public void endTurn() {
		this.mordorTurn = !this.mordorTurn;
		this.selectedUnit = null;
		this.lastCombatReport = null;
		resetMovementForArmy(this.mordorTurn ? "Mordor" : "Hommes");
	}

	private void resetMovementForArmy(String armyName) {
		for (Tile tile : getAllTiles()) {
			Unit unit = tile.getUnit();
			if (unit != null && unit.isAlive() && unit.getArmyName().equals(armyName)) {
				unit.resetMovementPoints();
			}
		}
	}

	public List<Tile> getAllTiles() {
		return this.middleEarth.getAllCOodrinates();
	}

	public Unit findUnitByName(String name) {
		for (Tile tile : getAllTiles()) {
			Unit unit = tile.getUnit();
			if (unit != null && unit.getName().equalsIgnoreCase(name)) {
				return unit;
			}
		}
		return null;
	}

	public Unit findFirstUnitForCurrentArmy() {
		String army = this.mordorTurn ? "Mordor" : "Hommes";
		for (Tile tile : getAllTiles()) {
			Unit unit = tile.getUnit();
			if (unit != null && unit.isAlive() && unit.getArmyName().equals(army)) {
				return unit;
			}
		}
		return null;
	}

	public boolean belongsToCurrentArmy(Unit unit) {
		if (unit == null) {
			return false;
		}
		String expected = this.mordorTurn ? "Mordor" : "Hommes";
		return expected.equals(unit.getArmyName());
	}

	public boolean moveSelectedUnit(Coordinates destination) {
		if (this.selectedUnit == null) {
			return false;
		}
		MoveManager moveManager = new MoveManager();
		Coordinates src = this.selectedUnit.getCoord();
		int cost = moveManager.lowerCost(src, destination, this.selectedUnit.getMvtRestants(),
				this.middleEarth, this.selectedUnit.getUnitType());
		if (cost < 0) {
			return false;
		}
		this.middleEarth.moveUnit(this.selectedUnit, destination);
		this.selectedUnit.spendMovement(cost);
		return true;
	}

	public AttackOutcome attackSelectedUnit(Coordinates targetCoord) {
		Unit attacker = this.selectedUnit;
		if (attacker == null || !attacker.canAttack()) {
			return AttackOutcome.invalid();
		}

		Unit defender = getUnitAt(targetCoord);
		if (defender == null || belongsToCurrentArmy(defender)) {
			return AttackOutcome.invalid();
		}
		if (!attacker.getCoord().getNeighbours().contains(targetCoord)) {
			return AttackOutcome.invalid();
		}

		int attackerRow = attacker.getCoord().getX();
		int attackerCol = attacker.getCoord().getY();
		int defenderRow = targetCoord.getX();
		int defenderCol = targetCoord.getY();

		CombatReport report = attacker.attack(defender, this.middleEarth);
		if (report != null) {
			recordCombatReport(report);
			if (attacker.isAlive() && attacker.getMvtRestants() <= 0) {
				this.selectedUnit = null;
			} else if (attacker.isAlive()) {
				this.selectedUnit = attacker;
			} else {
				this.selectedUnit = null;
			}
			if (report.isAttackerWon()) {
				return AttackOutcome.attackerWon(attackerRow, attackerCol, defenderRow, defenderCol,
						attacker.getStatsLabel(), report);
			}
			return AttackOutcome.attackerLost(attackerRow, attackerCol, defenderRow, defenderCol, report);
		}

		return AttackOutcome.invalid();
	}

	private void recordCombatReport(CombatReport report) {
		this.lastCombatReport = report;
		this.combatHighlights.add(0, report.formatHighlight());
		if (this.combatHighlights.size() > 8) {
			this.combatHighlights.remove(this.combatHighlights.size() - 1);
		}
	}

	public CombatReport getLastCombatReport() {
		return this.lastCombatReport;
	}

	public List<String> getCombatHighlights() {
		return List.copyOf(this.combatHighlights);
	}

	public String getArmyScoreLine() {
		Army humans = this.middleEarth.getHumans();
		Army mordor = this.middleEarth.getMordor();
		return "Scores : Hommes %du/%dK — Mordor %du/%dK".formatted(humans.getUnitCount(), humans.getKillCount(),
				mordor.getUnitCount(), mordor.getKillCount());
	}

	public String[] getHumansEndGameLines() {
		Army army = this.middleEarth.getHumans();
		List<String> lines = new ArrayList<>();
		lines.add("Unités restantes : %d".formatted(army.getUnitCount()));
		lines.add("Unités éliminées : %d".formatted(army.getKillCount()));
		if (this.middleEarth.getHumans().getUnitCount() == 0) {
			lines.add("Défaite");
		} else if (isGameOver()) {
			lines.add("Victoire !");
		}
		return lines.toArray(String[]::new);
	}

	public String[] getMordorEndGameLines() {
		Army army = this.middleEarth.getMordor();
		List<String> lines = new ArrayList<>();
		lines.add("Unités restantes : %d".formatted(army.getUnitCount()));
		lines.add("Unités éliminées : %d".formatted(army.getKillCount()));
		if (this.middleEarth.getMordor().getUnitCount() == 0) {
			lines.add("Défaite");
		} else if (isGameOver()) {
			lines.add("Victoire !");
		}
		return lines.toArray(String[]::new);
	}

	public boolean isGameOver() {
		return this.middleEarth.getHumans().getUnitCount() == 0
				|| this.middleEarth.getMordor().getUnitCount() == 0;
	}

	public String getWinnerLabel() {
		if (this.middleEarth.getHumans().getUnitCount() == 0) {
			return "Victoire du Mordor";
		}
		if (this.middleEarth.getMordor().getUnitCount() == 0) {
			return "Victoire des Hommes";
		}
		return "";
	}

	public Tile getTileAt(Coordinates coord) {
		return this.middleEarth.getTileWithCoord(coord);
	}

	public Unit getUnitAt(Coordinates coord) {
		return getTileAt(coord).getUnit();
	}

}
