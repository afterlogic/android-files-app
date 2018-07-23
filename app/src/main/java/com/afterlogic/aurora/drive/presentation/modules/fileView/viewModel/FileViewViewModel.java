package com.afterlogic.aurora.drive.presentation.modules.fileView.viewModel;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.databinding.ObservableList;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.afterlogic.aurora.drive.model.AuroraFile;

import java.util.List;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FileViewViewModel extends ViewPager.OnPageChangeListener{

    void viewCreatedWith(@Nullable AuroraFile target, List<AuroraFile> files);

    ObservableList<FileViewImageItemViewModel> getItems();

    ObservableField<String> getTitle();

    ObservableInt getCurrentPosition();

    ObservableBoolean getFullscreenMode();

    ObservableBoolean getCurrentOffline();

    void onItemClick();

    void onDelete();

    void onRename();

    void onOffline();

    void onDownload();

    void onSend();
}
