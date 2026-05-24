package helmosdeep.domains;

/**
 * Résultat d'une tentative d'attaque entre deux unités.
 */
public final class AttackOutcome {

	public enum Result {
		INVALID,
		ATTACKER_WON,
		ATTACKER_LOST
	}

	private final Result result;
	private final int attackerRow;
	private final int attackerCol;
	private final int defenderRow;
	private final int defenderCol;
	private final String attackerStats;
	private final CombatReport combatReport;

	private AttackOutcome(Result result, int attackerRow, int attackerCol, int defenderRow, int defenderCol,
			String attackerStats, CombatReport combatReport) {
		this.result = result;
		this.attackerRow = attackerRow;
		this.attackerCol = attackerCol;
		this.defenderRow = defenderRow;
		this.defenderCol = defenderCol;
		this.attackerStats = attackerStats;
		this.combatReport = combatReport;
	}

	public static AttackOutcome invalid() {
		return new AttackOutcome(Result.INVALID, -1, -1, -1, -1, null, null);
	}

	public static AttackOutcome attackerWon(int attackerRow, int attackerCol, int defenderRow, int defenderCol,
			String attackerStats, CombatReport combatReport) {
		return new AttackOutcome(Result.ATTACKER_WON, attackerRow, attackerCol, defenderRow, defenderCol,
				attackerStats, combatReport);
	}

	public static AttackOutcome attackerLost(int attackerRow, int attackerCol, int defenderRow, int defenderCol,
			CombatReport combatReport) {
		return new AttackOutcome(Result.ATTACKER_LOST, attackerRow, attackerCol, defenderRow, defenderCol, null,
				combatReport);
	}

	public boolean isValid() {
		return this.result != Result.INVALID;
	}

	public boolean attackerWon() {
		return this.result == Result.ATTACKER_WON;
	}

	public boolean attackerLost() {
		return this.result == Result.ATTACKER_LOST;
	}

	public int getAttackerRow() {
		return this.attackerRow;
	}

	public int getAttackerCol() {
		return this.attackerCol;
	}

	public int getDefenderRow() {
		return this.defenderRow;
	}

	public int getDefenderCol() {
		return this.defenderCol;
	}

	public String getAttackerStats() {
		return this.attackerStats;
	}

	public CombatReport getCombatReport() {
		return this.combatReport;
	}
}
