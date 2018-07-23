package com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.interactor;

import androidx.annotation.Nullable;
import android.util.Pair;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.Storage;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.Map;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */
public class StorageTypesMapper implements Mapper<Storage, String> {

    private final Map<String, String> captionsMap;

    @Inject
    StorageTypesMapper(AppResources appResources) {

        String[] types = appResources.getStringArray(R.array.folder_types);
        String[] captions = appResources.getStringArray(R.array.folder_captions);

        captionsMap = Stream.zip(
                Stream.of(types),
                Stream.of(captions),
                Pair::new
        ).collect(Collectors.toMap(
                p -> p.first,
                p -> p.second
        ));

    }

    @Override
    @Nullable
    public Storage map(String source) {

        String caption = captionsMap.get(source);

        if (caption == null) return null;

        return new Storage(source, caption);

    }

}
