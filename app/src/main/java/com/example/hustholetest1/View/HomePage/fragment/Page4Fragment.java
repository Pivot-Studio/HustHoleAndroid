package com.example.hustholetest1.View.HomePage.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.hustholetest1.R;


public class Page4Fragment extends Fragment {
    public static Page4Fragment newInstance() {
        Page4Fragment fragment = new Page4Fragment();
        return fragment;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page4fragment, container, false);
        return rootView;
    }
}
