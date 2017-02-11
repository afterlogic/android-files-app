package com.afterlogic.aurora.drive.presentation.modules.filesMain.interactor;

import android.util.Pair;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;
import com.afterlogic.aurora.drive.data.modules.files.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.presentation.common.modules.interactor.BaseInteractor;
import com.afterlogic.aurora.drive.model.FileType;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by sashka on 06.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFilesInteractorImpl extends BaseInteractor implements MainFilesInteractor {

    private final FilesRepository mFilesRepository;
    private final AppResources mAppResources;
    private final AuthRepository mAuthRepository;

    @Inject MainFilesInteractorImpl(ObservableScheduler scheduler,
                                    FilesRepository filesRepository,
                                    AppResources appResources,
                                    AuthRepository authRepository) {
        super(scheduler);
        mFilesRepository = filesRepository;
        mAppResources = appResources;
        mAuthRepository = authRepository;
    }

    @Override
    public Single<String> getUserLogin() {
        return mAuthRepository.getCurrentSession()
                .map(AuroraSession::getLogin)
                .toSingle()
                .compose(this::composeDefault);
    }

    @Override
    public Single<List<FileType>> getAvailableFileTypes() {
        return Single.defer(() -> {

            String[] types = mAppResources.getStringArray(R.array.folder_types);
            String[] captions = mAppResources.getStringArray(R.array.folder_captions);

            Map<String, String> captionsMap = Stream.zip(
                    Stream.of(types),
                    Stream.of(captions),
                    Pair::new
            ).collect(Collectors.toMap(
                    p -> p.first,
                    p -> p.second
            ));

            return mFilesRepository.getAvailableFileTypes()
                    .map(availableTypes -> Stream.of(availableTypes)
                            .map(type -> new FileType(type, captionsMap.get(type)))
                            .collect(Collectors.toList())
                    )
                    .compose(this::composeDefault);
        });
    }

    @Override
    public Completable logout() {
        return mAuthRepository.logoutAndClearData()
                .compose(this::composeDefault);
    }
}
