package helmosdeep.domains;

import java.util.Collection;

/**
 * Unité de jeu : position, type, points de mouvement et résolution de combat.
 */
public class Unit {

	private Coordinates coord;
	private EUnitType type;
	private String name;
	private int mvtRestants;
	private boolean alive;

	public Unit(Coordinates coord, EUnitType type, String name) {

		this.coord = coord;
		this.type = type;
		this.name = name;
		this.mvtRestants = type.getMvt();
		this.alive = true;

	}

	public EUnitType getUnitType() {
		return this.type;
	}

	/**
	 * 
	 * @return
	 */

	public int getAttackPower(MiddleEarth middleEarth) {
		return rollAttackPower(middleEarth).total();
	}

	public AttackPowerBreakdown rollAttackPower(MiddleEarth middleEarth) {
		return computeAttackPower(middleEarth, rollDice());
	}

	AttackPowerBreakdown computeAttackPower(MiddleEarth middleEarth, int dice) {
		Collection<Coordinates> neighbours = this.coord.getNeighbours();
		int nbAllies;
		if (this.getArmyName().equals("Hommes")) {
			nbAllies = middleEarth.getHumans().getNbOfAllies(neighbours);
		} else {
			nbAllies = middleEarth.getMordor().getNbOfAllies(neighbours);
		}
		return new AttackPowerBreakdown(this.type.getForce(), nbAllies, dice);
	}

	private static int rollDice() {
		return (int) (Math.random() * 6) + 1;
	}

	public Coordinates getCoord() {
		return this.coord;
	}

	public String getName() {
		return this.name;
	}

	public int getMvtRestants() {
		return this.mvtRestants;
	}

	public boolean isAlive() {
		return this.alive;
	}

	public void setCoord(Coordinates coord) {
		this.coord = coord;
	}

	public void spendMovement(int cost) {
		if (cost > 0) {
			this.mvtRestants -= cost;
		}
	}

	public void resetMovementPoints() {
		this.mvtRestants = this.type.getMvt();
	}

	public void grantPostAttackMovement() {
		this.mvtRestants = 1;
	}

	public boolean canAttack() {
		return this.alive && this.type != EUnitType.GENERAL;
	}

	public String getStatsLabel() {
		return this.type.getForce() + "-" + this.mvtRestants;
	}

	public void moveTo(Coordinates coord) {
		this.coord = coord;
	}

	public static Unit getUnitWithLetter(char c, Coordinates coord) throws IllegalArgumentException {
		switch (c) {
		case 'A':
			return new Unit(coord, EUnitType.GENERAL, "Aragorn");
		case 'S':
			return new Unit(coord, EUnitType.GENERAL, "Sauron");
		case 'E':
			return new Unit(coord, EUnitType.LOURDE, "Ents");
		case 'T':
			return new Unit(coord, EUnitType.LOURDE, "Trolls");
		case 'G':
			return new Unit(coord, EUnitType.MOYENNE, "Gondoriens");
		case 'O':
			return new Unit(coord, EUnitType.MOYENNE, "Orcs");
		case 'R':
			return new Unit(coord, EUnitType.LEGERE, "Rohirrim");
		case 'W':
			return new Unit(coord, EUnitType.LEGERE, "Wargs");
		case '.':
			return null;
		default:
			throw new IllegalArgumentException();
		}

	}

	public void die() {
		this.alive = false;
	}

	/**
	 * Attaque une unité ennemie adjacente.
	 *
	 * @return le compte-rendu du combat, ou {@code null} si l'attaque est invalide
	 */
	public CombatReport attack(Unit defender, MiddleEarth middleEarth) {
		return resolveAttack(defender, middleEarth, rollDice(), rollDice());
	}

	/**
	 * Résout un combat avec des dés fixés (tests unitaires).
	 */
	CombatReport resolveAttack(Unit defender, MiddleEarth middleEarth, int attackerDice, int defenderDice) {
		if (!canAttack() || defender == null || !defender.isAlive()) {
			return null;
		}
		if (!this.getCoord().getNeighbours().contains(defender.getCoord())) {
			return null;
		}
		if (this.getArmyName().equals(defender.getArmyName())) {
			return null;
		}

		AttackPowerBreakdown attackerPower = computeAttackPower(middleEarth, attackerDice);
		AttackPowerBreakdown defenderPower = defender.computeAttackPower(middleEarth, defenderDice);

		if (attackerPower.total() < defenderPower.total()) {
			middleEarth.removeUnit(this);
			return new CombatReport(this.name, defender.getName(), attackerPower, defenderPower, false);
		}

		Coordinates defenderCoord = defender.getCoord();
		middleEarth.removeUnit(defender);
		middleEarth.getArmyFor(this).recordKill();
		middleEarth.moveUnit(this, defenderCoord);
		spendMovement(getMvtRestants());
		if (this.type == EUnitType.LEGERE) {
			grantPostAttackMovement();
		}
		return new CombatReport(this.name, defender.getName(), attackerPower, defenderPower, true);
	}

	/**
	 * 
	 * @return retourne le nom de l'armée à laquelle appartient l'objet qui reçoit
	 *         l'appel.
	 */
	public String getArmyName() {
		String unitName = this.name;
		if (unitName.equalsIgnoreCase("Aragorn") || unitName.equalsIgnoreCase("Ents")
				|| unitName.equalsIgnoreCase("Gondoriens") || unitName.equalsIgnoreCase("Rohirrim")) {
			return "Hommes";
		}
		return "Mordor";
	}

}