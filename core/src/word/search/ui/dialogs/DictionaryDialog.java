package word.search.ui.dialogs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;


import word.search.WordGame;
import word.search.config.ColorConfig;
import word.search.config.GameConfig;
import word.search.graphics.AtlasRegions;
import word.search.graphics.NinePatches;
import word.search.managers.ResourceManager;
import word.search.model.Constants;
import word.search.model.Language;
import word.search.platform.analytics.AnalyticsEvent;
import word.search.platform.analytics.AnalyticsParam;
import word.search.platform.dict.WordMeaningProvider;
import word.search.platform.dict.WordMeaningRequest;
import word.search.screens.BaseScreen;


public class DictionaryDialog extends BaseDialog {

    private ClippingGroup mask;
    private Group allWords = new Group();
    private int index = 0;
    private ImageButton leftArrow;
    private ImageButton rightArrow;
    private ScrollPane.ScrollPaneStyle paneStyle = new ScrollPane.ScrollPaneStyle();
    private Label.LabelStyle wordTitlelabelStyle;
    private Label.LabelStyle meaninglabelStyle;
    private Image background;
    public static String[] words;
    private Rectangle clipBounds = new Rectangle(0,0,0,0);
    private Label page;


    public DictionaryDialog(BaseScreen screen) {
        super(screen);

        float aspectRatio = Constants.GAME_WIDTH / Constants.GAME_HEIGHT;
        content.setSize(screen.stage.getHeight() * aspectRatio * 0.75f, screen.stage.getHeight() * 0.65f);
        setContentBackground();

        String font1 = ResourceManager.fontSignikaBoldShadow;
        wordTitlelabelStyle = new Label.LabelStyle(screen.wordGame.resourceManager.get(font1, BitmapFont.class), Color.WHITE);

        meaninglabelStyle = new Label.LabelStyle(screen.wordGame.resourceManager.get(font1, BitmapFont.class), Color.WHITE);

        setTitleLabel(Language.get("dictionary"));
        setCloseButton();

        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getStage().getRoot().setTouchable(Touchable.disabled);
                hide();
            }
        });


        float maskWidth = content.getWidth() - 70f;
        float marginLeft = (content.getWidth() - maskWidth) * 0.5f;

        leftArrow = dirButton(true);
        leftArrow.addListener(onClick);
        leftArrow.setOrigin(Align.center);
        leftArrow.setX(marginLeft);
        leftArrow.setY(90f);
        content.addActor(leftArrow);

        rightArrow = dirButton(false);
        rightArrow.addListener(onClick);
        rightArrow.setX(content.getWidth() - marginLeft - rightArrow.getWidth());
        rightArrow.setY(leftArrow.getY());
        content.addActor(rightArrow);

        mask = new ClippingGroup();
        mask.setSize(maskWidth, content.getHeight() * 0.65f);
        mask.setX((content.getWidth() - mask.getWidth()) * 0.5f);
        mask.setY(300);

        if(background == null){
            background = new Image(NinePatches.iap_content);
            background.setSize(mask.getWidth(), mask.getHeight() );
            background.setColor(new Color(0xB3A996ff));
            background.setPosition(mask.getX(), mask.getY());
            content.addActor(background);
        }
        content.addActor(mask);

        clipBounds.x = mask.getX();
        clipBounds.y = mask.getY();
        clipBounds.width = mask.getWidth();
        clipBounds.height = mask.getHeight();

        Image logo = new Image(AtlasRegions.wordnik_badge_a1);
        logo.setX((content.getWidth() - logo.getWidth()) * 0.5f);
        logo.setY(mask.getY() - logo.getHeight() - 20f);
        content.addActor(logo);

        Label.LabelStyle style = new Label.LabelStyle();
        style.fontColor = ColorConfig.DIALOG_TEXT_COLOR;
        style.font = screen.wordGame.resourceManager.get(ResourceManager.fontSignikaBold, BitmapFont.class);

        page = new Label(" ", style);
        page.setFontScale(0.75f);
        page.setY(110f);
        content.addActor(page);
    }





    @Override
    public void show(){
        getStage().getRoot().setTouchable(Touchable.disabled);
        adjustButtons();
        setPageNumber();

        if(words.length == 1){
            leftArrow.getImage().getColor().a = 0.3f;
            rightArrow.getImage().getColor().a = 0.3f;
            leftArrow.setDisabled(true);
            rightArrow.setDisabled(true);
        }
        super.show();
    }




    @Override
    protected void openAnimFinished() {
        super.openAnimFinished();
        getAllWordMeanings();
        getStage().getRoot().setTouchable(Touchable.enabled);
    }




    @Override
    protected void hideAnimFinished() {
        super.hideAnimFinished();

        allWords.clearChildren();
        allWords.setX(0);
        index = 0;
    }





    private ChangeListener onClick = new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            if(actor == leftArrow){
                if(index == 0)
                    return;

                index--;
                pageMotion(mask.getWidth());
            }else if(actor == rightArrow){
                if(index == words.length - 1)
                    return;

                index++;
                pageMotion(-mask.getWidth());
            }
        }
    };




    private void pageMotion(float x){
        getStage().getRoot().setTouchable(Touchable.disabled);
        adjustButtons();
        setPageNumber();

        allWords.addAction(
                Actions.sequence(
                        Actions.moveBy(x, 0, 0.4f, Interpolation.smooth2),
                        Actions.run(pageMotionFinished)
                )
        );
    }




    private Runnable pageMotionFinished = new Runnable() {
        @Override
        public void run() {
            getStage().getRoot().setTouchable(Touchable.enabled);
        }
    };




    private void setPageNumber(){
        page.setText((index + 1) + "/" + words.length);
        GlyphLayout glyphLayout = Pools.obtain(GlyphLayout.class);
        glyphLayout.setText(page.getStyle().font, page.getText());
        page.setX((content.getWidth() - glyphLayout.width) * 0.5f);
        Pools.free(glyphLayout);
    }



    private void adjustButtons(){
        if(index == 0){
            leftArrow.getImage().getColor().a = 0.3f;
            rightArrow.getImage().getColor().a = 1f;

            leftArrow.setDisabled(true);
            rightArrow.setDisabled(false);
        }else if(index == words.length - 1){
            leftArrow.getImage().getColor().a = 1f;
            rightArrow.getImage().getColor().a = 0.3f;
            leftArrow.setDisabled(false);
            rightArrow.setDisabled(true);
        }else{
            leftArrow.getImage().getColor().a = 1f;
            rightArrow.getImage().getColor().a = 1f;
            leftArrow.setDisabled(false);
            rightArrow.setDisabled(false);
        }
    }





    private ImageButton dirButton(boolean left){
        TextureRegionDrawable bg = new TextureRegionDrawable(left ? AtlasRegions.arrow_left : AtlasRegions.arrow_right);
        ImageButton button = new ImageButton(bg);
        button.setOrigin(Align.center);
        return button;
    }





    private void getAllWordMeanings(){
        allWords.setSize(mask.getWidth() * words.length, mask.getHeight());

        float x = 0;
        float y = 0;

        for(int i = 0; i < words.length; i++){
            Group group = new Group();
            group.setWidth(mask.getWidth());
            group.setHeight(mask.getHeight());
            getWordContent(group, words[i]);
            group.setPosition(x, y);
            allWords.addActor(group);
            x += group.getWidth();
        }
        mask.addActor(allWords);
    }





    private void getWordContent(final Group group, final String word){
        Image loadingCircle = new Image(AtlasRegions.loading);
        loadingCircle.setX((group.getWidth() - loadingCircle.getWidth()) * 0.5f);
        loadingCircle.setY((group.getHeight() - loadingCircle.getHeight()) * 0.5f);
        loadingCircle.setOrigin(Align.center);
        loadingCircle.setColor(Color.WHITE);
        loadingCircle.setName("s");
        group.addActor(loadingCircle);
        loadingCircle.addAction(Actions.forever(Actions.rotateBy(360, 2f)));

        String langCode = Language.locale.code;
        WordMeaningProvider provider = Language.wordMeaningProviderMap.get(langCode);
        WordMeaningRequest wordMeaningRequest = provider.get(langCode);

        wordMeaningRequest.request(word, new DictionaryCallback() {

            @Override
            public void onMeaning(String word, String meaning) {
                prepareWord(group, word, meaning);
            }

            @Override
            public void onError(String msg) {
                prepareWord(group, word, msg);
            }
        });
    }






    private void prepareWord(Group group, String word, String meaning){
        Label title = new Label(word, wordTitlelabelStyle);
        title.setFontScale(1f);
        title.setAlignment(Align.center);
        title.setX((group.getWidth() - title.getWidth()) * 0.5f);
        group.addActor(title);

        float panelWidth = group.getWidth() * 0.9f;
        float paneHeight = group.getHeight() * 0.85f;

        Label text = new Label(meaning, meaninglabelStyle);
        text.setFontScale(0.9f);
        text.setOrigin(Align.bottom);
        text.setAlignment(Align.bottomLeft);
        text.setWrap(true);

        Table table = new Table();
        table.align(Align.bottomLeft);
        table.add(text).width(panelWidth * 0.9f).left();
        table.pack();

        Group container = new Group();
        container.setSize(group.getWidth(), Math.max(table.getHeight(), paneHeight));
        table.setX((container.getWidth() - table.getWidth()) * 0.5f);
        table.setY(container.getHeight() - table.getHeight());
        container.addActor(table);

        ScrollPane pane = new ScrollPane(container, paneStyle);
        pane.setSize(group.getWidth(), paneHeight);
        pane.setY(group.getHeight() * 0.02f);
        group.addActor(pane);

        float paneTop = pane.getY() + paneHeight;

        title.setY(paneTop + ((group.getHeight() - paneTop) - title.getHeight()) * 0.5f);

        Actor shader = group.findActor("s");
        if(shader != null){
            shader.remove();
            shader.setVisible(false);
        }

        if(word.equals(words[words.length - 1]))
            getStage().getRoot().setTouchable(Touchable.enabled);
    }






    class ClippingGroup extends Group{

        private Rectangle scissors = new Rectangle();

        @Override
        public void draw(Batch batch, float parentAlpha) {

            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

            getStage().calculateScissors(clipBounds, this.scissors);
            batch.flush();
            if (ScissorStack.pushScissors(this.scissors)) {
                super.draw(batch, parentAlpha);
                batch.flush();
                ScissorStack.popScissors();
            }

            batch.setColor(color.r, color.g, color.b, 1);
        }
    }





    public interface DictionaryCallback{
        void onMeaning(String word, String meaning);
        void onError(String msg);
    }


}
