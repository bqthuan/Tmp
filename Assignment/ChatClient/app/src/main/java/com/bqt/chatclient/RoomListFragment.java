package com.bqt.chatclient;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bqt.chatclient.databinding.RoomListFragmentBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mobiledev.bqt@gmail.com.
 */
public class RoomListFragment extends Fragment {

    public interface OnRoomClickListener {
        void onRoomClick(JSONObject jsonObject);
    }

    private RoomListFragmentBinding mBinding;
    private JSONArray mData;
    private Handler mHandler = new Handler();
    private RoomListAdapter mRoomListAdapter;
    private OnRoomClickListener mOnRoomClickListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnRoomClickListener = (OnRoomClickListener) context;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.roomlist_fragment, container, false);
        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRoomListAdapter = new RoomListAdapter(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = (JSONObject) v.getTag();
                mOnRoomClickListener.onRoomClick(jsonObject);
            }
        });
        mBinding.recyclerView.setAdapter(mRoomListAdapter);

        return mBinding.getRoot();
    }

    public void setData(final JSONArray data) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (getView() == null) {
                    mHandler.postDelayed(this, 100);
                } else {
                    mData = data;
                    try {
                        List<JSONObject> rooms = new ArrayList<>();
                        for (int i = 0; i < mData.length(); ++i) {
                            rooms.add(data.getJSONObject(i));
                        }
                        mRoomListAdapter.setData(rooms);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
