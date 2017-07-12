package com.afterlogic.aurora.drive.presentation.modules.fileView.view;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.core.common.util.FileUtil;
import com.afterlogic.aurora.drive.databinding.ActivityViewFileBinding;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsComponent;
import com.afterlogic.aurora.drive.presentation.common.binding.SimpleListener;
import com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters.ViewProvider;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.ItemsAdapter;
import com.afterlogic.aurora.drive.presentation.common.components.view.SelectionEditText;
import com.afterlogic.aurora.drive.presentation.common.modules.view.MVPActivity;
import com.afterlogic.aurora.drive.presentation.common.modules.view.ViewPresenter;
import com.afterlogic.aurora.drive.presentation.common.util.DialogUtil;
import com.afterlogic.aurora.drive.presentation.modules.fileView.presenter.FileViewPresenter;
import com.afterlogic.aurora.drive.presentation.modules.fileView.viewModel.FileViewImageItemViewModel;
import com.afterlogic.aurora.drive.presentation.modules.fileView.viewModel.FileViewViewModel;

import org.parceler.Parcels;

import java.text.NumberFormat;
import java.util.List;
import java.util.WeakHashMap;

import javax.inject.Inject;

import static android.R.string.cancel;
import static android.content.DialogInterface.BUTTON_NEGATIVE;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileViewActivity extends MVPActivity implements FileViewPresentationView{

    @Inject @ViewPresenter
    protected FileViewPresenter mPresenter;

    @Inject
    protected FileViewViewModel mViewModel;

    private final SimpleListener mTitleListener = new SimpleListener(this::updateTitle);
    private final SimpleListener mFullscreenListener = new SimpleListener(this::updateFullscreenMode);
    private final SimpleListener mOfflineIndicator = new SimpleListener(this::updateOfflineIndicator);

    private ProgressDialog mProgressDialog;
    private MenuItem mOfflineMenuItem;

    public static Intent intent(@Nullable AuroraFile file, @NonNull List<AuroraFile> files, Context context){
        return intent(new FileViewArgs(file, files), context);
    }

    public static Intent intent(FileViewArgs args, Context context) {
        return new Intent(context, FileViewActivity.class)
                .putExtra("args", Parcels.wrap(args));
    }

    @Override
    protected void assembly(InjectorsComponent modulesFactory) {
        modulesFactory.fileView().inject(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null){

            FileViewArgs args = Parcels.unwrap(getIntent().getParcelableExtra("args"));

            mViewModel.viewCreatedWith(
                    args.getFile(),
                    args.getFolderContent()
            );
        }

        ActivityViewFileBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_view_file);
        binding.setViewModel(mViewModel);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewModel.getFullscreenMode().addOnPropertyChangedCallback(mFullscreenListener);
        mViewModel.getTitle().addOnPropertyChangedCallback(mTitleListener);
        mViewModel.getCurrentOffline().addOnPropertyChangedCallback(mOfflineIndicator);

        updateTitle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_file_view, menu);
        mOfflineMenuItem = menu.findItem(R.id.action_offline);
        updateOfflineIndicator();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete: mViewModel.onDelete(); return true;
            case R.id.action_rename: mViewModel.onRename(); return true;
            case R.id.action_download: mViewModel.onDownload(); return true;
            case R.id.action_offline: mViewModel.onOffline(); return true;
            case R.id.action_send: mViewModel.onSend(); return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.getFullscreenMode().removeOnPropertyChangedCallback(mFullscreenListener);
        mViewModel.getTitle().removeOnPropertyChangedCallback(mTitleListener);
        mViewModel.getCurrentOffline().removeOnPropertyChangedCallback(mOfflineIndicator);
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
            mProgressDialog = new ProgressDialog(this, R.style.AppTheme_Dialog_CompatBackground);
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
        mProgressDialog = new ProgressDialog(this, R.style.AppTheme_Dialog_CompatBackground);
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
    public void showRenameDialog(AuroraFile file, Consumer<String> newNameConsumer) {
        //[START Prepare input view (disallow change file extension)]
        @SuppressLint("InflateParams")
        View inputView = LayoutInflater.from(this)
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
                this,
                (dialogInterface, input) -> {

                    String newName = input.getText().toString();
                    if (TextUtils.isEmpty(newName)){
                        input.setError(getString(R.string.error_field_required));
                        input.requestFocus();
                        return;
                    }

                    input.clearFocus();

                    InputMethodManager keyboard = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.hideSoftInputFromWindow(input.getWindowToken(), 0);

                    dialogInterface.dismiss();

                    final String trimmed = newName.trim();

                    //Check new name if it is same as old closeQuietly dialog without any action
                    if (!newName.equals(file.getName()) && !trimmed.equals(file.getName())){
                        newNameConsumer.consume(trimmed);
                    }
                });
    }



    private void updateTitle(){
        setTitle(mViewModel.getTitle().get());
    }

    private void updateFullscreenMode(){
        //no-op
    }

    private void updateOfflineIndicator(){
        if (mOfflineMenuItem == null) return;

        mOfflineMenuItem.setChecked(mViewModel.getCurrentOffline().get());
    }

    public static class Binder{

        private static final WeakHashMap<ViewPager, ItemsAdapter<FileViewImageItemViewModel>> PAGER_ADAPTERS = new WeakHashMap<>();

        public static ViewProvider<ItemsAdapter<FileViewImageItemViewModel>, ViewPager> itemsAdapter(){
            return pager -> {
                ItemsAdapter<FileViewImageItemViewModel> adapter = PAGER_ADAPTERS.get(pager);
                if (adapter == null){
                    FragmentManager fm = ((MVPActivity) pager.getContext()).getSupportFragmentManager();
                    adapter = new FilesItemsAdapter(fm);
                    PAGER_ADAPTERS.put(pager, adapter);
                }
                return adapter;
            };
        }
    }
}
