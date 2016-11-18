package com.thejuanandonly.schoolapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;

/**
 * Created by Daniel on 07-Nov-16.
 */

class ImageLoader extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private String data = "";
    private String uri;
    private boolean bool;
    Context context;
    int screenWidth, screenHeight;

    public ImageLoader(ImageView imageView, String uri, boolean bool, Context context) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        this.uri = uri;
        this.bool = bool;
        this.context = context;
        imageViewReference = new WeakReference<ImageView>(imageView);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        screenHeight = display.getHeight();
        screenWidth = display.getWidth();

    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(String... params) {
        data = params[0];
        return decodeSampledBitmapFromResource(data, 100, 100, bool, context);

    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {


                int bitmapWidth = bitmap.getWidth();
                int bitmapHeight = bitmap.getHeight();


                if (bitmapWidth > bitmapHeight) {
                    imageView.setImageBitmap(rotateImage(bitmap, 90f));
                } else {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    public Bitmap rotateImage(Bitmap src, float degree) {
        // create new matrix object
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postRotate(degree);
        // return new bitmap rotated using matrix
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }


    public static Bitmap decodeSampledBitmapFromResource(String picturePath, int width, int height, boolean resize, Context context) {

        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        if (resize) {
            sizeOptions.inSampleSize = inSampleSize;
        } else {
            sizeOptions.inSampleSize = 3;
            return BitmapFactory.decodeFile(picturePath, sizeOptions);
//            return BitmapFactory.decodeFile(picturePath);
        }

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
//        return BitmapFactory.decodeFile(picturePath);
    }



    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
