package com.bqt.unitconvertclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mUnitTxt;
    private TextView mResultTxt;
    private EditText mValueEdit;
    private ImageView mUnitEdit;
    private Button mConvertBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUnitTxt = (TextView) findViewById(R.id.unitText);
        mUnitTxt.setText("dollar-vietnam");
        mResultTxt = (TextView) findViewById(R.id.resultTxt);
        mValueEdit = (EditText) findViewById(R.id.valueEdit);
        mUnitEdit = (ImageView) findViewById(R.id.changeUnitBtn);
        mConvertBtn = (Button) findViewById(R.id.convertBtn);

        mUnitEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, UnitChooserActivity.class);
                startActivityForResult(intent, 11);
            }
        });
        mConvertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String[] units = mUnitTxt.getText().toString().split("-");
                            float val = Float.valueOf(mValueEdit.getText().toString());
                            String url = "http://128.199.72.110:3750/convert?src=" + units[0] + "&des=" + units[1] + "&val=" + val;
                            final JSONObject obj = new JSONObject(IOUtils.toString(new URL(url)));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (obj.optBoolean("success")) {
                                        mResultTxt.setText(obj.optString("result"));
                                    } else {
                                        mResultTxt.setText(obj.optString("error") + "\n" + obj.optString("supported"));
                                    }
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null)
            mUnitTxt.setText(data.getStringExtra("bi-unit"));
    }
}
