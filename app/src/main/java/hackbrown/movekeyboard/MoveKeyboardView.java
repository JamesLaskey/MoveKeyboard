package hackbrown.movekeyboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

import java.util.List;

/**
 * Created by jim on 2/8/15.
 */
public class MoveKeyboardView extends KeyboardView implements MoveKeyboardConstants{

    public MoveKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Context context = getContext();
        List<Keyboard.Key> keys = getKeyboard().getKeys();
        Drawable dr;
        for (Keyboard.Key key : keys) {
            switch(key.codes[0]) {
                case LINE_DOWN:
                    dr = (Drawable) context.getResources().getDrawable(R.drawable.ic_find_next_holo_dark);
                    dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                    dr.draw(canvas);
                    break;
                case LINE_UP:
                    dr = (Drawable) context.getResources().getDrawable(R.drawable.ic_find_previous_holo_dark);
                    dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                    dr.draw(canvas);
                    break;
                case CHAR_FOR:
                    dr = (Drawable) context.getResources().getDrawable(R.drawable.ic_find_right_holo_dark);
                    dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                    dr.draw(canvas);
                    break;
                case CHAR_BACK:
                    dr = (Drawable) context.getResources().getDrawable(R.drawable.ic_find_left_holo_dark);
                    dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                    dr.draw(canvas);
                    break;
                default:
                    break;
            }
            dr = (Drawable) context.getResources().getDrawable(R.drawable.keyboard_background);
            dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
            dr.setAlpha(100);
            dr.draw(canvas);
        }
    }
}
