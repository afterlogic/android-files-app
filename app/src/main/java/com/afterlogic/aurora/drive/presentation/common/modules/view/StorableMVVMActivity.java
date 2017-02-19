package com.afterlogic.aurora.drive.presentation.common.modules.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.afterlogic.aurora.drive.application.App;
import com.afterlogic.aurora.drive.presentation.assembly.MVVMComponentsStore;
import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsComponent;

import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by sashka on 17.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public abstract class StorableMVVMActivity extends AppCompatActivity implements StoreableMVVMView, MVVMView{

    private static final String STORE_ID = "storeId";

    @Inject
    protected MVVMComponentsStore mModulesComponentsStore;

    private UUID mStoreId;

    private boolean mIsActive;

    public abstract void assembly(InjectorsComponent injectors);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null){
            mStoreId = UUID.randomUUID();
        } else {
            String uuid = savedInstanceState.getString(STORE_ID);
            if (uuid != null) {
                mStoreId = UUID.fromString(uuid);
            }
        }

        App app = (App) getApplication();
        assembly(app.getInjectors());
    }

    @Override
    public UUID getStoreUuid() {
        return mStoreId;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mStoreId != null){
            outState.putString(STORE_ID, mStoreId.toString());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mIsActive = true;
    }

    @Override
    protected void onStop() {
        mIsActive = false;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing() && mModulesComponentsStore != null){
            mModulesComponentsStore.remove(getStoreUuid());
        }
    }

    @Override
    public boolean isActive() {
        return mIsActive;
    }
}
