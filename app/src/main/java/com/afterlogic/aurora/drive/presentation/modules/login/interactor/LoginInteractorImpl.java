package com.afterlogic.aurora.drive.presentation.modules.login.interactor;

import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler;
import com.afterlogic.aurora.drive.core.common.rx.Observables;
import com.afterlogic.aurora.drive.core.consts.Const;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.data.modules.apiChecker.checker.ApiChecker;
import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.model.error.UnknownApiVersionError;
import com.afterlogic.aurora.drive.presentation.common.modules.model.interactor.BaseInteractor;
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

    @Inject LoginInteractorImpl(ObservableScheduler scheduler,
                                SessionManager sessionManager,
                                ApiChecker apiChecker,
                                Provider<AuthRepository> authRepository) {
        super(scheduler);
        mSessionManager = sessionManager;
        mApiChecker = apiChecker;
        mAuthRepository = authRepository;
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
                    .collect(Observables.Collectors.concatObservables())
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
                )
                .doOnError(error -> mSessionManager.setSession(null))
                .compose(this::composeDefault);
    }

}
