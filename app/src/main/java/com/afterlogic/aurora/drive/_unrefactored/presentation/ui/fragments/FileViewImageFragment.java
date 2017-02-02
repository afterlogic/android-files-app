package com.afterlogic.aurora.drive._unrefactored.presentation.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive._unrefactored.AuraoraApp;
import com.afterlogic.aurora.drive._unrefactored.data.common.ApiProvider;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.DBHelper;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.dao.WatchingFileDAO;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.model.WatchingFile;
import com.afterlogic.aurora.drive._unrefactored.data.common.repository.FilesRepository;
import com.afterlogic.aurora.drive._unrefactored.presentation.services.SyncService;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import uk.co.senab.photoview.PhotoView;

//import com.squareup.picasso.Target;

/**
 * Created by sashka on 30.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class FileViewImageFragment extends Fragment{

    public static final String ARGS_FILE =
            FileViewImageFragment.class.getName() + ".ARGS_FILE";
    public static final String EXTRA_FULLSCREEN =
            FileViewImageFragment.class.getName() + ".EXTRA_FULLSCREEN_IMAGE";

    private AuroraFile mFile;
    private PhotoView mImageView;
    private boolean mIsFullscreen = false;
    private FileViewCallback mFileViewCallback;
    private MaterialProgressBar mProgress;
    private TextView mError;
    private boolean mImageLoaded = false;
    private WatchingFile mWatchingFile;

    private FilesRepository mFilesRepository;

    private BroadcastReceiver mSyncUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            WatchingFile target = intent.getParcelableExtra(SyncService.FileSyncAdapter.KEY_SYNC_TARGET);
            onSyncStateChanged(target);
        }
    };

    public static FileViewImageFragment newInstance(AuroraFile file) {

        Bundle args = new Bundle();
        args.putParcelable(ARGS_FILE, file);
        FileViewImageFragment fragment = new FileViewImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    ////////////////////////////////////////////////
    // [START Override superclass] // <editor-fold desc="Override superclass">
    ////////////////////////////////////////////////

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ApiProvider provider = new ApiProvider();
        ((AuraoraApp) getContext().getApplicationContext()).getDataComponent().inject(provider);
        mFilesRepository = provider.getFilesRepository();

        if (getArguments() != null) {
            mFile = getArguments().getParcelable(ARGS_FILE);
        }

        if (savedInstanceState != null) {
            mIsFullscreen = savedInstanceState.getBoolean(EXTRA_FULLSCREEN, false);
        }

        mFileViewCallback = (FileViewCallback) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preview_image, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mImageView = (PhotoView) view.findViewById(R.id.image);

        mImageView.setOnClickListener(v -> requireFullscreen(true));
        mImageView.setOnViewTapListener((v, x, y) -> requireFullscreen(!mIsFullscreen));
        mImageView.setZoomable(false);
        mImageView.setScale(1, true);

        mProgress = (MaterialProgressBar) view.findViewById(R.id.progress);
        mError = (TextView) view.findViewById(R.id.error);

        //[START Update WatchingFile]
        DBHelper db = new DBHelper(getContext());
        WatchingFileDAO dao = db.getWatchingFileDAO();
        mWatchingFile = dao.getWatching(mFile);
        db.close();
        //[END Update WatchingFile]

        if (mWatchingFile == null) {
            mFilesRepository.viewFile(mFile)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            this::showFile,
                            error -> {
                                error.printStackTrace();
                                onError();
                            }
                    );
        } else {
            getContext().registerReceiver(mSyncUpdateReceiver,
                    new IntentFilter(SyncService.FileSyncAdapter.ACTION_SYNC_STATUS_CHANGED));

            if (mWatchingFile.getSyncStatus() == WatchingFile.SYNCED){
                onSyncStateChanged(mWatchingFile);
            }
        }
    }

    private void showFile(Uri file){
        if (!isAdded()) return;

        MyLog.d(this, "Show file from: " + file.toString());
        Glide.with(this)
                .load(file)
                .listener(new RequestListener<Uri, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                        onError();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        mProgress.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(mImageView);
    }

    @Override
    public void onDestroyView() {
        Glide.clear(mImageView);
        if (mWatchingFile != null){
            getContext().unregisterReceiver(mSyncUpdateReceiver);
        }
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_FULLSCREEN, mIsFullscreen);
    }

    ////////////////////////////////////////////////
    // [END Override superclass] // </editor-fold>
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    // [START Implementation] // <editor-fold desc="Implementation">
    ////////////////////////////////////////////////



    ////////////////////////////////////////////////
    // [END Implementation] // </editor-fold>
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    // [START Own methods] // <editor-fold desc="Own methods">
    ////////////////////////////////////////////////

    private void onError(){
        mProgress.setVisibility(View.GONE);
        mError.setVisibility(View.VISIBLE);
    }

    /**
     * Handle sync update evens.
     * @param file - synced file.
     */
    private void onSyncStateChanged(WatchingFile file){
        if (file != null && file.getRemoteUniqueSpec().equals(mWatchingFile.getRemoteUniqueSpec())) {
            if (file.getSyncStatus() == WatchingFile.SYNCED) {
                showFile(Uri.fromFile(new File(mWatchingFile.getLocalFilePath())));
            } else {
                mProgress.setVisibility(View.VISIBLE);
                mImageView.setImageDrawable(null);
            }
        }
    }

    /**
     * Require fullscreen mode from activity.
     * If it proceed success set apply required fullscreen mode to image.
     * @param fullscreen - required fullscreen mode (true is fullscreen)
     */
    private void requireFullscreen(boolean fullscreen) {
        boolean success = mFileViewCallback.requireFullscreen(fullscreen);
        if (success) {
            applyFullscreen(fullscreen);
        }
    }

    /**
     * Set required fullscreen mode to image.
     * @param fullscreen - see {@link #requireFullscreen(boolean)}
     */
    private void applyFullscreen(boolean fullscreen) {
        if (getContext() == null) return;

        if (fullscreen) {
            mImageView.setZoomable(true);
            mImageView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black));
        } else {
            mImageView.setZoomable(false);
            mImageView.setScale(1, true);
            mImageView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.transparent));
        }
        mIsFullscreen = fullscreen;
    }

    ////////////////////////////////////////////////
    // [END Own methods] // </editor-fold>
    ////////////////////////////////////////////////
}
