package word.search.platform.iap;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import word.search.app;
import word.search.graphics.AtlasRegions;

public class ShoppingItemBundle extends ShoppingItemCoins{

    public enum Type{
        BIG(AtlasRegions.iap_bundle_big),
        SUPER(AtlasRegions.iap_bundle_super),
        MEGA(AtlasRegions.iap_bundle_mega);

        public TextureAtlas.AtlasRegion image;

        Type(TextureAtlas.AtlasRegion image){
            this.image = image;
        }
    }


    public Type type;
    public int bulbs, magnifiers, magicWands;




    public ShoppingItemBundle(String sku, String titleKey, Type type, int coins, int bulbs, int magnifiers, int magicWands) {
        super(sku, titleKey, coins);

        this.type = type;
        this.bulbs = bulbs;
        this.magnifiers = magnifiers;
        this.magicWands = magicWands;
    }


}
