package com.afterlogic.aurora.drive.presentation.common.modules.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.presentation.common.modules.presenter.Presenter;
import com.annimon.stream.Stream;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by sashka on 15.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Deprecated
public abstract class BaseSubView<C extends Context> implements SubView {

    private final int mId;
    private final C mContext;

    private final static String MODULE_UUID = ".MODULE_UUID:";

    private UUID mUuid;
    private boolean mActive = false;

    protected final Set<Presenter> mPresenters = new HashSet<>();

    public BaseSubView(int id, C context) {
        mContext = context;
        mId = id;

        reflectiveCollectPresenters(this);
    }

    @Override
    public void saveInstance(Bundle outState){
        if (mUuid != null) {
            outState.putString(getUuidTag(), mUuid.toString());
        }

        Stream.of(mPresenters).forEach(presenter -> presenter.onSaveInstanceState(outState));
    }

    @Override
    public void restoreInstance(Bundle savedInstanceState){
        if (savedInstanceState != null){
            String uuidString = savedInstanceState.getString(getUuidTag(), null);
            if (uuidString != null) {
                mUuid = UUID.fromString(uuidString);
            }
            Stream.of(mPresenters).forEach(presenter -> presenter.onRestoreInstanceState(savedInstanceState));
        }
    }

    @Override
    public void showLoadingProgress(String message, int type) {
        //no-op
    }

    @Override
    public void hideLoadingProgress(int type) {
        //no-op
    }

    @Override
    public void showMessage(String message, int type) {
        //no-op
    }

    @Override
    public boolean isActive() {
        return mActive;
    }

    @Override
    public UUID getModuleUuid() {
        return mUuid;
    }

    @Override
    public void setModuleUuid(UUID uuid) {
        mUuid = uuid;
    }

    @Override
    public <Cx extends Context> Cx getViewContext() {
        return (Cx) mContext;
    }

    @Override
    public void requestPermissions(String[] permissions, int id) {
        if (mContext instanceof Activity){
            ActivityCompat.requestPermissions(
                    ((Activity) mContext),
                    permissions,
                    id
            );
        } else {
            MyLog.majorException(this, "SubView can't request permissions");
        }
    }

    @Override
    public void onStart() {
        mActive = true;
        Stream.of(mPresenters).forEach(Presenter::onStart);
    }

    @Override
    public void onStop() {
        Stream.of(mPresenters).forEach(Presenter::onStop);
        mActive = false;
    }

    private String getUuidTag(){
        return MODULE_UUID + this.getClass().getName() + ":" + mId;
    }

    private static void reflectiveCollectPresenters(BaseSubView subView){
        Stream.of(subView.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ViewPresenter.class) &&
                        Presenter.class.isAssignableFrom(field.getType())
                )
                .forEach(field -> {
                    try {
                        Presenter presenter = (Presenter) field.get(subView);
                        if (presenter != null) {
                            subView.mPresenters.add(presenter);
                        } else {
                            MyLog.majorException(subView, "Field marked as ViewPresenter but it is null: " + field.getName());
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
    }
}
