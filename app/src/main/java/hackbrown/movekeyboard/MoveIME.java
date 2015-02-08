package hackbrown.movekeyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.LinearLayout;

/**
 * Created by jim on 2/7/15.
 */
public class MoveIME extends InputMethodService implements KeyboardView.OnKeyboardActionListener{

    private KeyboardView kvInsert;
    private Keyboard keyboardInsert;
    private Keyboard keyboardMove;
    private KeyboardView kvMove;

    private boolean caps = false;
    private boolean moveMode = false;


    private static final int PARA_UP = 1;
    private static final int PARA_DOWN = 2;
    private static final int LINE_START = 3;
    private static final int LINE_END = 4;
    private static final int WORD_FOR = 5;
    private static final int WORD_BACK = 6;
    private static final int CHAR_FOR = 7;
    private static final int CHAR_BACK = 8;
    private static final int LINE_UP = 9;
    private static final int LINE_DOWN = 10;
    private static final int PAGE_UP = 11;
    private static final int PAGE_DOWN = 12;

    private static final int MODE_SWITCH = 100;
    private static final int MODE_SELECT = 101;
    private static final int MODE_DELETE = 102;
    private static final int MODE_COPY = 103;
    private static final int MODE_CUT = 104;
    private static final int MODE_PASTE = 105;
    private static final int MODE_NUMVAL = 106;

    private int numVal = 0;
    private static final int PARA_SIZE_GUESS = 100;

    @Override
    public View onCreateInputView() {
        kvInsert = (KeyboardView)getLayoutInflater().inflate(R.layout.keyboard, null);
        keyboardInsert = new Keyboard(this, R.xml.qwerty);
        kvInsert.setKeyboard(keyboardInsert);
        kvInsert.setOnKeyboardActionListener(this);

        kvMove = (KeyboardView)getLayoutInflater().inflate(R.layout.movement, null);
        keyboardMove = new Keyboard(this, R.xml.move);
        kvMove.setKeyboard(keyboardMove);
        kvMove.setOnKeyboardActionListener(this);


        return kvInsert;
    }

    @Override
    public void onPress(int keyCode) {
        if(keyCode == MODE_NUMVAL) {
        }
    }

    @Override
    public void onRelease(int i) {

    }

    private void onMoveKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        switch(primaryCode) {
            case PARA_UP :
                paraJumpUp(ic);
                break;
            case PARA_DOWN :
                paraJumpDown(ic);
                break;
            case PAGE_UP :
                //super.onKeyDown(KeyEvent.KEYCODE_PAGE_UP);

                break;
            case PAGE_DOWN :

                break;
            case LINE_START :

                break;
            case LINE_END :

                break;
            case LINE_UP :

                break;
            case LINE_DOWN :

                break;
            case WORD_FOR :

                break;
            case WORD_BACK :

                break;
            case CHAR_BACK :

                break;
            case CHAR_FOR :

                break;
            case MODE_SWITCH :
                setInputView(kvInsert);
                moveMode = false;
                break;
            case MODE_SELECT :
                
                break;
            case MODE_COPY :

                break;
            case MODE_CUT :

                break;
            case MODE_PASTE :

                break;
            case MODE_DELETE :

                break;
            case MODE_NUMVAL :
                numVal = 0;
                kvMove.invalidateKey(10);
                keyboardMove.getKeys().get(9).label = Integer.toString(numVal);
                break;
        }
    }

    private void paraJumpDown(InputConnection ic) {

    }

    private void paraJumpUp(InputConnection ic) {
        CharSequence textAfter = ic.getTextBeforeCursor(3*PARA_SIZE_GUESS, 0);
        int cursorPos = -1;
        for (int i = textAfter.length()-1; i >= 0; i--) {
            if (textAfter.charAt(i) == '\n') {
                cursorPos = i + 1;
            }
        }
        ic.commitText("", cursorPos);


    }

    public void onInsertKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        switch(primaryCode){
            case Keyboard.KEYCODE_DELETE :
                ic.deleteSurroundingText(1, 0);
                break;
            case Keyboard.KEYCODE_SHIFT:
                caps = !caps;
                keyboardInsert.setShifted(caps);
                kvInsert.invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            case MODE_SWITCH :
                setInputView(kvMove);
                moveMode = true;
                break;
            default:
                char code = (char)primaryCode;
                if(Character.isLetter(code) && caps){
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code),1);
        }
    }
    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        if(moveMode) {
            onMoveKey(primaryCode, keyCodes);
        }else {
            onInsertKey(primaryCode, keyCodes);
        }
    }

    @Override
    public void onText(CharSequence charSequence) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}
