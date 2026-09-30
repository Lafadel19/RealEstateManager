package com.openclassrooms.realestatemanager.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.openclassrooms.realestatemanager.R;
import java.util.Locale;

public class LoanCalculatorBottomSheet extends BottomSheetDialogFragment {

    private EditText priceEdit, downPaymentEdit, durationEdit, interestRateEdit;
    private LinearLayout resultContainer;
    private TextView monthlyPaymentText, totalInterestText;

    public static LoanCalculatorBottomSheet newInstance() {
        return new LoanCalculatorBottomSheet();
    }

    @Nullable
    @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loan_calculator, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        priceEdit = view.findViewById(R.id.loan_price);
        downPaymentEdit = view.findViewById(R.id.loan_down_payment);
        durationEdit = view.findViewById(R.id.loan_duration);
        interestRateEdit = view.findViewById(R.id.loan_interest_rate);
        resultContainer = view.findViewById(R.id.result_container);
        monthlyPaymentText = view.findViewById(R.id.result_monthly_payment);
        totalInterestText = view.findViewById(R.id.result_total_interest);

        view.findViewById(R.id.btn_calculate).setOnClickListener(v -> calculateLoan());
    }

    private void calculateLoan() {
        String priceStr = priceEdit.getText().toString().trim();
        String downPaymentStr = downPaymentEdit.getText().toString().trim();
        String durationStr = durationEdit.getText().toString().trim();
        String interestRateStr = interestRateEdit.getText().toString().trim();

        if (priceStr.isEmpty() || durationStr.isEmpty() || interestRateStr.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            double downPayment = downPaymentStr.isEmpty() ? 0.0 : Double.parseDouble(downPaymentStr);
            int durationYears = Integer.parseInt(durationStr);
            double annualInterestRate = Double.parseDouble(interestRateStr);

            double loanAmount = price - downPayment;
            if (loanAmount < 0) {
                Toast.makeText(requireContext(), "Down payment cannot exceed price", Toast.LENGTH_SHORT).show();
                return;
            }

            double monthlyRate = annualInterestRate / 100.0 / 12.0;
            int totalMonths = durationYears * 12;

            double monthlyPayment;
            if (monthlyRate == 0) {
                monthlyPayment = totalMonths > 0 ? loanAmount / totalMonths : 0;
            } else {
                monthlyPayment = loanAmount * (monthlyRate / (1 - Math.pow(1 + monthlyRate, -totalMonths)));
            }

            // Round to 2 decimal places
            monthlyPayment = Math.round(monthlyPayment * 100.0) / 100.0;
            double totalInterestPaid = Math.round(((monthlyPayment * totalMonths) - loanAmount) * 100.0) / 100.0;

            monthlyPaymentText.setText(String.format(Locale.getDefault(), "Monthly Payment: $%.2f", monthlyPayment));
            totalInterestText.setText(String.format(Locale.getDefault(), "Total Interest Paid: $%.2f", totalInterestPaid));
            resultContainer.setVisibility(View.VISIBLE);

        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }
}
