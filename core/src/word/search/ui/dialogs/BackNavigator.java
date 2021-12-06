package word.search.ui.dialogs;


import word.search.screens.BaseScreen;

public interface BackNavigator {

    void notifyNavigationController(BaseScreen screen);
    boolean navigateBack();
}
