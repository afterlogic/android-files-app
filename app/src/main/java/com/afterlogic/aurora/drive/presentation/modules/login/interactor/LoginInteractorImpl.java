package com.afterlogic.aurora.drive.presentation.modules.login.interactor;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;

import com.afterlogic.aurora.drive._unrefactored.core.util.AccountUtil;
import com.afterlogic.aurora.drive._unrefactored.presentation.receivers.AccountLoginStateReceiver;
import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler;
import com.afterlogic.aurora.drive.core.common.rx.Observables;
import com.afterlogic.aurora.drive.core.consts.Const;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.data.modules.apiChecker.checker.ApiChecker;
import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.model.error.AccountManagerError;
import com.afterlogic.aurora.drive.model.error.UnknownApiVersionError;
import com.afterlogic.aurora.drive.presentation.common.modules.interactor.BaseInteractor;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import okhttp3.HttpUrl;

/**
 * Created by sashka on 02.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class LoginInteractorImpl extends BaseInteractor implements LoginInteractor {

    private final SessionManager mSessionManager;
    private final ApiChecker mApiChecker;
    private final Provider<AuthRepository> mAuthRepository;
    private final Context mContext;

    @Inject LoginInteractorImpl(ObservableScheduler scheduler,
                                SessionManager sessionManager,
                                ApiChecker apiChecker,
                                Provider<AuthRepository> authRepository,
                                Context context) {
        super(scheduler);
        mSessionManager = sessionManager;
        mApiChecker = apiChecker;
        mAuthRepository = authRepository;
        mContext = context;
    }

    @Override
    public Maybe<AuroraSession> getCurrentSession() {
        return Maybe.defer(() -> {
            AuroraSession session = mSessionManager.getSession();
            if (session == null){
                return Maybe.empty();
            } else {
                return Maybe.just(session);
            }
        });
    }

    @Override
    public Completable login(AuroraSession session, boolean manual) {
        return Single.defer(() -> {
            //Get api version

            HttpUrl domain = session.getDomain();

            List<HttpUrl> domains = new ArrayList<>();

            if (manual){
                domains.add(domain);
            } else {
                domains.add(domain.newBuilder().scheme("http").build());
                domains.add(domain.newBuilder().scheme("https").build());
            }

            return Stream.of(domains)
                    .map(mApiChecker::getApiVersion)
                    .map(Single::toObservable)
                    .collect(Observables.ObservableCollectors.concatObservables())
                    .filter(version -> version != Const.ApiVersion.API_NONE)
                    .firstElement()
                    .switchIfEmpty(Maybe.error(new UnknownApiVersionError()))
                    .toSingle();
        })//----|
                //Get auth repository by api version
                .map(apiVersion -> {
                    session.setApiType(apiVersion);
                    mSessionManager.setSession(session);
                    return mAuthRepository.get();
                })
                //Auth
                .flatMapCompletable(authRepository ->
                        authRepository.getSystemAppData()
                                .flatMapCompletable(appData -> {
                                    mSessionManager.getSession().setAppToken(appData.getToken());
                                    return Completable.complete();
                                })
                                .andThen(authRepository.login(session.getLogin(), session.getPassword()))
                                .toCompletable()
                )
                .andThen(storeAuthData())
                .doOnError(error -> mSessionManager.setSession(null))
                .compose(this::composeDefault);
    }

    private Completable storeAuthData(){
        return Completable.fromAction(() -> {

            AccountManager am = (AccountManager) mContext.getSystemService(Context.ACCOUNT_SERVICE);
            AuroraSession session = mSessionManager.getSession();

            Account account = null;
            for (Account a:am.getAccountsByType(AccountUtil.ACCOUNT_TYPE)){
                if (a.name.equals(session.getLogin())){
                    account = a;
                    break;
                }
            }

            boolean isNew = account == null;
            if (isNew) {
                account = new Account(session.getLogin(), AccountUtil.ACCOUNT_TYPE);
                if (!am.addAccountExplicitly(account, session.getPassword(), null)) {
                    throw new AccountManagerError();
                }
            }

            AccountUtil.updateAccountCredentials(account, session, am);

            Intent loggedBroadcast = new Intent(AccountLoginStateReceiver.ACTION_AURORA_LOGIN);
            loggedBroadcast.putExtra(AccountLoginStateReceiver.ACCOUNT, account);
            loggedBroadcast.putExtra(AccountLoginStateReceiver.AURORA_SESSION, session);
            loggedBroadcast.putExtra(AccountLoginStateReceiver.IS_NEW, isNew);
            mContext.sendBroadcast(loggedBroadcast);
        });
    }

}
