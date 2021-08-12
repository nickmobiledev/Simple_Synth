package com.example.wavemaker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.wavemaker.databinding.RecyclerViewLayoutBinding;

public class RecyclerViewFragment extends Fragment {
    MainActivity mainActivity;

    public RecyclerViewFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerViewLayoutBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.recycler_view_layout,
                container,
                false);
        binding.recyclerview.setAdapter(new RecyclerViewAdapter(mainActivity));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(binding.getRoot().getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerview.setLayoutManager(layoutManager);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(binding.recyclerview);
        return binding.getRoot();

    }
}

