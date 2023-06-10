package io.laonda.storestamp.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import io.laonda.storestamp.R;

public class CrashLogActivity extends Activity {

    public static final String CRASH_LOG = "CRASH_LOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crash_log_activity);

        ((TextView) findViewById(R.id.crash_log_textview)).setText(getIntent().getStringExtra(CRASH_LOG));
    }
}
