package helmosdeep.domains;

import helmosdeep.util.TxtFileReader;

public class HelmosDeepGameFactory implements IGameFactory {

	private HelmosDeepGame game;

	@Override
	public void newGame(String mapFilePath) {
		String gameDescription = TxtFileReader.getContent(mapFilePath);
		this.game = new HelmosDeepGame(gameDescription);
	}

	@Override
	public HelmosDeepGame getcurrentGame() {
		return this.game;
	}
}
