package helmosdeep.supervisors.game;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import helmosdeep.domains.AttackOutcome;
import helmosdeep.domains.CombatReport;
import helmosdeep.domains.Coordinates;
import helmosdeep.domains.ETileType;
import helmosdeep.domains.HelmosDeepGame;
import helmosdeep.domains.IGameFactory;
import helmosdeep.domains.MoveManager;
import helmosdeep.domains.Tile;
import helmosdeep.domains.Unit;
import helmosdeep.supervisors.ViewsId;

/**
 * Répond aux demandes de l'utilisateur en rafraichissant d'abord le modèle,
 * puis la vue.
 */
public final class GameSupervisor implements GameViewListener {
	private final GameView view;
	private final IGameFactory factory;
	private final MoveManager moveManager = new MoveManager();
	private final Map<String, Color> baseTileColors = new HashMap<>();
	private HelmosDeepGame game;

	private int currentRow;
	private int currentCol;

	/**
	 * Construit un superviseur pour la vue {@code view}.
	 */
	public GameSupervisor(GameView view, IGameFactory factory) {
		this.view = Objects.requireNonNull(view, "Arg. view != null attendu");
		this.view.addListener(this);
		this.factory = factory;
	}

	@Override
	public void onViewEntered() {
		this.game = this.factory.getcurrentGame();

		for (Tile tile : this.game.getAllTiles()) {
			Coordinates coord = tile.getCoord();
			Color color = tileColor(tile.getTilType());
			this.baseTileColors.put(tileKey(coord.getX(), coord.getY()), color);
			this.view.addHex(coord.getX(), coord.getY(), color);

			Unit unit = tile.getUnit();
			if (unit != null) {
				this.view.addUnit(coord.getX(), coord.getY(), unit.getName(), unit.getStatsLabel());
			}
		}

		Unit activeUnit = this.game.findUnitByName("Sauron");
		if (activeUnit == null) {
			activeUnit = this.game.findFirstUnitForCurrentArmy();
		}
		if (activeUnit != null) {
			this.currentRow = activeUnit.getCoord().getX();
			this.currentCol = activeUnit.getCoord().getY();
		} else {
			this.currentRow = 0;
			this.currentCol = 0;
		}

		this.view.moveCameraTo(this.currentRow, this.currentCol);
		this.refreshStatusPanel();
	}

