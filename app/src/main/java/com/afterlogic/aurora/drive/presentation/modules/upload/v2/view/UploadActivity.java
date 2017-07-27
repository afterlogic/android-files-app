package com.afterlogic.aurora.drive.presentation.modules.upload.v2.view;

import android.arch.lifecycle.ViewModelProvider;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.util.ObjectsUtil;
import com.afterlogic.aurora.drive.databinding.UploadActivityBinding;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.UnbindableObservable;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.InjectableMVVMActivity;
import com.afterlogic.aurora.drive.presentation.modules.upload.v2.viewModel.UploadViewModel;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.JELLY_BEAN;

/**
 * Created by aleksandrcikin on 19.07.17.
 * mail: mail@sunnydaydev.me
 */

public class UploadActivity extends InjectableMVVMActivity<UploadViewModel> implements HasSupportFragmentInjector {

    @Inject
    protected DispatchingAndroidInjector<Fragment> fragmentInjector;

    public static Intent intent(Context context) {
        return new Intent(context, UploadActivity.class);
    }

    @Override
    public ViewDataBinding createBinding() {
        UploadActivityBinding binding =  DataBindingUtil.setContentView(this, R.layout.upload_activity);

        setSupportActionBar(binding.toolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        return binding;
    }

    @Override
    public UploadViewModel createViewModel(ViewModelProvider provider) {
        return provider.get(UploadViewModel.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getViewModel().setArgs(parseParams());
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }

    @Override
    protected void bindCreated(UploadViewModel vm, UnbindableObservable.Bag bag) {
        super.bindCreated(vm, bag);

        UnbindableObservable.bind(vm.title, bag, field -> setTitle(field.get()));

        UnbindableObservable.bind(vm.fileTypesLocked, bag, field -> {
            ActionBar ab = getSupportActionBar();
            if (ab != null) {
                if (field.get()) {
                    ab.setHomeAsUpIndicator(null);
                } else {
                    ab.setHomeAsUpIndicator(R.drawable.ic_close);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        getViewModel().onBackPressed();
    }

    private List<Uri> parseParams(){
        Intent intent = getIntent();
        List<Uri> data = new ArrayList<>();
        if (SDK_INT >= JELLY_BEAN) {
            ClipData clipData = intent.getClipData();
            for (int i = 0; i < clipData.getItemCount(); i++){
                data.add(clipData.getItemAt(i).getUri());
            }
        } else {
            data.add(intent.getData());
        }

        return Stream.of(data)
                .filter(ObjectsUtil::nonNull)
                .toList();
    }
}
