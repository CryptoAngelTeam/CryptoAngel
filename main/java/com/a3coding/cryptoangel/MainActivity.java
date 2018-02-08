/*
 * Copyright (c) 2017 3Coding Inc.
 * All right, including trade secret rights, reserved.
 */

package com.a3coding.cryptoangel;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import static com.a3coding.cryptoangel.MainActivity.Status.*;


public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, View.OnClickListener {

    private TextToSpeech mTextToSpeech;
    private TextView mHigherText;
    private TextView mBottomText;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private final int ALL_PERMISSIONS_REQUEST = 1;
    private HighlightTextTask mHighlightTextTask;
    private String mLastSpeechedText;
    private VoiceButtonAnimation mVoiceButtonAnimation;
    private ImageButton mVoiceButton;
    private ViewGroup mSocialIcons;
    private View.OnTouchListener mListener;
    private boolean mSocialShown = true;
    private String mGoal;

    enum Status {
        INITIAL, INTRODUCING, NAME_CONFORMATION, NAME_CONFORMED, GOAL_SET, FINISHED
    }
    private static Status mStatus = INITIAL;

    // Known Google bug, willl be suppored
    private static int VOICE_RECOGNITION_TIMEOUT = 0;

    private PermissionDialogFragment mPermissinDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        Utils.chechAndRequestAllPermissions(this);
        mHigherText = (TextView) findViewById(R.id.higher_text);
        mBottomText = (TextView) findViewById(R.id.bottom_text);
        mVoiceButton = (ImageButton) findViewById(R.id.voice_button);
        mVoiceButtonAnimation = (VoiceButtonAnimation) findViewById(R.id.voice_button_animator);
        mSocialIcons = (ViewGroup) findViewById(R.id.social_icons);
        findViewById(R.id.facebook_button).setOnClickListener(this);
        findViewById(R.id.twitter_button).setOnClickListener(this);
        findViewById(R.id.linkedin_button).setOnClickListener(this);
        findViewById(R.id.instgram_button).setOnClickListener(this);
        findViewById(R.id.youtube_button).setOnClickListener(this);

        mTextToSpeech = new TextToSpeech(this, this);
        mTextToSpeech.setOnUtteranceProgressListener(new SpeechListener());

        updateStatus();
        
        mListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mStatus == INITIAL) {
                    mStatus = INTRODUCING;
                    speak(getString(R.string.introduction_voice));
                } else {
                    startVoiceInput();
                }
                enableButtons(false);
                return false;
            }};
    }

    private class HighlightTextTask extends AsyncTask<String, SpannableString, Void> {
        private TextView tv;
        public HighlightTextTask(TextView textView) {
            tv = textView;
        }
        protected Void doInBackground(String... params) {
            String s = params[0];
            int i = 0;
            while(s != null) {
                while (s.charAt(i) == ' ') {
                    i = (i+1) % s.length();
                }
                SpannableString ss1=  new SpannableString(s);
                // set size
                ss1.setSpan(new RelativeSizeSpan(1.2f), i, i+1, 0);
                // set color
                ss1.setSpan(new ForegroundColorSpan(ContextCompat.getColor(
                        getBaseContext(), R.color.colorAccent)), i, i+1, 0);
                i = (i+1) % s.length();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress(ss1);
                // Escape early if cancel() is called
                if (isCancelled()) {
                    break;
                }
            }
            return null;
        }

        protected void onProgressUpdate(SpannableString... progress) {
            tv.setText(progress[0]);
        }

        protected void onCancelled() {
            tv.setText(tv.getText().toString());
        }
    }

    private void highlightText(TextView tv) {
        tv.setVisibility(View.VISIBLE);
        if (mHighlightTextTask != null) {
            mHighlightTextTask.cancel(true);
        }
        mHighlightTextTask = new HighlightTextTask(tv);
        mHighlightTextTask.execute(tv.getText().toString());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_REQUEST: {
                for (int permissionResult : grantResults) {
                    if (permissionResult != PackageManager.PERMISSION_GRANTED) {
                        if (mPermissinDialog != null) {
                            mPermissinDialog.dismiss();
                        }
                        mPermissinDialog = PermissionDialogFragment.newInstance(R.string.permission_dialog_text);
                        mPermissinDialog.show(getSupportFragmentManager(), PermissionDialogFragment.TAG);
                        // Hide all visible elements
                        mVoiceButton.setVisibility(View.INVISIBLE);
                        mVoiceButtonAnimation.setVisibility(View.INVISIBLE);
                        mHigherText.setVisibility(View.INVISIBLE);
                    }
                }
                // All permissions are granted
                if (Utils.chechAllPermissions(this)) {
                    mVoiceButton.setVisibility(View.VISIBLE);
                    mVoiceButton.setOnTouchListener(mListener);
                    mHigherText.setVisibility(View.VISIBLE);
                    mHigherText.setOnTouchListener(mListener);
                    mVoiceButtonAnimation.setVisibility(View.VISIBLE);
                    mVoiceButtonAnimation.startAnimation();
                }
            }
        }
    }

    @Override
    public void onInit(int i) {
    }

    private void enableButtons(boolean enable) {
        mHigherText.setEnabled(enable);
        mVoiceButton.setEnabled(enable);
    }

    private void speak(String textToSpech) {
        mHigherText.setVisibility(View.VISIBLE);
        mHigherText.setText(getString(R.string.speaking));
        enableButtons(false);
        animateSocialIcons(false);
        Utils.fadeOut(mBottomText, this);
        highlightText(mHigherText);

        if (!mTextToSpeech.isSpeaking()) {
            if (!Locale.getDefault().getLanguage().equals("ru")) {
                mTextToSpeech.setLanguage(Locale.US);
            }
            mTextToSpeech.speak(textToSpech, TextToSpeech.QUEUE_FLUSH, null, "");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Utils.chechAndRequestAllPermissions(this)) {
            mVoiceButton.setVisibility(View.VISIBLE);
            mVoiceButton.setOnTouchListener(mListener);
            mHigherText.setVisibility(View.VISIBLE);
            mHigherText.setOnTouchListener(mListener);
            mVoiceButtonAnimation.setVisibility(View.VISIBLE);
            mVoiceButtonAnimation.startAnimation();
        } else {
            mVoiceButton.setVisibility(View.INVISIBLE);
            mVoiceButtonAnimation.setVisibility(View.INVISIBLE);
            mHigherText.setVisibility(View.INVISIBLE);
        }
        if (mStatus == FINISHED) {
            mStatus = INITIAL;
        } else if (mStatus == INITIAL) {
            animateSocialIcons(true);
            mBottomText.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //clean if already is shown
        if (mPermissinDialog != null) {
            mPermissinDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        //clean if already is shown
        if (mPermissinDialog != null) {
            mPermissinDialog.dismiss();
        }
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!mTextToSpeech.isSpeaking()) {
            super.onBackPressed();
        }
    }

    private void startVoiceInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getVoiceInputText());
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
                VOICE_RECOGNITION_TIMEOUT);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
                VOICE_RECOGNITION_TIMEOUT);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,
                VOICE_RECOGNITION_TIMEOUT);

        animateSocialIcons(false);
        Utils.fadeIn(mBottomText, this);
        highlightText(mBottomText);
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    private void animateSocialIcons(boolean shouldShow) {
        // fade in animation only if view is not shown
        if (shouldShow && !mSocialShown) {
            Utils.fadeIn(mSocialIcons, this);
        }
        // fade out only if it is shown
        else if (!shouldShow && mSocialShown){
            Utils.fadeOut(mSocialIcons, this);
        }
        mSocialShown = shouldShow;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mHighlightTextTask != null) {
            mHighlightTextTask.cancel(true);
        }
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mLastSpeechedText = result.get(0);
                    handleRegognizedText(mLastSpeechedText);
                   // mVoiceInputTv.setText(result.get(0));
                } else if (resultCode == RESULT_CANCELED) {
                    enableButtons(true);
                    animateSocialIcons(true);
                    Utils.fadeOut(mBottomText, this);
                    mHigherText.setText(getString(R.string.tap_to_start));
                }
                break;
            }

        }
    }

    private String getVoiceInputText() {
        String helpText = "";
        switch (mStatus) {
            case INITIAL:
                break;
            case INTRODUCING:
                helpText = getString(R.string.your_name_text);
                break;
            case NAME_CONFORMATION:
                helpText = String.format(getString(R.string.name_conformation_text), mLastSpeechedText);
                break;
            case NAME_CONFORMED:
            case GOAL_SET:
                helpText = getString(R.string.your_goal_text);
                break;
            default:
                break;
        }
        return helpText;
    }

    private void handleRegognizedText(String text) {
        if (!isActionRecognized(text)) {
            switch (mStatus) {
                case INITIAL:
                    break;
                case INTRODUCING:
                    mStatus = NAME_CONFORMATION;
                    speak(String.format(getString(R.string.name_conformation_voice), text));
                    break;
                case NAME_CONFORMATION:
                    if (text.equalsIgnoreCase(getString(R.string.conformation_1)) ||
                            text.equalsIgnoreCase(getString(R.string.conformation_2)) ||
                            text.equalsIgnoreCase(getString(R.string.conformation_3))) {
                        mStatus = GOAL_SET;
                        speak(getString(R.string.goal_question));
                    } else {
                        mStatus = INTRODUCING;
                        speak(getString(R.string.your_name_text));
                    }
                    break;
                case GOAL_SET:
                    mGoal = text;
                    speak(getString(R.string.setting_goal));
                    mStatus = FINISHED;
                    break;
                default:
                    break;
            }
        } else {
            Utils.fadeOut(mBottomText, getBaseContext());
            animateSocialIcons(true);
        }
    }

    private void updateStatus() {
            switch (mStatus) {
                case INITIAL:
                    mHigherText.setText(getString(R.string.tap_to_start));
                    break;
                case INTRODUCING:
                    //mHigherText.setVisibility(View.INVISIBLE);
                    break;
                default:
                    break;
            }
    }

    private boolean isActionRecognized(String text) {
        if (text.toLowerCase().indexOf(getString(R.string.about_us)) != -1) {
            enableButtons(true);
            Utils.webHomeIntent(this);
            return true;
        }
        return false;
    }

    private class SpeechListener extends UtteranceProgressListener {
        @Override
        public void onStart(String utteranceId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        @Override
        public void onDone(String utteranceId) {
           // mHighlightTextTask.cancel(true);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mHighlightTextTask != null) {
                        mHighlightTextTask.cancel(true);
                    }
                    switch (mStatus) {
                        case INITIAL:
                            enableButtons(true);
                            mHigherText.setText(getString(R.string.tap_to_start));
                            Utils.fadeOut(mBottomText, getBaseContext());
                            break;
                        case FINISHED:
                            Utils.goalPageIntent(getBaseContext(), mGoal);
                            enableButtons(true);
                            mHigherText.setText(getString(R.string.tap_to_start));
                            mStatus = INITIAL;
                            break;
                        default:
                            startVoiceInput();
                            break;
                    }
                }
            });
        }

        @Override
        public void onError(String s) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  //  MainActivity.this.mHigherText.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.facebook_button:
                Utils.openPage(Utils.getFacebookIntent(getBaseContext()), getBaseContext());
                break;
            case R.id.twitter_button:
                Utils.openPage(Utils.getTwitterIntent(getBaseContext()), getBaseContext());
                break;
            case R.id.linkedin_button:
                Utils.openPage(Utils.getLinkedInIntent(getBaseContext()), getBaseContext());
                break;
            case R.id.instgram_button:
                Utils.openPage(Utils.getInstagramIntent(getBaseContext()), getBaseContext());
                break;
            case R.id.youtube_button:
                Utils.openPage(Utils.getYoutubeIntent(getBaseContext()), getBaseContext());
                break;
            default:
                break;
        }
    }
}
