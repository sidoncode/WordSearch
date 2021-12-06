package word.search;



import android.os.Bundle;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;

import net.codecanyon.trimax.android.wordsearchinnovation.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import word.search.platform.iap.ShoppingCallback;
import word.search.platform.iap.ShoppingItem;
import word.search.platform.iap.ShoppingItemBundle;
import word.search.platform.iap.ShoppingItemCoins;
import word.search.platform.iap.ShoppingItemRemoveAds;
import word.search.platform.iap.ShoppingProcessor;

public class IAPActivity extends AndroidApplication implements PurchasesUpdatedListener, BillingClientStateListener, ShoppingProcessor {


    //This must be the same ID as in Google Play Developer Console/In app products/Remove Ads ID.
    //Buradaki ID Google Play Developer Konsol/In app products/Remove Ads ID ile aynı olmalı.
    private final String SKU_REMOVE_ADS = "remove_ads";


    /**
     * Setup IAP items below. You can change anything you want as long as the sku id is the same as the id in google play
     * developer console/In app products item id. When the user opens the shopping dialog, prive info is received from
     * google play developer console and the list below is updated with this info. The item titles are in strings.properties files.
     * Note that google play does not allow renaming a sku id once it is created. Just change the quantities and ordering below, if you
     * change sku ids then you may run into trouble. You may delete any of them or add new ones as long as it is consistent with
     * in app products of google play console.
     *
     * Dükkan ürünlerini aşağıda düzenleyin. Aşağıdaki sku id'si ve google play developer consol/in app products/ürün id'si tutarlı
     * olmak zorunda. Dükkan diyaloğu açılınca aşağıdaki liste, google play'den gelen fiyat bilgisiyle güncellenip diyaloğa yansıtılır.
     * Ürün başlıklarını strings.properties dosyasında bulabilirsiniz. Uyarı: google play'de oluşturduğunuz ürünlerin id'lerini daha
     * sonra değiştirmeniz münkün değildir. Aşağıda sadece miktarları ve sıralamaları değiştirin, sku id'leri değiştirirseniz kendinizi sıkıntıya
     * sokabilirsiniz. İstemediklerinizi silebilir veya yenilerini ekleyebilirsiniz, böyle birşey yaparsanız listenin developer console/in products
     * ile tutarlı olduğundan emin olun.
     */
    private void setUpShoppingItems(){

        //remove ads(reklam kaldırma)
        items.add(new ShoppingItemRemoveAds(SKU_REMOVE_ADS,  "remove_ads"));

        //bundles(paket)
        items.add(new ShoppingItemBundle("bundle_mega", "bundle_mega", ShoppingItemBundle.Type.MEGA, 4000, 8, 8, 8));
        items.add(new ShoppingItemBundle("bundle_super",  "bundle_super", ShoppingItemBundle.Type.SUPER, 2000, 4,4,4));
        items.add(new ShoppingItemBundle("bundle_big",  "bundle_big", ShoppingItemBundle.Type.BIG, 1000, 2, 2, 2));

        //coins(jeton)
        items.add(new ShoppingItemCoins("coins_200",  "coins_200", 200));
        items.add(new ShoppingItemCoins("coins_500",  "coins_500", 500));
        items.add(new ShoppingItemCoins("coins_1000",  "coins_1000", 1000));
        items.add(new ShoppingItemCoins("coins_5000",  "coins_5000", 5000));
    }




    //////////////////////////////////////////////////////////////////////////////////////////////////


    private List<ShoppingItem> items = new ArrayList<>();
    private BillingClient billingClient;
    private ShoppingCallback shoppingCallback;
    private List<SkuDetails> skuDetailsList;
    protected boolean isInterstitialEnabled = true;
    protected boolean purchasedRemovedAds = true;
    private List<ShoppingItem> itemsToDisplay = new ArrayList<>();
    protected Runnable removeAdsPurchasedCommand;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public void init() {
        if(isIAPEnabled()) {
            setUpShoppingItems();
            Collections.reverse(items);
            billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
            billingClient.startConnection(this);
        }
    }





