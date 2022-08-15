package com.tobi.simplemusicapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.tobi.simplemusicapp.databinding.PlaylistFragmentBinding;

import java.util.ArrayList;

class PlaylistAdapter extends ArrayAdapter<Song> {

    // invoke the suitable constructor of the ArrayAdapter class
    public PlaylistAdapter(@NonNull Context context, ArrayList<Song> arrayList) {

        // pass the context and arrayList for the super
        // constructor of the ArrayAdapter class
        super(context, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // convertView which is recyclable view
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.playlist_listview, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        Song currentSong = getItem(position);

        // then according to the position of the view assign the desired image for the same
        TextView titleTextview = currentItemView.findViewById(R.id.title);
        titleTextview.setText(currentSong.getTitle());

        TextView subtitleTextview = currentItemView.findViewById(R.id.subtitle);
        subtitleTextview.setText(currentSong.getArtist());

        // then return the recyclable view
        return currentItemView;
    }
}

public class PlaylistFragment extends Fragment {


    private PlaylistFragmentBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = PlaylistFragmentBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}