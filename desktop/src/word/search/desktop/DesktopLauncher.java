package word.search.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.util.HashMap;
import java.util.Map;


import word.search.WordGame;
import word.search.platform.AppEvents;
import word.search.platform.dict.WordMeaningProvider;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();


		config.width = 366;//480;//;
		config.height = 650;//640;//;
		config.y = 0;




		Map<String, WordMeaningProvider> provider = new HashMap<>();
		provider.put("en", new WordMeaningProviderDesktop());

		WordGame wordGame = new WordGame(new NetworkDesktop(), provider);
		WordGame.analytics = new FirebaseAnalyticsImp();
		wordGame.adManager = new DesktopAdManager();
		wordGame.menuConfig = new MenuConfigDesktop();
		wordGame.appEvents = new AppEvents() {
			@Override
			public void exitApp() {
				Gdx.app.exit();
			}

			@Override
			public void toggleFullScreen() {

			}


		};
		wordGame.preloaderInterface = new PreloaderInterfaceDesktop();

		new LwjglApplication(wordGame, config);
	}
}
