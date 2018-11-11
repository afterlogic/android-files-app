package com.afterlogic.aurora.drive.presentation.modules.fileView.view;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.databinding.FragmentViewImageBinding;
import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.view.MVPFragment;
import com.afterlogic.aurora.drive.presentation.modules.fileView.viewModel.FileViewImageItemViewModel;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileViewImageItemFragment extends MVPFragment implements FileViewImageItemView {

    private FileViewImageItemViewModel mViewModel;

    @Override
    protected void assembly(InjectorsComponent modulesFactory) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mViewModel == null) return;
        mViewModel.onViewStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mViewModel == null) return;
        mViewModel.onViewStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_image, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentViewImageBinding binding = DataBindingUtil.bind(view);
        binding.setViewModel(mViewModel);
    }

    public void setViewModel(FileViewImageItemViewModel viewModel) {
        mViewModel = viewModel;
    }
}
