package com.tobi.simplemusicapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tobi.simplemusicapp.databinding.PlaylistFragmentBinding;
import com.tobi.simplemusicapp.databinding.PlaylistListviewBinding;


public class PlaylistListview extends Fragment {

    private PlaylistListviewBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = PlaylistListviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}