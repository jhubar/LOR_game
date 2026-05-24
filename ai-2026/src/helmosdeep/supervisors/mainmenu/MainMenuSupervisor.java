package helmosdeep.supervisors.mainmenu;

import helmosdeep.domains.IGameFactory;
import helmosdeep.supervisors.ViewsId;
import helmosdeep.util.Contract;

/**
 * Superviseur chargé de gérer les interactions et l'affichage du menu principal
 * du jeu.
 * <p>
 * Cette classe implémente {@link MainMenuListener} pour réagir aux événements
 * du menu principal, comme la sélection d'un élément ou l'initialisation de la
 * vue.
 * </p>
 */
public class MainMenuSupervisor implements MainMenuListener {

	/** Vue associée à ce superviseur pour l'affichage et la navigation. */
	private MainMenuView view;
	private IGameFactory gameFactory;

	/**
	 * Construit un nouveau superviseur pour le menu principal.
	 * <p>
	 * Ajoute ce superviseur comme écouteur de la vue.
	 * </p>
	 * @param view la vue du menu principal à superviser
	 */
	public MainMenuSupervisor(MainMenuView view, IGameFactory factory) {
		Contract.require(factory != null, "Le paramètre factory ne peut pas être null.");
		this.view = view;
		this.view.addListener(this);
		this.gameFactory = factory;
	}

	/**
	 * Initialise les éléments du menu principal lors de son entrée à l'écran.
	 * <p>
	 * Définit les options disponibles dans le menu (ex: "Nouvelle partie",
	 * "Quitter").
	 * </p>
	 */
	@Override
	public void onViewEntered() {
		this.view.setItems("Nouvelle partie", "Quitter");
	}

	/**
	 * Réagit à la sélection d'un élément du menu principal par l'utilisateur.
	 * <p>
	 * Redirige vers la vue appropriée en fonction de l'index de l'élément
	 * sélectionné :
	 * <ul>
	 * <li>0 : Lance une nouvelle partie ({@link ViewsId#PLAY_GAME}).</li>
	 * <li>1 : Demande une confirmation pour quitter l'application.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param itemIndex l'index de l'élément sélectionné dans le menu
	 */
	@Override
	public void onItemSelected(int itemIndex) {
		if (itemIndex == 0) {
			this.gameFactory.newGame("resources/maps/level-1.txt");
			view.goTo(ViewsId.PLAY_GAME);
		}
		if (itemIndex == 1) {
			view.confirmQuit();
		}
	}
}
