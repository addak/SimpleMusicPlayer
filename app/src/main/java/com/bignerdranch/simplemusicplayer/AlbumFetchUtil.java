package com.bignerdranch.simplemusicplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class AlbumFetchUtil {
    public static Bitmap getScaledBitmap(byte[] imgArray, int destHeight, int destWidth ){
        //Read file's dimensions( pixel)
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(imgArray, 0, imgArray.length, options);

        // Retrieve Source's Height and Width
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        // Figure out how much to scale down by
        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            float heightScale = srcHeight / destHeight;
            float widthScale = srcWidth / destWidth;
            inSampleSize = Math.round(heightScale > widthScale ? heightScale :
                    widthScale);
        }

        options = new BitmapFactory.Options();
        //Reduces height and width by n, where n is the value of inSampleSize (in int)
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeByteArray(imgArray, 0, imgArray.length, options);

    }
}
