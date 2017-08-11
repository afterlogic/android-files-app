package com.afterlogic.aurora.drive.presentation.common.modules.view;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.application.App;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.consts.NotificationConst;
import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.model.presenter.Presenter;
import com.annimon.stream.Stream;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by sashka on 26.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Deprecated
public abstract class MVPService extends Service implements PresentationView {

    private boolean mIsActive;

    protected abstract void assembly(InjectorsComponent modulesFactory);

    private UUID mUUID;

    private int mMessageId = 0;

    final Set<Presenter> mPresenters = new HashSet<>();

    @Override
    public void onCreate() {
        super.onCreate();
        MyLog.d("onCreate()");

        mIsActive = true;

        InjectorsComponent wireframeFactory = ((App) getApplication()).getInjectors();
        assembly(wireframeFactory);

        PresentationViewUtil.reflectiveCollectPresenters(this);

        Stream.of(mPresenters).forEach(Presenter::onStart);
    }

    @Override
    public void onDestroy() {
        MyLog.d("onDestroy()");
        Stream.of(mPresenters).forEach(Presenter::onStop);
        mIsActive = false;
        ((App) getApplication()).getInjectors().modulesStore().remove(getModuleUuid());

        if (mMessageId > 0){
            NotificationManagerCompat nm  = NotificationManagerCompat.from(this);
            Stream.range(0, mMessageId)
                    .map(this::getMajorMessageTag)
                    .forEach(tag -> nm.cancel(tag, NotificationConst.SERVICE_MAJOR_MESSAGE));
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyLog.d("onStartCommand(): id: " + startId + ", intent: " + intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        MyLog.d("onBind(): " + intent);
        return null;
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
    public void showMessage(int messageId, int type) {
        showMessage(getString(messageId), type);
    }

    @Override
    public void showMessage(String message, int type) {
        switch (type){
            case PresentationView.TYPE_MESSAGE_MAJOR:
                NotificationManagerCompat nm  = NotificationManagerCompat.from(this);
                NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this);

                Notification notification = notifyBuilder.setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_notify)
                        .setTicker(message)
                        .setContentText(message)
                        .setContentTitle(getString(R.string.app_name))
                        .build();

                nm.notify(getMajorMessageTag(), NotificationConst.SERVICE_MAJOR_MESSAGE, notification);
                break;

            case PresentationView.TYPE_MESSAGE_MINOR:
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean isActive() {
        return mIsActive;
    }

    @Override
    public <T extends Context> T getViewContext() {
        return (T) this;
    }

    @Override
    public void requestPermissions(String[] permissions, int id) {
        MyLog.majorException(this, "Service can't request permissions");
    }

    @Override
    public UUID getModuleUuid() {
        return mUUID;
    }

    @Override
    public void setModuleUuid(UUID uuid) {
        mUUID = uuid;
    }

    private String getMajorMessageTag(){
        return getMajorMessageTag(mMessageId++);
    }

    private String getMajorMessageTag(int id){
        return getClass().getName() + ":" + id;
    }
}
