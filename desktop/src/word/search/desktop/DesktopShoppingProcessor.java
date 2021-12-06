package word.search.desktop;

import java.util.ArrayList;
import java.util.List;

import word.search.platform.iap.ShoppingCallback;
import word.search.platform.iap.ShoppingItem;
import word.search.platform.iap.ShoppingItemBundle;
import word.search.platform.iap.ShoppingItemCoins;
import word.search.platform.iap.ShoppingItemRemoveAds;
import word.search.platform.iap.ShoppingProcessor;

public class DesktopShoppingProcessor implements ShoppingProcessor {


    private ShoppingCallback callback;


    @Override
    public void init() {

    }

    @Override
    public boolean isIAPEnabled() {
        return true;
    }

    private String removeAdsSKU = "remove_ads";

    @Override
    public void queryShoppingItems(ShoppingCallback callback) {
        this.callback = callback;

        List<ShoppingItem> items = new ArrayList<>();

        //bundles(paket)
        items.add(new ShoppingItemBundle("bundle_big",  "bundle_big", ShoppingItemBundle.Type.BIG, 1000, 2, 2, 2));
        items.add(new ShoppingItemBundle("bundle_super",  "bundle_super", ShoppingItemBundle.Type.SUPER, 2000, 4,4,4));
        items.add(new ShoppingItemBundle("bundle_mega", "bundle_mega", ShoppingItemBundle.Type.MEGA, 4000, 8, 8, 8));

        //remove ads(reklam kaldırma)
        items.add(new ShoppingItemRemoveAds(removeAdsSKU,  "remove_ads"));

        //coins(jeton)
        items.add(new ShoppingItemCoins("coins_200",  "coins_200", 200));
        items.add(new ShoppingItemCoins("coins_500",  "coins_500", 500));
        items.add(new ShoppingItemCoins("coins_1000",  "coins_1000", 1000));
        items.add(new ShoppingItemCoins("coins_5000",  "coins_5000", 5000));

        callback.onShoppingItemsReady(items);
        //callback.onShoppingItemsError("No shopping items found");

    }

    @Override
    public void reportItemRetrivalError(int code) {

    }

    @Override
    public void reportTransactionError(int code) {

    }

    @Override
    public void makeAPurchase(String sku) {

    }

    @Override
    public void hasMadeAPurchase(String sku, boolean newPurchase) {

    }

    @Override
    public boolean isRemoveAdsPurchased() {
        return false;
    }


}
