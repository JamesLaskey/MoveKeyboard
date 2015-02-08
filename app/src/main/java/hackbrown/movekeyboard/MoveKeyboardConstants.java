package hackbrown.movekeyboard;

/**
 * Created by jim on 2/8/15.
 */
public interface MoveKeyboardConstants {

    public enum Mode {
        MOVE,
        INSERT,
        SELECT
    }

    public static final int PARA_UP = 1;
    public static final int PARA_DOWN = 2;
    public static final int LINE_START = 3;
    public static final int LINE_END = 4;
    public static final int WORD_FOR = 5;
    public static final int WORD_BACK = 6;
    public static final int CHAR_FOR = 7;
    public static final int CHAR_BACK = 8;
    public static final int LINE_UP = 9;
    public static final int LINE_DOWN = 10;
    public static final int PAGE_UP = 11;
    public static final int PAGE_DOWN = 12;

    public static final int MODE_SWITCH = 1000;
    public static final int MODE_SELECT = 101;
    public static final int MODE_DELETE = 102;
    public static final int MODE_COPY = 103;
    public static final int MODE_CUT = 104;
    public static final int MODE_PASTE = 105;
    public static final int MODE_NUMVAL = 106;
    
}
