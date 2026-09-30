package com.openclassrooms.realestatemanager.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import com.openclassrooms.realestatemanager.ui.fragments.RealEstateDetailFragment;
import com.openclassrooms.realestatemanager.data.models.RealEstate;
import com.openclassrooms.realestatemanager.R;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_container);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        }

        if (savedInstanceState == null) {
            RealEstate realEstate = (RealEstate) getIntent().getSerializableExtra("realEstate");
            if (realEstate != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.second_activity_fragment_container, RealEstateDetailFragment.newInstance(realEstate))
                        .commit();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
