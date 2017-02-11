package com.afterlogic.aurora.drive.presentation.modules.upload.view;

import android.content.ClipData;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.core.common.streams.ExtCollectors;
import com.afterlogic.aurora.drive.core.common.util.ObjectsUtil;
import com.afterlogic.aurora.drive.databinding.ActivityUploadBinding;
import com.afterlogic.aurora.drive.presentation.assembly.wireframes.ModulesFactoryComponent;
import com.afterlogic.aurora.drive.presentation.common.binding.SimpleListener;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.view.BaseFilesActivity;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.view.BaseFilesListFragment;
import com.afterlogic.aurora.drive.presentation.modules.upload.presenter.UploadPresenter;
import com.afterlogic.aurora.drive.presentation.modules.upload.viewModel.UploadViewModel;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.JELLY_BEAN;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class UploadActivity extends BaseFilesActivity<UploadViewModel, UploadPresenter> implements UploadView{

    private List<Uri> mIntentParams = new ArrayList<>();

    private SimpleListener mLockedListener = new SimpleListener(this::updateActionBar);

    @Override
    protected void assembly(ModulesFactoryComponent modulesFactory) {
        modulesFactory.upload().inject(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!parseParams()){
            //TODO error toast
            finish();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        updateActionBar();

        mViewModel.getLocked().addOnPropertyChangedCallback(mLockedListener);
    }

    @Override
    public BaseFilesListFragment getFilesContent(String type) {
        return new UploadFilesFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.getLocked().removeOnPropertyChangedCallback(mLockedListener);
    }

    @Override
    public ViewDataBinding onCreateBind() {
        ActivityUploadBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_upload);
        binding.createFolder.setOnClickListener(view -> ifFragment(UploadFilesFragment::createFolder));
        binding.upload.setOnClickListener(view -> ifFragment(current -> current.upload(mIntentParams)));
        return binding;
    }

    private void ifFragment(Consumer<UploadFilesFragment> consumer){
        UploadFilesFragment fragment = (UploadFilesFragment) mAdapter.getPrimaryFragment();
        if (fragment != null){
            consumer.consume(fragment);
        }
    }

    private boolean parseParams(){
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

        mIntentParams.clear();
        Stream.of(data)
                .filter(ObjectsUtil::nonNull)
                .collect(ExtCollectors.to(mIntentParams));
        return mIntentParams.size() > 0;
    }

    private void updateActionBar(){
        ActionBar ab = getSupportActionBar();
        if (ab == null) return;

        if (!mViewModel.getLocked().get()){
            ab.setHomeAsUpIndicator(R.drawable.ic_close);
        }else{
            ab.setHomeAsUpIndicator(null);
        }
    }

    @Override
    protected void updateHomeButtonByViewModel() {
        //no-op
    }
}
