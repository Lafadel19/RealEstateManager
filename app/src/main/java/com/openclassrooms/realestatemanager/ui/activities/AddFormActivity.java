package com.openclassrooms.realestatemanager.ui.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.openclassrooms.realestatemanager.data.database.AppDatabase;
import com.openclassrooms.realestatemanager.data.repository.RealEstateRepository;
import com.openclassrooms.realestatemanager.viewmodels.AddRealEstateViewModel;
import com.openclassrooms.realestatemanager.viewmodels.ViewModelFactory;
import com.openclassrooms.realestatemanager.data.models.RealEstate;
import com.openclassrooms.realestatemanager.ui.adapters.PropertyPhotoAdapter;
import com.openclassrooms.realestatemanager.R;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddFormActivity extends AppCompatActivity {

    private EditText city, postcode, price, description, surface, rooms, bathrooms, bedrooms, address, addDate;
    private CheckBox schoolCheck, stationCheck, shopsCheck, isSoldCheck;
    private AutoCompleteTextView type, agentDropdown;
    private RecyclerView photosRecyclerView;
    private PropertyPhotoAdapter photoAdapter;
    private final List<String> photoPaths = new ArrayList<>();
    private Uri cameraPhotoUri;
    private AddRealEstateViewModel viewModel;
    private final Calendar calendar = Calendar.getInstance();
    private long editingRealEstateId = -1;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    if (selectedImage != null) {
                        photoPaths.add(selectedImage.toString());
                        photoAdapter.notifyItemInserted(photoPaths.size() - 1);
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    photoPaths.add(cameraPhotoUri.toString());
                    photoAdapter.notifyItemInserted(photoPaths.size() - 1);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_add);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_add_real_estate);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        }

        configureViewModel();
        initViews();
        setupRecyclerView();
        setupButtons();
        setupTypeDropdown();
        setupAgentDropdown();
        setupDatePicker();
        observeViewModel();

        RealEstate editingEstate = (RealEstate) getIntent().getSerializableExtra("realEstate");
        if (editingEstate != null) {
            editingRealEstateId = editingEstate.getId();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.title_edit_real_estate);
            }
            populateForm(editingEstate);
        }
    }

    private void populateForm(RealEstate estate) {
        type.setText(estate.getType(), false);
        city.setText(estate.getCity());
        postcode.setText(estate.getPostcode());
        price.setText(String.valueOf(estate.getPrice()));
        description.setText(estate.getDescription());
        surface.setText(String.valueOf(estate.getSurface()));
        rooms.setText(String.valueOf(estate.getRooms()));
        bathrooms.setText(String.valueOf(estate.getBathrooms()));
        bedrooms.setText(String.valueOf(estate.getBedrooms()));
        address.setText(estate.getAddress());
        addDate.setText(estate.getAddDate());
        agentDropdown.setText(estate.getAgentName(), false);

        if (estate.isSold()) {
            isSoldCheck.setChecked(true);
        }

        if (estate.getInterestPoints() != null) {
            schoolCheck.setChecked(estate.getInterestPoints().contains("School"));
            stationCheck.setChecked(estate.getInterestPoints().contains("Station"));
            shopsCheck.setChecked(estate.getInterestPoints().contains("Shops"));
        }

        if (estate.getPhotos() != null) {
            photoPaths.clear();
            photoPaths.addAll(estate.getPhotos());
            photoAdapter.notifyDataSetChanged();
        }
    }

    private void configureViewModel() {
        RealEstateRepository repository = new RealEstateRepository(AppDatabase.getDatabase(this.getApplicationContext()).realEstateDao());
        ViewModelFactory factory = new ViewModelFactory(repository);
        this.viewModel = new ViewModelProvider(this, factory).get(AddRealEstateViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getSaveSuccess().observe(this, success -> {
            if (success != null) {
                if (success) {
                    Toast.makeText(this, editingRealEstateId != -1 ? R.string.msg_property_updated : R.string.msg_property_added, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, R.string.msg_check_inputs, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.add).setVisible(false);
        menu.findItem(R.id.back).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        type = findViewById(R.id.form_type);
        city = findViewById(R.id.form_city);
        postcode = findViewById(R.id.form_postcode);
        price = findViewById(R.id.form_price);
        description = findViewById(R.id.form_description);
        surface = findViewById(R.id.form_surface);
        rooms = findViewById(R.id.form_rooms);
        bathrooms = findViewById(R.id.form_bathrooms);
        bedrooms = findViewById(R.id.form_bedrooms);
        address = findViewById(R.id.form_address);
        addDate = findViewById(R.id.form_add_date);
        agentDropdown = findViewById(R.id.form_agent);
        schoolCheck = findViewById(R.id.poi_school);
        stationCheck = findViewById(R.id.poi_station);
        shopsCheck = findViewById(R.id.poi_shops);
        isSoldCheck = findViewById(R.id.form_is_sold);
        photosRecyclerView = findViewById(R.id.form_photos_recycler_view);
    }

    private void setupRecyclerView() {
        photoAdapter = new PropertyPhotoAdapter(photoPaths);
        photosRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        photosRecyclerView.setAdapter(photoAdapter);
    }

    private void setupButtons() {
        findViewById(R.id.btn_add_photo).setOnClickListener(v -> showPhotoOptions());
        findViewById(R.id.btn_validate).setOnClickListener(v -> validateForm());
    }

    private void setupTypeDropdown() {
        String[] types = {"House", "Apartment", "Penthouse"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, types);
        type.setAdapter(adapter);
    }

    private void setupAgentDropdown() {
        String[] agents = {"Alexa", "Walter", "Harry", "Emma"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, agents);
        agentDropdown.setAdapter(adapter);
    }

    private void setupDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            updateLabel();
        };

        addDate.setOnClickListener(v -> new DatePickerDialog(AddFormActivity.this, dateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.getDefault());
        addDate.setText(dateFormat.format(calendar.getTime()));
    }

    private void showPhotoOptions() {
        CharSequence[] options = {getString(R.string.dialog_take_photo), getString(R.string.dialog_choose_gallery)};
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_add_photo)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        launchCamera();
                    } else {
                        launchGallery();
                    }
                })
                .show();
    }

    private void launchGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void launchCamera() {
        try {
            File photoFile = createImageFile();
            cameraPhotoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPhotoUri);
            cameraLauncher.launch(intent);
        } catch (IOException e) {
            Toast.makeText(this, R.string.msg_error_file, Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void validateForm() {
        String addressStr = address.getText() != null ? address.getText().toString().trim() : "";
        String cityStr = city.getText() != null ? city.getText().toString().trim() : "";
        String postcodeStr = postcode.getText() != null ? postcode.getText().toString().trim() : "";


        if ((!addressStr.isEmpty() || !cityStr.isEmpty()) && Geocoder.isPresent()) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                String query = addressStr + ", " + postcodeStr + " " + cityStr;
                List<Address> addresses = geocoder.getFromLocationName(query, 1);
                if (addresses == null || addresses.isEmpty()) {
                    Log.w("AddFormActivity", "Geocoder could not resolve location: " + query);
                }
            } catch (IOException e) {
                Log.e("AddFormActivity", "Geocoder exception", e);
            }
        }

        List<String> poi = new ArrayList<>();
        if (schoolCheck.isChecked()) poi.add("School");
        if (stationCheck.isChecked()) poi.add("Station");
        if (shopsCheck.isChecked()) poi.add("Shops");

        viewModel.validateAndSave(
                editingRealEstateId,
                type.getText().toString(),
                cityStr,
                postcodeStr,
                price.getText().toString(),
                description.getText().toString(),
                surface.getText().toString(),
                rooms.getText().toString(),
                bathrooms.getText().toString(),
                bedrooms.getText().toString(),
                addressStr,
                photoPaths,
                poi,
                isSoldCheck.isChecked(),
                agentDropdown.getText().toString(),
                addDate.getText().toString()
        );
    }
}
