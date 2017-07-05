package com.afterlogic.aurora.drive.presentation.modules.replace.interactor;

import android.util.Pair;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.FileType;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.Map;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */
class TypesMapper implements Mapper<FileType, String> {

    private final Map<String, String> captionsMap;

    TypesMapper(AppResources appResources) {
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
    public FileType map(String source) {
        return new FileType(source, captionsMap.get(source));
    }
}
