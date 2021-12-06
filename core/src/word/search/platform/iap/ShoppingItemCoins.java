package word.search.platform.iap;

public class ShoppingItemCoins extends ShoppingItem{

    public int coins;

    public ShoppingItemCoins(String sku, String titleKey, int coins) {
        super(sku, titleKey);
        this.coins = coins;
    }


}
