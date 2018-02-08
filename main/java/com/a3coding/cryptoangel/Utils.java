/*
 * Copyright (c) 2017 3Coding Inc.
 * All right, including trade secret rights, reserved.
 */

package com.a3coding.cryptoangel;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

public class Utils {

    private static final String COMPANY_WEB = "http://crypto-angel.com";

    private static final String GOAL_SET_PAGE = COMPANY_WEB + "/goal/index.php";

    private static final String HOME_WEB = "https://www.3coding.com";

    public static final int ALL_PERMISSIONS_REQUEST = 1;

    /**
     * Checks and request all necessary permissions.
     *
     * @param context
     * @return true if all permissions are granted, false otherwise
     */
    public static boolean chechAndRequestAllPermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissionsList = new ArrayList<>();

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(Manifest.permission.RECORD_AUDIO);
            }
//             Will be added as soon as application get these features
//            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED) {
//                permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
//            }
//            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BODY_SENSORS)
//                    != PackageManager.PERMISSION_GRANTED) {
//                permissionsList.add(Manifest.permission.BODY_SENSORS);
//            }
            if (permissionsList.size() > 0) {
                ActivityCompat.requestPermissions((Activity) context,
                        permissionsList.toArray(new String[permissionsList.size()]), ALL_PERMISSIONS_REQUEST);
                return false;
            }
        }
        return true;
    }

    /**
     * Checks all necessary permissions.
     *
     * @param context
     * @return true if all permissions are granted, false otherwise
     */
    public static boolean chechAllPermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissionsList = new ArrayList<>();

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(Manifest.permission.RECORD_AUDIO);
            }
//             Will be added as soon as application gets these features
//            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED) {
//                permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
//            }
//            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BODY_SENSORS)
//                    != PackageManager.PERMISSION_GRANTED) {
//                permissionsList.add(Manifest.permission.BODY_SENSORS);
//            }
            if (permissionsList.size() > 0) {
                return false;
            }
        }
        return true;
    }

    public static void webHomeIntent(Context context) {
        String url = COMPANY_WEB;
        String languageSuffixs = "";

        switch (Locale.getDefault().getLanguage()) {
            case "ru":
                languageSuffixs = "?lang=ru";
                break;
            case "zh":
                languageSuffixs = "?lang=zh";
                break;
            default:
                break;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url + languageSuffixs));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        openPage(intent, context);
    }

    public static void goalPageIntent(Context context, String goal) {
        String url = Uri.parse(GOAL_SET_PAGE)
                .buildUpon()
                .appendQueryParameter("param", goal)
                .build().toString();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        openPage(intent, context);
    }

    public static void openPage(Intent intent, Context context) {
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Intent getYoutubeIntent(Context context) {
        final String YOUTUBE_URL = "https://www.youtube.com/channel/";
        final String YOUTUBE_PAGE_ID = "UCbFIn6vrTJ4oiFWsoLCZrpg";
        final String YOUTUBE_PACKAGE_NAME = "com.google.android.youtube";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (isAppEnabled(context, YOUTUBE_PACKAGE_NAME)) {
            intent.setPackage(YOUTUBE_PACKAGE_NAME);
            intent.setData(Uri.parse(YOUTUBE_URL + YOUTUBE_PAGE_ID));
        }
        else {
            intent.setData(Uri.parse(YOUTUBE_URL + YOUTUBE_PAGE_ID));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static Intent getInstagramIntent(Context context) {
        final String INSTAGRAM_URL = "http://instagram.com/_u/";
        final String INSTAGRAM_PAGE_ID = "crypto.angel";
        final String INSTAGRAM_PACKAGE_NAME = "com.instagram.android";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(INSTAGRAM_URL + INSTAGRAM_PAGE_ID));
        if (isAppEnabled(context, INSTAGRAM_PACKAGE_NAME)) {
            intent.setPackage(INSTAGRAM_PACKAGE_NAME);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static Intent getTwitterIntent(Context context) {
        final String TWITTER_PACKAGE_NAME = "com.twitter.android";
        final String TWITER_URL = "twitter://user?screen_name=";
        final String TWITER_SIMPLE_URL = "https://mobile.twitter.com/";
        final String TWITTER_ID = "cryptoangelcoin";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (isAppEnabled(context, TWITTER_PACKAGE_NAME)) {
            intent.setData(Uri.parse(TWITER_URL + TWITTER_ID));
        } else {
            intent.setData(Uri.parse(TWITER_SIMPLE_URL + TWITTER_ID));

        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static Intent getLinkedInIntent(Context context) {
        final String LINKEDIN_PACKAGE_NAME = "com.linkedin.android";
        final String LINKEDIN_URL = "http://www.linkedin.com/company/";
        final String LINKEDIN_ID = "18331930";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(LINKEDIN_URL + LINKEDIN_ID));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static Intent getFacebookIntent(Context context) {
        final String FACEBOOK_URL = "https://www.facebook.com/";
        final String FACEBOOK_PAGE_ID = "CryptoAngel-896638590489835";
        final String FACEBOOK_PACKAGE_NAME = "com.facebook.katana";
        final int NEW_VERSON = 3002850;
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (isAppEnabled(context, FACEBOOK_PACKAGE_NAME)) {
            try {
                int versionCode = packageManager.getPackageInfo(FACEBOOK_PACKAGE_NAME, 0).versionCode;
                if (versionCode < NEW_VERSON) {
                    //older versions of fb app
                    intent.setData(Uri.parse("fb://page/" + FACEBOOK_PAGE_ID));
                } else {
                    //newer versions of fb app
                    intent.setData(Uri.parse("fb://facewebmodal/f?href=" + FACEBOOK_URL + FACEBOOK_PAGE_ID));
                }
            } catch (PackageManager.NameNotFoundException e) {
                intent.setData(Uri.parse(FACEBOOK_URL));
            }
        }
        else {
            intent.setData(Uri.parse(FACEBOOK_URL));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    private static boolean isAppEnabled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        boolean installed;
        try {
            installed = pm.getApplicationInfo(packageName,0).enabled;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    public static void fadeIn(View v, Context context){
        Animation animFadeIn = AnimationUtils.loadAnimation(
                context.getApplicationContext(), R.anim.fade_in);
        v.startAnimation(animFadeIn);
    }

    public static void fadeOut(View v, Context context){
        if (v.getVisibility() == View.VISIBLE) {
            Animation animFadeOut = AnimationUtils.loadAnimation(
                    context.getApplicationContext(), R.anim.fade_out);
            v.startAnimation(animFadeOut);
        }
    }
}
