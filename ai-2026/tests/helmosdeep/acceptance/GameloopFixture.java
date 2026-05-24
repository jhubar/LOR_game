package helmosdeep.acceptance;

import java.awt.Frame;

import javax.swing.JFrame;

import org.fest.swing.fixture.FrameFixture;

import ai.engine.core.AbstractScene;

public class GameloopFixture extends FrameFixture {

	public GameloopFixture(Frame target) {
		super(target);
	}
	
	public <T extends AbstractScene> T scene(String sceneName, Class<T> clazz) {
		
		if(!(component() instanceof JFrame frame)) {
			throw new IllegalStateException("This loop has no content pane");
		}
		
		var contentPane = frame.getContentPane();
		for(int i=0; i < contentPane.getComponentCount(); ++i) {
			var c = contentPane.getComponent(i);
			
			if(sceneName.equalsIgnoreCase(c.getName())) {
				if(clazz.isInstance(c)) {
					return clazz.cast(c);
				}
			}
		}
		
		throw new IllegalArgumentException("Unable to find scene "+sceneName);
	}

}
