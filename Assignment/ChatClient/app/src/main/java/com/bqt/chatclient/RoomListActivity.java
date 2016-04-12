package com.bqt.chatclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bqt.chatclient.databinding.RoomListActivityBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class RoomListActivity extends AppCompatActivity
        implements RoomListFragment.OnRoomClickListener {

    private RoomListActivityBinding mBinding;
    private RoomListFragment mRoomListFragment;
    private Socket mClient;
    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_roomlist);
        setSupportActionBar(mBinding.toolbar);

        mClient = ((ChatApplication) getApplication()).getSocket();

        mRoomListFragment = new RoomListFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.contentFrame, mRoomListFragment).commit();

        mUsername = getIntent().getStringExtra("username");
        getSupportActionBar().setTitle(mUsername);


        mBinding.addRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(RoomListActivity.this, CreateRoomActivity.class));
            }
        });

        setupEvents();

        mClient.emit("updateRoomList");
    }

    private void setupEvents() {
        mClient.on("updateRoomList", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONArray jsonArray = (JSONArray) args[0];
                mRoomListFragment.setData(jsonArray);
            }
        });

    }

    @Override
    public void onRoomClick(JSONObject jsonObject) {

        Intent intent = new Intent(this, RoomActivity.class);
        intent.putExtra("room", jsonObject.toString());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_logout) {

            SharedPreferences sharedPreferences = getSharedPreferences("settings", 0);
            sharedPreferences.edit().clear().commit();
            Intent intent = new Intent(this, AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
