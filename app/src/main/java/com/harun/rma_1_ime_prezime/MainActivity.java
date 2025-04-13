package com.harun.rma_1_ime_prezime;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.harun.rma_1_ime_prezime.databinding.ActivityMainBinding;
import com.harun.rma_1_ime_prezime.form_control.MainActivityControl;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.emptySectionMessage.setVisibility(View.VISIBLE);

        MainActivityControl.animateHeader(binding);

        MainActivityControl.setupMenu(binding);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }


}