package com.rs.roomservice.activities;

import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.rs.roomservice.R;
import com.rs.roomservice.fragments.RoomsFragment;
import com.rs.roomservice.fragments.TasksFragment;
import com.rs.roomservice.ui.main.SectionsPagerAdapter;

public class ManagerActivity extends AppCompatActivity implements TasksFragment.OnFragmentInteractionListener, RoomsFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}