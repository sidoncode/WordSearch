package word.search.ui.game.board;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.StringBuilder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import word.search.GameController;
import word.search.app;
import word.search.config.ColorConfig;
import word.search.config.ConfigProcessor;
import word.search.config.GameConfig;
import word.search.config.UIConfig;
import word.search.graphics.NinePatches;
import word.search.managers.DataManager;
import word.search.managers.ResourceManager;
import word.search.model.Constants;
import word.search.model.Level;
import word.search.model.Word;
import word.search.ui.game.category.CategoryRibbon;
import word.search.ui.game.particle.Ring;
import word.search.ui.tutorial.TutorialFindWord;

public class Board extends Group {

    public static float POSITION_Y;

    public int mRows, mColumns;
    private Array<Array<Letter>> letters = new Array<Array<Letter>>();
    public GameController gameController;
    private Group container;
    private boolean dragging;
    private int selectionI, selectionJ;
    private Letter startLetterNode;
    private int mSelStartPosition;
    private Direction mSelectionDirection;
    private int mSelectionSteps = -1;
    private Array<Letter> mPreviousSelection = new Array<>();
    private StringBuilder answerStringBuilder = new StringBuilder();
    private Image currentSelectionImage, selectionAnimImage;
    public Array<Integer> completedWords = new Array<>();
    private Array<Image> correctAnswerSelectionImages = new Array<>();
    private Level level;
    private int prevLevelBoardWidth;
    private char[][] board2D;
    private Array<Letter> lettersToThrow = new Array<>();
    private Set<Integer> allWordsPositions = new HashSet<>();
    private Set<Integer> allBoardPositions = new HashSet<>();
    private JsonReader jsonReader = new JsonReader();
    private TutorialFindWord tutorialFindWord;
    private MagicWand magicWand;
    private Queue<SaveData> queue = new Queue<>();
    private float letterSize;
    float rotationTime = 0.5f;


    public Board(GameController gameController){
        this.gameController = gameController;

        setSize(Constants.GAME_CONTENT_WIDTH, Constants.GAME_CONTENT_WIDTH);
        Image background = new Image(NinePatches.board_bg);
        background.setSize(getWidth(), getHeight());
        background.getColor().a = UIConfig.GAME_BOARD_ALPHA;
        addActor(background);
        container = new Group();
        float padding = UIConfig.GAME_BOARD_PADDING_PX;
        container.setSize(getWidth() - padding, getHeight() - padding);
        container.setOrigin(Align.center);
        container.setX(padding * 0.5f);
        container.setY(padding * 0.5f);
        addActor(container);
    }



    public void resize(){
        POSITION_Y = gameController.gameScreen.stage.getHeight() * 0.145f;
        setX((gameController.gameScreen.stage.getWidth() - gameController.gameScreen.board.getWidth()) * 0.5f);
        setY(POSITION_Y);
    }


    public void setInputListener(){
        getParent().addListener(inputListener);
    }



    public void removeInputListener(){
        getParent().removeListener(inputListener);
    }




    public void clearLevel(){
        for(int i = 0; i < letters.size; i++){
            for(int j = 0; j < letters.get(i).size; j++){
                Letter letter = letters.get(i).get(j);
                letter.remove();
                Pools.free(letter);
            }
        }
        letters.clear();
        letters.shrink();

        ArrayMap.Keys<Integer> keys = hintPositions.keys();
        while(keys.hasNext()) {
            Integer position = keys.next();
            HintData hintData = hintPositions.get(position);
            Pools.free(hintData);
        }

        hintPositions.clear();
        hintPositions.shrink();

        completedWords.clear();
        if(mPreviousSelection != null) mPreviousSelection.clear();

        container.setRotation(0);
        queue.clear();

        for(Image image : correctAnswerSelectionImages){
            image.remove();
            image = null;
        }
        correctAnswerSelectionImages.clear();
    }





    public void setLevelData(Level level){
        this.level = level;
        mRows = level.boardSize;
        mColumns = level.boardSize;
        board2D = new char[mRows][mColumns];

        setBoard();
        createHintMapping();
        restoreIncompleteLevel();
        prepareNewLevel();

    }



