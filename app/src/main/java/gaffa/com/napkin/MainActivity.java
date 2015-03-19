package gaffa.com.napkin;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.io.File;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;


public class MainActivity extends Activity {
    private DrawView mDraw;

    // Constants
    /**
     * Register your here app https://dev.twitter.com/apps/new and get your
     * consumer key and secret
     */
    static String TWITTER_CONSUMER_KEY = "S7o5ZsahM7KhGxEStkWVAh0sS"; // place your cosumer key here
    static String TWITTER_CONSUMER_SECRET = "sykynffVCPCj4aKCIaTuIOrVEHjexeuaNDLiJFIlcdwQjHofpB"; // place your consumer secret here

    // Preference Constants

    static final String PREF_KEY_OAUTH_TOKEN = "3096804793-eafsMP7ur5q5RfHn0lk8RYCVEwyyOkTYLjbNSuG";
    static final String PREF_KEY_OAUTH_SECRET = "vd0rw6lSrypFtSBMQSSVTat8fa40FVD8ZMun4vc00nC8s";
    static final String PREF_KEY_TWITTER_LOGIN = "8ch33se8";

    static final String TWITTER_CALLBACK_URL = "http://napkinsapp.com";

    // Twitter oauth urls

    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";


    // Login button
    Button btnShareTwitter;
    WebView myWebView;

    // Twitter
    private static Twitter twitter;
    private static RequestToken requestToken;

    // Shared Preferences
    private static SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // All UI elements
        btnShareTwitter = (Button) findViewById(R.id.btnLoginTwitter);
        myWebView = (WebView) findViewById(R.id.webView1);

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {

                if (url != null && url.startsWith(TWITTER_CALLBACK_URL))
                    new AfterLoginTask().execute(url);
                else
                    webView.loadUrl(url);
                return true;
            }
        });

        // Shared Preferences
        mSharedPreferences = getApplicationContext().getSharedPreferences(
                "MyPref", 0);

        /**
         * Twitter login button click event will call loginToTwitter() function
         * */
        btnShareTwitter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Call login twitter function
                new LoginTask().execute();
            }
        });

    }

    /**
     * Function to login twitter
     */
    private void loginToTwitter() {
        // Check if already logged in
        if (!isTwitterLoggedInAlready()) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
            Configuration configuration = builder.build();

            TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();

            try {
                requestToken = twitter
                        .getOAuthRequestToken(TWITTER_CALLBACK_URL);
            } catch (TwitterException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Check user already logged in your application using twitter Login flag is
     * fetched from Shared Preferences
     */
    private boolean isTwitterLoggedInAlready() {
        // return twitter login status from Shared Preferences
        return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
    }

    public void handleTwitterCallback(String url) {

        Uri uri = Uri.parse(url);

        // oAuth verifier
        final String verifier = uri
                .getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

        try {

            // Get the access token
            AccessToken accessToken1 = twitter.getOAuthAccessToken(
                    requestToken, verifier);

            // Shared Preferences
            SharedPreferences.Editor e = mSharedPreferences.edit();

            // After getting access token, access token secret
            // store them in application preferences
            e.putString(PREF_KEY_OAUTH_TOKEN, accessToken1.getToken());
            e.putString(PREF_KEY_OAUTH_SECRET, accessToken1.getTokenSecret());
            // Store login status - true
            e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
            e.commit(); // save changes

            Log.e("Twitter OAuth Token", "> " + accessToken1.getToken());

            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);

            // Access Token
            String access_token = mSharedPreferences.getString(
                    PREF_KEY_OAUTH_TOKEN, "");
            // Access Token Secret
            String access_token_secret = mSharedPreferences.getString(
                    PREF_KEY_OAUTH_SECRET, "");

            AccessToken accessToken = new AccessToken(access_token,
                    access_token_secret);
            Twitter twitter = new TwitterFactory(builder.build())
                    .getInstance(accessToken);
            // Log.i("HEy", "YOOO");
            // Update status
            // twitter4j.Status response = twitter
            //        .updateStatus("Hey there");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class postTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub
            post();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {


        }

    }

    public void postToTwitter(View view) {
        new postTask().execute();
        mDraw.reset();
    }

    public void post() {

        try {

            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);

            // Access Token
            String access_token = mSharedPreferences.getString(
                    PREF_KEY_OAUTH_TOKEN, "");
            // Access Token Secret
            String access_token_secret = mSharedPreferences.getString(
                    PREF_KEY_OAUTH_SECRET, "");

            AccessToken accessToken = new AccessToken(access_token,
                    access_token_secret);
            Twitter twitter = new TwitterFactory(builder.build())
                    .getInstance(accessToken);


            // Update status
          //  twitter4j.Status response = twitter
           //         .updateStatus("Hey there");
            File fileToTweet = mDraw.save();
            if (fileToTweet != null) {
                Log.i("HEy", "YOOO: " + fileToTweet.toString());
                StatusUpdate status = new StatusUpdate("Hey there "+fileToTweet.toString());
                status.setMedia(fileToTweet);
                twitter.updateStatus(status);
            }


            // twitter.upda

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class LoginTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub
            loginToTwitter();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            if (requestToken != null) {
                myWebView.loadUrl(requestToken.getAuthenticationURL());
                myWebView.setVisibility(View.VISIBLE);
                myWebView.requestFocus(View.FOCUS_DOWN);
                Log.i("", "YA");
            }else{
                setContentView(R.layout.draw_canvas);
                mDraw = (DrawView) findViewById(R.id.single_touch_view);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            Log.i("", "NA");

        }

    }

    class AfterLoginTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            myWebView.clearHistory();
            Log.i("", "SHIZZZ");
        }

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            handleTwitterCallback(params[0]);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            myWebView.setVisibility(View.GONE);
            setContentView(R.layout.draw_canvas);
            mDraw = (DrawView) findViewById(R.id.single_touch_view);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        }

    }

    public void save(View view) {

        mDraw.save();

    }

    @Override
    public void onBackPressed() {
        if (myWebView.getVisibility() == View.VISIBLE) {
            if (myWebView.canGoBack()) {
                myWebView.goBack();
                return;
            } else {
                myWebView.setVisibility(View.GONE);
                return;
            }
        }
        super.onBackPressed();
    }

    public void changeColorToBlack(View view) {
        mDraw.changeColorToBlack();
    }

    public void changeColorToRed(View view) {
        mDraw.changeColorToRed();
    }

    public void changeColorToBlue(View view) {
        mDraw.changeColorToBlue();
    }
}
