package com.stevenswang.agerasample;

import android.view.View;

import com.google.android.agera.BaseObservable;



/**
 * Created by wolun on 16/5/3.
 */
final public class ObservableFactory {
    public static BaseObservable injectClickEvent(View targetView){
        ClickObservable clickObservable = new ClickObservable();
        targetView.setOnClickListener(clickObservable);
        return clickObservable;
    }
    public static class ClickObservable extends BaseObservable  implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            dispatchUpdate();
        }
    }
}
