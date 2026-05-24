package helmosdeep.supervisors.gameover;

import java.awt.Color;

import helmosdeep.domains.HelmosDeepGame;
import helmosdeep.domains.IGameFactory;
import helmosdeep.supervisors.ViewsId;
import helmosdeep.util.Contract;

/**
 * Superviseur chargé de gérer les interactions et l'affichage
 * de la vue de fin de partie.
 */
public final class GameOverSupervisor implements GameOverListener {

	private final GameOverView view;
	private final IGameFactory gameFactory;

	public GameOverSupervisor(GameOverView view, IGameFactory gameFactory) {
		this.view = view;
		Contract.require(gameFactory != null, "Le paramètre gameFactory ne peut pas être null.");
		this.gameFactory = gameFactory;
	}

	@Override
	public void onViewEntered() {
		HelmosDeepGame game = this.gameFactory.getcurrentGame();
		if (game == null) {
			view.setLeftPanel(new Color(46, 88, 148, 200), "Armée des hommes", "Partie introuvable.");
			view.setRightPanel(new Color(194, 103, 165, 200), "Armée du mordor", "Partie introuvable.");
			view.setBottomPanel(new Color(50, 130, 84, 200), "Moments forts", "Aucune donnée.");
			return;
		}

		view.setLeftPanel(new Color(46, 88, 148, 200), "Armée des Hommes", game.getHumansEndGameLines());
		view.setRightPanel(new Color(194, 103, 165, 200), "Armée du Mordor", game.getMordorEndGameLines());

		java.util.ArrayList<String> bottomLines = new java.util.ArrayList<>();
		bottomLines.add(game.getWinnerLabel());
		for (String highlight : game.getCombatHighlights()) {
			bottomLines.add(highlight);
		}
		view.setBottomPanel(new Color(50, 130, 84, 200), "Moments forts",
				bottomLines.toArray(String[]::new));
	}

	@Override
	public void onAction() {
		view.goTo(ViewsId.MAIN_MENU);
	}
}
