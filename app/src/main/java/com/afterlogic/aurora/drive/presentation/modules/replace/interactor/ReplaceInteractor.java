package com.afterlogic.aurora.drive.presentation.modules.replace.interactor;

import android.util.Pair;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.FileType;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceInteractor {

    private final FilesRepository filesRepository;
    private final AppResources appResources;

    @Inject
    public ReplaceInteractor(FilesRepository filesRepository, AppResources appResources) {
        this.filesRepository = filesRepository;
        this.appResources = appResources;
    }

    public Single<List<FileType>> getAvailableFileTypes() {
        return Single.defer(() -> {

            String[] types = appResources.getStringArray(R.array.folder_types);
            String[] captions = appResources.getStringArray(R.array.folder_captions);

            Map<String, String> captionsMap = Stream.zip(
                    Stream.of(types),
                    Stream.of(captions),
                    Pair::new
            ).collect(Collectors.toMap(
                    p -> p.first,
                    p -> p.second
            ));

            return filesRepository.getAvailableFileTypes()
                    .map(availableTypes -> Stream.of(availableTypes)
                            .map(type -> new FileType(type, captionsMap.get(type)))
                            .collect(Collectors.toList())
                    );
        });
    }

}
