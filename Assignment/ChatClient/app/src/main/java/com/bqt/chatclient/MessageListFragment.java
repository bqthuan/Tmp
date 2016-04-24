package com.bqt.chatclient;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bqt.chatclient.databinding.RoomFragmentBinding;

import org.json.JSONObject;

/**
 * Created by mobiledev.bqt@gmail.com.
 */
public class MessageListFragment extends Fragment {

    private RoomFragmentBinding mBinding;
    private MessageListAdapter mMessageListAdapter;
    private Handler mHandler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.room_fragment, container, false);
        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mMessageListAdapter = new MessageListAdapter();
        mBinding.recyclerView.setAdapter(mMessageListAdapter);
        return mBinding.getRoot();
    }

    public void addMessage(final JSONObject jsonObject) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (getView() == null) {
                    mHandler.postDelayed(this, 100);
                } else {
                    mMessageListAdapter.addMessage(jsonObject);
                    mBinding.recyclerView.scrollToPosition(mMessageListAdapter.getItemCount() - 1);
                }
            }
        });
    }
}
