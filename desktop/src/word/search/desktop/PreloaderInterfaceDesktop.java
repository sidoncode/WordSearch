package word.search.desktop;

import word.search.platform.PreloaderInterface;

public class PreloaderInterfaceDesktop implements PreloaderInterface {

    @Override
    public void preloadBundle(String bundle, Callback callback) {
        callback.onBundlePreloaded(bundle);
    }
}
