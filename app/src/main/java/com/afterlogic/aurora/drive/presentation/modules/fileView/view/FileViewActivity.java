package com.afterlogic.aurora.drive.presentation.modules.fileView.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.streams.ExtCollectors;
import com.afterlogic.aurora.drive.databinding.ActivityViewFileBinding;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.assembly.modules.ModulesFactoryComponent;
import com.afterlogic.aurora.drive.presentation.common.binding.SimpleListener;
import com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters.ViewProvider;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.ItemsAdapter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.BaseActivity;
import com.afterlogic.aurora.drive.presentation.common.modules.view.ViewPresenter;
import com.afterlogic.aurora.drive.presentation.modules.fileView.presenter.FileViewPresenter;
import com.afterlogic.aurora.drive.presentation.modules.fileView.viewModel.FileViewImageItemViewModel;
import com.afterlogic.aurora.drive.presentation.modules.fileView.viewModel.FileViewViewModel;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import javax.inject.Inject;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileViewActivity extends BaseActivity implements FileViewPresentationView{

    @Inject @ViewPresenter
    protected FileViewPresenter mPresenter;

    @Inject
    protected FileViewViewModel mViewModel;

    private final SimpleListener mTitleListener = new SimpleListener(this::updateTitle);
    private final SimpleListener mFullscreenListener = new SimpleListener(this::updateFullscreenMode);

    public static Intent intent(@Nullable AuroraFile file, @NonNull List<AuroraFile> files, Context context){
        Intent intent = new Intent(context, FileViewActivity.class);
        intent.putExtra("file", file);

        ArrayList<AuroraFile> prewieable = Stream.of(files)
                .filter(AuroraFile::isPreviewAble)
                .collect(ExtCollectors.toArrayList());
        intent.putParcelableArrayListExtra("files", prewieable);

        return intent;
    }

    @Override
    protected void assembly(ModulesFactoryComponent modulesFactory) {
        modulesFactory.fileView().inject(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null){
            mViewModel.viewCreatedWith(
                    getIntent().getParcelableExtra("file"),
                    getIntent().getParcelableArrayListExtra("files")
            );
        }

        ActivityViewFileBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_view_file);
        binding.setViewModel(mViewModel);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewModel.getFullscreenMode().addOnPropertyChangedCallback(mFullscreenListener);
        mViewModel.getTitle().addOnPropertyChangedCallback(mTitleListener);

        updateTitle();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.getFullscreenMode().removeOnPropertyChangedCallback(mFullscreenListener);
        mViewModel.getTitle().removeOnPropertyChangedCallback(mTitleListener);
    }

    private void updateTitle(){
        setTitle(mViewModel.getTitle().get());
    }

    private void updateFullscreenMode(){
        //no-op
    }


    public static class Binder{

        private static final WeakHashMap<ViewPager, ItemsAdapter<FileViewImageItemViewModel>> PAGER_ADAPTERS = new WeakHashMap<>();

        public static ViewProvider<ItemsAdapter<FileViewImageItemViewModel>, ViewPager> itemsAdapter(){
            return pager -> {
                ItemsAdapter<FileViewImageItemViewModel> adapter = PAGER_ADAPTERS.get(pager);
                if (adapter == null){
                    FragmentManager fm = ((BaseActivity) pager.getContext()).getSupportFragmentManager();
                    adapter = new FilesItemsAdapter(fm);
                    PAGER_ADAPTERS.put(pager, adapter);
                }
                return adapter;
            };
        }
    }
}
