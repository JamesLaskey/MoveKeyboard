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

    private static final int MODE_SWITCH = 1000;
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

    private void sendKeyUpDown(InputConnection ic, int keyCode) {
        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyCode));
    }

    private void onMoveKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        switch(primaryCode) {
            case PARA_UP :
                jumpBackward(ic, new char[] {'\n'});
                break;
            case PARA_DOWN :
                jumpForward(ic, new char[] {'\n'});
                break;
            case PAGE_UP :
                sendKeyUpDown(ic, KeyEvent.KEYCODE_PAGE_UP);
                break;
            case PAGE_DOWN :
                sendKeyUpDown(ic, KeyEvent.KEYCODE_PAGE_DOWN);
                break;
            case LINE_START :
                sendKeyUpDown(ic, KeyEvent.KEYCODE_HOME);
                break;
            case LINE_END :
                sendKeyUpDown(ic, KeyEvent.KEYCODE_MOVE_END);
                break;
            case LINE_UP :
                sendKeyUpDown(ic, KeyEvent.KEYCODE_DPAD_UP);
                break;
            case LINE_DOWN :
                sendKeyUpDown(ic, KeyEvent.KEYCODE_DPAD_DOWN);
                break;
            case WORD_FOR :

                break;
            case WORD_BACK :

                break;
            case CHAR_BACK :
                sendKeyUpDown(ic, KeyEvent.KEYCODE_DPAD_LEFT);
                break;
            case CHAR_FOR :
                sendKeyUpDown(ic, KeyEvent.KEYCODE_DPAD_RIGHT);
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


    private boolean cmpChars(char toCheck, char[] against) {
        for(char c : against) {
            if (toCheck == c) {
                return true;
            }
        }
        return false;
    }

    private void jumpForward(InputConnection ic, char[] seekCharacters) {
        if (ic == null) {
            System.err.println("ic null");
            return;
        }
        int cursorPos = -1;
        int prevSize = 0;
        boolean found = false;
        CharSequence textAfter = null;
        while(!found) {
            textAfter = ic.getTextAfterCursor(prevSize + PARA_SIZE_GUESS, 0);
            int start = 1;
            if(textAfter.length() > 0 &&
                    cmpChars(textAfter.charAt(0), seekCharacters) &&
                    start == 0) {
                start++;
            }
            for (int i = prevSize; i < textAfter.length(); i++) {
                if (cmpChars(textAfter.charAt(i), seekCharacters)) {
                    System.err.println("found");
                    cursorPos = i;
                    found = true;
                    break;
                }
            }
            if (prevSize == textAfter.length()) {
                break;
            }
            prevSize = textAfter.length();
        }
        // no new lines in doc
        if (!found) {
            ic.commitText("", textAfter.length());
            return;
        }

        ic.commitText("", cursorPos);
    }

    private void jumpBackward(InputConnection ic, char[] seekCharacters) {
        if (ic == null) {
            System.err.println("ic null");
            return;
        }
        int cursorPos = -1;
        int prevSize = 0;
        boolean found = false;
        CharSequence textBefore = null;
        while(!found) {
            textBefore = ic.getTextBeforeCursor(prevSize + PARA_SIZE_GUESS, 0);
            int start = (PARA_SIZE_GUESS - 1 > textBefore.length()) ? textBefore.length() - 1: PARA_SIZE_GUESS - 1;
            if(textBefore.length() > 0 &&
                    cmpChars(textBefore.charAt(textBefore.length()-1), seekCharacters) &&
                    start == textBefore.length()-1) {
                start--;
            }
            for (int i = start; i >= 0; i--) {
                if (cmpChars(textBefore.charAt(i), seekCharacters)) {
                    System.err.println("found");
                    cursorPos = prevSize + PARA_SIZE_GUESS - i;
                    found = true;
                    break;
                }
            }
            if (prevSize == textBefore.length()) {
                break;
            }
            prevSize = textBefore.length();
        }
        // no new lines in doc
        if (!found) {
            ic.commitText("", -1 * textBefore.length());
            return;
        }

        ic.commitText("", -1 * cursorPos + 1);
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
