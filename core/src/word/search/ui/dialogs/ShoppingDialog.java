package word.search.ui.dialogs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.util.List;

import word.search.actions.Interpolation;
import word.search.config.ColorConfig;
import word.search.config.UIConfig;
import word.search.graphics.AtlasRegions;
import word.search.graphics.NinePatches;
import word.search.managers.ResourceManager;
import word.search.model.Constants;
import word.search.model.Language;
import word.search.platform.iap.ShoppingItem;
import word.search.platform.iap.ShoppingItemBundle;
import word.search.platform.iap.ShoppingItemCoins;
import word.search.platform.iap.ShoppingItemRemoveAds;
import word.search.screens.BaseScreen;
import word.search.ui.Glitter;
import word.search.ui.game.buttons.DarkeningImageButton;
import word.search.ui.game.buttons.DarkeningTextButton;

public class ShoppingDialog extends Group implements BackNavigator {

    private Modal modal;
    private Runnable openCallback, closeCallback;
    private Group titleBar;
    private BaseScreen screen;
    private Group content = new Group();
    private Label.LabelStyle cardTitleStyle, coinCountStyle, hintCountStyle;
    private TextButton.TextButtonStyle bundleButtonStyle;
    private Color cardBgColor = new Color(0xede5d6ff);
    private float buttonScale = 0.75f;
    private Image loadingCircle;
    private Array<Group> groups = new Array<>();
    private float cardWidth;
    private float coinViewX, coinViewY;
    public boolean madeAPurchase;
    private List<ShoppingItem> shoppingItems;

    public ShoppingDialog(float width, float height, BaseScreen screen, Runnable openCallback, Runnable closeCallback) {
        setSize(width, height);

        this.screen = screen;
        this.openCallback = openCallback;
        this.closeCallback = closeCallback;
        notifyNavigationController(screen);

        modal = new Modal(getWidth(), getHeight());
        modal.getColor().a = 0;
        addActor(modal);

        modal.addAction(Actions.sequence(
            Actions.fadeIn(0.3f),
            Actions.run(fadeInEnd)
        ));

        screen.modalOpened();
    }



    private Runnable fadeInEnd = new Runnable() {
        @Override
        public void run() {
            loadingCircle = new Image(AtlasRegions.loading);
            loadingCircle.setX((getWidth() - loadingCircle.getWidth()) * 0.5f);
            loadingCircle.setY((getHeight() - loadingCircle.getHeight()) * 0.5f);
            loadingCircle.setOrigin(Align.center);
            addActor(loadingCircle);
            loadingCircle.addAction(Actions.forever(Actions.rotateBy(360, 2f)));
            openCallback.run();
        }
    };



    public void setShoppingItems(List<ShoppingItem> items){
        this.shoppingItems = items;
        titleBar = new Group();
        titleBar.getColor().a = 0;
        addActor(titleBar);

        Image titleBarBg = new Image(AtlasRegions.tent);
        titleBarBg.setScaleX(getWidth() / titleBarBg.getWidth());

        titleBar.setSize(titleBarBg.getWidth() * titleBarBg.getScaleX(), titleBarBg.getHeight());
        titleBar.addActor(titleBarBg);
        titleBar.setY(getHeight() - titleBarBg.getHeight());

        float bottom = 80f;

        Vector2 vec2 = screen.hud.coinView.localToActorCoordinates(titleBar, new Vector2());

        coinViewX = screen.hud.coinView.getX();
        coinViewY = screen.hud.coinView.getY();

        screen.hud.coinView.remove();
        screen.hud.coinView.plus.setTouchable(Touchable.disabled);
        screen.hud.coinView.setPosition(vec2.x, bottom + (titleBar.getHeight() - screen.hud.coinView.getHeight()) * 0.5f);
        titleBar.addActor(screen.hud.coinView);

        DarkeningImageButton back = new DarkeningImageButton(new TextureRegionDrawable(AtlasRegions.btn_back_iap));
        back.setX(getWidth() - (screen.hud.coinView.getX() + screen.hud.coinView.getWidth()));
        back.setY(bottom + (titleBar.getHeight() - back.getHeight()) * 0.5f);
        back.addListener(closer);
        titleBar.addActor(back);

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = screen.wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class);

        Label label = new Label(Language.get("shop"), style);
        label.setX((titleBar.getWidth() - label.getWidth()) * 0.5f);
        label.setY(bottom + (titleBar.getHeight() - label.getHeight()) * 0.5f);
        titleBar.addActor(label);
        titleBar.addAction(Actions.sequence(Actions.fadeIn(0.25f)));

