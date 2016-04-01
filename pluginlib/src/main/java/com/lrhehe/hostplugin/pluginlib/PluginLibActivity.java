package com.lrhehe.hostplugin.pluginlib;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * @Author ray
 * @Date 3/31/16.
 */
public class PluginLibActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pluginlib);

        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PluginLibUtils.toast(PluginLibActivity.this, "changed nice nice!");
            }
        });
    }
}
