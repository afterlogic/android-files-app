package com.afterlogic.aurora.drive.presentation.modules.fileView.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;

import com.afterlogic.aurora.drive.model.AuroraFile;

import java.util.List;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FileViewViewModel extends ViewPager.OnPageChangeListener{

    void viewCreatedWith(@Nullable AuroraFile target, List<AuroraFile> files);

    ObservableList<AuroraFile> getItems();

    ObservableField<String> getTitle();

    ObservableInt getCurrentPosition();

    ObservableBoolean getFullscreenMode();

    void onItemClick();
}