        float aspectRatio = Constants.GAME_WIDTH / Constants.GAME_HEIGHT;
        cardWidth = Constants.GAME_HEIGHT * aspectRatio * 0.85f;
        content.setWidth(getWidth());

        initStyles();
        populate(items);
    }



    public ShoppingItem getShoppingItemBySKU(String sku){
        for(ShoppingItem item : shoppingItems){
            if(item.sku.equals(sku))return item;
        }
        return null;
    }



    private void initStyles(){
        cardTitleStyle = new Label.LabelStyle();
        cardTitleStyle.font = screen.wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class);

        bundleButtonStyle = new TextButton.TextButtonStyle();
        bundleButtonStyle.font = screen.wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class);
        bundleButtonStyle.up = new NinePatchDrawable(NinePatches.btn_green_large);
        bundleButtonStyle.down = bundleButtonStyle.up;

        coinCountStyle = new Label.LabelStyle();
        coinCountStyle.font = screen.wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class);
        coinCountStyle.fontColor = new Color(0xffefd5ff);

        hintCountStyle = new Label.LabelStyle();
        hintCountStyle.font = screen.wordGame.resourceManager.get(ResourceManager.fontSignikaBold, BitmapFont.class);
        hintCountStyle.fontColor = ColorConfig.DIALOG_TEXT_COLOR;
    }






    private void populate(List<ShoppingItem> items){
        float x = (content.getWidth() - cardWidth) * 0.5f;
        float y = 30;

        for(int i = 0; i < items.size(); i++){
            Group group = null;
            ShoppingItem item = items.get(i);

            if(item instanceof ShoppingItemBundle) group = createBundleBox((ShoppingItemBundle)item);
            else if(item instanceof ShoppingItemRemoveAds) group = createRemoveAdsBox((ShoppingItemRemoveAds)item);
            else if(item instanceof ShoppingItemCoins) group = createCoinBox((ShoppingItemCoins)item);

            group.setX(x);
            group.setY(y);
            y += group.getHeight() + 30;

            groups.add(group);
            content.addActor(group);
            if(i == items.size() - 1) content.setHeight(group.getY() + group.getHeight());
        }

        ScrollPane pane = new ScrollPane(content);
        pane.setScrollbarsVisible(false);
        pane.setSize(getWidth(), titleBar.getY() - 30);
        addActor(pane);

        loadingCircle.remove();

        animateGroupsIn();
    }




    private Group createRemoveAdsBox(ShoppingItemRemoveAds item){
        Group row = new Group();

        Image bg = new Image(NinePatches.iap_card);
        bg.setWidth(cardWidth);
        bg.setHeight(150);
        bg.setColor(cardBgColor);
        row.setWidth(bg.getWidth());
        row.setHeight(bg.getHeight());
        row.addActor(bg);

        float marginH = 40;
        Image icon = new Image(AtlasRegions.noads);
        icon.setX(marginH);
        icon.setY((row.getHeight() - icon.getHeight()) * 0.5f);
        row.addActor(icon);

        DarkeningTextButton btnBuy = new DarkeningTextButton(item.price, bundleButtonStyle);
        btnBuy.getLabelCell().padBottom(UIConfig.BUTTON_BOTTOM_PADDING);
        btnBuy.setName(item.sku);
        btnBuy.setTransform(true);
        btnBuy.setScale(buttonScale);
        btnBuy.addListener(makePurchase);
        btnBuy.setX(row.getWidth() - btnBuy.getWidth() * btnBuy.getScaleX() - marginH);
        btnBuy.setY((row.getHeight() - btnBuy.getHeight() * btnBuy.getScaleY()) * 0.5f);
        row.addActor(btnBuy);

        Label label = new Label(Language.get(item.titleKey), hintCountStyle);
        float iconRight = icon.getX() + icon.getWidth();
        float dx = (btnBuy.getX()) - (iconRight);
        float maxWidth = dx * 0.9f;
        if(label.getWidth() > maxWidth) label.setFontScale(maxWidth / label.getWidth());

        label.setX(iconRight + (dx - label.getWidth() * label.getFontScaleX()) * 0.5f);
        label.setY((row.getHeight() - label.getHeight()) * 0.5f);
        row.addActor(label);

        row.setOrigin(Align.center);
        row.setScale(0);

        return row;
    }





    private Group createBundleBox(ShoppingItemBundle item) {
        Group row = new Group();

        Image bg = new Image(NinePatches.iap_card_half);

        bg.setWidth(cardWidth);
        bg.setHeight(300);
        bg.setColor(cardBgColor);
        row.setWidth(bg.getWidth());
        row.addActor(bg);

        Image title = new Image(NinePatches.iap_card_title);
        title.setWidth(bg.getWidth());
        title.setY(bg.getHeight());
        row.addActor(title);
        row.setHeight(title.getY() + title.getHeight());

        Label lblTitle = new Label(Language.get(item.titleKey), cardTitleStyle);
        lblTitle.setX((title.getWidth() - lblTitle.getWidth()) * 0.5f);
        lblTitle.setY(310);
        row.addActor(lblTitle);

        float marginLeft = 80;

        Image coinBg = new Image(AtlasRegions.coin_bg);
        coinBg.setPosition(marginLeft, 200);
        row.addActor(coinBg);

        Image coin = new Image(AtlasRegions.iap_coin);
        coin.setX(coinBg.getX() - coin.getWidth() * 0.5f);
        coin.setY(coinBg.getY() - (coin.getHeight() - coinBg.getHeight()) * 0.5f);
        row.addActor(coin);

        Label lblCoins = new Label("x" + String.valueOf(item.coins), coinCountStyle);
        lblCoins.setFontScale(0.9f);
        lblCoins.setOrigin(Align.bottom);
        lblCoins.setX(coin.getX() + coin.getWidth() + 20);
        lblCoins.setY(coinBg.getY() + (coinBg.getHeight() - lblCoins.getHeight()) * 0.6f);
        row.addActor(lblCoins);

        float vSpacing = 20f;
        float qScale = 0.8f;

        Image icMagic = new Image(AtlasRegions.ic_magic_wand);
        icMagic.setX(coin.getX());
        icMagic.setY(coin.getY() - icMagic.getHeight() - vSpacing);
        row.addActor(icMagic);

        Label lblMagic = new Label("x" + String.valueOf(item.magicWands), hintCountStyle);
        lblMagic.setAlignment(Align.left);
        lblMagic.setFontScale(qScale);
        lblMagic.setX(icMagic.getX() + icMagic.getWidth() + 20);
        lblMagic.setY(icMagic.getY());
        row.addActor(lblMagic);

        Image icMagnifier = new Image(AtlasRegions.ic_magnifier);
        icMagnifier.setX(coin.getX());
        icMagnifier.setY(icMagic.getY() - icMagnifier.getHeight() - vSpacing);
        row.addActor(icMagnifier);

        Label lblMagnifier = new Label("x" + String.valueOf(item.magnifiers), hintCountStyle);
        lblMagnifier.setFontScale(qScale);
        lblMagnifier.setX(lblMagic.getX());
        lblMagnifier.setY(icMagnifier.getY());
        row.addActor(lblMagnifier);

        Image icBulb = new Image(AtlasRegions.ic_bulb);
        icBulb.setX(lblMagic.getX() + lblMagic.getWidth() + 40);
        icBulb.setY(icMagic.getY());
        row.addActor(icBulb);

        Label lblBulb = new Label("x" + String.valueOf(item.bulbs), hintCountStyle);
        lblBulb.setFontScale(qScale);
        lblBulb.setX(icBulb.getX() + icBulb.getWidth() + 10);
        lblBulb.setY(icBulb.getY());
        row.addActor(lblBulb);

        DarkeningTextButton btnBuy = new DarkeningTextButton(item.price, bundleButtonStyle);
        btnBuy.getLabelCell().padBottom(UIConfig.BUTTON_BOTTOM_PADDING);
        btnBuy.setName(item.sku);
        btnBuy.setTransform(true);
        btnBuy.setScale(buttonScale);
        btnBuy.addListener(makePurchase);
        float contentRight = coinBg.getX() + coinBg.getWidth();
        float dx = row.getWidth() - contentRight;
        btnBuy.setX(contentRight + (dx - btnBuy.getWidth() * btnBuy.getScaleX()) * 0.5f);
        btnBuy.setY(30);
        row.addActor(btnBuy);

        Image image = new Image(item.type.image);
        image.setX(contentRight + (dx - image.getWidth()) * 0.5f);
        image.setY(btnBuy.getY() + btnBuy.getHeight() * btnBuy.getScaleY() + 20);
        row.addActor(image);

        float x = getX() + row.getX() + image.getX();
        float y = getY() + row.getY() + image.getY();

        for(int i = 0; i < 5; i++){
            Glitter glitter = new Glitter(x, y, image.getWidth(), image.getHeight());
            row.addActor(glitter);
            glitter.running = true;
        }

        row.setOrigin(Align.center);
        row.setScale(0);

        return row;
    }




    private Group createCoinBox(ShoppingItemCoins item){
        Group row = new Group();

        Image bg = new Image(NinePatches.iap_card);
        bg.setWidth(cardWidth);
        bg.setHeight(150);
        bg.setColor(cardBgColor);
        row.setWidth(bg.getWidth());
        row.setHeight(bg.getHeight());
        row.addActor(bg);

        float marginLeft = 80;

        Image coinBg = new Image(AtlasRegions.coin_bg);
        coinBg.setPosition(marginLeft, (row.getHeight() - coinBg.getHeight()) * 0.5f);
        row.addActor(coinBg);

        Image coin = new Image(AtlasRegions.iap_coins);
        coin.setX(coinBg.getX() - coin.getWidth() * 0.5f);
        coin.setY(coinBg.getY() - (coin.getHeight() - coinBg.getHeight()) * 0.5f);
        row.addActor(coin);

        Label lblCoins = new Label(Language.get(item.titleKey), coinCountStyle);
        lblCoins.setFontScale(0.9f);
        lblCoins.setX(coin.getX() + coin.getWidth() + 20);
        lblCoins.setY(coinBg.getY() + (coinBg.getHeight() - lblCoins.getHeight()) * 0.6f);
        row.addActor(lblCoins);

        DarkeningTextButton btnBuy = new DarkeningTextButton(item.price, bundleButtonStyle);
        btnBuy.getLabelCell().padBottom(UIConfig.BUTTON_BOTTOM_PADDING);
        btnBuy.setName(item.sku);
        btnBuy.setTransform(true);
        btnBuy.setScale(buttonScale);
        btnBuy.addListener(makePurchase);
        btnBuy.setX(row.getWidth() - btnBuy.getWidth() * btnBuy.getScaleX() - 40);
        btnBuy.setY((row.getHeight() - btnBuy.getHeight() * btnBuy.getScaleY()) * 0.5f);
        row.addActor(btnBuy);

        row.setOrigin(Align.center);
        row.setScale(0);

        return row;
    }




    private ChangeListener makePurchase = new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            screen.wordGame.shoppingProcessor.makeAPurchase(actor.getName());
        }
    };




    private void animateGroupsIn(){
        for(int i = groups.size - 1; i >= 0; i--){
            SequenceAction actions = Actions.sequence(
                    Actions.delay(0.1f * (float)(groups.size - i)),
                    Actions.scaleTo(1f, 1f, 0.3f, Interpolation.backOut)
            );

            if(i == 0) actions.addAction(Actions.run(animateInEnd));
            groups.get(i).addAction(actions);
        }
    }



    private Runnable animateInEnd = new Runnable() {
        @Override
        public void run() {
            getStage().getRoot().setTouchable(Touchable.enabled);
        }
    };




    private ChangeListener closer = new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            getStage().getRoot().setTouchable(Touchable.disabled);

            for(int i = 0; i < groups.size; i++){
                SequenceAction actions = Actions.sequence(
                        Actions.delay(0.1f * (float)(i)),
                        Actions.scaleTo(0, 0, 0.4f, Interpolation.backIn)
                );
                if(i == groups.size - 1) actions.addAction(Actions.run(closerEnd));
                groups.get(i).addAction(actions);
            }
        }
    };



    private Runnable closerEnd = new Runnable() {
        @Override
        public void run() {
            screen.hud.coinView.remove();
            screen.hud.coinView.setPosition(coinViewX, coinViewY);
            screen.stage.addActor(screen.hud.coinView);
            float time = 0.25f;
            modal.addAction(Actions.fadeOut(time));

            titleBar.addAction(Actions.sequence(
                    Actions.fadeOut(time),
                    Actions.run(closeDelay)
            ));


        }
    };


    private Runnable closeDelay = new Runnable() {
        @Override
        public void run() {
            closeCallback.run();
            screen.modalClosed();
        }
    };



    public void close(){
        closer.changed(null, null);
    }



    public void onTransactionError(int code){
        screen.hud.showAlertDialog(Language.get("iap_error"), Language.format("iap_error_text", code), Language.get("ok"), null);
    }



    @Override
    public void notifyNavigationController(BaseScreen screen) {
        screen.backNavQueue.push(this);
    }



    @Override
    public boolean navigateBack() {
        closer.changed(null, null);
        return true;
    }
}
