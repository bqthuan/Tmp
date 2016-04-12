package com.bqt.chatclient;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.bqt.chatclient.databinding.CreateRoomActivityBinding;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;

public class CreateRoomActivity extends AppCompatActivity {

    private CreateRoomActivityBinding mBinding;
    private Socket mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_room);

        mClient = ((ChatApplication) getApplication()).getSocket();

        mBinding.createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mBinding.roomNameExit.getText().toString();
                mBinding.msgTxt.setVisibility(View.GONE);
                if (TextUtils.isEmpty(name)) {
                    mBinding.msgTxt.setVisibility(View.VISIBLE);
                    mBinding.msgTxt.setText("Room name must not empty");
                } else {
                    try {
                        mBinding.progressBar.setVisibility(View.VISIBLE);
                        JSONObject obj = new JSONObject();
                        obj.put("name", name);
                        mClient.emit("createRoom", obj);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}
