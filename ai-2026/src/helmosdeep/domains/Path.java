package helmosdeep.domains;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Chemin reconstruit entre deux cases, avec son coût total.
 */
public final class Path {

	private final List<Coordinates> steps;
	private final int totalCost;

	public Path(List<Coordinates> steps, int totalCost) {
		this.steps = List.copyOf(steps);
		this.totalCost = totalCost;
	}

	public List<Coordinates> getSteps() {
		return steps;
	}

	public int getTotalCost() {
		return totalCost;
	}

	public boolean isEmpty() {
		return steps.isEmpty();
	}

	@Override
	public String toString() {
		return "coût=%d, étapes=%s".formatted(totalCost, steps);
	}

	/**
	 * Reconstruit le chemin de {@code dest} vers {@code src} via la map des parents.
	 */
	public static Path fromParents(Coordinates src, Coordinates dest,
			java.util.Map<Coordinates, Coordinates> parent, int totalCost) {
		if (src.equals(dest)) {
			return new Path(List.of(src), 0);
		}
		ArrayList<Coordinates> reversed = new ArrayList<>();
		Coordinates current = dest;
		while (current != null && !current.equals(src)) {
			reversed.add(current);
			current = parent.get(current);
		}
		if (current == null) {
			return new Path(Collections.emptyList(), -1);
		}
		reversed.add(src);
		Collections.reverse(reversed);
		return new Path(reversed, totalCost);
	}
}
