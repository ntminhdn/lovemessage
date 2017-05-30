package com.example.user.lovemessages;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;

/**
 * Created by User on 23/02/2017.
 */

public class Utility {
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static String getNgayHienTai() {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        DateTime dt = new DateTime();
        return df.format(dt.toDate());
    }

    public static String getValue() {
        DateTime start = new DateTime(2015, 03, 19, 0, 0, 0, 0);
        DateTime end = new DateTime();

        //số ngày yêu nhau - 1
        Days days = Days.daysBetween(start, end);

        //số ngày yêu nhau tính đến 8/3 được tính = 1
        int value = days.getDays() - 720;

        return String.valueOf(value);
    }

    public static int countDays(DateTime end) {
        DateTime start = new DateTime(2015, 03, 19, 0, 0, 0, 0);
        Days days = Days.daysBetween(start, end);
        return days.getDays() + 1;
    }

    public static DateTime convertToDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
        DateTime dt = formatter.parseDateTime(dateStr);
        return dt;
    }
}
