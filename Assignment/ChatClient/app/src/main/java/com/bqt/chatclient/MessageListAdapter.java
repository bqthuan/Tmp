package com.bqt.chatclient;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bqt.chatclient.databinding.MessageItemViewBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mobiledev.bqt@gmail.com.
 */
public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageViewHolder> {

    private List<JSONObject> mData = new ArrayList<>();

    public void addMessage(JSONObject data) {
        mData.add(data);
        notifyItemInserted(mData.size() - 1);
        notifyItemRangeChanged(mData.size() - 1, mData.size());
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        MessageItemViewBinding binding = DataBindingUtil.inflate(inflater, R.layout.message_itemview, parent, false);
        return new MessageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        holder.setJSONObject(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private MessageItemViewBinding mBinding;
        private JSONObject mJSONObject;

        public MessageViewHolder(MessageItemViewBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.cardView.setUseCompatPadding(true);
        }

        public JSONObject getJSONObject() {
            return mJSONObject;
        }

        public void setJSONObject(JSONObject JSONObject) {
            mJSONObject = JSONObject;

            boolean isMe = mJSONObject.optBoolean("isMe");
            mBinding.meMsgContainer.setVisibility(isMe ? View.VISIBLE : View.INVISIBLE);
            mBinding.otherMsgContainer.setVisibility(!isMe ? View.VISIBLE : View.INVISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBinding.cardView.getLayoutParams();
            if (isMe) {
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                mBinding.meUserTxt.setText(mJSONObject.optString("user"));
                mBinding.meMsgTxt.setText(mJSONObject.optString("msg"));
            } else {
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                mBinding.otherUserTxt.setText(mJSONObject.optString("user"));
                mBinding.otherMsgTxt.setText(mJSONObject.optString("msg"));
            }

        }
    }
}
