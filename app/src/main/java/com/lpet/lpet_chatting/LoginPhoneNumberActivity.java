package com.lpet.lpet_chatting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.hbb20.CountryCodePicker;

public class LoginPhoneNumberActivity extends AppCompatActivity {
    CountryCodePicker countryCodePicker;
    EditText phoneNumber;
    Button sendOtpBtn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_number);

        countryCodePicker = findViewById(R.id.ccp);
        phoneNumber = findViewById(R.id.et_phone_number);
        sendOtpBtn = findViewById(R.id.btn_send_otp);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(ProgressBar.GONE);

        countryCodePicker.registerCarrierNumberEditText(phoneNumber);

        sendOtpBtn.setOnClickListener(v -> {
            if (!countryCodePicker.isValidFullNumber()) {
                phoneNumber.setError("Numero de telefono invalido");
                return;
            }

            Intent i = new Intent(LoginPhoneNumberActivity.this, LoginOtpActivity.class);
            i.putExtra("phone", countryCodePicker.getFullNumberWithPlus());
            startActivity(i);
        });
    }
}