    private void setBoard(){
        letterSize = container.getWidth() / this.mColumns;

        for(int i = 0; i < level.boardSize; i++){
            letters.add(new Array<Letter>());
            board2D[i] = new char[mColumns];
            for(int j = 0; j < level.boardSize; j++){
                Letter letter = Pools.obtain(Letter.class);
                letter.init(gameController);
                letter.setSize(letterSize, letterSize);
                letter.setOrigin(Align.center);
                letter.setPosition(i * letterSize, j * letterSize);
                container.addActor(letter);
                int index = (i * level.boardSize) + j;
                char c = level.boardData[index];
                letter.index = index;
                letter.setChar(c);
                letters.get(i).add(letter);
                board2D[i][j] = c;
            }
        }
    }





    public void rotate(){
        gameController.gameScreen.stage.getRoot().setTouchable(Touchable.disabled);

        float diagonal = (float)Math.hypot(getWidth(), getHeight());
        float scale = getWidth() / diagonal;

        container.addAction(Actions.sequence(
                Actions.scaleTo(scale, scale, rotationTime * 0.5f, Interpolation.slowFast),
                Actions.rotateBy(90, rotationTime, Interpolation.slowFast),
                Actions.scaleTo(1, 1, rotationTime * 0.5f, Interpolation.slowFast),
                Actions.run(rotationFinished)
        ));

        addAction(Actions.sequence(
                Actions.delay(rotationTime * 0.5f),
                Actions.run(rotationDelayFinished))
        );

        if(!ConfigProcessor.mutedSfx) {
            Sound sound = gameController.gameScreen.wordGame.resourceManager.get(ResourceManager.rotate, Sound.class);
            sound.play();
        }
    }



    private Runnable rotationFinished = new Runnable() {
        @Override
        public void run() {
            gameController.gameScreen.stage.getRoot().setTouchable(Touchable.enabled);
        }
    };




