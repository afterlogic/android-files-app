package com.afterlogic.aurora.drive.presentation.ui.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.databinding.FragmentConnectionFailedBinding;
import com.afterlogic.aurora.drive.presentation.ui.SendFilesActivity;

/**
 * Created by sashka on 18.01.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class ConnectionFailedFragment extends Fragment {

    public static ConnectionFailedFragment newInstance() {

        Bundle args = new Bundle();

        ConnectionFailedFragment fragment = new ConnectionFailedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_connection_failed, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentConnectionFailedBinding binding = DataBindingUtil.bind(view);
        binding.setOnRetryListener(button -> ((SendFilesActivity) getActivity()).showOnlineState());
    }
}
