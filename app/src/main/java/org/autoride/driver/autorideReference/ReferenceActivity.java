package org.autoride.driver.autorideReference;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.autoride.driver.R;
import org.autoride.driver.app.LocaleHelpers;
import org.autoride.driver.notifications.commons.Common;

public class ReferenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reference);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content, RecyclerViewFragment.newInstance())
                .commit();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelpers.setLocale(base, Common.getSelectedLanguage(base)));
    }
}
