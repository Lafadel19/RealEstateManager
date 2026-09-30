package com.openclassrooms.realestatemanager.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.utils.SettingsManager;

public class SettingsBottomSheet extends BottomSheetDialogFragment {

    private RadioGroup currencyGroup, dateFormatGroup;
    private Runnable onSettingsSavedListener;

    public static SettingsBottomSheet newInstance(Runnable onSettingsSavedListener) {
        SettingsBottomSheet fragment = new SettingsBottomSheet();
        fragment.onSettingsSavedListener = onSettingsSavedListener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        initViews(view);
        loadCurrentSettings(view);
        return view;
    }

    private void initViews(View view) {
        currencyGroup = view.findViewById(R.id.radio_group_currency);
        dateFormatGroup = view.findViewById(R.id.radio_group_date_format);

        view.findViewById(R.id.btn_save_settings).setOnClickListener(v -> saveSettings());
    }

    private void loadCurrentSettings(View view) {
        String currency = SettingsManager.getCurrency(requireContext());
        if ("EUR".equals(currency)) {
            ((RadioButton) view.findViewById(R.id.radio_eur)).setChecked(true);
        } else {
            ((RadioButton) view.findViewById(R.id.radio_usd)).setChecked(true);
        }

        String dateFormat = SettingsManager.getDateFormat(requireContext());
        if ("MM/dd/yyyy".equals(dateFormat)) {
            ((RadioButton) view.findViewById(R.id.radio_date_mm_dd_yyyy)).setChecked(true);
        } else {
            ((RadioButton) view.findViewById(R.id.radio_date_dd_mm_yyyy)).setChecked(true);
        }
    }

    private void saveSettings() {
        String currency = currencyGroup.getCheckedRadioButtonId() == R.id.radio_eur ? "EUR" : "USD";
        String dateFormat = dateFormatGroup.getCheckedRadioButtonId() == R.id.radio_date_mm_dd_yyyy ? "MM/dd/yyyy" : "dd/MM/yyyy";

        SettingsManager.setCurrency(requireContext(), currency);
        SettingsManager.setDateFormat(requireContext(), dateFormat);

        Toast.makeText(requireContext(), "Settings saved successfully", Toast.LENGTH_SHORT).show();

        if (onSettingsSavedListener != null) {
            onSettingsSavedListener.run();
        }
        dismiss();
    }
}
