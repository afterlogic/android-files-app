package com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.interactor;

import android.util.Pair;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.presentation.common.modules.model.interactor.BaseInteractor;
import com.afterlogic.aurora.drive.model.FileType;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class BaseFilesInteractor extends BaseInteractor implements FilesInteractor {

    private final FilesRepository mFilesRepository;
    private final AppResources mAppResources;
    private final SessionManager mSessionManager;

    @Inject
    public BaseFilesInteractor(ObservableScheduler scheduler,
                               FilesRepository filesRepository,
                               AppResources appResources,
                               SessionManager sessionManager) {
        super(scheduler);
        mFilesRepository = filesRepository;
        mAppResources = appResources;
        mSessionManager = sessionManager;
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
    public Single<Boolean> getAuthStatus() {
        return Single.fromCallable(() -> mSessionManager.getSession() != null && mSessionManager.getSession().isComplete());
    }
}
