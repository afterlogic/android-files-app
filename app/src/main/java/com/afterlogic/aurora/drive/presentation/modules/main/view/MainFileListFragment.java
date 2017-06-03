package com.afterlogic.aurora.drive.presentation.modules.main.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsComponent;
import com.afterlogic.aurora.drive.presentation.common.util.DialogUtil;
import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.core.common.util.FileUtil;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.FilesSelection;
import com.afterlogic.aurora.drive.presentation.common.binding.SimpleListener;
import com.afterlogic.aurora.drive.presentation.common.components.view.SelectionEditText;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnBackPressedListener;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.view.BaseFilesListFragment;
import com.afterlogic.aurora.drive.presentation.modules.main.model.presenter.MainFileListPresenter;
import com.afterlogic.aurora.drive.presentation.modules.main.viewModel.MainFileItemViewModel;
import com.afterlogic.aurora.drive.presentation.modules.main.viewModel.MainFileListViewModel;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFileListFragment extends BaseFilesListFragment<MainFileListViewModel, MainFileListPresenter> implements MainFileListView, OnBackPressedListener, FileActionsBottomSheet.FileActionCallback {

    private static final String FILE_ACTIONS = MainFileListFragment.class.getName()  + ".file_actions";

    private FileActionsBottomSheet mFileActions;

    @Nullable
    private FileListFragmentCallback mCallback;

    private SimpleListener mMultiChoiseListener = new SimpleListener(this::updateSelection);
    private SimpleListener mFileForActionsListener = new SimpleListener(this::updateFileActions);
    private SimpleListener mActionsEnabledListener = new SimpleListener(this::updateActionsEnabled);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FileListFragmentCallback){
            mCallback = (FileListFragmentCallback) context;
        }
    }

    @Override
    protected void assembly(InjectorsComponent modulesFactory) {
        modulesFactory.mainFileList().inject(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFileActions = (FileActionsBottomSheet) getFragmentManager().findFragmentByTag(FILE_ACTIONS);

        mViewModel.getSelection().addOnPropertyChangedCallback(mMultiChoiseListener);
        mViewModel.getFileRequeireActions().addOnPropertyChangedCallback(mFileForActionsListener);
        mViewModel.getActionsEnabled().addOnPropertyChangedCallback(mActionsEnabledListener);

        updateFileActions();
        updateActionsEnabled();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_files_list_main, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return onFileActionWithResult(item.getItemId()) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgress();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.getSelection().removeOnPropertyChangedCallback(mMultiChoiseListener);
        mViewModel.getFileRequeireActions().removeOnPropertyChangedCallback(mFileForActionsListener);
        mViewModel.getActionsEnabled().removeOnPropertyChangedCallback(mActionsEnabledListener);
    }

    @Override
    public boolean onBackPressed() {
        return mPresenter.onBackPressed();
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
        @SuppressLint("InflateParams")
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
    public void onFileAction(int action) {
        onFileActionWithResult(action);
    }

    @Override
    public MainFileItemViewModel getFileActionTarget() {
        return mViewModel.getFileRequeireActions().get();
    }

    @Override
    public void onCancelFileActions() {
        mViewModel.onCancelFileActions();
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

    private boolean onFileActionWithResult(int action){
        switch (action){
            case R.id.action_download: mPresenter.onDownload(); return true;
            case R.id.action_delete: mPresenter.onDelete(); return true;
            case R.id.action_send: mPresenter.onSendTo(); return true;
            case R.id.action_rename: mPresenter.onRename(); return true;
            case R.id.action_offline: mPresenter.onToggleOffline(); return true;
            case R.id.action_public_link: mPresenter.onTogglePublicLink(); return true;

            default: return false;
        }
    }

    private void updateSelection(){
        if (mCallback != null) {
            FilesSelection selection = mViewModel.getSelection().get();
            mCallback.onSelectedFilesChanged(
                    selection.getCount(),
                    selection.hasFolder()
            );
        }
    }

    private void updateFileActions(){
        hideFileActions();

        if (mViewModel.getFileRequeireActions().get() != null) {
            mFileActions = FileActionsBottomSheet.newInstance();
            mFileActions.setTargetFragment(this, 0);
            mFileActions.show(getFragmentManager(), FILE_ACTIONS);
        }
    }

    private void updateActionsEnabled() {
        if (mCallback != null) {
            mCallback.onActionsEnabledChanged(mViewModel.getActionsEnabled().get());
        }
    }

    private void hideFileActions(){
        if (mFileActions != null){
            mFileActions.dismissAllowingStateLoss();
            mFileActions = null;
        }
    }
}
