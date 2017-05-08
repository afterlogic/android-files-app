package com.afterlogic.aurora.drive.presentation.modules._baseFiles.view;

import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afterlogic.aurora.drive.BR;
import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.binding.SimpleListener;
import com.afterlogic.aurora.drive.presentation.common.modules.view.MVPFragment;
import com.afterlogic.aurora.drive.presentation.common.modules.view.ViewPresenter;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.presenter.FilesListPresenter;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFilesListViewModel;

import java.text.NumberFormat;

import javax.inject.Inject;

import static android.R.string.cancel;
import static android.content.DialogInterface.BUTTON_NEGATIVE;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public abstract class BaseFilesListFragment<VM extends BaseFilesListViewModel, P extends FilesListPresenter> extends MVPFragment implements FilesListView{

    public static final String ARGS_TYPE = "ARGS_TYPE";

    @Inject @ViewPresenter
    protected P mPresenter;

    @Inject
    protected VM mViewModel;

    private ProgressDialog mProgressDialog;

    private SimpleListener mCurrentFolderListener = new SimpleListener(this::updateActvityCurrentFolder);

    public void setArgsType(String type){
        Bundle args = getArguments();
        if (args == null){
            args = new Bundle();
        }
        args.putString(ARGS_TYPE, type);
        setArguments(args);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.initWith(getArguments().getString(ARGS_TYPE));
        mViewModel.getCurrentFolder().addOnPropertyChangedCallback(mCurrentFolderListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_files_list_base, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewDataBinding binding = onCreateBind(view);
        binding.setVariable(BR.viewModel, mViewModel);
    }

    protected ViewDataBinding onCreateBind(View view){
        return DataBindingUtil.bind(view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.getCurrentFolder().removeOnPropertyChangedCallback(mCurrentFolderListener);
    }

    @Override
    public boolean onBackPressed() {
        return mPresenter.onBackPressed();
    }

    @Override
    public void showLoadProgress(String fileName, String title, @FloatRange(from = -1, to = 100) float progress) {
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
            mProgressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dialog_CompatBackground);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setTitle(title);
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
    public void showProgress(String title, String message) {
        mProgressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dialog_CompatBackground);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    @Override
    public void hideProgress() {
        if (mProgressDialog != null){
            mProgressDialog.hide();
            mProgressDialog = null;
        }
    }

    private void updateActvityCurrentFolder(){
        if (getActivity() instanceof FilesListCallback){
            AuroraFile folder = (AuroraFile) mViewModel.getCurrentFolder().get();
            ((FilesListCallback) getActivity()).onOpenFolder(folder);
        }
    }
}
