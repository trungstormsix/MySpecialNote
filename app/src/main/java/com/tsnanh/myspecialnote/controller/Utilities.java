package com.tsnanh.myspecialnote.controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.tsnanh.myspecialnote.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public final class Utilities {

    public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = Calendar.getInstance().getTime();

        String dateStr = dateFormat.format(date);

        return dateStr;
    }

    public static Bitmap centerCropForIcon(Bitmap bitmap) {
        Bitmap newBmp;
        if (bitmap.getWidth() >= bitmap.getHeight()) {
            newBmp = Bitmap.createBitmap(
                    bitmap,
                    bitmap.getWidth()/2 - bitmap.getHeight()/2,
                    0,
                    bitmap.getHeight(),
                    bitmap.getHeight()
            );
        } else {
            newBmp = Bitmap.createBitmap(
                    bitmap,
                    0,
                    bitmap.getHeight()/2 - bitmap.getWidth()/2,
                    bitmap.getWidth(),
                    bitmap.getWidth()
            );
        }
        return getResizedBitmap(newBmp, 56, 56);
    }

    public static Bitmap getResizedBitmap(Bitmap image, int bitmapWidth, int bitmapHeight) {
        return Bitmap.createScaledBitmap(image, bitmapWidth, bitmapHeight, true);
    }

    public static Bitmap getImageFromData(Context context, String name, int type) {
        if (type == Config.MODE_ICON) {
            name = name.substring(0, name.length() - 4) + Config.ICON_IMG;
        }
        File f = new File(context.getDir("MSNote", Context.MODE_PRIVATE), name);
        Bitmap bitmap = null;
        if (f.exists()) {
             bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
        }
        return bitmap;
    }

    public static int randomColor(Context context) {
        int[] colors = context.getResources().getIntArray(R.array.androidcolors);
        int colorSelected = colors[new Random().nextInt(colors.length)];
        return colorSelected;
    }

    public static void deleteOldImage(Context context, String name) {
        File file = new File(context.getDir("MSNote", Context.MODE_PRIVATE).getAbsolutePath(), name);
        Log.e("delete", file.getAbsolutePath());
        File file1 = new File(context.getDir("MSNote", Context.MODE_PRIVATE).getAbsolutePath(), name.substring(0, name.length() - 4) + Config.ICON_IMG);
        if (file.exists() && file1.exists()) {
            file.delete();
            file1.delete();
        }
    }

    public static void hideSoftKeyBoard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static String saveImage(Context context, Bitmap bitmap, String name, int type) {
        String name2;
        if (type == Config.MODE_NORMAL) {
             name2 = name + ".png";
        } else if (type == Config.MODE_ICON) {
            name2 = name + " - icon.png";
        } else {
            return null;
        }
        File dir = context.getDir("MSNote", Context.MODE_PRIVATE);
        File f = new File(dir, name2);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            // Use the compress method on the BitMap object to write image to the OutputStream
            if (type == Config.MODE_ICON) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 30, fos);
            } else {
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, fos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return type == Config.MODE_NORMAL ? f.getName() : null;
    }

    public static String dateTimeFormat(String datetime) {
        return datetime.replace("/", "").replace(" ", "").replace(":", "");
    }
}
