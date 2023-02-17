package word.search.desktop;

import word.search.platform.MenuConfig;

public class MenuConfigDesktop implements MenuConfig {



    @Override
    public boolean rateUsEnabled() {
        return false;
    }

    @Override
    public boolean showPrivacyDialogOnFirstRun() {
        return true;
    }

    @Override
    public boolean showPrivacyDialogInSettingsDialog() {
        return true;
    }

    @Override
    public boolean termsOfUseLinkAvailable() {
        return true;
    }

    @Override
    public boolean appShareEnabled() {
        return false;
    }

    @Override
    public boolean emailSupportEnabled() {
        return false;
    }
}
