package com.bqt.chatclient;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bqt.chatclient.databinding.RoomItemViewBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mobiledev.bqt@gmail.com.
 */
public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.RoomViewHolder> {

    private List<JSONObject> mData = new ArrayList<>();
    private View.OnClickListener mOnItemClickListener;

    public RoomListAdapter(View.OnClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setData(List<JSONObject> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RoomItemViewBinding binding = DataBindingUtil.inflate(inflater, R.layout.room_itemview, parent, false);
        binding.getRoot().setOnClickListener(mOnItemClickListener);
        return new RoomViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RoomViewHolder holder, int position) {
        holder.setJSONObject(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        private RoomItemViewBinding mBinding;
        private JSONObject mJSONObject;

        public RoomViewHolder(RoomItemViewBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public JSONObject getJSONObject() {
            return mJSONObject;
        }

        public void setJSONObject(JSONObject JSONObject) {
            mJSONObject = JSONObject;
            mBinding.getRoot().setTag(mJSONObject);
//            Picasso.with(mBinding.getRoot().getContext())
//                    .load(mJSONObject.optString("icon"))
//                    .into(mBinding.imageView);
            mBinding.roomTxt.setText(mJSONObject.optString("name"));
            mBinding.countTxt.setText(mJSONObject.optString("count") + " users");
        }
    }
}
