package co.bongga.touristeando.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by bongga on 2/8/17.
 */

public class TouryTextViewPier extends TextView {
    public TouryTextViewPier(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TouryTextViewPier(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TouryTextViewPier(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/Poppins-Regular.ttf");
        setTypeface(tf);
    }
}
