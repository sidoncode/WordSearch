package word.search.platform.iap;

public interface ShoppingProcessor {

    void init();
    boolean isIAPEnabled();

    void queryShoppingItems(ShoppingCallback callback);//SkuDetails

    void reportItemRetrivalError(int code);
    void reportTransactionError(int code);

    void makeAPurchase(String sku);

    void hasMadeAPurchase(String sku, boolean newPurchase);

    boolean isRemoveAdsPurchased();


}
