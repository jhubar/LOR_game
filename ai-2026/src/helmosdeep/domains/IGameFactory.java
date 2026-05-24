package helmosdeep.domains;

public interface IGameFactory {

	void newGame(String mapFilePath);

	HelmosDeepGame getcurrentGame();

}
