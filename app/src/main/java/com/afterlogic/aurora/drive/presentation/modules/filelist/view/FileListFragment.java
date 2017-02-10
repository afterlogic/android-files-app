package com.afterlogic.aurora.drive.presentation.modules.filelist.view;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive._unrefactored.core.util.DialogUtil;
import com.afterlogic.aurora.drive._unrefactored.presentation.ui.common.dialogs.FileActionsBottomSheet;
import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.databinding.FragmentFilesListBindBinding;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.assembly.wireframes.ModulesFactoryComponent;
import com.afterlogic.aurora.drive.presentation.common.components.view.SelectionEditText;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnBackPressedListener;
import com.afterlogic.aurora.drive.presentation.common.modules.view.BaseFragment;
import com.afterlogic.aurora.drive.presentation.common.modules.view.ViewPresenter;
import com.afterlogic.aurora.drive.presentation.common.util.FileUtil;
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

    @Nullable
    private FileListFragmentCallback mCallback;
    private Observable.OnPropertyChangedCallback mSelectedFilesCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            if (mCallback != null) {
                mCallback.onSelectedFilesChanged(
                        mViewModel.getSelectedCount().get(),
                        mViewModel.getSelectionHasFolder().get()
                );
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FileListFragmentCallback){
            mCallback = (FileListFragmentCallback) context;
        }
    }

    @Override
    protected void assembly(ModulesFactoryComponent modulesFactory) {
        modulesFactory.fileList().inject(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.initWith(getArguments().getString(ARGS_TYPE), (MainFilesCallback) getActivity());
        mViewModel.getSelectedCount().addOnPropertyChangedCallback(mSelectedFilesCallback);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete: mPresenter.onDelete(null); break;
            case R.id.action_download: mPresenter.onDownload(null); break;
            case R.id.action_send: mPresenter.onSendTo(null); break;
            case R.id.action_offline: mPresenter.onToggleOffline(null); break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgress();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.getSelectedCount().removeOnPropertyChangedCallback(mSelectedFilesCallback);
    }

    @Override
    public boolean onBackPressed() {
        return mPresenter.onBackPressed();
    }

    @Override
    public void showLoadProgress(String fileName, @FloatRange(from = -1, to = 100) float progress) {
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
    public void showProgress(String title, String message) {
        mProgressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dialog);
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

    @Override
    public void showFileActions(AuroraFile file) {
        FileActionsBottomSheet actions = FileActionsBottomSheet.newInstance(file);
        actions.setTargetFragment(this, 0);
        actions.show(getFragmentManager(), "file_actions");
    }

    @Override
    public void showRenameDialog(AuroraFile file, Consumer<String> newNameConsumer) {
        //[START Prepare input view (disallow change file extension)]
        @SuppressLint("InflateParams")
        View inputView = LayoutInflater.from(getContext())
                .inflate(R.layout.item_layout_dialog_input, null);
        final String ext = FileUtil.getFileExtension(file.getName());
        if (ext != null && !file.isFolder() && !file.isLink()) {
            //Set disallow only for 'normal' file
            final SelectionEditText input = (SelectionEditText) inputView.findViewById(R.id.input);
            input.setOnSelectionChangeListener((start, end) -> {
                int lenght = input.getText().length();
                int max = lenght - ext.length() - 1;
                boolean fixed = false;
                if (start > max){
                    start = max;
                    fixed = true;
                }
                if (end > max){
                    end = max;
                    fixed = true;
                }
                if (fixed){
                    input.setSelection(start, end);
                }
            });
        }
        //[END Prepare input view (disallow change file extension)]

        //Show dialog
        DialogUtil.showInputDialog(
                inputView,
                getString(R.string.prompt_input_new_file_name),
                file.getName(),
                getContext(),
                (dialogInterface, input) -> {

                    String newName = input.getText().toString();
                    if (TextUtils.isEmpty(newName)){
                        input.setError(getString(R.string.error_field_required));
                        input.requestFocus();
                        return;
                    }

                    input.clearFocus();

                    InputMethodManager keyboard = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.hideSoftInputFromWindow(input.getWindowToken(), 0);

                    dialogInterface.dismiss();

                    final String trimmed = newName.trim();

                    //Check new name if it is same as old closeQuietly dialog without any action
                    if (!newName.equals(file.getName()) && !trimmed.equals(file.getName())){
                        newNameConsumer.consume(trimmed);
                    }
                });
    }

    @Override
    public void showNewFolderNameDialog(Consumer<String> newNameConsumer) {
        View inputView = LayoutInflater.from(getContext())
                .inflate(R.layout.item_layout_dialog_input, null);

        //Show dialog
        DialogUtil.showInputDialog(
                inputView,
                getString(R.string.prompt_create_folder),
                "",
                getContext(),
                (dialogInterface, input) -> {

                    String newName = input.getText().toString().trim();
                    if (TextUtils.isEmpty(newName)){
                        input.setError(getString(R.string.error_field_required));
                        input.requestFocus();
                        return;
                    }

                    input.clearFocus();

                    InputMethodManager keyboard = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.hideSoftInputFromWindow(input.getWindowToken(), 0);

                    dialogInterface.dismiss();
                    newNameConsumer.consume(newName);
                });
    }

    @Override
    public void onFileAction(int action, AuroraFile file) {
        switch (action){
            case R.id.action_download: mPresenter.onDownload(file); break;
            case R.id.action_delete: mPresenter.onDelete(file); break;
            case R.id.action_send: mPresenter.onSendTo(file); break;
            case R.id.action_rename: mPresenter.onRename(file); break;

            case R.id.action_offline:
            case R.id.action_offline_on:
            case R.id.action_offline_off:
                mPresenter.onToggleOffline(file);
                break;
        }
    }

    public void createFolder(){
        mPresenter.onCreateFolder();
    }

    public void uploadFile(){
        mPresenter.onFileUpload();
    }

    public void setMultiChoiseMode(boolean mode){
        mPresenter.onMultiChoseMode(mode);
    }
}
