package com.afterlogic.aurora.drive.presentation.modules.upload.view;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.common.util.DialogUtil;
import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.presentation.assembly.modules.ModulesFactoryComponent;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.view.BaseFilesListFragment;
import com.afterlogic.aurora.drive.presentation.modules.upload.presenter.UploadFilesPresenter;
import com.afterlogic.aurora.drive.presentation.modules.upload.viewModel.UploadFilesViewModel;

import java.util.List;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class UploadFilesFragment extends BaseFilesListFragment<UploadFilesViewModel, UploadFilesPresenter> implements UploadFilesView{

    @Override
    protected void assembly(ModulesFactoryComponent modulesFactory) {
        modulesFactory.uploadFileList().inject(this);
    }

    void upload(List<Uri> sources){
        mPresenter.onUpload(sources);
    }

    void createFolder(){
        mPresenter.onCreateFolder();
    }

    @Override
    public void showNewFolderNameDialog(Consumer<String> nameConsumer) {
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
                    nameConsumer.consume(newName);
                });
    }
}
