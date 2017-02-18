package com.afterlogic.aurora.drive.presentation.modules.fileView.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.databinding.FragmentViewImageBinding;
import com.afterlogic.aurora.drive.presentation.assembly.modules.ModulesFactoryComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.view.BaseFragment;
import com.afterlogic.aurora.drive.presentation.modules.fileView.viewModel.FileViewImageItemViewModel;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileViewImageItemFragment extends BaseFragment implements FileViewImageItemView {

    private FileViewImageItemViewModel mViewModel;

    @Override
    protected void assembly(ModulesFactoryComponent modulesFactory) {

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