    private Runnable rotationDelayFinished = new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i < letters.size; i++){
                for (int j = 0; j < letters.size; j++){
                    Letter letter = letters.get(i).get(j);
                    if(!letter.thrown) letter.rotate(rotationTime);
                }
            }
        }
    };





    private void prepareNewLevel(){
        shuffleColors();
        if(prevLevelBoardWidth == 0 || letters.size > prevLevelBoardWidth){
            currentSelectionImage = getSelectionImage();
            selectionAnimImage = getSelectionAnimImage();
        }
        prevLevelBoardWidth = letters.size;
    }




    private void shuffleColors(){
        Color[] array = ColorConfig.GAME_BOARD_COLORS;
        int index;
        Color temp;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--){
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }



    private InputListener inputListener = new InputListener(){

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            dragging = true;
            //adjust stage coords to local
            x -= getX();
            y -= getY();
            selectionChanged(x, y);
            return true;
        }


        @Override
        public void touchDragged(InputEvent event, float x, float y, int pointer) {
            if(dragging) {
                //adjust stage coords to local
                x -= getX();
                y -= getY();
                selectionChanged(x, y);
            }
        }


        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            dragging = false;
            selectionFinished();
        }

    };



    private Vector2 rotateVec2 = new Vector2();

    private Array<Letter> oldViews = new Array<>();


    private void selectionChanged(float xPos, float yPos){
        rotatePosition(container, xPos, yPos, rotateVec2);
        xPos = rotateVec2.x;
        yPos = rotateVec2.y;

        if(letters.isEmpty()) return;
        Letter selectedNode = pointToLetterNode(xPos, yPos);

        if(selectedNode == null) return;

        if(!isPointInsideView(xPos, yPos, selectedNode)) return;

        if(tutorialFindWord != null) tutorialFindWord.selectingLetters(true);

        if(startLetterNode == null){
            mSelStartPosition = this.pointToPosition(xPos, yPos);
            startLetterNode = selectedNode;
        }

        float xDelta = xPos - (startLetterNode.getX() + startLetterNode.getWidth() * 0.5f);
        float yDelta = yPos - (startLetterNode.getY() + startLetterNode.getHeight() * 0.5f);
        float distance = (float)Math.hypot(xDelta, yDelta);

        Direction previousDirection = mSelectionDirection;
        int previousSteps = this.mSelectionSteps;
        mSelectionDirection = Direction.getDirection((float) Math.atan2(yDelta, xDelta));

        float mColumnWidth = selectedNode.getWidth();
        float stepSize = mSelectionDirection.isAngle() ? (float) Math.hypot(mColumnWidth, mColumnWidth) : mColumnWidth;
        mSelectionSteps = Math.round((float) distance / stepSize);

        if(mSelectionDirection != previousDirection || mSelectionSteps != previousSteps){
            Array<Letter> selectedViews = getSelectionViews();
            if (selectedViews.isEmpty()) {
                return;
            }

            if(!selectedViews.isEmpty() && (selectedViews.first().thrown || selectedViews.first().winded)){
                clearSelectionData();
                return;
            }

            for(Letter letter : selectedViews)
                if(letter.thrown || letter.winded) return;

            if(selectedViews.isEmpty()){
                return;
            }

            if(this.mPreviousSelection.size > 0){
                oldViews.clear();
                oldViews.addAll(mPreviousSelection);
                oldViews.removeAll(selectedViews, true);

                for (Letter letter : oldViews) {
                    if(!letter.isSolved()) letter.setLabelColor(Letter.mDefaultColor);
                    letter.resetAnimated();
                }
            }

            mPreviousSelection.clear();
            mPreviousSelection.addAll((selectedViews));
            if (!selectedViews.isEmpty()) {
                for (Letter letter : selectedViews) letter.setLabelColor(Letter.mSelectionColor);
            }

            if(mSelectionSteps != previousSteps){
                if(selectedViews.size > 0){
                    selectedViews.get(selectedViews.size - 1).animateFirstLetter();
                }
                sfx();
            }

            drawCurrentSelection();
            gameController.selectingLetters(constructSelectedText());
        }
    }





    private boolean isPointInsideView(float x, float y, Letter letter){
        float margin = letter.getWidth() * 0.1f ;
        float left = letter.originalX + margin;
        float right = letter.originalX + letter.getWidth() - margin;
        float bottom = letter.originalY + margin;
        float top = letter.originalY + letter.getHeight() - margin;
        return x > left && x < right && y > bottom && y < top;
    }



    private Image getSelectionImage(){
        Image image = null;

        switch (letters.size){
            case 3:
                image = new Image(NinePatches.selection_3x3);
                break;
            case 4:
                image = new Image(NinePatches.selection_4x4);
                break;
            case 5:
                image = new Image(NinePatches.selection_5x5);
                break;
            case 6:
                image = new Image(NinePatches.selection_6x6);
                break;
            case 7:
                image = new Image(NinePatches.selection_7x7);
                break;
            case 8:
                image = new Image(NinePatches.selection_8x8);
                break;
        }

        image.setOrigin(image.getWidth() * 0.5f, image.getHeight() * 0.5f);

        return image;
    }




    private Image getSelectionAnimImage(){
        switch (letters.size){
            case 3: return new Image(NinePatches.sel_anim_3x3);
            case 4: return new Image(NinePatches.sel_anim_4x4);
            case 5: return new Image(NinePatches.sel_anim_5x5);
            case 6: return new Image(NinePatches.sel_anim_6x6);
            case 7: return new Image(NinePatches.sel_anim_7x7);
            case 8: return new Image(NinePatches.sel_anim_8x8);
            default: return null;
        }

    }



    private void drawCurrentSelection(){
        if(mPreviousSelection.size == 1) currentSelectionImage.setVisible(false);
        currentSelectionImage.setColor(ColorConfig.GAME_BOARD_COLORS[completedWords.size]);
        drawSelection(currentSelectionImage, mPreviousSelection, mSelectionDirection);

        if(mPreviousSelection.size == 1){
            container.addActorBefore(letters.get(0).get(0), currentSelectionImage);
            if(!currentSelectionImage.isVisible()) currentSelectionImage.setVisible(true);
        }
    }




    private void drawCorrectAnswer(){
        Image selection = getSelectionImage();
        correctAnswerSelectionImages.add(selection);
        Color color = currentSelectionImage.getColor();
        color.a = UIConfig.COMPLETED_WORD_SELECTION_COLOR_OPACITY;
        selection.setColor(color);
        drawSelection(selection, mPreviousSelection, mSelectionDirection);
        container.addActorAt(1 + completedWords.size, selection);
    }





    private void drawSelection(Image image, Array<Letter> selection, Direction direction){
        float mColumnWidth =selection.get(0).getWidth();

        float stepSize = direction.isAngle() ? (float) Math.hypot(mColumnWidth, mColumnWidth) : mColumnWidth;
        float lineLength = stepSize * selection.size;
        float extra = (mColumnWidth - image.getHeight()) * 0.5f;

        image.setY(selection.get(0).getY() + extra);
        image.setX(selection.get(0).getX() + extra);

        if(direction.isAngle()){
            float dst = (stepSize - image.getHeight());
            lineLength -= dst;
        }else{
            lineLength -= extra * 2f;
        }

        image.setWidth(lineLength);
        image.setRotation(-direction.getAngleDegree());
    }





    private Letter pointToLetterNode(float x, float y){
        pointToIndices(x, y);
        if(selectionI < 0 || selectionJ < 0 || selectionI >= mColumns || selectionJ >= mColumns) return null;
        return letters.get(selectionI).get(selectionJ);
    }



    public static void rotatePosition(Actor actor, float x, float y, Vector2 vec2){
        float a = MathUtils.degreesToRadians * actor.getRotation();
        float centerX = actor.getWidth() * 0.5f;

        float cosX = MathUtils.cos(a);
        float sinX = MathUtils.sin(a);

        float newX = centerX + ( cosX * (x-centerX) + sinX * (y -centerX));
        float newY = centerX + ( -sinX * (x-centerX) + cosX * (y -centerX));

        vec2.x = newX;
        vec2.y = newY;
    }




    private void pointToIndices(float x, float y){
        selectionI = (int)Math.floor(x / getHeight() * letters.size);
        selectionJ = (int)Math.floor(y / getWidth() * letters.get(0).size);
    }



    private int pointToPosition(float x, float y){
        pointToIndices(x, y);
        return selectionJ + (selectionI * letters.size);
    }



    private Letter getChildAtPosition(int index){
        int total = letters.size;
        return letters.get((int) Math.floor(index / total)).get(index % total);
    }


    private Array<Letter> selectionViews = new Array<>();

    private Array<Letter> getSelectionViews() {
        if (mSelStartPosition == -1 || mSelectionDirection == null) {
            return null;
        }

        selectionViews.clear();

        for (Integer position : getPositionsOnPath(mSelectionDirection, mSelStartPosition, mSelectionSteps)) {
            selectionViews.add(getChildAtPosition(position));
        }
        return selectionViews;
    }




    public Array<Letter> getSelectedViewsInstant(){
        return mPreviousSelection;
    }


    private Array<Integer> positionsOnPath = new Array<>();


    public Array<Integer> getPositionsOnPath(Direction direction, int startPosition, int steps) {
        positionsOnPath.clear();
        int curRow = startPosition % mColumns;
        int curCol = (int)Math.floor(startPosition / mColumns);

        for (int i = 0; i <= steps; i++) {
            positionsOnPath.add((curCol * mColumns) + curRow);

            if (direction.isUp()) {
                curRow += 1;
            } else if (direction.isDown()) {
                curRow -= 1;
            }

            if (direction.isLeft()) {
                curCol -= 1;
            } else if (direction.isRight()) {
                curCol += 1;
            }

            if (curRow < 0 || curCol < 0 || curRow >= mRows || curCol >= mColumns) {
                break;
            }
        }
        return positionsOnPath;
    }





    private void selectionFinished(){
        if(tutorialFindWord != null) tutorialFindWord.selectingLetters(false);

        if(mPreviousSelection.isEmpty()){
            clearSelection();
            clearSelectionData();
            return;
        }

        if(letters.isEmpty()) return;

        String answer = this.constructSelectedText();

        if(answer.length() <= 2){
            clearSelection();
            clearSelectionData();
            gameController.gameScreen.preview.hide();
            return;
        }

        if(isWordAnsweredWordBefore(answer)){
            clearSelection();
            clearSelectionData();
            gameController.gameScreen.wordsView.shakeWord(answer);
            gameController.gameScreen.preview.hide();
            if(!ConfigProcessor.mutedSfx) {
                Sound sound = gameController.gameScreen.wordGame.resourceManager.get(ResourceManager.word_found_before, Sound.class);
                sound.play();
            }

            return;
        }
        gameController.evaluateAnswer(answer);
    }




    public void clearSelection(){
        if (this.mSelectionDirection != null && this.mSelectionSteps > -1) {
            for(Letter letter : mPreviousSelection) {
                if(!letter.isSolved()) letter.setLabelColor(Letter.mDefaultColor);
                letter.resetAnimated();
            }
        }
        currentSelectionImage.setRotation(0);
        currentSelectionImage.remove();
    }




    public void clearSelectionData(){
        mSelStartPosition = -1;
        mSelectionSteps = -1;
        mSelectionDirection = null;
        startLetterNode = null;
        if(mPreviousSelection != null) mPreviousSelection.clear();
    }




    private String constructSelectedText(){
        answerStringBuilder.clear();

        for(Letter letter : mPreviousSelection) {
            answerStringBuilder.append(letter.c);
        }

        return answerStringBuilder.toString();
    }




    private boolean isWordAnsweredWordBefore(String answer){
        int index = Word.findIndexOfAnswer(answer, level.words);
        if(index == -1) return false;
        return completedWords.indexOf(index, true) > -1;
    }




    private void sfx(){
        if(ConfigProcessor.mutedSfx) return;
        String file = null;
        int size = mPreviousSelection.size;

        if(size == 1) file      = ResourceManager.letter_select_01;
        else if(size == 2) file = ResourceManager.letter_select_02;
        else if(size == 3) file = ResourceManager.letter_select_03;
        else if(size == 4) file = ResourceManager.letter_select_04;
        else if(size == 5) file = ResourceManager.letter_select_05;
        else if(size == 6) file = ResourceManager.letter_select_06;
        else if(size == 7) file = ResourceManager.letter_select_07;
        else if(size == 8) file = ResourceManager.letter_select_08;

        if(file != null){
            Sound sound = gameController.gameScreen.wordGame.resourceManager.get(file, Sound.class);
            sound.play();
        }
    }



    public void answeredCorrect(Word word){
        if(tutorialFindWord != null) tutorialFindWord.answeredCorrect();

        word.solved = true;

        for(int i = 0; i < mPreviousSelection.size; i++){
            Letter letter = mPreviousSelection.get(i);
            letter.setSolved();

            if(i == 0 /*|| i == mPreviousSelection.size - 1*/){
                removeHint(word, letter);
            }

            String reversed = new StringBuilder(word.answer).reverse().toString();//for words like radar
            if(reversed.equals(word.answer) && i == mPreviousSelection.size - 1) removeHint(word, letter);
        }

        drawCorrectAnswer();
        animateCorrectSelectionFrame();
        int index = level.words.indexOf(word, false);
        completedWords.add(index);

        SaveData saveData = new SaveData();
        saveData.selectionDirection = mSelectionDirection;
        saveData.positions = getPositionsOnPath(mSelectionDirection, mSelStartPosition, mSelectionSteps);
        queue.addFirst(saveData);
    }


    private void removeHint(Word word, Letter letter){
        int position = Solver.findFirstLetterPosition(board2D, word);

        if(hintPositions.containsKey(position)){
            HintData hintData = hintPositions.get(position);
            hintData.solvedCount++;
            if(hintData.totalHints > 0 && hintData.usedCount > 0 && letter.hasHint()){
                hintData.consumed++;

                if(hintData.consumed > 0 && (hintData.usedCount == hintData.consumed || hintData.solvedCount == hintData.totalHints)){
                    letter.removeHint();
                }
            }
            saveSingleHint();
        }
    }



    public void throwLetter(Word word){
        findLettersToThrow();
        if(lettersToThrow.isEmpty()) {
            int index = level.words.indexOf(word, false);
            if(!levelEnded()) saveCorrectAnswer(index, -1);
            clearSelectionData();
            return;
        }

        int thrownLetterIndex = -1;

        if(completedWords.size < level.words.size) {
            Letter letter = lettersToThrow.random();
            thrownLetterIndex = letter.index;
            throwLetter(letter);
        }else{
            for(int i = 0; i < lettersToThrow.size; i++) {
                throwLetter(lettersToThrow.get(i));
            }
        }
        int index = level.words.indexOf(word, false);
        if(!levelEnded()) saveCorrectAnswer(index, thrownLetterIndex);
        clearSelectionData();
    }




    private void animateCorrectSelectionFrame(){
        Image ref = correctAnswerSelectionImages.get(correctAnswerSelectionImages.size - 1);

        selectionAnimImage.setColor(ref.getColor());
        selectionAnimImage.setSize(ref.getWidth(), ref.getHeight());
        selectionAnimImage.setOrigin(Align.center);
        selectionAnimImage.setRotation(ref.getRotation());

        float letterHeight = letters.get(0).get(0).getHeight();
        float cx = 0, cy = 0;

        if(mPreviousSelection.size % 2 != 0){
            Letter middle = mPreviousSelection.get(mPreviousSelection.size / 2);
            cx = middle.getX();
            cy = middle.getY();
        }else{
            Letter left = mPreviousSelection.get((int)Math.floor(mPreviousSelection.size / 2) - 1);
            Letter right = mPreviousSelection.get((int)Math.ceil(mPreviousSelection.size / 2));

            float offsetx = Math.min(left.getX(), right.getX());
            float offsety = Math.min(left.getY(), right.getY());

            cx = offsetx + Math.abs(right.getX() - left.getX()) * 0.5f;
            cy = offsety + Math.abs(right.getY() - left.getY()) * 0.5f;
        }

        selectionAnimImage.setPosition(cx + letterHeight * 0.5f - selectionAnimImage.getWidth() * 0.5f, cy + (letterHeight - selectionAnimImage.getHeight()) * 0.5f);
        if(selectionAnimImage.getParent() == null) container.addActor(selectionAnimImage);

        float sy = 1.3f;
        float targetHeight = selectionAnimImage.getHeight() * sy;
        float diff = targetHeight - selectionAnimImage.getHeight();
        float targetWidth = selectionAnimImage.getWidth() + diff;
        float sx = targetWidth / selectionAnimImage.getWidth();

        float time = 0.7f;

        selectionAnimImage.setVisible(true);
        selectionAnimImage.addAction(Actions.sequence(
                Actions.parallel(
                        Actions.scaleTo(sx, sy, time, Interpolation.fastSlow),
                        Actions.sequence(
                                Actions.delay(time * 0.5f),
                                Actions.fadeOut(time * 0.5f, Interpolation.fastSlow)
                        )

                ),
                Actions.run(frameAnimFinished)
        ));
    }



    private Runnable frameAnimFinished = new Runnable() {
        @Override
        public void run() {
            selectionAnimImage.setVisible(false);
            selectionAnimImage.setScale(1f);
        }
    };




    public boolean thrownLetter(){
        return !lettersToThrow.isEmpty();
    }



    public Array<Letter> findViewsForAnswer(String answer){
        Set<Integer> positions = new LinkedHashSet<>();
        Solver.findPositions(board2D, answer, positions);

        Array<Letter> letters = new Array<>();
        for(Integer i : positions) letters.add(getChildAtPosition(i));
        return letters;
    }



    public void setTutorialMode(TutorialFindWord tutorialFindWord){
        this.tutorialFindWord = tutorialFindWord;
    }



    public void unsetTutorialMode(){
        tutorialFindWord = null;
    }




    private void findLettersToThrow(){
        allWordsPositions.clear();

        for(Word word : level.words) Solver.findPositions(board2D, word.answer, allWordsPositions);

        allBoardPositions.clear();
        for(int i = 0; i < level.boardData.length; i++) allBoardPositions.add(i);

        allBoardPositions.removeAll(allWordsPositions);
        lettersToThrow.clear();

        for(Integer position : allBoardPositions) {
            Letter letter = getChildAtPosition(position);
            if(!letter.thrown && !letter.winded) lettersToThrow.add(letter);
        }
    }




    private void throwLetter(Letter letter){
        letter.thrown = true;
        letter.throwLabel();
    }




    private void saveCorrectAnswer(int index, int thrownLetterIndex){
        while(!queue.isEmpty()){
            SaveData saveData = queue.removeLast();
            if(saveData.selectionDirection == null) continue;
            Level.saveCorrectAnswer(index, thrownLetterIndex, saveData.selectionDirection, saveData.positions);
        }
    }




    private void restoreIncompleteLevel(){
        String existingJsonString = DataManager.get(DataManager.getLocaleAwareKey(Constants.SAVED_LEVEL), null);
        if(existingJsonString != null && !existingJsonString.equals("[]")) {

            JsonValue doc = jsonReader.parse(existingJsonString);

            for (int i = 0; i < doc.size; i++) {
                JsonValue jsonValue = doc.get(i);
                int index = jsonValue.getInt("index");

                if (!jsonValue.has("direction")) continue;

                Direction direction = Direction.values()[jsonValue.get("direction").asInt()];

                Color color = ColorConfig.GAME_BOARD_COLORS[completedWords.size];
                color.a = UIConfig.COMPLETED_WORD_SELECTION_COLOR_OPACITY;

                Image image = getSelectionImage();
                image.setColor(color);
                correctAnswerSelectionImages.add(image);

                JsonValue positions = jsonValue.get("positions");
                Array<Letter> lettersToSelect = new Array<>();
                for (int j = 0; j < positions.size; j++) {
                    Letter letter = getChildAtPosition(positions.get(j).asInt());
                    letter.setLabelColor(Letter.mSelectionColor);
                    lettersToSelect.add(letter);
                    letter.setSolved();
                }

                container.addActorBefore(letters.get(0).get(0), image);
                drawSelection(image, lettersToSelect, direction);

                if(jsonValue.has("thrown")) {
                    int thrown = jsonValue.getInt("thrown");
                    Letter letter = getChildAtPosition(thrown);
                    letter.setVisible(false);
                    letter.thrown = true;
                }

                level.words.get(index).solved = true;
                completedWords.add(index);
            }
        }

        String windedJson = DataManager.get(DataManager.getLocaleAwareKey(Constants.WIND_HINTED_INDICES), null);
        if(windedJson == null) return;

        JsonValue doc = jsonReader.parse(windedJson);

        for(int i = 0; i < doc.size; i++){
            Letter letter = getChildAtPosition(doc.get(i).asInt());
            letter.winded = true;
            letter.setScale(0);
        }
    }



    public boolean levelEnded(){
        return completedWords.size == level.words.size;
    }


    private ArrayMap<Integer, HintData> hintPositions = new ArrayMap<>();




    //Finds the start of words that are shared more than one word.
    private void createHintMapping(){
        String existingJsonString = DataManager.get(DataManager.getLocaleAwareKey(Constants.BOARD_HINT_MAPPING), null);

        if(existingJsonString != null){
            restoreHintMapping(existingJsonString);
            return;
        }

        for(Word word : level.words){
            int position = Solver.findFirstLetterPosition(board2D, word);

            if(!hintPositions.containsKey(position)){
                HintData hintData = Pools.obtain(HintData.class);
                hintData.totalHints = 1;
                hintPositions.put(position, hintData);
            }else{
                HintData hintData = hintPositions.get(position);
                hintData.totalHints++;
            }

        }

    }



    private void restoreHintMapping(String existingJsonString){
        if(existingJsonString != null) {
            JsonValue doc = jsonReader.parse(existingJsonString);
            for (int i = 0; i < doc.size; i++) {
                JsonValue jsonValue = doc.get(i);
                int position = jsonValue.getInt("position");

                HintData hintData = Pools.obtain(HintData.class);
                hintData.totalHints = jsonValue.getInt("total");
                hintData.usedCount = jsonValue.getInt("used");
                hintData.solvedCount = jsonValue.getInt("solved");
                hintData.consumed = jsonValue.getInt("consumed");
                hintPositions.put(position, hintData);

                if(hintData.usedCount > 0 &&  hintData.consumed < hintData.usedCount){
                    Letter letter = getChildAtPosition(position);
                    letter.revealHint();
                }
            }
        }
    }




    private int findPositionToGiveSingleHint(){
        ArrayMap.Keys<Integer> keys = hintPositions.keys();
        while(keys.hasNext()){
            Integer position = keys.next();
            HintData hintData = hintPositions.get(position);
            if(hintData.solvedCount >= hintData.totalHints) continue;
            if(hintData.usedCount >= hintData.totalHints) continue;
            if(hintData.solvedCount + (hintData.usedCount - hintData.consumed) >= hintData.totalHints) continue;

            hintData.usedCount++;
            return position;
        }
        return -1;
    }





    private void saveSingleHint(){
        JsonValue array = new JsonValue(JsonValue.ValueType.array);

        ArrayMap.Keys<Integer> keys = hintPositions.keys();
        while(keys.hasNext()) {
            Integer position = keys.next();
            HintData hintData = hintPositions.get(position);

            JsonValue obj = new JsonValue(JsonValue.ValueType.object);
            obj.addChild("position", new JsonValue(position));
            obj.addChild("total", new JsonValue(hintData.totalHints));
            obj.addChild("used", new JsonValue(hintData.usedCount));
            obj.addChild("solved", new JsonValue(hintData.solvedCount));
            obj.addChild("consumed", new JsonValue(hintData.consumed));//incremented when a hinted position is solved
            array.addChild(obj);
        }
        DataManager.set(DataManager.getLocaleAwareKey(Constants.BOARD_HINT_MAPPING), array.toJson(JsonWriter.OutputType.json));
    }




    public boolean deliverSingleHint(){
        int position = findPositionToGiveSingleHint();
        if(position == -1){
            return false;
        }

        getStage().getRoot().setTouchable(Touchable.disabled);
        saveSingleHint();
        Letter letter = getChildAtPosition(position);
        letter.revealHint();

        Ring ring = Pools.obtain(Ring.class);
        ring.setTarget(letter, ringCallback);
        letter.getParent().addActor(ring);
        ring.animate();

        if(!ConfigProcessor.mutedSfx) {
            Sound reveal = gameController.gameScreen.wordGame.resourceManager.get(ResourceManager.board_reveal, Sound.class);
            reveal.play();
        }

        if(getStage() != null) getStage().getRoot().setTouchable(Touchable.enabled);

        return true;
    }




    private Ring.RingCallback ringCallback = new Ring.RingCallback() {

        @Override
        public void part1Finished(Actor target) {
            ((Letter)target).revealHint();
        }

        @Override
        public void part2Finished(Actor target) {
            getStage().getRoot().setTouchable(Touchable.enabled);
        }

    };




    public void show(boolean in){
        addAction(Actions.sequence(
                Actions.delay(in ? CategoryRibbon.ANIM_TIME_1 + CategoryRibbon.ANIM_TIME_2 : 0f),
                Actions.parallel(
                        (in ? Actions.fadeIn(CategoryRibbon.ANIM_TIME_3, Interpolation.fastSlow) : Actions.fadeOut(CategoryRibbon.ANIM_TIME_3, Interpolation.fastSlow)),
                        Actions.moveTo(getX(), in ? Board.POSITION_Y : Board.POSITION_Y - CategoryRibbon.ANIM_MOVE_DST, CategoryRibbon.ANIM_TIME_3, Interpolation.fastSlow)
                )
            )
        );
    }



    public boolean magicReveal(){
        findLettersToThrow();
        lettersToThrow.shuffle();
        int count = Math.min(GameConfig.maxNumberOfLettersToRemoveForMagicWand(mRows), lettersToThrow.size);

        if(count > 0){
            getStage().getRoot().setTouchable(Touchable.disabled);
            if(magicWand == null){
                magicWand = new MagicWand();
                magicWand.setX((getWidth() - magicWand.getWidth()) * 0.5f);
                magicWand.setY(-getY() - magicWand.getHeight());
            }
            magicWand.getColor().a = 0;
            addActor(magicWand);

            float cos = MathUtils.cos(-1.5054374f);
            float sin = MathUtils.sin(-1.5054374f);
            float radius = (getHeight() - magicWand.getHeight()) * 0.5f;
            float center = radius;
            magicWand.setPosition(center + cos * radius, center + sin * radius);

            magicWand.addAction(Actions.sequence(
                    Actions.fadeIn(0.3f),
                    Actions.run(magic1)
            ));

            return true;
        }
        return false;
    }




    private Runnable magic1 = new Runnable() {

        @Override
        public void run() {
            FloatAction floatAction = new FloatAction(){

                float radius = (getHeight() - magicWand.getHeight()) * 0.5f;
                float center = radius;

                @Override
                protected void update(float percent) {
                    super.update(percent);
                    float angle = MathUtils.lerp(- 90 * MathUtils.degreesToRadians, (360 - 90) * MathUtils.degreesToRadians, percent);

                    float cos = MathUtils.cos(angle);
                    float sin = MathUtils.sin(angle);
                    magicWand.setPosition(center + cos * radius, center + sin * radius);

                }
            };

            floatAction.setDuration(1.7f);

            magicWand.addAction(Actions.sequence(
                    floatAction,
                    Actions.run(magicWandMotionEnd)
            ));

            magicWand.startSparkle();

            if(!ConfigProcessor.mutedSfx) {
                Sound sound = gameController.gameScreen.wordGame.resourceManager.get(ResourceManager.magic, Sound.class);
                sound.play();
            }
        }
    };




    private Runnable magicWandMotionEnd = new Runnable() {
        @Override
        public void run() {
            performMagicReveal();
            magicWand.stopSparkle();
            magicWand.addAction(Actions.sequence(
                    Actions.fadeOut(0.5f, Interpolation.fastSlow),
                    Actions.run(magicWandHidden)
            ));
        }
    };



    private Runnable magicWandHidden = new Runnable() {
        @Override
        public void run() {
            magicWand.remove();
        }
    };



    public void performMagicReveal(){
        int count = Math.min(GameConfig.maxNumberOfLettersToRemoveForMagicWand(mRows), lettersToThrow.size);
        int[] indices = new int[count];

        for(int i = 0; i < count; i++){
            Letter letter = lettersToThrow.get(i);
            letter.winded = true;
            indices[i] = letter.index;

            letter.addAction(Actions.parallel(
                    Actions.rotateBy(720, 0.5f, Interpolation.slowFast),
                    Actions.scaleTo(0, 0, 0.5f, Interpolation.slowFast),
                    Actions.run(magicPerformEnd)
            ));
        }
        Level.saveWindHintIndices(indices);
    }


    private Runnable magicPerformEnd = new Runnable() {
        @Override
        public void run() {
            getStage().getRoot().setTouchable(Touchable.enabled);
        }
    };


}
