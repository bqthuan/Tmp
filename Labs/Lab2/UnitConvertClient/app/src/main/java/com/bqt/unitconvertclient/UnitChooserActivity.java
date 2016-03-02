package com.bqt.unitconvertclient;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class UnitChooserActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private String mUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_chooser);
        mRecyclerView = (RecyclerView) findViewById(R.id.unitRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new TestAdapter());
        findViewById(R.id.okBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUnit != null) {
                    Intent data = new Intent();
                    data.putExtra("bi-unit", mUnit);
                    setResult(RESULT_OK, data);
                }
                finish();
            }
        });
    }

    class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestViewHolder> implements View.OnClickListener {

        List<String> mData = new ArrayList<>();
        View mPreView;

        {
            mData.add("dollar-vietnam");
            mData.add("vietnam-dollar");
        }

        @Override
        public void onClick(View v) {
            if (mPreView != null) {
                mPreView.setBackgroundColor(0);
            }
            mUnit = ((TextView) v).getText().toString();
            mPreView = v;
            mPreView.setBackgroundColor(Color.RED);
        }

        @Override
        public TestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView itemView = new TextView(parent.getContext());
            itemView.setGravity(Gravity.CENTER);
            itemView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100));
            itemView.setOnClickListener(this);
            TestViewHolder holder = new TestViewHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(TestViewHolder holder, int position) {
            holder.setUnit(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class TestViewHolder extends RecyclerView.ViewHolder {
            TextView mUnitTxt;

            public TestViewHolder(View itemView) {
                super(itemView);
                mUnitTxt = (TextView) itemView;

            }

            public void setUnit(String units) {
                mUnitTxt.setText(units);
            }

        }

    }
}
