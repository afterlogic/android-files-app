package com.afterlogic.aurora.drive.presentation.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.Menu;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.data.common.api.Api;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.ui.fragments.FilesRootFragment;
import com.afterlogic.aurora.drive.core.util.DownloadType;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.core.util.AccountUtil;

import java.io.File;
import java.util.List;

/**
 * Created by sashka on 28.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class ChooseFileActivity extends FilesListActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean success = true;
        AuroraSession session = Api.getCurrentSession();

        if (session == null){
            AccountManager am = (AccountManager) getSystemService(ACCOUNT_SERVICE);
            Account[] accounts = am.getAccountsByType(AccountUtil.ACCOUNT_TYPE);
            if (accounts.length > 0){
                Account account = accounts[0];
                session = AccountUtil.fromAccount(account, am);
            }
            if (session != null){
                if (session.isComplete()) {
                    Api.setCurrentSession(session);
                }else{
                    success = false;
                }
            }else{
                success = false;
            }
        }else if (!session.isComplete()){
            success = false;
        }

        if (!success){
            onLoginFailed();
        }
    }

    @Override
    public FilesRootFragment getOnlineContentFragment() {
        return FilesRootFragment.newInstance(false);
    }

    @Override
    public void initActionBar(ActionBar ab) {
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void showActions(AuroraFile file) {
        //Stub
    }

    @Override
    public void onFileClicked(AuroraFile file, List<AuroraFile> all) {
        if (!file.isLink()){
            downloadFile(file, DownloadType.DOWNLOAD_RESULT);
        }else{
            new AlertDialog.Builder(this, R.style.Dialog)
                    .setMessage(getString(R.string.prompt_dialog_dowload_link_file))
                    .setPositiveButton(R.string.dialog_ok, null)
                    .show();
        }
    }

    @Override
    protected void onFileDownloaded(AuroraFile file, File result, DownloadType downloadType) {
        if (!isActive()) return;

        if (downloadType == DownloadType.DOWNLOAD_RESULT) {
            Intent i = new Intent();
            i.setData(Uri.fromFile(result));
            setResult(RESULT_OK, i);
            finish();
        }
    }

    @Override
    protected void updateHomeAsUpIndicator(boolean isRoot, ActionBar ab) {
        if (isRoot){
            ab.setHomeAsUpIndicator(R.drawable.ic_close);
        }else{
            ab.setHomeAsUpIndicator(null);
        }
    }
}
