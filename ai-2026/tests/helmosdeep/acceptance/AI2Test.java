package helmosdeep.acceptance;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.KeyEvent;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SuiteDisplayName;

import helmosdeep.HelmosDeep;
import helmosdeep.supervisors.ViewsId;
import helmosdeep.views.GameScene;

@SuiteDisplayName("Tests d'acceptation AI-2")
class AI2Test {
	private GameloopFixture mainWindow;

	@BeforeEach
	public void setUp() {
		var innerLoop = HelmosDeep.makeGameLoop();
		mainWindow = new GameloopFixture(innerLoop);
		innerLoop.run();
	}

	@AfterEach
	public void tearDown() {
		mainWindow.cleanUp();

	}
	
	@DisplayName("TA 2.1")
	@Test
	void should_select_active_hex_when_sauron_is_and_display_hex_info_on_game_started() {
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
		
		var scene = mainWindow.<GameScene>scene(ViewsId.PLAY_GAME, GameScene.class);
		
		assertTrue(scene.checkActiveHex( ht -> ht.getId().endsWith("08-04")), 
				"You should have moved the camera to row 4 and col 8");
		assertTrue(scene.hasUnitAt(4, 8), 
				"Sauron should be at row 8 and col 4");
		assertTrue(scene.checkUnitAt( ut -> ut.hasName("Sauron"), 4, 8), 
				"Sauron should appear here");
		
		
		var statusPanel = mainWindow.panel("Situation");
		var firstLabel = statusPanel.label("Situation[0]");
		var secondLabel = statusPanel.label("Situation[1]");
		var thirdLabel = statusPanel.label("Situation[2]");
		var fourthLabel = statusPanel.label("Situation[3]");
		
		firstLabel.requireText(Pattern.compile("Le Mordor vs les Hommes", Pattern.CASE_INSENSITIVE));
		secondLabel.requireText(Pattern.compile("Au tour du Mordor", Pattern.CASE_INSENSITIVE));
		thirdLabel.requireText(Pattern.compile("^.*8.*4.*$"));
		fourthLabel.requireText(Pattern.compile("^.*MONTAGNE\s*\\[3\\].*$"));		
	}
	
	@DisplayName("TA 2.2")
	@Test
	void should_update_hex_info_on_game_left_move() {
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
		
		var scene = mainWindow.<GameScene>scene(ViewsId.PLAY_GAME, GameScene.class);
		
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_LEFT);
		
		assertTrue(scene.checkActiveHex( ht -> ht.getId().endsWith("07-04")), 
				"You should have moved the camera to row 4 and col 7");
		
		
		var statusPanel = mainWindow.panel("Situation");
		var firstLabel = statusPanel.label("Situation[0]");
		var secondLabel = statusPanel.label("Situation[1]");
		var thirdLabel = statusPanel.label("Situation[2]");
		var fourthLabel = statusPanel.label("Situation[3]");
		
		firstLabel.requireText(Pattern.compile("Le Mordor vs les Hommes", Pattern.CASE_INSENSITIVE));
		secondLabel.requireText(Pattern.compile("Au tour du Mordor", Pattern.CASE_INSENSITIVE));
		thirdLabel.requireText(Pattern.compile("^.*7.*4.*$"));
		fourthLabel.requireText(Pattern.compile("^.*PLAINE\s*\\[1\\].*$"));		
	}
	
	@DisplayName("TA 2.3")
	@Test
	void should_update_hex_info_on_game_up_move() {
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
		
		var scene = mainWindow.<GameScene>scene(ViewsId.PLAY_GAME, GameScene.class);
		
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_UP);
		
		assertTrue(scene.checkActiveHex( ht -> ht.getId().endsWith("08-03")), 
				"You should have moved the camera to row 3 and col 8");
		
		
		var statusPanel = mainWindow.panel("Situation");
		var firstLabel = statusPanel.label("Situation[0]");
		var secondLabel = statusPanel.label("Situation[1]");
		var thirdLabel = statusPanel.label("Situation[2]");
		var fourthLabel = statusPanel.label("Situation[3]");
		
		firstLabel.requireText(Pattern.compile("Le Mordor vs les Hommes", Pattern.CASE_INSENSITIVE));
		secondLabel.requireText(Pattern.compile("Au tour du Mordor", Pattern.CASE_INSENSITIVE));
		thirdLabel.requireText(Pattern.compile("^.*8.*3.*$"));
		fourthLabel.requireText(Pattern.compile("^.*PLAINE\s*\\[1\\].*$"));		
	}
	
	@DisplayName("TA 2.4")
	@Test
	void should_update_hex_info_on_many_moves() {
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_SPACE);
		
		var scene = mainWindow.<GameScene>scene(ViewsId.PLAY_GAME, GameScene.class);
		
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_UP);
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_UP);
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_LEFT);
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_LEFT);
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_DOWN);
		mainWindow.pressAndReleaseKeys(KeyEvent.VK_RIGHT);
		
		assertTrue(scene.checkActiveHex( ht -> ht.getId().endsWith("07-03")), 
				"You should have moved the camera to row 3 and col 7");
		
		var statusPanel = mainWindow.panel("Situation");
		var firstLabel = statusPanel.label("Situation[0]");
		var secondLabel = statusPanel.label("Situation[1]");
		var thirdLabel = statusPanel.label("Situation[2]");
		var fourthLabel = statusPanel.label("Situation[3]");
		
		firstLabel.requireText(Pattern.compile("Le Mordor vs les Hommes", Pattern.CASE_INSENSITIVE));
		secondLabel.requireText(Pattern.compile("Au tour du Mordor", Pattern.CASE_INSENSITIVE));
		thirdLabel.requireText(Pattern.compile("^.*7.*3.*$"));
		fourthLabel.requireText(Pattern.compile("^.*FOR.T\s*\\[2\\].*$"));		
		
		assertTrue(scene.hasUnitAt(3, 7), 
				"Orcs should be at row 3 and col 7");
		assertTrue(scene.checkUnitAt( ut -> ut.hasName("Orcs"), 3, 7), 
				"Orcs should appear here");
	}
}
