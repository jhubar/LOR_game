package helmosdeep.domains;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Calcule les déplacements possibles sur la carte hexagonale (exploration élaguée
 * type Dijkstra : on coupe toute branche trop coûteuse ou déjà dominée).
 */
public class MoveManager {

	private record FrontierNode(Coordinates coord, int costSoFar) {
	}

	/**
	 * @return coût minimal pour aller de src à dest, ou -1 si impossible avec mvt PM
	 */
	public int lowerCost(Coordinates src, Coordinates dest, int mvt, MiddleEarth middleEarth, EUnitType unitType) {
		Map<Coordinates, Integer> bestCosts = computeReachableCosts(src, mvt, middleEarth, unitType);
		return bestCosts.getOrDefault(dest, -1);
	}

	/**
	 * @return toutes les cases atteignables avec leur coût minimal (src incluse à 0)
	 */
	public Map<Coordinates, Integer> reachableCosts(Coordinates src, int mvt, MiddleEarth middleEarth,
			EUnitType unitType) {
		return computeReachableCosts(src, mvt, middleEarth, unitType);
	}

	/**
	 * @return le chemin le moins cher vers dest, ou chemin vide si impossible
	 */
	public Path findPath(Coordinates src, Coordinates dest, int mvt, MiddleEarth middleEarth, EUnitType unitType) {
		Map<Coordinates, Integer> bestCosts = new HashMap<>();
		Map<Coordinates, Coordinates> parent = new HashMap<>();
		PriorityQueue<FrontierNode> frontier = new PriorityQueue<>(Comparator.comparingInt(FrontierNode::costSoFar));

		bestCosts.put(src, 0);
		frontier.add(new FrontierNode(src, 0));

		while (!frontier.isEmpty()) {
			FrontierNode current = frontier.poll();
			int costSoFar = current.costSoFar();
			Coordinates coord = current.coord();

			Integer known = bestCosts.get(coord);
			if (known == null || costSoFar > known) {
				continue;
			}

			for (Coordinates neighbour : coord.getNeighbours()) {
				if (!middleEarth.isValidCoordinate(neighbour)) {
					continue;
				}
				if (!middleEarth.isPassable(neighbour, src)) {
					continue;
				}

				int stepCost = middleEarth.getMovementCost(neighbour, unitType);
				int newCost = costSoFar + stepCost;

				if (newCost > mvt) {
					continue;
				}

				Integer previousBest = bestCosts.get(neighbour);
				if (previousBest == null || newCost < previousBest) {
					bestCosts.put(neighbour, newCost);
					parent.put(neighbour, coord);
					frontier.add(new FrontierNode(neighbour, newCost));
				}
			}
		}

		Integer destCost = bestCosts.get(dest);
		if (destCost == null) {
			return new Path(java.util.Collections.emptyList(), -1);
		}
		return Path.fromParents(src, dest, parent, destCost);
	}

	private Map<Coordinates, Integer> computeReachableCosts(Coordinates src, int mvt, MiddleEarth middleEarth,
			EUnitType unitType) {
		Map<Coordinates, Integer> bestCosts = new HashMap<>();
		PriorityQueue<FrontierNode> frontier = new PriorityQueue<>(Comparator.comparingInt(FrontierNode::costSoFar));

		bestCosts.put(src, 0);
		frontier.add(new FrontierNode(src, 0));

		while (!frontier.isEmpty()) {
			FrontierNode current = frontier.poll();
			int costSoFar = current.costSoFar();
			Coordinates coord = current.coord();

			Integer known = bestCosts.get(coord);
			if (known == null || costSoFar > known) {
				continue;
			}

			for (Coordinates neighbour : coord.getNeighbours()) {
				if (!middleEarth.isValidCoordinate(neighbour)) {
					continue;
				}
				if (!middleEarth.isPassable(neighbour, src)) {
					continue;
				}

				int stepCost = middleEarth.getMovementCost(neighbour, unitType);
				int newCost = costSoFar + stepCost;

				if (newCost > mvt) {
					continue;
				}

				Integer previousBest = bestCosts.get(neighbour);
				if (previousBest == null || newCost < previousBest) {
					bestCosts.put(neighbour, newCost);
					frontier.add(new FrontierNode(neighbour, newCost));
				}
			}
		}

		return bestCosts;
	}
}
