package com.afterlogic.aurora.drive.data.modules.auth.repository;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Build;

import com.afterlogic.aurora.drive._unrefactored.core.util.AccountUtil;
import com.afterlogic.aurora.drive.presentation.modulesBackground.session.SessionTrackUtil;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStore;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.data.common.repository.Repository;
import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.model.error.AccountManagerError;

import io.reactivex.Completable;
import io.reactivex.Maybe;

import static android.content.Context.ACCOUNT_SERVICE;

/**
 * Created by sashka on 09.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

@SuppressWarnings("WeakerAccess")
abstract class BaseAuthRepository extends Repository implements AuthRepository {

    private final Context mContext;
    private final SessionManager mSessionManager;

    public BaseAuthRepository(SharedObservableStore cache,
                              String repositoryId,
                              Context context,
                              SessionManager sessionManager) {
        super(cache, repositoryId);
        mContext = context;
        mSessionManager = sessionManager;
    }

    @Override
    public Completable logoutAndClearData() {
        return Completable.fromAction(() -> {
            //[START Clear account and session data]
            AuroraSession current = mSessionManager.getSession();
            mSessionManager.setSession(null);

            AccountManager am = (AccountManager) mContext.getSystemService(ACCOUNT_SERVICE);
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
            //[END Clear account and session data]
        });
    }

    @Override
    public Maybe<AuroraSession> getCurrentSession() {
        return Maybe.defer(() -> {
            AuroraSession session = mSessionManager.getSession();
            if (session != null){
                return Maybe.just(session);
            } else {
                return Maybe.empty();
            }
        });
    }

    protected Completable storeAuthData(){
        return Completable.fromAction(() -> {
            AccountManager am = (AccountManager) mContext.getSystemService(ACCOUNT_SERVICE);
            AuroraSession session = mSessionManager.getSession();

            Account account = AccountUtil.getCurrentAccount(mContext);
            if (account == null) {
                account = new Account(session.getLogin(), AccountUtil.ACCOUNT_TYPE);
                if (!am.addAccountExplicitly(account, session.getPassword(), null)) {
                    throw new AccountManagerError();
                }
            }

            AccountUtil.updateAccountCredentials(account, session, am);
            SessionTrackUtil.fireSessionChanged(session, mContext);
        });
    }
}
