package io.reist.dali.drawables;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.ImageView;

/**
 *
 * Created by m039 on 12/28/15.
 */
public class CircleFadingBitmapDrawable extends FadingBitmapDrawable {

    public CircleFadingBitmapDrawable(Context context, Bitmap bitmap, Drawable placeholder, boolean noFade) {
        super(context, bitmap, placeholder, noFade);
        init();
    }

    private BitmapShader mBitmapShader;
    private final Paint mBitmapPaint = new Paint();

    private float mBitmapWidth;
    private float mBitmapHeight;

    private final Matrix mShaderMatrix = new Matrix();
    private final Matrix mInverseMatrix = new Matrix();

    private Rect mClipBounds = new Rect();

    private float mWidth = -1;
    private float mHeight = -1;

    private float mCx;
    private float mCy;
    private float mR;

    private void init() {

        Bitmap bitmap = getBitmap();

        mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);

        mBitmapWidth = bitmap.getWidth();
        mBitmapHeight = bitmap.getHeight();

    }

    @Override
    protected void draw(Canvas canvas, float normalized) {

        final ImageView imageView = findImageView();

        if (imageView == null) {
            return;
        }

        int saveCount = canvas.getSaveCount();

        Matrix matrix = imageView.getImageMatrix();
        if (!matrix.isIdentity()) {
            if (matrix.invert(mInverseMatrix)) {
                canvas.concat(mInverseMatrix);
            }
        }

        updateShaderMatrix();

        mBitmapPaint.setAlpha((int) (0xFF * normalized));

        canvas.drawCircle(mCx, mCy, mR, mBitmapPaint);

        canvas.restoreToCount(saveCount);

    }

    @Nullable
    private ImageView findImageView() {
        Object obj = getCallback();
        if (obj instanceof ImageView) {
            return (ImageView) obj;
        } else {
            return null;
        }
    }

    private void updateShaderMatrix() {

        float width = getIntrinsicWidth();
        float height = getIntrinsicHeight();

        if (mWidth != width || mHeight != height) {

            float scale;
            float dx = 0;
            float dy = 0;

            mShaderMatrix.set(null);

            if (mBitmapWidth * height > width * mBitmapHeight) {
                scale = height / mBitmapHeight;
                dx = (width - mBitmapWidth * scale) * 0.5f;
            } else {
                scale = width / mBitmapWidth;
                dy = (height - mBitmapHeight * scale) * 0.5f;
            }

            mShaderMatrix.setScale(scale, scale);
            mShaderMatrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));

            mBitmapShader.setLocalMatrix(mShaderMatrix);

            mCx = width / 2.0f;
            mCy = height / 2.0f;
            mR = Math.min(mCx, mCy);

            mWidth = width;
            mHeight = height;

        }

    }

    @Override
    protected void drawPlaceholder(Canvas canvas, Drawable placeholder, float normalized) {

        canvas.save();

        // placeholders must be circles, not ovals
        float scaleX, scaleY;
        if (mBitmapWidth > mBitmapHeight) {
            scaleX = mBitmapHeight / mBitmapWidth;
            scaleY = 1f;
        } else {
            scaleX = 1f;
            scaleY = mBitmapWidth / mBitmapHeight;
        }
        canvas.scale(scaleX, scaleY, mClipBounds.exactCenterX(), mClipBounds.exactCenterY());

        super.drawPlaceholder(canvas, placeholder, normalized);

        canvas.restore();

    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mClipBounds = bounds;
    }

}