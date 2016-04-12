package com.bqt.chatclient;

import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.bqt.chatclient.databinding.RoomActivityBinding;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class RoomActivity extends AppCompatActivity
implements EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener{

    private RoomActivityBinding mBinding;
    private MessageListFragment mMessageListFragment;
    private Socket mSocket;
    private JSONObject mJSONObject;
    private InputMethodManager mInputMethodManager;
    private JSONArray mUserList = new JSONArray();
    private int mSoftKeyboardHeight = ViewGroup.LayoutParams.WRAP_CONTENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_room);
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        try {
            mJSONObject = new JSONObject(getIntent().getStringExtra("room"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getSupportActionBar().setTitle(mJSONObject.optString("name"));
        mMessageListFragment = new MessageListFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.contentFrame, mMessageListFragment).commit();

        mBinding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mBinding.editEmojicon.getText().toString();
                if (TextUtils.isEmpty(msg)) {
                } else {
                    try {
                        JSONObject data = new JSONObject();
                        data.put("msg", msg);
                        mSocket.emit("sendMessage", data);
                        mBinding.editEmojicon.setText("");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mBinding.editEmojicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

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
        final View content = findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mSoftKeyboardHeight = content.getRootView().getHeight() - content.getHeight();
            }
        });

    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(mBinding.editEmojicon);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(mBinding.editEmojicon, emojicon);
    }

    private PopupWindow mPopupWindow;


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
