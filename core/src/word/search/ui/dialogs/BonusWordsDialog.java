package word.search.ui.dialogs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.StringBuilder;

import word.search.config.ColorConfig;
import word.search.config.GameConfig;
import word.search.graphics.AtlasRegions;
import word.search.graphics.NinePatches;
import word.search.managers.DataManager;
import word.search.managers.ResourceManager;
import word.search.model.Constants;
import word.search.model.Language;
import word.search.screens.BaseScreen;
import word.search.ui.game.ProgressBar;

public class BonusWordsDialog extends BaseDialog {


    private ProgressBar progressBar;
    private Label wordsLabel, lblCount;
    private JsonReader jsonReader;
    private Array<String> extraWords = new Array<>();
    private StringBuilder stringBuilder = new StringBuilder();



    public BonusWordsDialog(BaseScreen screen) {
        super(screen);

        float aspectRatio = Constants.GAME_WIDTH / Constants.GAME_HEIGHT;
        content.setWidth(screen.stage.getHeight() * aspectRatio * 0.7f);

        float maxWidth = AtlasRegions.progressbar.getRegionWidth() + AtlasRegions.coin_view_coin.getRegionWidth() * 0.5f;
        float marginLeft = (content.getWidth() - maxWidth) * 0.5f;

        Group wordsGroup = new Group();
        wordsGroup.setWidth(maxWidth);
        wordsGroup.setHeight(500);
        wordsGroup.setPosition(marginLeft, marginBottom * 2f);
        content.addActor(wordsGroup);

        Image wordsGroupBg = new Image(NinePatches.iap_content);
        wordsGroupBg.setSize(wordsGroup.getWidth(), wordsGroup.getHeight());
        wordsGroupBg.setColor(new Color(0xc6b087ff));
        wordsGroup.addActor(wordsGroupBg);

        Label.LabelStyle wordsStyle = new Label.LabelStyle(screen.wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class), Color.WHITE);
        wordsLabel = new Label(" ", wordsStyle);
        wordsLabel.setAlignment(Align.center);
        wordsLabel.setWrap(true);

        ScrollPane pane = new ScrollPane(wordsLabel);
        pane.setScrollbarsVisible(false);
        pane.setSize(wordsGroup.getWidth(), wordsGroup.getHeight() * 0.98f);
        pane.setY(wordsGroup.getHeight() * 0.01f);
        pane.setupFadeScrollBars(0,0);
        wordsGroup.addActor(pane);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.fontColor = ColorConfig.DIALOG_TEXT_COLOR;
        labelStyle.font = screen.wordGame.resourceManager.get(ResourceManager.fontSignikaBold, BitmapFont.class);

        Label label = new Label(Language.get("bonus_text"), labelStyle);
        label.setFontScale(0.8f);
        label.setAlignment(Align.bottom);
        label.setOrigin(Align.bottom);
        label.setWidth(maxWidth);
        label.setWrap(true);
        label.setX((content.getWidth() - label.getWidth()) * 0.5f);
        label.setY(wordsGroup.getY() + wordsGroup.getHeight() + marginBottom * 0.3f);
        content.addActor(label);

        progressBar = new ProgressBar(AtlasRegions.progressbar_track, AtlasRegions.progressbar);
        progressBar.setOrigin(Align.center);
        progressBar.setX(marginLeft);
        progressBar.setY(label.getY() + label.getPrefHeight() + marginBottom * 2f);
        content.addActor(progressBar);
        progressBar.setPercent(0.5f);

        Image coin = new Image(AtlasRegions.coin_view_coin);
        coin.setX(progressBar.getX() + progressBar.getWidth() - coin.getWidth() * 0.5f);
        coin.setY(progressBar.getY() - (coin.getHeight() - progressBar.getHeight()) * 0.5f);
        content.addActor(coin);

        lblCount = new Label(" ", labelStyle);
        lblCount.setOrigin(Align.bottom);
        content.addActor(lblCount);

        lblCount.setY(progressBar.getY() + progressBar.getHeight() + marginBottom * 0.3f);

        content.setHeight(lblCount.getY() + lblCount.getPrefHeight() + marginBottom + NinePatches.dialog_title.getTotalHeight());

        setContentBackground();
        setTitleLabel(Language.get("bonus"));
        setCloseButton();

        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getStage().getRoot().setTouchable(Touchable.disabled);
                hide();
            }
        });
    }





    @Override
    public void show() {
        super.show();
        updateViewWithData();
    }





    public void updateViewWithData(){
        int current = DataManager.get(DataManager.getLocaleAwareKey(Constants.KEY_EXTRA_WORD_COUNT), 0);
        int target = GameConfig.NUMBER_OF_BONUS_WORDS_TO_FIND_FOR_REWARD;

        lblCount.setText(current + "/" + target);
        GlyphLayout glyphLayout = Pools.obtain(GlyphLayout.class);
        glyphLayout.setText(lblCount.getStyle().font, lblCount.getText());
        lblCount.setX((content.getWidth() - glyphLayout.width) * 0.5f);
        Pools.free(glyphLayout);

        progressBar.setPercent((float)current / (float)target);

        if(wordsLabel != null)
            wordsLabel.setText(getWordList());
    }




    private String getWordList(){
        String json = DataManager.get(DataManager.getLocaleAwareKey(Constants.KEY_EXTRA_WORDS), "[]");
        if(jsonReader == null) jsonReader = new JsonReader();
        JsonValue doc = jsonReader.parse(json);

        for(int i = 0; i < doc.size; i++) extraWords.add(doc.get(i).asString());

        stringBuilder.clear();
        String lineBreak = "";

        for(int i = 0; i < extraWords.size; i++){
            stringBuilder.append(lineBreak);
            stringBuilder.append(extraWords.get(i));
            if(lineBreak.isEmpty())
                lineBreak = "\n";
        }
        return stringBuilder.toString();
    }




    @Override
    protected void hideAnimFinished() {
        extraWords.clear();
        super.hideAnimFinished();
    }
}
