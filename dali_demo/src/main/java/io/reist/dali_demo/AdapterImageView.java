package io.reist.dali_demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by Reist on 14.06.16.
 */
public class AdapterImageView extends ImageView {

    private static final String TAG = AdapterImageView.class.getSimpleName();

    private int position;

    public AdapterImageView(Context context) {
        super(context);
    }

    public AdapterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdapterImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        Log.d(TAG, String.format("setBackgroundColor(%s) @ %s", color, position));
    }

    @Override
    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);
        Log.d(TAG, String.format("setBackgroundResource(%s) @ %s", resid, position));
    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
        Log.d(TAG, String.format("setBackground(%s) @ %s", background, position));
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(background);
        Log.d(TAG, String.format("setBackgroundDrawable(%s) @ %s", background, position));
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        Log.d(TAG, String.format("setImageResource(%s) @ %s", resId, position));
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        Log.d(TAG, String.format("setImageDrawable(%s) @ %s%s", drawable, position, getFlatStackTrace()));
    }

    private String getFlatStackTrace() {
        String r = "";
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 3; i < Math.min(stackTrace.length, 7); i++) {
            StackTraceElement stackTraceElement = stackTrace[i];
            r += " <- " + stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName();
        }
        return r;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        Log.d(TAG, String.format("setImageBitmap(%s) @ %s", bm, position));
    }

    public void setPosition(int position) {
        this.position = position;
    }

}
