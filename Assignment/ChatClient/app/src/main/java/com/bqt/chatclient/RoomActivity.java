package com.bqt.chatclient;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.bqt.chatclient.databinding.RoomActivityBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class RoomActivity extends AppCompatActivity {

    private RoomActivityBinding mBinding;
    private MessageListFragment mMessageListFragment;
    private EmojIconActions mEmojIconActions;

    private Socket mSocket;
    private JSONObject mJSONObject;
    private JSONArray mUserList = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_room);

        mEmojIconActions = new EmojIconActions(this, mBinding.getRoot(), mBinding.emojiconEditText, mBinding.emojiBtn);
        mEmojIconActions.ShowEmojIcon();
        mEmojIconActions.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
            }

            @Override
            public void onKeyboardClose() {
            }
        });

        mBinding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = mBinding.emojiconEditText.getText().toString();
                JSONObject obj = new JSONObject();
                try {
                    obj.put("msg", msg);
                    mSocket.emit("sendMessage", obj);
                    mBinding.emojiconEditText.setText("");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            mJSONObject = new JSONObject(getIntent().getStringExtra("room"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getSupportActionBar().setTitle(mJSONObject.optString("name"));
        mMessageListFragment = new MessageListFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.contentFrame, mMessageListFragment).commit();

        mSocket = ((ChatApplication) getApplication()).getSocket();
        mSocket.emit("joinRoom", mJSONObject.optString("id"));
        mSocket.on("sendMessage", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject data = (JSONObject) args[0];
                    JSONObject user = getUser(data.optString("userId"));
                    JSONObject msg = data.getJSONObject("msg");
                    msg.put("user", user.optString("username"));
                    msg.put("isMe", user.optString("id").equals("/#" + mSocket.id()));

                    mMessageListFragment.addMessage(msg);
                } catch (JSONException e) {
                    Log.e("TEST", e.getMessage());
                }
            }
        });
        mSocket.on("updateUserList", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                mUserList = (JSONArray) args[0];
            }
        });

    }

    private JSONObject getUser(String id) throws JSONException {
        for (int i = 0; i < mUserList.length(); ++i) {
            JSONObject user = mUserList.getJSONObject(i);
            if (user.optString("id").equals(id)) {
                return user;
            }
        }
        return null;
    }
}
