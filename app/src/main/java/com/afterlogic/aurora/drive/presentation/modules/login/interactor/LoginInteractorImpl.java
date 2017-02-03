package com.afterlogic.aurora.drive.presentation.modules.login.interactor;

import com.afterlogic.aurora.drive._unrefactored.data.common.SessionManager;
import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;
import com.afterlogic.aurora.drive.data.modules.apiChecker.checker.ApiChecker;
import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler;
import com.afterlogic.aurora.drive.core.common.rx.Observables;
import com.afterlogic.aurora.drive.core.consts.Const;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.model.error.UnknownApiVersionError;
import com.afterlogic.aurora.drive.presentation.common.modules.interactor.BaseInteractor;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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
    private final AuthRepository mAuthRepository;

    @Inject LoginInteractorImpl(ObservableScheduler scheduler,
                                SessionManager sessionManager,
                                ApiChecker apiChecker,
                                AuthRepository authRepository) {
        super(scheduler);
        mSessionManager = sessionManager;
        mApiChecker = apiChecker;
        mAuthRepository = authRepository;
    }

    @Override
    public Maybe<AuroraSession> getCurrentSession() {
        return Maybe.defer(() -> {
            AuroraSession session = mSessionManager.getAuroraSession();
            if (session == null){
                return Maybe.empty();
            } else {
                return Maybe.just(session);
            }
        });
    }

    @Override
    public Completable login(AuroraSession session, boolean manual) {
        Single.defer(() -> {
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
                .flatMap(apiVersion -> {
                    session.setApiType(apiVersion);
                    mSessionManager.setAuroraSession(session);
                    mAuthRepository.
                })

        return mApiCheckRepository.checkDomain(session.getDomain())
                .flatMap()
    }

}
