package helmosdeep.domains;

/**
 * Détail d'une puissance d'attaque : force + alliés voisins + dé.
 */
public record AttackPowerBreakdown(int force, int allies, int dice) {

	public int total() {
		return this.force + this.allies + this.dice;
	}

	public String formatCompact() {
		return "%d+%d+%d=%d".formatted(this.force, this.allies, this.dice, total());
	}
}
