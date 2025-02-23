

package com.quick.liveswitcher.ui.previewarea;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;


public class ControlLayout extends View {
    public ControlLayout(Context context) {
        super(context);
    }

    public ControlLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void update(){

    }
    public void synChildPos(){

    }

    public void reset() {
    }

    public void onConfigureChanged(){
        requestLayout();
    }
}
