package com.afterlogic.aurora.drive.core.util.task;

import android.os.AsyncTask;

import com.afterlogic.aurora.drive.data.common.api.ApiCallback;
import com.afterlogic.aurora.drive.data.common.api.ApiError;
import com.afterlogic.aurora.drive.data.common.api.ApiResponseError;
import com.afterlogic.aurora.drive.data.common.api.Task;
import com.afterlogic.aurora.drive.core.util.IOUtil;
import com.afterlogic.aurora.drive.data.common.api.Api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by sashka on 25.04.16.
 * mail: sunnyday.development@gmail.com
 */
public class CopyFileTask extends AsyncTask<File, Void, Boolean> {
    private Task mTask;
    private File mResult;
    private ApiCallback<File> mApiCallback;

    public CopyFileTask(ApiCallback<File> apiCallback) {
        mApiCallback = apiCallback;
    }

    @Override
    protected Boolean doInBackground(File... params) {
        mResult = params[1];

        FileOutputStream fos = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(params[0]);
            fos = new FileOutputStream(params[1]);

            int read;
            long total = 0;
            long lenght = params[0].length();
            mTask.notifyChanged(total, lenght);

            byte[] buffer = new byte[2024];
            while ((read = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
                total += read;
                mTask.notifyChanged(total, lenght);
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.close(fos);
            IOUtil.close(fis);
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean) {
            mApiCallback.onSucces(mResult);
        } else {
            mApiCallback.onError(new ApiResponseError(ApiResponseError.ERROR_CODE_NOT_EXIST, null));
        }
        mTask.notifyEnd(aBoolean ?
                Task.EndStatus.SUCCESS : Task.EndStatus.FAILED);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        mApiCallback.onError(new ApiResponseError(ApiError.TASK_CANCELLED, null));
        mTask.cancel();
    }

    public int prepare() {
        //Simulate api request
        mTask = Api.getTaskStateHandler().startNewTask();
        return mTask.getId();
    }
}
