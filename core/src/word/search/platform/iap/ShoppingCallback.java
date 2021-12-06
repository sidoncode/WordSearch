package word.search.platform.iap;

import java.util.List;

public interface ShoppingCallback {

    void onShoppingItemsReady(List<ShoppingItem> items);
    void onShoppingItemsError(int code);
    void onTransactionError(int code);
    void onPurchase(String sku);
}
