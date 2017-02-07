package com.afterlogic.aurora.drive.presentation.modules.filelist.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.databinding.FragmentFilesListBindBinding;
import com.afterlogic.aurora.drive.presentation.assembly.wireframes.ModulesFactoryComponent;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnBackPressedListener;
import com.afterlogic.aurora.drive.presentation.common.modules.view.BaseFragment;
import com.afterlogic.aurora.drive.presentation.common.modules.view.ViewPresenter;
import com.afterlogic.aurora.drive.presentation.modules.filelist.presenter.FileListPresenter;
import com.afterlogic.aurora.drive.presentation.modules.filelist.viewModel.FileListViewModel;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.view.MainFilesCallback;

import javax.inject.Inject;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileListFragment extends BaseFragment implements FileListView, OnBackPressedListener {

    private static final String ARGS_TYPE = FileListFragment.class.getName() + ".TYPE";

    public static FileListFragment newInstance(String type) {

        Bundle args = new Bundle();
        args.putString(ARGS_TYPE, type);
        FileListFragment fragment = new FileListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Inject @ViewPresenter
    protected FileListPresenter mPresenter;

    @Inject
    protected FileListViewModel mViewModel;

    @Override
    protected void assembly(ModulesFactoryComponent modulesFactory) {
        modulesFactory.fileList().inject(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.initWith(getArguments().getString(ARGS_TYPE), (MainFilesCallback) getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_files_list_bind, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentFilesListBindBinding binding = DataBindingUtil.bind(view);
        binding.setViewModel(mViewModel);
    }

    @Override
    public boolean onBackPressed() {
        return mPresenter.onBackPressed();
    }
}
