package hackbrown.movekeyboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Created by jim on 2/7/15.
 */
public class MoveIME extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener, MoveKeyboardConstants{



    private KeyboardView kvInsert;
    private Keyboard keyboardInsert;
    private Keyboard keyboardMove;
    private KeyboardView kvMove;


    private CharSequence clipboardText = "";

    private boolean caps = false;
    private Mode keyMode = Mode.INSERT;


    private int numVal = 0;
    private static final int PARA_SIZE_GUESS = 100;




    @Override
    public View onCreateInputView() {
        kvInsert = (KeyboardView)getLayoutInflater().inflate(R.layout.keyboard, null);
        keyboardInsert = new Keyboard(this, R.xml.qwerty);
        kvInsert.setKeyboard(keyboardInsert);
        kvInsert.setOnKeyboardActionListener(this);

        kvMove = (MoveKeyboardView)getLayoutInflater().inflate(R.layout.movement, null);
        keyboardMove = new Keyboard(this, R.xml.move);
        kvMove.setKeyboard(keyboardMove);
        kvMove.setOnKeyboardActionListener(this);


        return kvInsert;
    }

    @Override
    public void onPress(int keyCode) {

    }

    @Override
    public void onRelease(int i) {

    }

    private void sendKeyUpDown(InputConnection ic, int keyCode) {
        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyCode));
    }

    private Keyboard.Key findKey(Keyboard kb, int keycode) {
        for(Keyboard.Key k : kb.getKeys()) {
            if(k.codes[0] == keycode) {
                return k;
            }
        }
        return null;
    }

    private void onMoveKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        int move;
        switch(primaryCode) {
            case PARA_UP :
                move = jumpBackward(ic, new char[] {'\n'});
                if(move != 0) {
                    ic.commitText("", move);
                }
                break;
            case PARA_DOWN :
                move = jumpForward(ic, new char[] {'\n'});
                if(move != 0) {
                    ic.commitText("", move);
                }
                break;
            case PAGE_UP :
                sendKeyUpDown(ic, KeyEvent.KEYCODE_PAGE_UP);
                break;
            case PAGE_DOWN :
                sendKeyUpDown(ic, KeyEvent.KEYCODE_PAGE_DOWN);
                break;
            case LINE_START :
                sendKeyUpDown(ic, KeyEvent.KEYCODE_MOVE_HOME);
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
                move = jumpForward(ic, new char[] {' ', '\t', '\n'});
                if(move != 0) {
                    ic.commitText("", move);
                }
                break;
            case WORD_BACK :
                move = jumpBackward(ic, new char[]{' ', '\t', '\n'});
                if(move != 0) {
                    ic.commitText("", move);
                }
                break;
            case CHAR_BACK :
                sendKeyUpDown(ic, KeyEvent.KEYCODE_DPAD_LEFT);
                break;
            case CHAR_FOR :
                sendKeyUpDown(ic, KeyEvent.KEYCODE_DPAD_RIGHT);
                break;
            case MODE_SWITCH :
                setInputView(kvInsert);
                keyMode = Mode.INSERT;
                break;
            case MODE_SELECT :
                keyMode = Mode.SELECT;
                Keyboard.Key key = findKey(keyboardMove, MODE_SELECT);
                key.label = "SELECT";
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

    private int jumpForward(InputConnection ic, char[] seekCharacters) {
        if (ic == null) {
            System.err.println("ic null");
            return 0;
        }
        int cursorPos = 0;
        int prevSize = 0;
        boolean found = false;
        CharSequence textAfter = null;
        while(!found) {
            textAfter = ic.getTextAfterCursor(prevSize + PARA_SIZE_GUESS, 0);
            if(textAfter.length() == 0) {
                return 0;
            }
            if(textAfter.length() > 0 &&
                    cmpChars(textAfter.charAt(0), seekCharacters) &&
                    prevSize == 0) {
                prevSize++;
            }
            for (int i = prevSize; i < textAfter.length(); i++) {
                if (cmpChars(textAfter.charAt(i), seekCharacters)) {
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
            return textAfter.length();
        }

        return cursorPos+1;
    }

    private int jumpBackward(InputConnection ic, char[] seekCharacters) {
        if (ic == null) {
            System.err.println("ic null");
            return 0;
        }
        int cursorPos = -1;
        int prevSize = 0;
        boolean found = false;
        CharSequence textBefore = null;
        while(!found) {
            textBefore = ic.getTextBeforeCursor(prevSize + PARA_SIZE_GUESS, 0);
            if(textBefore.length() == 0) {
                return 0;
            }
            int start = (PARA_SIZE_GUESS - 1 > textBefore.length()) ? textBefore.length() - 1: PARA_SIZE_GUESS - 1;
            if(textBefore.length() > 0 &&
                    cmpChars(textBefore.charAt(textBefore.length()-1), seekCharacters) &&
                    start == textBefore.length()-1) {
                start--;
            }
            for (int i = start; i >= 0; i--) {
                if (cmpChars(textBefore.charAt(i), seekCharacters)) {
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
            return -1 * textBefore.length();
        }

        return -1 * cursorPos + 1;
    }


    private void onInsertKey(int primaryCode, int[] keyCodes) {
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
                keyMode = Mode.MOVE;
                break;
            default:
                char code = (char)primaryCode;
                if(Character.isLetter(code) && caps){
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code),1);
        }
    }


    private void onSelectKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        switch(primaryCode) {
            case PARA_UP : {
                int jump = jumpForward(ic, new char[]{'\n'});
                ExtractedText et = ic.getExtractedText(new ExtractedTextRequest(), 0);
                ic.setSelection(et.selectionStart, et.selectionEnd + jump);
                break;
            }
            case PARA_DOWN : {
                int jump = jumpBackward(ic, new char[]{'\n'});
                ExtractedText et = ic.getExtractedText(new ExtractedTextRequest(), 0);
                ic.setSelection(et.selectionStart + jump, et.selectionEnd);
                break;
            }
            case PAGE_UP :
                setSelectionToKeyStroke(ic, KeyEvent.KEYCODE_PAGE_UP, true);
                break;
            case PAGE_DOWN :
                setSelectionToKeyStroke(ic, KeyEvent.KEYCODE_PAGE_DOWN, false);
                break;
            case LINE_START :
                setSelectionToKeyStroke(ic, KeyEvent.KEYCODE_MOVE_HOME, true);
                break;
            case LINE_END :
                setSelectionToKeyStroke(ic, KeyEvent.KEYCODE_MOVE_END, false);
                break;
            case LINE_UP :
                setSelectionToKeyStroke(ic, KeyEvent.KEYCODE_DPAD_UP, true);
                break;
            case LINE_DOWN :
                setSelectionToKeyStroke(ic, KeyEvent.KEYCODE_DPAD_DOWN, false);
                break;
            case WORD_FOR : {
                int jump = jumpForward(ic, new char[]{' ', '\t', '\n'});
                ExtractedText et = ic.getExtractedText(new ExtractedTextRequest(), 0);
                ic.setSelection(et.selectionStart, et.selectionEnd + jump);
                break;
            }
            case WORD_BACK : {
                int jump = jumpBackward(ic, new char[]{' ', '\t', '\n'});
                ExtractedText et = ic.getExtractedText(new ExtractedTextRequest(), 0);
                ic.setSelection(et.selectionStart + jump, et.selectionEnd);
                break;
            }
            case CHAR_BACK : {
                ExtractedText et = ic.getExtractedText(new ExtractedTextRequest(), 0);
                ic.setSelection(et.selectionStart - 1, et.selectionEnd);
                break;
            }
            case CHAR_FOR : {
                ExtractedText et = ic.getExtractedText(new ExtractedTextRequest(), 0);
                ic.setSelection(et.selectionStart, et.selectionEnd + 1);
                break;
            }
            case MODE_SWITCH :
                setInputView(kvInsert);
                keyMode = Mode.INSERT;
                break;
            case MODE_SELECT :
                keyMode = Mode.MOVE;
                Keyboard.Key key = findKey(keyboardMove, MODE_SELECT);
                key.label = "select";
                break;
            case MODE_COPY :
                saveText(ic);
                keyMode = Mode.MOVE;
                break;
            case MODE_CUT :
                saveText(ic);
                deleteSelection(ic);
                keyMode = Mode.MOVE;
                break;
            case MODE_PASTE :
                deleteSelection(ic);
                ic.commitText(clipboardText, 1);
                keyMode = Mode.MOVE;
                break;
            case MODE_DELETE :
                deleteSelection(ic);
                break;
            case MODE_NUMVAL :
                break;

        }
    }


    private void setSelectionToKeyStroke(InputConnection ic, int keyCode, boolean isUp) {
        ExtractedText curr = ic.getExtractedText(new ExtractedTextRequest(), 0);
        sendKeyUpDown(ic, keyCode);
        ExtractedText newt = ic.getExtractedText(new ExtractedTextRequest(), 0);
        if (isUp) {
            ic.setSelection(newt.selectionStart, curr.selectionEnd);
        } else {
            ic.setSelection(curr.selectionStart, newt.selectionEnd);
        }

    }

    private boolean saveText(InputConnection ic) {
        CharSequence text = ic.getSelectedText(0);
        if (text != null && !text.equals("")) {
            //ic.performContextMenuAction(action);
            clipboardText = text;
            return true;
        }
        return false;
    }

    private void deleteSelection(InputConnection ic) {
        //suspect from SO
        ic.commitText("", 1);
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        switch(keyMode) {
            case MOVE:
                onMoveKey(primaryCode, keyCodes);
                break;
            case INSERT:
                onInsertKey(primaryCode, keyCodes);
                break;
            case SELECT:
                onSelectKey(primaryCode, keyCodes);
                break;
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