	private static int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}

	@Override
	public void onMove(int xDir, int yDir) {
		int maxRow = this.game.getMiddleEarth().getHeight() - 1;
		int maxCol = this.game.getMiddleEarth().getWidth() - 1;

		this.currentRow = clamp(this.currentRow + yDir, 0, maxRow);
		this.currentCol = clamp(this.currentCol + xDir, 0, maxCol);

		this.view.moveCameraTo(this.currentRow, this.currentCol);
		this.refreshStatusPanel();
	}

	@Override
	public void onAction() {
		Coordinates cursor = new Coordinates(this.currentRow, this.currentCol);
		Unit unitAtCursor = this.game.getUnitAt(cursor);

		if (this.game.getSelectedUnit() == null) {
			if (unitAtCursor != null && this.game.belongsToCurrentArmy(unitAtCursor)) {
				this.game.selectUnit(unitAtCursor);
				this.view.selectUnit(this.currentRow, this.currentCol);
				updateReachableHighlights();
			}
			this.refreshStatusPanel();
			return;
		}

		Unit selected = this.game.getSelectedUnit();
		int fromRow = selected.getCoord().getX();
		int fromCol = selected.getCoord().getY();

		if (cursor.equals(selected.getCoord())) {
			this.game.clearSelection();
			this.view.unselectUnit(fromRow, fromCol);
			restoreReachableHighlights();
			this.refreshStatusPanel();
			return;
		}

		if (unitAtCursor != null && !this.game.belongsToCurrentArmy(unitAtCursor)) {
			AttackOutcome outcome = this.game.attackSelectedUnit(cursor);
			if (outcome.isValid()) {
				applyAttackOutcome(outcome, fromRow, fromCol);
				updateReachableHighlights();
				this.refreshStatusPanel();
				return;
			}
		}

		if (this.game.moveSelectedUnit(cursor)) {
			Unit moved = this.game.getUnitAt(cursor);
			this.view.moveUnit(fromRow, fromCol, this.currentRow, this.currentCol, moved.getStatsLabel());
			this.view.unselectUnit(this.currentRow, this.currentCol);
			this.game.clearSelection();
			restoreReachableHighlights();
		} else if (unitAtCursor != null && this.game.belongsToCurrentArmy(unitAtCursor)
				&& !unitAtCursor.equals(selected)) {
			this.view.unselectUnit(fromRow, fromCol);
			this.game.selectUnit(unitAtCursor);
			this.view.selectUnit(this.currentRow, this.currentCol);
			updateReachableHighlights();
		}

		this.refreshStatusPanel();
	}

	@Override
	public void onCancel() {
		this.view.goTo(ViewsId.MAIN_MENU);
	}

	@Override
	public void onEndTurn() {
		clearSelection();
		this.game.endTurn();
		Unit nextUnit = this.game.findFirstUnitForCurrentArmy();
		if (nextUnit != null) {
			this.currentRow = nextUnit.getCoord().getX();
			this.currentCol = nextUnit.getCoord().getY();
			this.view.moveCameraTo(this.currentRow, this.currentCol);
		}
		this.refreshStatusPanel();
	}

	private void applyAttackOutcome(AttackOutcome outcome, int fromRow, int fromCol) {
		if (outcome.attackerLost()) {
			this.view.removeUnit(outcome.getAttackerRow(), outcome.getAttackerCol());
			this.view.unselectUnit(fromRow, fromCol);
		} else {
			this.view.removeUnit(outcome.getDefenderRow(), outcome.getDefenderCol());
			this.view.moveUnit(outcome.getAttackerRow(), outcome.getAttackerCol(), outcome.getDefenderRow(),
					outcome.getDefenderCol(), outcome.getAttackerStats());
			Unit attacker = this.game.getSelectedUnit();
			if (attacker == null) {
				this.view.unselectUnit(outcome.getDefenderRow(), outcome.getDefenderCol());
			} else {
				this.view.selectUnit(outcome.getDefenderRow(), outcome.getDefenderCol());
			}
		}

		if (this.game.isGameOver()) {
			this.view.goTo(ViewsId.GAME_OVER);
		}
	}

	private void clearSelection() {
		Unit selected = this.game.getSelectedUnit();
		if (selected != null) {
			Coordinates coord = selected.getCoord();
			this.view.unselectUnit(coord.getX(), coord.getY());
		}
		this.game.clearSelection();
		restoreReachableHighlights();
	}

	private void refreshStatusPanel() {
		Tile activeTile = this.game.getTileAt(new Coordinates(this.currentRow, this.currentCol));
		java.util.ArrayList<String> lines = new java.util.ArrayList<>();
		lines.add(this.game.getMatchLabel());
		lines.add(this.game.getTurnLabel());
		lines.add("Case active : %d, %d".formatted(this.currentCol, this.currentRow));
		lines.add(activeTile.getTilType().getDisplayLabel());
		lines.add(this.game.getArmyScoreLine());

		CombatReport lastCombat = this.game.getLastCombatReport();
		if (lastCombat != null) {
			lines.add(lastCombat.formatSituationLine());
		}

		Unit selected = this.game.getSelectedUnit();
		if (selected != null) {
			lines.add("Sélection : %s [%s]".formatted(selected.getName(), selected.getStatsLabel()));
			String typeDescr = selected.getUnitType().getDescr();
			if (typeDescr != null && !typeDescr.isBlank()) {
				lines.add(typeDescr);
			}
		}

		this.view.setStatusPanel(lines.toArray(String[]::new));
	}

	private void updateReachableHighlights() {
		restoreReachableHighlights();
		Unit selected = this.game.getSelectedUnit();
		if (selected == null || selected.getMvtRestants() <= 0) {
			return;
		}
		Map<Coordinates, Integer> reachable = this.moveManager.reachableCosts(selected.getCoord(),
				selected.getMvtRestants(), this.game.getMiddleEarth(), selected.getUnitType());
		for (Map.Entry<Coordinates, Integer> entry : reachable.entrySet()) {
			if (entry.getValue() > 0) {
				Coordinates coord = entry.getKey();
				Color base = this.baseTileColors.get(tileKey(coord.getX(), coord.getY()));
				if (base != null) {
					this.view.setHexColor(coord.getX(), coord.getY(), brighten(base));
				}
			}
		}
	}

	private void restoreReachableHighlights() {
		for (Map.Entry<String, Color> entry : this.baseTileColors.entrySet()) {
			String[] parts = entry.getKey().split(",");
			int row = Integer.parseInt(parts[0]);
			int col = Integer.parseInt(parts[1]);
			this.view.setHexColor(row, col, entry.getValue());
		}
	}

	private static String tileKey(int row, int col) {
		return row + "," + col;
	}

	private static Color brighten(Color base) {
		return new Color(Math.min(255, base.getRed() + 50), Math.min(255, base.getGreen() + 50),
				Math.min(255, base.getBlue() + 50));
	}

	private static Color tileColor(ETileType type) {
		return type.getCouleur();
	}
}
