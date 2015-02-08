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
                case MODE_CUT :
                    dr = (Drawable) context.getResources().getDrawable(android.R.drawable.ic_menu_crop);
                    dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                    dr.draw(canvas);
                    break;
                default:
                    dr = (Drawable) context.getResources().getDrawable(android.R.drawable.btn_default);
                    dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                    dr.draw(canvas);
                    break;
            }
        }
    }
}
