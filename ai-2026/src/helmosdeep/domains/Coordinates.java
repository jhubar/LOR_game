package helmosdeep.domains;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import helmosdeep.util.Contract;

/**
 * Coordonnée sur la carte hexagonale (x = ligne, y = colonne, base 0).
 */
public class Coordinates {

	private final int x;
	private final int y;

	public Coordinates(int x, int y) {
		Contract.require(x >= 0 && y >= 0, "Aucune des coordonnées ne peut être négative.");
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	/**
	 * @return les 6 voisins hex de cette case
	 */
	public Collection<Coordinates> getNeighbours() {
		Collection<Coordinates> result = new ArrayList<>(6);

		addIfNonNegative(result, this.x, y - 1);
		addIfNonNegative(result, this.x, y + 1);
		addIfNonNegative(result, this.getX() - 1, this.getY());
		addIfNonNegative(result, this.getX() + 1, this.getY());

		if (this.getX() % 2 == 0) {
			addIfNonNegative(result, this.getX() + 1, this.getY() - 1);
			addIfNonNegative(result, this.getX() - 1, this.getY() - 1);
		} else {
			addIfNonNegative(result, this.getX() - 1, this.getY() + 1);
			addIfNonNegative(result, this.getX() + 1, this.getY() + 1);
		}

		return result;
	}

	private static void addIfNonNegative(Collection<Coordinates> result, int x, int y) {
		if (x >= 0 && y >= 0) {
			result.add(new Coordinates(x, y));
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Coordinates other)) {
			return false;
		}
		return this.x == other.x && this.y == other.y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public String toString() {
		return "(%d,%d)".formatted(x, y);
	}

}
