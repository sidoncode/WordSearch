package word.search.ui.game.wordsview;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.SnapshotArray;

import word.search.GameController;
import word.search.WordGame;
import word.search.app;
import word.search.config.ConfigProcessor;
import word.search.config.GameConfig;
import word.search.config.UIConfig;
import word.search.graphics.AtlasRegions;
import word.search.graphics.NinePatches;
import word.search.managers.DataManager;
import word.search.managers.ResourceManager;
import word.search.model.Constants;
import word.search.model.Language;
import word.search.model.Level;
import word.search.model.Word;
import word.search.ui.game.board.Board;
import word.search.ui.game.board.Letter;
import word.search.ui.game.category.CategoryRibbon;

import word.search.ui.game.particle.Ring;
import word.search.ui.game.particle.SparkleParticle;
import word.search.ui.hud.CoinView;
import word.search.ui.hud.game_hud.GameScreenHud;
import word.search.ui.util.UiUtil;

public class WordsView extends Group  {

    public static float POSITION_Y;

    public Array<PoolableGroup> wordBoxes = new Array<>();
    private Array<PoolableGroup> rowBoxes = new Array<>();

    private Array<WordBg> wordBgs = new Array<>();
    private Array<AnimatedLetter> animatedLetters = new Array<>();
    private Board board;
    private Level level;
    private float placeHolderScale;
    private int singleHintIndex;
    public GameController gameController;


    public WordsView(GameController gameController, Board board){
        setSize(900, 300);
        this.gameController = gameController;
        this.board = board;
        if(GameConfig.DEBUG_WORD_ORDER) debug();
    }


    public void resize(){
        POSITION_Y = gameController.gameScreen.stage.getHeight() * 0.675f;
        setX(board.getX());
        setY(WordsView.POSITION_Y);
    }


    public void clearLevel(){
        for(PoolableGroup poolableGroup : wordBoxes){
            Pools.free(poolableGroup);
            poolableGroup.remove();

            if(GameConfig.DEBUG_WORD_ORDER){
                poolableGroup.clearChildren();
            }
        }
        wordBoxes.clear();
        for(PoolableGroup poolableGroup : rowBoxes){
            Pools.free(poolableGroup);
            poolableGroup.remove();

            if(GameConfig.DEBUG_WORD_ORDER){
                poolableGroup.clearChildren();
            }
        }
        rowBoxes.clear();
        for(WordBg wordBg : wordBgs){
            Pools.free(wordBg);
            wordBg.remove();

            if(GameConfig.DEBUG_WORD_ORDER){
                wordBg.clearChildren();
            }
        }
        wordBgs.clear();
        singleHintIndex = 0;
        coinsAnimating = false;
    }





    public void setData(Level level, String rows, float preferredWidth){
        this.level = level;
        String[] rowsSplit = rows.split("#");

        for(int i = 0; i < level.words.size; i++){
            level.words.get(i).row = Integer.parseInt(rowsSplit[i]);
        }

        int rowCount = findRowCount(rowsSplit);
        float boxWidth = findBoxSize(rowCount, preferredWidth);
        placeHolderScale = boxWidth / AtlasRegions.letter_holder.getRegionWidth();
        PoolableGroup prevRow = null;

        for(int t = 0; t < rowCount; t++){
            PoolableGroup rowBoxNode = Pools.obtain(PoolableGroup.class);
            rowBoxes.add(rowBoxNode);
            float wordSpacing = 0;
            float rowBoxWidth = 0;
            PoolableGroup prevSubbox = null;

            for(int i = 0; i < level.words.size; i++){
                if (level.words.get(i).row == t) {
                    PoolableGroup subBox = Pools.obtain(PoolableGroup.class);
                    wordBoxes.add(subBox);
                    int wordLength = level.words.get(i).answer.length();

                    for(int j = 0; j < wordLength && !level.words.get(i).solved; j++){
                        PlaceHolder imgNode = Pools.obtain(PlaceHolder.class);
                        imgNode.setScale(placeHolderScale);
                        imgNode.setX(j * boxWidth);
                        imgNode.index = j;
                        subBox.addActor(imgNode);

                        if(!GameConfig.DEBUG_WORD_ORDER && level.coinedWordIndex == i) {
                            PlaceHolderCoin coin = Pools.obtain(PlaceHolderCoin.class);
                            coin.setScale(imgNode.getHeight() * imgNode.getScaleY() * 0.7f / coin.getHeight());
                            coin.setX(imgNode.getX() + (imgNode.getWidth() * imgNode.getScaleX() - coin.getWidth() * coin.getScaleX()) * 0.5f);
                            coin.setY(imgNode.getY() + (imgNode.getHeight() * imgNode.getScaleY() - coin.getHeight() * coin.getScaleY()) * 0.5f);
                            subBox.addActor(coin);
                        }
                    }

                    subBox.setSize(boxWidth * wordLength, boxWidth);
                    rowBoxWidth += subBox.getWidth();
                    rowBoxNode.addActor(subBox);

                    wordSpacing = boxWidth;
                    if(prevSubbox == null) subBox.setX(0);
                    else subBox.setX(prevSubbox.getX() + prevSubbox.getWidth() + wordSpacing);
                    prevSubbox = subBox;

                    if(level.words.size > 1 && i + 1 < level.words.size && level.words.get(i).row == level.words.get(i + 1).row){
                        rowBoxWidth += wordSpacing;
                    }
                }
            }

            rowBoxNode.setSize(rowBoxWidth, boxWidth);
            rowBoxNode.setX((getWidth() - rowBoxNode.getWidth()) * 0.5f);

            float remainingSpace = getHeight() - rowCount * boxWidth;
            float dst = remainingSpace / (rowCount + 1);

            if(prevRow != null) rowBoxNode.setY(prevRow.getY() - prevRow.getHeight() - dst);
            else rowBoxNode.setY(getHeight() - rowBoxNode.getHeight() - dst);

            prevRow = rowBoxNode;
            addActor(rowBoxNode);
        }

        restoreIncompleteLevel();
    }




