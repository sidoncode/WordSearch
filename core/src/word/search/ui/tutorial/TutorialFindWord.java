package word.search.ui.tutorial;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.compression.lzma.Base;

import word.search.app;
import word.search.graphics.AtlasRegions;
import word.search.managers.ResourceManager;
import word.search.model.Constants;
import word.search.model.Language;
import word.search.model.Word;
import word.search.screens.BaseScreen;
import word.search.ui.game.Preview;
import word.search.ui.game.board.Board;
import word.search.ui.game.board.Letter;

public class TutorialFindWord extends Tutorial{

    private Image hand;
    private Array<Letter> letters;
    private Board board;
    private Preview preview;
    private int previewDepth;
    private Group previewParent;
    private Array<Word> words;
    private int wordIndex;
    private Word currentWord;



    public TutorialFindWord(float width, float height, BaseScreen screen) {
        super(width, height, screen);
    }



    public void setPreview(Preview preview){
        this.preview = preview;
        previewDepth = preview.getZIndex();
        previewParent = preview.getParent();
    }



    public void indicateWords(Board board, Array<Word> words){
        this.board = board;
        this.words = words;
        wordIndex = board.completedWords.size;
        askToFindWord();
    }



    private void askToFindWord(){
        currentWord = words.get(wordIndex);

        text(getText(), Constants.GAME_CONTENT_WIDTH, screen.wordGame.resourceManager.get(ResourceManager.fontSignikaBoldShadow, BitmapFont.class));
        letters = board.findViewsForAnswer(currentWord.answer);
        hand = new Image(AtlasRegions.hand);
        hand.setTouchable(Touchable.disabled);
        addActor(hand);
        moveHand();
        board.setTutorialMode(this);
    }



    public Word getCurrentWord(){
        return currentWord;
    }



    private String getText(){
        String text = "[#ffffff]" + Language.get(wordIndex == 0 ? "tutorial_find_word1" : "tutorial_find_word2") + "[]";
        StringBuilder sb = new StringBuilder(words.get(wordIndex).answer.length() + words.get(wordIndex).answer.length() - 1);

        String delim = "";
        for(int i = 0; i < words.get(wordIndex).answer.length(); i++){
            sb.append(delim);
            sb.append(words.get(wordIndex).answer.charAt(i));
            if(delim.isEmpty()) delim = "-";
        }

        text = text.replaceFirst("\\{0\\}", "[#dee574]" + sb.toString() + "[]");
        return text;
    }



    private void moveHand(){
        final float halfLetter = letters.get(0).getWidth() * 0.5f;
        final float handHeight = hand.getHeight() - 10;
        hand.setPosition(board.getX() + letters.get(0).getX() + halfLetter, board.getY() + letters.get(0).getY() + halfLetter - handHeight);

        SequenceAction sequenceAction = Actions.sequence();

        for(int i = 1; i < letters.size; i++){
            Letter letter = letters.get(i);
            sequenceAction.addAction(Actions.moveTo(board.getX() + letter.getX() + halfLetter, board.getY() + letter.getY() + halfLetter - handHeight, 0.5f));
        }
        sequenceAction.addAction(Actions.fadeOut(0.5f));
        sequenceAction.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                hand.setPosition(board.getX() + letters.get(0).getX() + halfLetter, board.getY() + letters.get(0).getY() + halfLetter - handHeight);
            }
        }));

        sequenceAction.addAction(Actions.fadeIn(0.5f));
        hand.addAction(Actions.forever(sequenceAction));
    }




    public void selectingLetters(boolean flag){
        getLabelContainer().clearActions();
        hand.clearActions();

        if(flag){
            getLabelContainer().addAction(Actions.fadeOut(0.2f));
            hand.addAction(Actions.fadeOut(0.2f));
            addActor(preview);
        }else{
            getLabelContainer().addAction(Actions.fadeIn(0.2f));
            hand.addAction(Actions.fadeIn(0.2f));
            moveHand();
            previewParent.addActorAt(previewDepth, preview);
        }
    }




    public void answeredCorrect(){
        hand.clearActions();

        if(wordIndex == words.size - 1) {
            close();
        }else{
            wordIndex++;

            modal.addAction(Actions.fadeOut(0.5f));
            getLabelContainer().addAction(Actions.fadeOut(0.5f));

            addAction(Actions.sequence(
                    Actions.delay(0.5f),
                    Actions.run(delayerEnd))
            );
        }
    }



    private Runnable delayerEnd = new Runnable() {
        @Override
        public void run() {
            askToFindWord();
            modal.addAction(Actions.fadeIn(0.5f));
            getLabelContainer().addAction(Actions.fadeIn(0.5f));
        }
    };




    @Override
    public void close() {
        hand.setVisible(false);
        board.unsetTutorialMode();
        super.close();
    }


}
