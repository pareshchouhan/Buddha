package com.quotenspire.buddha;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.marcoscg.easylicensesdialog.EasyLicensesDialog;
import com.quotenspire.buddha.Service.QuoteNotificationReciever;
import com.quotenspire.buddha.provider.quotes.QuotesContentValues;
import com.quotenspire.buddha.provider.quotes.QuotesCursor;
import com.quotenspire.buddha.provider.quotes.QuotesSelection;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class QuoteNSpireActivity extends AppCompatActivity {

    RelativeLayout mContentView;

    private TextView quoteText;
    private RelativeLayout quoteBubble;
    private View bubblePivot;

    private ImageView characterView;

    AdView adView;
    private FirebaseAnalytics mFirebaseAnalytics;

    private ImageView previousImage;
    private ImageView aboutImage;
    private ImageView shareImage;
    private ImageView audioStatusImage;

    ArrayList<Integer> previousQuoteList;
    int previousQuid = -1;
    int currentPos;

    final String AD_TEST_ID = "61A935F0FF283916D2372533249F4E56";

    MediaPlayer mediaPlayer;
    int QUOTE_COUNT = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.acitivty_quote);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        MobileAds.initialize(getApplicationContext(), getString(R.string.ad_app_id));

        mContentView = (RelativeLayout) findViewById(R.id.fullscreen_content);

        //Setup ad view
        adView = (AdView) new AdView(this);
        adView.setAdUnitId(getString(R.string.banner_quotenspire_ad_unit_id));
        adView.setAdSize(AdSize.BANNER);

        RelativeLayout.LayoutParams adParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        adParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        mContentView.addView(adView, adParams);

        //get reference to all the views.
        previousImage = (ImageView) findViewById(R.id.previous);
        shareImage = (ImageView) findViewById(R.id.share);
        aboutImage = (ImageView) findViewById(R.id.about);
        audioStatusImage = (ImageView) findViewById(R.id.audio_switch);

        hideUI();
        quoteText = (TextView) findViewById(R.id.quote_text);
        characterView = (ImageView) findViewById(R.id.fab);
        quoteBubble = (RelativeLayout) findViewById(R.id.quote_bubble);
        bubblePivot = (View) findViewById(R.id.bubble_pivot);

        final ViewGroup rootView = (ViewGroup) findViewById(R.id.fullscreen_content);
        Log.w("ViewTag", "View tag is : " + rootView.getTag());

        mediaPlayer = MediaPlayer.create(this, R.raw.bell);


        //add to DB
        addToDb();

//        RelativeLayout.LayoutParams bubblePivotLayoutParams = (RelativeLayout.LayoutParams)
//                bubblePivot
//                .getLayoutParams();
//        quoteBubble.setLayoutParams(bubblePivotLayoutParams);
        quoteBubble.setVisibility(View.INVISIBLE);

        int uid = getIntent().getIntExtra(getString(R.string.uid), -1);
        //If user clicks on notification redirect them to that quote :)
        if (uid != -1) {
            String quote = fetchQuote(uid, false);
            animateBubble(quote);
        }
        //Setup alarms for our lovely notification
        SharedPreferences sharedPrefs = getSharedPreferences(getString(R.string.app_name),
                Context.MODE_PRIVATE);
        //If this is first run of device set an Alarm Manager to fire Notification at 6:00 AM.
        if (sharedPrefs.getBoolean(getString(R.string.app_first_run), false) == false) {
            //Set app_first_run to true.
            AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context
                    .ALARM_SERVICE);
            sharedPrefs.edit().putBoolean(getString(R.string.app_first_run), true).apply();
            Calendar time = Calendar.getInstance();