    private Array<String> rows = new Array<>();


    private int findRowCount(String[] input){
        rows.clear();
        for (int i = 0; i < input.length; i++) {
            if (rows.indexOf(input[i], false) == -1) {
                rows.add(input[i]);
            }
        }
        return rows.size;
    }




    private float findBoxSize(int rowCount, float preferredWidth){
        float spacing = 0;
        int widest = 0;
        int maxWordCount = 0;

        for(int t = 0; t < rowCount; t++){
            float temp = 0;
            int boxCount = 0;
            int wordCount = 0;

            for(int i = 0; i < level.words.size; i++){
                if(level.words.get(i).row == t){
                    int wordLength = level.words.get(i).answer.length() + 1;
                    boxCount += wordLength;
                    temp += wordLength - 1;
                    wordCount++;
                }
            }

            boxCount += wordCount - 1;
            if(boxCount >= widest){
                spacing = temp;
                widest = boxCount;
                maxWordCount = wordCount;
            }
        }

        float availableWidth = getWidth();
        float width = availableWidth / (spacing + (maxWordCount - 1));

        width = Math.min(width, preferredWidth);
        return width;
    }


    private Vector2 tempVec2 = new Vector2();


    public void setCompletedWord(int subBoxId, String answer, boolean animate){
        Group subBox = wordBoxes.get(subBoxId);
        tempVec2.x = 0;
        tempVec2.y = 0;

        Vector2 v2 = subBox.localToActorCoordinates(this, tempVec2);

        WordBg wordBg = Pools.obtain(WordBg.class);
        wordBg.setMaxBgWidth(subBox.getWidth());
        wordBg.setBgScale(placeHolderScale * 1.15f);
        wordBg.setText(answer, gameController.gameScreen.wordGame.resourceManager);
        wordBg.setOrigin(Align.center);
        wordBg.setX(v2.x + (subBox.getWidth() - wordBg.getWidth()) * 0.5f);
        wordBg.setY(v2.y + (subBox.getHeight() - wordBg.getHeight()) * 0.5f);
        if(animate)wordBg.setScale(0);
        wordBgs.add(wordBg);

        clearPlaceHolders(subBox);

        addActor(wordBg);

        if(animate) wordBg.growAndShrink();

        if(Language.wordMeaningProviderMap.containsKey(Language.locale.code)) wordBg.addListener(inputListener);
    }




