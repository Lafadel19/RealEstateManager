package com.openclassrooms.realestatemanager.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.lifecycle.ViewModelProvider;
import com.openclassrooms.realestatemanager.data.database.AppDatabase;
import com.openclassrooms.realestatemanager.data.repository.RealEstateRepository;
import com.openclassrooms.realestatemanager.viewmodels.MainViewModel;
import com.openclassrooms.realestatemanager.viewmodels.ViewModelFactory;
import com.openclassrooms.realestatemanager.ui.fragments.RealEstateDetailFragment;
import com.openclassrooms.realestatemanager.ui.fragments.LoanCalculatorBottomSheet;
import com.openclassrooms.realestatemanager.ui.fragments.SearchFilterBottomSheet;
import com.openclassrooms.realestatemanager.ui.fragments.SettingsBottomSheet;
import com.openclassrooms.realestatemanager.data.models.RealEstate;
import com.openclassrooms.realestatemanager.ui.adapters.RealEstateAdapter;
import com.openclassrooms.realestatemanager.R;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FrameLayout detailContainer;
    private RealEstateAdapter adapter;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.configureViewModel();
        this.detailContainer = findViewById(R.id.activity_main_detail_container);
        this.configureRecyclerView();
        this.observeRealEstates();
    }

    private void configureViewModel() {
        RealEstateRepository repository = new RealEstateRepository(AppDatabase.getDatabase(this).realEstateDao());
        ViewModelFactory factory = new ViewModelFactory(repository);
        this.viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);
    }

    private void observeRealEstates() {
        viewModel.getRealEstates().observe(this, realEstates -> {
            if (realEstates != null) {
                adapter.setRealEstates(realEstates);
            }
        });
    }

    private void configureRecyclerView() {
        this.recyclerView = findViewById(R.id.activity_main_recycler_view);
        this.adapter = new RealEstateAdapter(new ArrayList<>(), realEstate -> {
            if (detailContainer != null) {
                // Tablet mode: Update detail fragment in the same activity
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.activity_main_detail_container, RealEstateDetailFragment.newInstance(realEstate))
                        .commit();
                
                // Show back arrow on tablet when a property is selected
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
                }
            } else {
                // Phone mode: Launch SecondActivity
                Intent intent = new Intent(this, SecondActivity.class);
                intent.putExtra("realEstate", realEstate);
                startActivity(intent);
            }
        });
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.back).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (detailContainer != null) {
                // Clear detail fragment on tablet and hide back arrow
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.activity_main_detail_container);
                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    }
                }
            }
            return true;
        }
        if (item.getItemId() == R.id.action_loan_calc) {
            LoanCalculatorBottomSheet.newInstance().show(getSupportFragmentManager(), "LoanCalculatorBottomSheet");
            return true;
        }
        if (item.getItemId() == R.id.action_map) {
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == R.id.action_search) {
            SearchFilterBottomSheet.newInstance(viewModel.getCurrentFilter(), filter -> {
                viewModel.setFilter(filter);
            }).show(getSupportFragmentManager(), "SearchFilterBottomSheet");
            return true;
        }
        if (item.getItemId() == R.id.action_settings) {
            SettingsBottomSheet.newInstance(() -> {
                adapter.notifyDataSetChanged();
            }).show(getSupportFragmentManager(), "SettingsBottomSheet");
            return true;
        }
        if (item.getItemId() == R.id.add) {
            Intent intent = new Intent(this, AddFormActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
