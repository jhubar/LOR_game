package helmosdeep.domains;

/**
 * Compte-rendu d'un duel entre deux unités, avec le détail des puissances.
 */
public final class CombatReport {

	private final String attackerName;
	private final String defenderName;
	private final AttackPowerBreakdown attackerPower;
	private final AttackPowerBreakdown defenderPower;
	private final boolean attackerWon;

	public CombatReport(String attackerName, String defenderName, AttackPowerBreakdown attackerPower,
			AttackPowerBreakdown defenderPower, boolean attackerWon) {
		this.attackerName = attackerName;
		this.defenderName = defenderName;
		this.attackerPower = attackerPower;
		this.defenderPower = defenderPower;
		this.attackerWon = attackerWon;
	}

	public String getAttackerName() {
		return this.attackerName;
	}

	public String getDefenderName() {
		return this.defenderName;
	}

	public AttackPowerBreakdown getAttackerPower() {
		return this.attackerPower;
	}

	public AttackPowerBreakdown getDefenderPower() {
		return this.defenderPower;
	}

	public boolean isAttackerWon() {
		return this.attackerWon;
	}

	/** Une ligne pour le panneau Situation en cours de partie. */
	public String formatSituationLine() {
		if (this.attackerWon) {
			return "Combat : %s %d bat %s %d (%s vs %s)".formatted(this.attackerName, this.attackerPower.total(),
					this.defenderName, this.defenderPower.total(), this.attackerPower.formatCompact(),
					this.defenderPower.formatCompact());
		}
		return "Combat : %s %d vaincu par %s %d (%s vs %s)".formatted(this.attackerName, this.attackerPower.total(),
				this.defenderName, this.defenderPower.total(), this.attackerPower.formatCompact(),
				this.defenderPower.formatCompact());
	}

	/** Résumé pour l'écran de fin de partie. */
	public String formatHighlight() {
		if (this.attackerWon) {
			return "%s (%s) élimine %s (%s)".formatted(this.attackerName, this.attackerPower.formatCompact(),
					this.defenderName, this.defenderPower.formatCompact());
		}
		return "%s (%s) éliminé par %s (%s)".formatted(this.attackerName, this.attackerPower.formatCompact(),
				this.defenderName, this.defenderPower.formatCompact());
	}
}