    private InputListener inputListener = new InputListener(){

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            return true;
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            Actor eventTarget = event.getTarget();
            WordBg wordBg = (WordBg)eventTarget.getParent();
            ((GameScreenHud)gameController.gameScreen.hud).showDictionary(new String[]{wordBg.text});
        }

    };




    private void clearPlaceHolders(Group subBox){
        SnapshotArray array = subBox.getChildren();

        Object[] items = array.begin();
        for (int i = 0, n = array.size; i < n; i++) {
            if(items[i] instanceof PlaceHolder) {
                PlaceHolder placeHolder = (PlaceHolder) items[i];
                Pools.free(placeHolder);
                placeHolder.remove();
            }
        }
        array.end();
    }




    public void setAnimatedLetter(AnimatedLetter animatedLetter){
        animatedLetters.add(animatedLetter);
    }



    private void restoreIncompleteLevel(){
        singleHintIndex = DataManager.get(DataManager.getLocaleAwareKey(Constants.WSINGLE_HINT_INDEX), 0);
        String existingJsonString = DataManager.get(DataManager.getLocaleAwareKey(Constants.SAVED_LEVEL), null);
        if(existingJsonString == null || existingJsonString.equals("[]")) return;

        JsonReader jsonReader = new JsonReader();
        JsonValue doc = jsonReader.parse(existingJsonString);

        for(int i = 0; i < doc.size; i++){
            JsonValue jsonValue = doc.get(i);
            int index = jsonValue.getInt("index");
            Word word = level.words.get(index);

            if(jsonValue.has("direction")){
                setCompletedWord(index, word.answer, false);
                continue;
            }

            if(jsonValue.has("whints")){
                Group group = wordBoxes.get(index);
                int count = jsonValue.getInt("whints");
                for(int j = 0; j < count; j++){
                    PlaceHolder placeHolder = (PlaceHolder)group.getChild(j);
                    placeHolder.hinted = true;
                    placeHolder.c = word.answer.charAt(j);
                    createHintLabel(placeHolder);
                }
                word.revealedLetterCount = count;
            }
        }
    }




    private boolean animateCoins(Word word){
        if(GameConfig.DEBUG_WORD_ORDER) return false;
        final int wordIndex = level.words.indexOf(word, false);
        if(gameController.level.coinedWordIndex == wordIndex){

            if(GameConfig.ENABLE_LOGGING_EARN_VIRTUAL_CURRENCY_EVENT) WordGame.analytics.logEarnedCoinEvent(word.answer.length());

            Group wordBox = wordBoxes.get(wordIndex);
            final CoinView coinView = gameController.gameScreen.hud.coinView;

            SnapshotArray array = wordBox.getChildren();
            Object[] items = array.begin();
            int index = 0;

            Vector2 v2 = new Vector2();
            Actor targetCoin = coinView.coin;
            float tx = targetCoin.getX() + (targetCoin.getWidth() - AtlasRegions.placeholder_coin.getRegionWidth()) * 0.5f;
            float ty = targetCoin.getY() + (targetCoin.getHeight() - AtlasRegions.placeholder_coin.getRegionHeight()) * 0.5f;

            for (int i = 0, n = array.size; i < n; i++) {
                if(items[i] instanceof PlaceHolderCoin){
                    PlaceHolderCoin coin = (PlaceHolderCoin)items[i];
                    v2.x = 0;
                    v2.y = 0;
                    Vector2 newPos = coin.localToActorCoordinates(coinView, v2);
                    coin.remove();
                    coin.setPosition(newPos.x, newPos.y);
                    coinView.addActor(coin);

                    coin.animate((index) * 0.1f, tx, ty, coinView, index == 0, index == word.answer.length() - 1,  null);
                    coin.run = true;
                    index++;
                }
            }
            array.end();
            return true;
        }
        return false;
    }






    public boolean coinsAnimating;


    public void animateCorrectAnswer(final Word word){
        coinsAnimating = animateCoins(word);
        Array<AnimatedLetter> list = prepareAnimatedLetters(word);

        for(int i = 0; i < list.size; i++){
            AnimatedLetter animatedLetter = list.get(i);
            animatedLetter.setPosition(animatedLetter.srcX, animatedLetter.srcY);
            addActor(animatedLetter);

            MoveToAction moveTo = Actions.moveTo(animatedLetter.dstX, animatedLetter.dstY , 0.3f, Interpolation.slowFast);
            ScaleToAction scaleTo = Actions.scaleTo(animatedLetter.dstScale, animatedLetter.dstScale, 0.3f, Interpolation.slowFast);
            ParallelAction parallelAction = Actions.parallel(scaleTo, moveTo);

            final boolean lastIteration = i == word.answer.length() - 1;

            animatedLetter.addAction(Actions.sequence(
                    parallelAction,
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            clearAnimatedLetters();

                            if(lastIteration) {
                                int index = level.words.indexOf(word, false);
                                setCompletedWord(index, word.answer, true);
                                gameController.gameScreen.board.throwLetter(word);
                                checkLevelFinished();
                                sparkle(wordBoxes.get(index));
                            }
                        }
                    })
            ));
        }
    }



    private void checkLevelFinished(){
        if(board.levelEnded()){
            float time = 1;

            if(coinsAnimating){
                time = 1.1f;
            }else{
                if(!board.thrownLetter()){
                    time = 0.8f;
                }
            }

            addAction(Actions.sequence(
                    Actions.delay(time),
                    Actions.run(levelFinishedSignaller)
            ));
        }
    }




    private Runnable levelFinishedSignaller = new Runnable() {
        @Override
        public void run() {
            gameController.levelFinished();
        }
    };



    private void clearAnimatedLetters(){
        for(AnimatedLetter animatedLetter : animatedLetters){
            Pools.free(animatedLetter);
            animatedLetter.remove();
        }
        animatedLetters.clear();
    }





    private Array<AnimatedLetter> prepareAnimatedLetters(Word word){
        final int wordIndex = level.words.indexOf(word, false);

        Group wordBox = wordBoxes.get(wordIndex);
        Array<Letter> selection = board.getSelectedViewsInstant();
        Array<AnimatedLetter> list = new Array<>();

        for(int i = 0; i < word.answer.length(); i++){
            Label copyFrom = selection.get(i).label;
            AnimatedLetter animatedLetter = createAnimatedLetter(String.valueOf(word.answer.charAt(i)), copyFrom.getStyle(), copyFrom.getFontScaleX());

            Vector2 p1 = copyFrom.localToActorCoordinates(this, animatedLetter.getVector2());
            animatedLetter.srcX = p1.x;
            animatedLetter.srcY = p1.y;

            PlaceHolder placeHolder = (PlaceHolder)wordBox.getChild(i);
            float scale = calculateAnimatedLetterTargetScale(placeHolder, animatedLetter);
            animatedLetter.dstScale = scale;

            Vector2 p2 = calculateAnimatedLetterTargetPosition(placeHolder, animatedLetter, scale);
            animatedLetter.dstX = p2.x;
            animatedLetter.dstY = p2.y;

            list.add(animatedLetter);
        }
        return list;
    }



    private AnimatedLetter createAnimatedLetter(String s, Label.LabelStyle labelStyle, float fontScale){
        AnimatedLetter animatedLetter = Pools.obtain(AnimatedLetter.class);
        animatedLetter.init(s, labelStyle, fontScale);
        setAnimatedLetter(animatedLetter);
        return animatedLetter;
    }





    private float calculateAnimatedLetterTargetScale(PlaceHolder placeHolder, AnimatedLetter animatedLetter){
        return (placeHolder.getHeight() / animatedLetter.getHeight()) * 0.65f;
    }




    private Vector2 calculateAnimatedLetterTargetPosition(PlaceHolder placeHolder, AnimatedLetter animatedLetter, float scale){
        float xoffset = (placeHolder.getWidth() - animatedLetter.getWidth() * scale) * 0.5f;
        return placeHolder.localToActorCoordinates(this, new Vector2(xoffset, animatedLetter.getHeight() * 0.03f));
    }




    public void shakeWord(String answer){
        for(WordBg wordBg : wordBgs){
            if(wordBg.text.equals(answer)){
                UiUtil.shake(wordBg, true, wordBg.getWidth() * 0.25f, null);
                return;
            }
        }
    }




    public boolean deliverSingleHint(){
        PlaceHolder placeHolder = findAPlaceHolderToGiveHint();

        if(placeHolder != null){
            getStage().getRoot().setTouchable(Touchable.disabled);
            placeHolder.hinted = true;
            Level.saveWordHint(singleHintIndex);
            placeHolder.c = level.words.get(singleHintIndex).answer.charAt(placeHolder.index);
            revealAnimation(placeHolder);
            singleHintIndex++;
            singleHintIndex %= level.words.size;
            DataManager.set(DataManager.getLocaleAwareKey(Constants.WSINGLE_HINT_INDEX), singleHintIndex);
            if(!ConfigProcessor.mutedSfx) {
                Sound sound = gameController.gameScreen.wordGame.resourceManager.get(ResourceManager.word_reveal, Sound.class);
                sound.play();
            }
            if(getStage() != null) getStage().getRoot().setTouchable(Touchable.enabled);
            return true;
        }
        return false;
    }




    public void revealAnimation(PlaceHolder placeHolder){
        Ring ring = Pools.obtain(Ring.class);
        ring.setTarget(placeHolder, ringCallback);
        placeHolder.getParent().addActor(ring);
        ring.animate();
    }




    private Ring.RingCallback ringCallback = new Ring.RingCallback() {

        @Override
        public void part1Finished(Actor target) {
            createHintLabel((PlaceHolder)target);
        }

        @Override
        public void part2Finished(Actor target) {
            //getStage().getRoot().setTouchable(Touchable.enabled);
        }

    };




    private void createHintLabel(PlaceHolder placeHolder){
        Label label = placeHolder.getLabel(gameController.gameScreen.wordGame.resourceManager);
        if(placeHolder.getParent() != null) placeHolder.getParent().addActor(label);

    }


    private Array<Word> candidates = new Array<>();

    private PlaceHolder findAPlaceHolderToGiveHint(){
        if(allFilled()) return null;

        candidates.clear();
        for(int i = 0; i < level.words.size; i++){
            Word word = level.words.get(i);

            if(i != level.coinedWordIndex && !word.solved && word.revealedLetterCount < word.answer.length()){
                candidates.add(level.words.get(i));
            }
        }

        if(candidates.size == 0) return null;

        Word candidate = candidates.get(0);
        int nextIndex = candidate.index;

        Group subbox = wordBoxes.get(nextIndex);

        for(int i = 0; i < subbox.getChildren().size; i++){
            Actor actor = subbox.getChild(i);
            if(actor instanceof PlaceHolder) {
                PlaceHolder child = (PlaceHolder) actor;
                if (!child.hinted) {
                    if(subbox.getParent() != null) subbox.getParent().setZIndex(100);
                    singleHintIndex = nextIndex;
                    candidate.revealedLetterCount++;
                    return child;
                }
            }
        }

        return null;
    }



    public boolean allFilled(){
        for(int i = 0; i < level.words.size; i++){
            if(!level.words.get(i).solved) return false;
        }
        return true;
    }




    public void show(boolean in){
        addAction(
                Actions.sequence(
                        Actions.delay(in ? CategoryRibbon.ANIM_TIME_1 + CategoryRibbon.ANIM_TIME_2 : 0f),
                        Actions.parallel(
                                (in ? Actions.fadeIn(CategoryRibbon.ANIM_TIME_3, Interpolation.fastSlow) : Actions.fadeOut(CategoryRibbon.ANIM_TIME_3, Interpolation.fastSlow)),
                                Actions.moveTo(getX(), in ? WordsView.POSITION_Y : WordsView.POSITION_Y + CategoryRibbon.ANIM_MOVE_DST, CategoryRibbon.ANIM_TIME_3, Interpolation.fastSlow)
                        )
                )
        );
    }




    private Array<SparkleParticle> sparkles = new Array<>();


    public void sparkle(PoolableGroup group){
        Vector2 v2 = new Vector2();
        Color color = Color.WHITE;

        for(int i = 0; i < 30; i++){
            SparkleParticle p = Pools.obtain(SparkleParticle.class);

            float angle = MathUtils.random() * MathUtils.PI2;
            p.setColor(color);
            v2.x = 0;
            v2.y = 0;
            Vector2 pos = group.localToActorCoordinates(this, v2);

            p.x = pos.x + group.getWidth() * 0.5f - p.getWidth() * 0.5f;
            p.y = pos.y + group.getHeight() * 0.5f - p.getHeight() * 0.5f;
            p.radius = MathUtils.random(0.5f, 1.0f);
            p.rotation = angle;
            p.speed = MathUtils.random(6f, 10f);
            p.friction = 0.97f;
            p.opacity =  MathUtils.random(0.7f, 0.9f);
            sparkles.add(p);
        }
    }




    @Override
    protected void drawChildren(Batch batch, float parentAlpha) {

        for(int i = 0; i < sparkles.size; i++){
            SparkleParticle p = sparkles.get(i);
            p.x += p.speed * MathUtils.cos(p.rotation);
            p.y += p.speed * MathUtils.sin(p.rotation);
            p.speed *= p.friction;
            p.radius *= 0.96f;
            p.setPosition(p.x, p.y);
            p.setScale(p.radius, p.radius);
            p.setRotation(p.getRotation());
            p.opacity -= 0.01f;
            p.setAlpha(p.opacity);
            p.draw(batch);

            if (p.getColor().a <= 0 || p.getScaleX() <= 0) {
                sparkles.removeIndex(i);
                Pools.free(p);

            }
        }
        super.drawChildren(batch, parentAlpha);
    }


}