    @Override
    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
            queryPurchases();
            Log.d("iap", "billing service disconnected");
        }else{
            Log.d("iap", "error in billing service connection, error code:"+billingResult.getResponseCode());
        }
    }




    public void queryPurchases(){
        if(billingClient == null) return;

        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, new PurchasesResponseListener(){

            @Override
            public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
                if(list != null) handlePurchases(list);
            }
        });
    }




    private void handlePurchases(List<Purchase> purchases) {
        for(Purchase purchase : purchases){
            if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){
                handlePurchase(purchase);
            }
        }
    }




    void handlePurchase(Purchase purchase){
        if(purchase == null) return;
        Log.d("iap", "Found purchase:"+purchase.getSkus().get(0)+", purchase state:"+purchase.getPurchaseState());
        if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){

            boolean resetRemoveAdsPurchase = false;

            if(resetRemoveAdsPurchase) {
                ConsumeParams consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
                billingClient.consumeAsync(consumeParams, consumeResponseListener);
            }else{
                if(!purchase.isAcknowledged()) {

                    if (purchase.getSkus().get(0).equals(SKU_REMOVE_ADS)) {
                        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
                        billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
                    } else {
                        ConsumeParams consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
                        billingClient.consumeAsync(consumeParams, consumeResponseListener);
                    }
                    hasMadeAPurchase(purchase.getSkus().get(0), true);
                }else{
                    if (purchase.getSkus().get(0).equals(SKU_REMOVE_ADS)) {
                        hasMadeAPurchase(purchase.getSkus().get(0), false);
                    }
                }
            }
        }
    }





    private AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
        @Override
        public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
            Log.d("iap", "non-consumable purchase acknowledged, result:" + billingResult.getResponseCode()+", "+billingResult.getDebugMessage());
        }
    };




    private ConsumeResponseListener consumeResponseListener = new ConsumeResponseListener() {
        @Override
        public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
            Log.d("iap", "consumable purchase consumed, result:" + billingResult.getResponseCode()+", "+billingResult.getDebugMessage());
        }
    };





    @Override
    public void onBillingServiceDisconnected() {
        Log.d("iap", "billing service disconnected");
    }




    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            handlePurchases(purchases);
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d("iap", "IAP operation cancelled");
        } else {
            reportTransactionError(billingResult.getResponseCode());
            Log.d("iap", "IAP error on purchase, error code:"+billingResult.getResponseCode());
        }
    }




    @Override
    public boolean isIAPEnabled() {
        return getResources().getBoolean(R.bool.ENABLE_IAP);
    }




    @Override
    public void queryShoppingItems(ShoppingCallback callback) {
        shoppingCallback = callback;
        showProducts();
    }



    private void showProducts(){
        List<String> skuList = new ArrayList<>();

        for(ShoppingItem item : items){
            skuList.add(item.sku);
        }

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(params.build(), skuDetailsResponseListener);
    }




    SkuDetailsResponseListener skuDetailsResponseListener = new SkuDetailsResponseListener() {

        @Override
        public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
            if(list == null){
                reportItemRetrivalError(-100);
                return;
            }

            if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                returnShoppingItems(list);
            }else {
                reportItemRetrivalError(billingResult.getResponseCode());
            }
        }

    };




    public void returnShoppingItems(List<SkuDetails> list){
        if(list == null) {
            shoppingCallback.onShoppingItemsError(-1);
            return;
        }

        skuDetailsList = list;
        itemsToDisplay.clear();

        for(ShoppingItem offlineItem : items){
            inner:for(SkuDetails skuDetails : list){
                if(offlineItem.sku.equals(skuDetails.getSku())){
                    offlineItem.price = skuDetails.getPrice();
                    if(skuDetails.getSku().equals(SKU_REMOVE_ADS) && !isInterstitialEnabled && purchasedRemovedAds) break inner;
                    itemsToDisplay.add(offlineItem);
                    break inner;
                }
            }
        }
        shoppingCallback.onShoppingItemsReady(itemsToDisplay);
    }



    @Override
    public void reportItemRetrivalError(int code) {
        shoppingCallback.onShoppingItemsError(code);
    }



    @Override
    public void reportTransactionError(int code) {
        shoppingCallback.onTransactionError(code);
    }



    @Override
    public void makeAPurchase(String sku) {
        Gdx.app.log("iap", "purchase this:" + sku);
        startPurchase(sku);
    }




    public void startPurchase(String productId){
        Log.d("iap", "START PURCHASE, product id to purchase:."+productId);

        if(billingClient != null){
            for(SkuDetails skuDetails : skuDetailsList){
                if(skuDetails.getSku().equals(productId)){
                    BillingFlowParams flowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build();
                    Log.d("iap", "billingClient.isReady(): " + billingClient.isReady());
                    BillingResult result = billingClient.launchBillingFlow(this, flowParams);
                    if(result.getResponseCode() != BillingClient.BillingResponseCode.OK)
                        Log.d("iap", "Failed to start purchase, error code:"+result.getResponseCode()+", "+result.getDebugMessage());
                    break;
                }
            }
        }
    }





    @Override
    public void hasMadeAPurchase(String sku, boolean newPurchase) {
        if(sku.equals(SKU_REMOVE_ADS)) {
            purchasedRemovedAds = true;
            isInterstitialEnabled = false;
            if(removeAdsPurchasedCommand != null) removeAdsPurchasedCommand.run();
        }

        if(newPurchase && shoppingCallback != null) {
            shoppingCallback.onPurchase(sku);
            Log.d("iap", "has made a purchase:" + sku + ", new: " + newPurchase);
        }
    }



    @Override
    public boolean isRemoveAdsPurchased() {
        return purchasedRemovedAds;
    }


}