//            time.set(Calendar.DAY_OF_YEAR, 1);
            time.set(Calendar.HOUR_OF_DAY, 6);
            time.set(Calendar.MINUTE, 0);
            time.set(Calendar.SECOND, 0);
            time.set(Calendar.MILLISECOND, 0);
            //Hack to get the Notification from next day instead of notifications firing instantly.
            if (Calendar.getInstance().after(time)) {
                time.add(Calendar.DATE, 1);
            }

            Intent launchIntent = new Intent(this, QuoteNotificationReciever.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, launchIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    time.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);
        }

        if (sharedPrefs.getBoolean(getString(R.string.audio_preference), true) == true) {
            audioStatusImage.setImageResource(R.drawable.ic_volume_up);
        } else {
            audioStatusImage.setImageResource(R.drawable.ic_volume_off);
        }

        characterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSharedPreferences(getString(R.string.app_name),
                        Context.MODE_PRIVATE).getBoolean(getString(R.string.audio_preference),
                        true)) {
                    if (mediaPlayer != null) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                            mediaPlayer.seekTo(0);
                        }
                        mediaPlayer.start();
                    }
                }
                String quote = fetchQuote(-1, false);
                animateBubble(quote);
            }
        });

        //Generate Ad
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AD_TEST_ID)
                .build();
        adView.loadAd(adRequest);

        Animation fadeInCharacter = new AlphaAnimation(0, 1);
        fadeInCharacter.setInterpolator(new FastOutLinearInInterpolator());
        fadeInCharacter.setDuration(2000);
        characterView.startAnimation(fadeInCharacter);

        setupListeners();
    }

    /**
     * setupListeners
     * Used to setup On Click Listeners
     */
    private void setupListeners() {
        aboutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                animateSlideIn(aboutImage);
                EasyLicensesDialog easyLicensesDialog = new EasyLicensesDialog
                        (QuoteNSpireActivity.this);
                easyLicensesDialog.setTitle("About");
                easyLicensesDialog.setCancelable(true);
                easyLicensesDialog.show();
            }
        });

        previousImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                animateSlideIn(previousImage);
                if (previousQuid != -1) {
                    //do nothing
                    //show previous
                    //check if we are already showing previous quotes?
                    if (currentPos == -1) {
                        currentPos = previousQuoteList.size() - 1;
                    }
                    if (currentPos >= 0) {
                        int quid = previousQuoteList.get(currentPos--);
                        String quote = fetchQuote(quid, true);
                        animateBubble(quote);
                    }
                    if (currentPos == -1) {
                        hidePreviousAndAnimate();
                        previousImage.setClickable(false);
                    }
                }
            }
        });

        shareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveScreenShot();
            }
        });

        audioStatusImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPrefs = getSharedPreferences(getString(R.string.app_name),
                        Context.MODE_PRIVATE);
                if (sharedPrefs.getBoolean(getString(R.string.audio_preference), true) == true) {
                    sharedPrefs.edit().putBoolean(getString(R.string.audio_preference), false)
                            .apply();
                    audioStatusImage.setImageResource(R.drawable.ic_volume_off);
                } else {
                    sharedPrefs.edit().putBoolean(getString(R.string.audio_preference), true).apply();
                    audioStatusImage.setImageResource(R.drawable.ic_volume_up);
                }
            }
        });
    }

    private void showPreviousAndAnimate() {
        final Animation fade = new AlphaAnimation(0, 1);
        fade.setInterpolator(new AccelerateInterpolator());
        fade.setDuration(500);
        fade.setFillAfter(true);
        previousImage.startAnimation(fade);
    }

    private void hidePreviousAndAnimate() {
        final Animation fade = new AlphaAnimation(-1, 0);
        fade.setInterpolator(new AccelerateInterpolator());
        fade.setDuration(500);
        fade.setFillAfter(true);
        previousImage.startAnimation(fade);

    }

    /**
     * animateBubble
     * Runs a animation to bring the quote buubble to center with the specified quote.
     * @param quote
     */
    private void animateBubble(String quote) {
        quoteText.setText("");

//        RelativeLayout.LayoutParams centerParams = (RelativeLayout.LayoutParams) bubblePivot
//                .getLayoutParams();
//        quoteBubble.setLayoutParams(centerParams);
        quoteText.setText(quote);
        characterView.startAnimation(getAnimator());

        final AnimationSet animationFadeAndScale = new AnimationSet(true);
        final Animation scale = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f,
                Animation.RELATIVE_TO_PARENT, 0.50f, Animation.RELATIVE_TO_PARENT, 0.70f);
        final Animation fade = new AlphaAnimation(0, 1);
        fade.setInterpolator(new AccelerateInterpolator());
        fade.setDuration(500);
        scale.setDuration(500);
        scale.setInterpolator(new AccelerateInterpolator());
        animationFadeAndScale.addAnimation(scale);
        animationFadeAndScale.addAnimation(fade);
        animationFadeAndScale.setFillAfter(true);
        quoteBubble.startAnimation(animationFadeAndScale);
    }


    /**
     * Slidein Animate the provided view to center
     * @param view
     */
    private void animateSlideIn(ImageView view) {
        final Animation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -0.4f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f
        );
        translateAnimation.setInterpolator(new AccelerateInterpolator());
        translateAnimation.setDuration(500);
        view.startAnimation(translateAnimation);
    }

    /**
     * fetchQuote
     * Used to fetch a random (sequential random??) quote or quote specified by uid
     * @param uid Uid of Quote to fetch
     * @param isPrevious if we need to save previous quote or not.
     * @return String Quote that was fetched
     */
    private String fetchQuote(int uid, boolean isPrevious) {
        if (!isPrevious) {
            currentPos = -1;
            if (previousQuid != -1) {
                if (previousQuoteList == null) {
                    previousQuoteList = new ArrayList<>();
                }
                previousQuoteList.add(previousQuid);
            }
        }
        if (previousQuid != -1) {
            showPreviousAndAnimate();
            previousImage.setClickable(true);
        }
        QuotesSelection quotesSelection = new QuotesSelection();

        Log.w("QuoteActivity", "Quote Count : " + QUOTE_COUNT);

        if (uid == -1) {
            quotesSelection.status(false);
            quotesSelection.limit(1);
        } else {
//            quotesSelection.status();
            quotesSelection.uid(uid);
            quotesSelection.limit(1);
        }
//                int uid = (int)(Math.random() * QUOTE_COUNT);

        QuotesCursor q = quotesSelection.query(getApplicationContext());
        if (q.moveToFirst() == false) {
            resetDb();
            q.close();
            q = new QuotesSelection().uid(0).query(getApplicationContext());
//                    q = quotesSelection.query(getApplicationContext());
            QuotesSelection where = new QuotesSelection();
            if (q.moveToFirst()) {
                where.id(q.getId());
                QuotesContentValues updatedValue = new QuotesContentValues();
                updatedValue.putStatus(true);
                updatedValue.update(getApplicationContext(), where);
            } else {
                return "Oops?";
            }

            //We are out of status=false, reset our D and reselct a new status.
        } else {
            //Update the selected id status to true, so it doesn't get selected again.
            QuotesSelection where = new QuotesSelection();
            where.id(q.getId());
            QuotesContentValues updatedValue = new QuotesContentValues();
            updatedValue.putStatus(true);
            updatedValue.update(getApplicationContext(), where);
        }
        final String quote = q.getQuote();
        final int quid = q.getUid();
        if (!isPrevious) {
            previousQuid = quid;
        }

        if (!q.isClosed()) {
            q.close();
        }
        return quote;
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideUI();
    }

    /**
     * hideUI
     * used for hiding UI buttons and other UI elements of Android AOSP
     */
    public void hideUI() {
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    /**
     * scaleView
     *
     * @param v
     * @param startScale
     * @param endScale
     */
    public void scaleView(View v, float startScale, float endScale) {
        Animation anim = new ScaleAnimation(
                    1f, 1f, // Start and end values for the X axis scaling
                startScale, endScale, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 1f); // Pivot point of Y scaling
//        anim.setFillAfter(true); // Needed to keep the result of the animation
        v.startAnimation(anim);
    }


    //Source : http://stackoverflow.com/questions/20991764/how-to-programatically-animate-an-imageview

    /**
     * getAnimator
     * Shrink grow animation
     * @return AnimationSet returns a Grow and Shrink animation set.
     */
    public AnimationSet getAnimator() {
        final float growTo = 1.1f;
        final long duration = 300;

        ScaleAnimation grow = new ScaleAnimation(1, growTo, 1, growTo,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        grow.setDuration(duration / 2);
        ScaleAnimation shrink = new ScaleAnimation(growTo, 1, growTo, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(duration / 2);
        shrink.setStartOffset(duration / 2);
        AnimationSet growAndShrink = new AnimationSet(true);
        growAndShrink.setInterpolator(new LinearInterpolator());
        growAndShrink.addAnimation(grow);
        growAndShrink.addAnimation(shrink);
        return growAndShrink;
    }

    /**
     * addToDb
     * adds quotes to DB, called on first run of the Application.
     */
    private void addToDb() {
        final Context context = getApplicationContext();
        final SharedPreferences sharedPrefs = context.getSharedPreferences(getString(R.string
                .app_name), Context
                .MODE_PRIVATE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = sharedPrefs.getInt("count", 0);
                final String[] quotes = getResources().getStringArray(R.array.quotes);
                if (sharedPrefs.getBoolean(context.getString(R.string.shared_preference_key), false) == false
                        || (count == 0 || count != quotes.length)) {
                    Log.w("AddingData", "Adding more rows");
                    int len = quotes.length;
                    QuotesContentValues[] contentValues = new QuotesContentValues[len];
                    int i = 0;
                    for (i = 0; i < len; i++) {
                        contentValues[i] = new QuotesContentValues();
                        contentValues[i].putQuote(quotes[i]);
                        contentValues[i].putUid(i);
                        contentValues[i].putStatus(false);
                        contentValues[i].insert(context);
                    }
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putBoolean(context.getString(R.string
                            .shared_preference_key), true);
                    editor.putInt("count", i);
                    editor.apply();
                }
            }
        }).start();
    }

    /**
     * resetDb
     * resets DB in case we got a DB upgrade.
     */
    private void resetDb() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                QuotesContentValues quotesContentValues = new QuotesContentValues();
                quotesContentValues.putStatus(false);
                QuotesSelection where = new QuotesSelection();
                where.status(true);
                quotesContentValues.update(getApplicationContext(), where);
            }
        }).start();
    }

    /**
     * saveScreenShot
     * Removes UI elements (Icons/Other things) and captures Screenshot.
     */
    private void saveScreenShot() {
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        boolean isPreviousVisible = false;
        Log.w("Buddha", "trying Hiding previousImage");
        if (previousQuoteList != null && previousQuoteList.size() > 0) {
            Log.w("Buddha", "Hiding previousImage");
            hidePreviousAndAnimate();
            previousImage.setClickable(false);
            isPreviousVisible = true;
        }
        shareImage.setVisibility(View.INVISIBLE);
        aboutImage.setVisibility(View.INVISIBLE);
        adView.setVisibility(View.INVISIBLE);
        audioStatusImage.setVisibility(View.INVISIBLE);
        Bitmap bm = getScreenShot(rootView);
        storeScreenShot(bm);
        shareImage();
        if (isPreviousVisible) {
//            previousImage.setVisibility(View.VISIBLE);
            showPreviousAndAnimate();
        }
        shareImage.setVisibility(View.VISIBLE);
        aboutImage.setVisibility(View.VISIBLE);
        adView.setVisibility(View.VISIBLE);
        audioStatusImage.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AD_TEST_ID)
                .build();
        adView.loadAd(adRequest);
    }

    /**
     * getScreenShot
     * get Screenshot from the view.
     * @param view
     * @return Bitmap returns bitmap image of the ScreenShot.
     */
    private Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    /**
     * storeScreenshot
     * stores ScreenShot temporarily for sharing.
     * @param bm Bitmap image of screenshot.
     */
    private void storeScreenShot(Bitmap bm) {
        File cachePath = new File(QuoteNSpireActivity.this.getCacheDir(), "images");
        cachePath.mkdirs(); // don't forget to make the directory
        try {
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png");
            bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * shareImage
     * Calls ShareImage Intent for easy sharing of images. :)
     */
    private void shareImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        File imagePath = new File(getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(this, getString(R.string.file_provider),
                newFile);


        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        intent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.extra_text));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM, contentUri);
        try {
            startActivity(Intent.createChooser(intent, "Share Screenshot"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No App Available", Toast.LENGTH_SHORT).show();
        }
    }
}