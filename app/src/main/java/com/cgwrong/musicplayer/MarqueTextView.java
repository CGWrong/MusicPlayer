package com.cgwrong.musicplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by admin on 2018/2/7.
 */

public class MarqueTextView extends android.support.v7.widget.AppCompatTextView {


        public MarqueTextView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        public MarqueTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public MarqueTextView(Context context) {
            super(context);
        }

        @Override
        public boolean isFocused() {
            return true;
        }

}
