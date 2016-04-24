package com.bqt.chatclient;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.bqt.chatclient.databinding.RoomItemViewBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mobiledev.bqt@gmail.com.
 */
public class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.RoomViewHolder>
        implements Filterable {
    private List<JSONObject> mOriginData = new ArrayList<>();
    private List<JSONObject> mData = new ArrayList<>();
    private View.OnClickListener mOnItemClickListener;

    public RoomListAdapter(View.OnClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setData(List<JSONObject> data) {
        mOriginData.clear();
        mOriginData.addAll(data);
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                List<JSONObject> data = new ArrayList<>();
                for (JSONObject object : mOriginData) {
                    if (object.optString("name").toLowerCase().contains(charSequence.toString())) {
                        data.add(object);
                    }
                }
                results.count = data.size();
                results.values = data;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mData.clear();
                if (filterResults.count == 0) {

                } else {
                    mData.addAll((List<JSONObject>) filterResults.values);
                }
                notifyDataSetChanged();
            }
        };
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        private RoomItemViewBinding mBinding;
        private JSONObject mJSONObject;

        public RoomViewHolder(RoomItemViewBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.cardView.setUseCompatPadding(true);
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
