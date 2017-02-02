package com.afterlogic.aurora.drive._unrefactored.data.modules.project8.modules.checker.repository;

import com.afterlogic.aurora.drive._unrefactored.core.annotations.qualifers.RepositoryCache;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStore;
import com.afterlogic.aurora.drive._unrefactored.data.common.repository.ApiCheckRepository;
import com.afterlogic.aurora.drive._unrefactored.data.common.repository.BaseRepository;
import com.afterlogic.aurora.drive._unrefactored.data.modules.project8.modules.checker.service.ApiCheckerServiceP8;

import javax.inject.Inject;

import io.reactivex.Single;
import okhttp3.HttpUrl;

/**
 * Created by sashka on 19.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class ApiCheckRepository8Impl extends BaseRepository implements ApiCheckRepository {

    private static final String CHECK_P_8 = "checkP8";
    private final ApiCheckerServiceP8 mApiCheckerService;

    @Inject public ApiCheckRepository8Impl(@RepositoryCache SharedObservableStore cache, ApiCheckerServiceP8 apiCheckerService) {
        super(cache, CHECK_P_8);
        mApiCheckerService = apiCheckerService;
    }

    @Override
    public Single<Boolean> checkDomain(HttpUrl domain) {
        return mApiCheckerService.ping(domain).map(result -> true);
    }
}
