package com.afterlogic.aurora.drive.presentation.modules.filelist.view;

import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive._unrefactored.presentation.ui.common.dialogs.FileActionsBottomSheet;
import com.afterlogic.aurora.drive.databinding.FragmentFilesListBindBinding;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.assembly.wireframes.ModulesFactoryComponent;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnBackPressedListener;
import com.afterlogic.aurora.drive.presentation.common.modules.view.BaseFragment;
import com.afterlogic.aurora.drive.presentation.common.modules.view.ViewPresenter;
import com.afterlogic.aurora.drive.presentation.modules.filelist.presenter.FileListPresenter;
import com.afterlogic.aurora.drive.presentation.modules.filelist.viewModel.FileListViewModel;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.view.MainFilesCallback;

import java.text.NumberFormat;

import javax.inject.Inject;

import static android.R.string.cancel;
import static android.content.DialogInterface.BUTTON_NEGATIVE;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileListFragment extends BaseFragment implements FileListView, OnBackPressedListener, FileActionsBottomSheet.FileActionListener {

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

    private ProgressDialog mProgressDialog;

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
    public void onStop() {
        super.onStop();
        hideProgress();
    }

    @Override
    public boolean onBackPressed() {
        return mPresenter.onBackPressed();
    }

    @Override
    public void showDownloadProgress(String fileName, @FloatRange(from = -1, to = 100) float progress) {
        if (mProgressDialog != null) {
            if (progress != -1) {
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMessage(fileName);
                mProgressDialog.setMax(100);
                mProgressDialog.setProgress((int) progress);
                mProgressDialog.setProgressPercentFormat(NumberFormat.getPercentInstance());
                mProgressDialog.setProgressNumberFormat("%1d/%2d");
            } else {
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setProgressPercentFormat(null);
                mProgressDialog.setProgressNumberFormat(null);
            }
        } else {
            mProgressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dialog);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setTitle(R.string.dialog_downloading);
            mProgressDialog.setMessage(fileName);
            mProgressDialog.setButton(
                    BUTTON_NEGATIVE,
                    getString(cancel),
                    (dialogInterface, i) -> mPresenter.onCancelCurrentTask()
            );
            mProgressDialog.show();
        }
    }

    @Override
    public void hideProgress() {
        if (mProgressDialog != null){
            mProgressDialog.hide();
            mProgressDialog = null;
        }
    }

    @Override
    public void showFileActions(AuroraFile file) {
        FileActionsBottomSheet actions = FileActionsBottomSheet.newInstance(file);
        actions.setTargetFragment(this, 0);
        actions.show(getFragmentManager(), "file_actions");
    }

    @Override
    public void onFileAction(int action, AuroraFile file) {
        switch (action){
            case R.id.action_download:
                mPresenter.onDownload(file);
                break;
        }
    }
}
