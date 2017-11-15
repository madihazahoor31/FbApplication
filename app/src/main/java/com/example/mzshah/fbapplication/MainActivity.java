package com.example.mzshah.fbapplication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.ArrayList;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class MainActivity extends AppCompatActivity {
    TextView txtval;
    LoginButton loginButton;
    CallbackManager callbackManager;
    EditText email, phone;
    String emailstring, phonestring,otp;
    String result = null;
    public static final int CONNECTION_TIMEOUT = 15000;
    public static final String SERVER_ADDRESS = "http://madihachaty.000webhostapp.com/";
    ProgressDialog progressDialog;

    int flag = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        initializeControls();
        loginWithFb();

    }
    private void initializeControls(){
         txtval = (TextView) findViewById(R.id.textview);
         loginButton=(LoginButton)findViewById(R.id.login_button);
         callbackManager=CallbackManager.Factory.create();
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);
    }
    public void onRegisterNextClick(View view) {
        emailstring = email.getText().toString();
        phonestring = phone.getText().toString();
        Log.e("ERROR","reached at buttonclick ok");
        if (emailstring.equals(null)) {
            Toast.makeText(this, "email not entered", Toast.LENGTH_LONG).show();
            flag = 1;
        }
        if (phonestring.equals(null)) {
            Toast.makeText(this, "phone not entered", Toast.LENGTH_LONG).show();
            flag = 1;
        }
        if (flag == 0) {
            askforotp();
            Log.e("ERROR","reached   jhbjhbat backaskforotp");
        }
    } private void askforotp() {
        Log.e("ERROR","reached at askforotp");
        Boolean check=isNetworkAvailable();

        if(!check==Boolean.FALSE)
            new StoreDataAsyncTask().execute();
        else
            Toast.makeText(this, "network not available", Toast.LENGTH_LONG).show();
        Log.e("ERROR","reached at progressdismiss");

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public class StoreDataAsyncTask extends AsyncTask<Void, Void, Void> {
        public StoreDataAsyncTask() {
        }
        @Override
        protected void onPreExecute() {
            progressDialog=new ProgressDialog(MainActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Processing");
            progressDialog.setMessage("Please wait");
            Log.d("Reached","inside show dialog");
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            Log.e("ERROR","reached at doinbackground");
            ArrayList<NameValuePair> data_to_send = new ArrayList<NameValuePair>();
            data_to_send.add(new BasicNameValuePair("email", emailstring));
            data_to_send.add(new BasicNameValuePair("phone", phonestring));
            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "otpgenerator.php");
            try {
                post.setEntity(new UrlEncodedFormEntity(data_to_send));
                Log.e("ERROR"," before response");

                HttpResponse httpResponse = client.execute(post);
                HttpEntity entity = httpResponse.getEntity();
                Log.e("ERROR","reached at response");
                result = EntityUtils.toString(entity);
                Log.e("ERROR",result);

                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.length() == 0) {
                    Log.e("OTP=","nothing");
                } else {

                    if (jsonObject.has("otp")) {
                        otp = jsonObject.getString("otp");
                        Log.e("OTP=",otp.toString());

                    }
                    else {
                        Log.e("OTP=","has no otp");

                    }

                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void v) {
            progressDialog.dismiss();
            Intent intent = new Intent(MainActivity.this, OTPActivity.class);
            startActivity(intent);

        }
    }
    private void loginWithFb(){
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
         txtval.setText("Login success\n"+loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                txtval.setText("Login cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                txtval.setText("Login error");
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
