package com.bqt.chatclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.bqt.chatclient.databinding.AuthActivityBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class AuthActivity extends AppCompatActivity {

    private AuthActivityBinding mBinding;
    private Socket mClient;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_auth);

        mSharedPreferences = getSharedPreferences("settings", 0);
        String username = mSharedPreferences.getString("username", null);
        if (username != null) {
            connect(username);
        } else {
            mBinding.authBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String username = mBinding.usernameEdit.getText().toString();
                    mBinding.msgTxt.setVisibility(View.GONE);
                    if (TextUtils.isEmpty(username)) {
                        mBinding.msgTxt.setVisibility(View.VISIBLE);
                        mBinding.msgTxt.setText("Username must not empty");
                    } else {
                        mBinding.progressBar.setVisibility(View.VISIBLE);

                        connect(username);
                    }
                }
            });
        }

    }

    private void connect(final String username) {
        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.reconnection = false;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username);
            opts.query = "user=" + jsonObject.toString();
            mClient = IO.socket("http://10.0.2.2:3000", opts);
            mClient.on("auth", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    ((ChatApplication) getApplication()).setSocket(mClient);
                    mSharedPreferences.edit().putString("username", username).commit();
                    Intent intent = new Intent(AuthActivity.this, RoomListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("username", username);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mBinding.progressBar.setVisibility(View.GONE);
                        }
                    });
                    startActivity(intent);
                }
            });
            mClient.connect();

            ((ChatApplication) getApplication()).setSocket(mClient);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
