package com.afterlogic.aurora.drive.data.modules.auth.repository;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.afterlogic.aurora.drive.core.common.util.AccountUtil;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStore;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.data.common.repository.Repository;
import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;
import com.afterlogic.aurora.drive.data.modules.cleaner.DataCleaner;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.model.error.AccountManagerError;
import com.afterlogic.aurora.drive.presentation.modulesBackground.session.SessionTrackUtil;

import io.reactivex.Completable;
import io.reactivex.Maybe;

import static android.content.Context.ACCOUNT_SERVICE;

/**
 * Created by sashka on 09.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

@SuppressWarnings("WeakerAccess")
abstract class BaseAuthRepository extends Repository implements AuthRepository {

    private final Context context;
    private final SessionManager sessionManager;
    private final DataCleaner dataCleaner;

    public BaseAuthRepository(SharedObservableStore cache,
                              String repositoryId,
                              Context context,
                              SessionManager sessionManager,
                              DataCleaner dataCleaner) {
        super(cache, repositoryId);
        this.context = context;
        this.sessionManager = sessionManager;
        this.dataCleaner = dataCleaner;
    }

    @Override
    public Completable logoutAndClearData() {
        return dataCleaner.cleanAllUserData();
    }

    @Override
    public Maybe<AuroraSession> getCurrentSession() {
        return Maybe.defer(() -> {
            AuroraSession session = sessionManager.getSession();
            if (session != null){
                return Maybe.just(session);
            } else {
                return Maybe.empty();
            }
        });
    }

    @Override
    public Completable setCurrentSession(AuroraSession session) {
        return storeAuthData()
                .startWith(Completable.fromAction(() -> sessionManager.setSession(session)));
    }

    protected Completable storeAuthData(){
        return Completable.fromAction(() -> {
            AuroraSession session = sessionManager.getSession();

            AccountManager am = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
            if (am == null) {
                throw new AccountManagerError();
            }

            Account account = AccountUtil.getCurrentAccount(context);
            if (account == null) {
                account = new Account(session.getLogin(), AccountUtil.ACCOUNT_TYPE);
                if (!am.addAccountExplicitly(account, session.getPassword(), null)) {
                    throw new AccountManagerError();
                }
            }

            AccountUtil.updateAccountCredentials(account, session, am);
            SessionTrackUtil.fireSessionChanged(session, context);
        });
    }
}
