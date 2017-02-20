package com.afterlogic.aurora.drive.data.modules.cleaner;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Build;

import com.afterlogic.aurora.drive.core.common.util.AccountUtil;
import com.afterlogic.aurora.drive.data.common.db.DataBaseProvider;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.data.modules.files.service.FilesLocalService;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.presentation.modulesBackground.session.SessionTrackUtil;

import javax.inject.Inject;

import io.reactivex.Completable;

/**
 * Created by sashka on 21.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class DataCleanerImpl implements DataCleaner {

    private final FilesLocalService mFilesLocalService;
    private final Context mContext;
    private final SessionManager mSessionManager;
    private final DataBaseProvider mDataBaseProvider;

    @Inject DataCleanerImpl(FilesLocalService filesLocalService, Context context, SessionManager sessionManager, DataBaseProvider dataBaseProvider) {
        mFilesLocalService = filesLocalService;
        mContext = context;
        mSessionManager = sessionManager;
        mDataBaseProvider = dataBaseProvider;
    }

    @Override
    public Completable cleanAllUserData(){
        return cleanUserAccountAndSession()
                .andThen(mFilesLocalService.clear())
                .andThen(Completable.fromAction(mDataBaseProvider::reset));
    }

    @Override
    public Completable cleanUserAccountAndSession(){
        return Completable.fromAction(() -> {
            AuroraSession current = mSessionManager.getSession();
            mSessionManager.setSession(null);

            AccountManager am = (AccountManager) mContext.getSystemService(Context.ACCOUNT_SERVICE);
            Account account = AccountUtil.getAccount(current.getLogin(), am);
            if (account != null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
                    //noinspection deprecation
                    am.removeAccount(account, null, null);
                } else {
                    am.removeAccountExplicitly(account);
                }
            }

            SessionTrackUtil.fireSessionChanged(null, mContext);
        });
    }
}
