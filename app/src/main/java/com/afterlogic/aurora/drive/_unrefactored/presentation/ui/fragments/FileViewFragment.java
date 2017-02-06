package com.afterlogic.aurora.drive._unrefactored.presentation.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.DBHelper;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.dao.WatchingFileDAO;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.model.WatchingFile;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive._unrefactored.presentation.ui.common.views.DisablableViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sashka on 30.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class FileViewFragment extends Fragment{

    public static final String ARGS_PREVIEWABLE_FILES =
            FileViewFragment.class.getName() + ".ARGS_PREVIEWABLE_FILES";
    public static final String ARGS_CURRENT_POSITION =
            FileViewFragment.class.getName() + ".ARGS_CURRENT_POSITION";

    private DisablableViewPager mVp;
    private MenuItem mOfflineMenuItem;

    private FileViewCallback mCallback;

    private PreviewAdapter mAdapter;
    private DBHelper mDBHelper;

    private List<AuroraFile> mFiles;
    private int mCurrentPosition;

    public static FileViewFragment newInstance(List<AuroraFile> allFiles, AuroraFile current) {
        int currentPosition = 0;

        ArrayList<AuroraFile> previewAble = new ArrayList<>();
        for (AuroraFile file:allFiles){
            if (file.isPreviewAble()){
                if (current == file){
                    currentPosition = previewAble.size();
                }
                previewAble.add(file);
            }
        }
        Bundle args = new Bundle();

        args.putParcelableArrayList(ARGS_PREVIEWABLE_FILES, previewAble);
        args.putInt(ARGS_CURRENT_POSITION, currentPosition);

        FileViewFragment fragment = new FileViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (FileViewCallback) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);

        mDBHelper = new DBHelper(getContext());

        if (getArguments() != null){
            mFiles = getArguments().getParcelableArrayList(ARGS_PREVIEWABLE_FILES);
            mCurrentPosition = getArguments().getInt(ARGS_CURRENT_POSITION);
        }

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new PreviewAdapter(getChildFragmentManager(), mFiles);

        mVp = (DisablableViewPager) view.findViewById(R.id.pager);
        mVp.setAdapter(mAdapter);
        mVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                updateToolbarByCurrentFile();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mVp.setCurrentItem(mCurrentPosition, false);
        updateToolbarByCurrentFile();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdapter = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDBHelper.close();
        mDBHelper = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_file_view, menu);
        mOfflineMenuItem = menu.findItem(R.id.action_offline);
        updateCurrentFileOfflineIndicator();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int currentPosition = mVp.getCurrentItem();
        AuroraFile file = mFiles.get(currentPosition);

        if (file == null) return true;

        switch (item.getItemId()){
            case R.id.action_download:
                mCallback.downloadFile(file);
                break;
            case R.id.action_delete:
                mCallback.deleteFile(file);
                break;
            case R.id.action_send:
                mCallback.sendFile(file);
                break;
            case R.id.action_rename:
                mCallback.renameFile(file);
                break;
            case R.id.action_offline:
                item.setChecked(!item.isChecked());
                mCallback.makeFileOffline(file, item.isChecked());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void notifyDataSetChanged(){
        if (mAdapter != null){
            mAdapter.notifyDataSetChanged();
            updateToolbarByCurrentFile();
        }
    }

    public void onFilesDeleted(List<AuroraFile> files){
        mFiles.removeAll(files);
        int lastIndex = mFiles.size() - 1;
        if (lastIndex < mCurrentPosition){
            mCurrentPosition = lastIndex;
        }
        notifyDataSetChanged();
    }

    private void updateToolbarByCurrentFile(){
        AuroraFile file = mFiles.get(mCurrentPosition);

        //update title
        mCallback.updateTitle(file.getName());

        updateCurrentFileOfflineIndicator();
    }

    private void updateCurrentFileOfflineIndicator(){
        if (mOfflineMenuItem == null || mDBHelper == null) return;

        AuroraFile file = mFiles.get(mCurrentPosition);

        //update offline availability
        WatchingFileDAO dao = mDBHelper.getWatchingFileDAO();
        WatchingFile watchingFile = dao.getWatching(file);
        boolean offline = watchingFile != null && watchingFile.getType() == WatchingFile.TYPE_OFFLINE;
        mOfflineMenuItem.setChecked(offline);
    }

    public void setSwipeAble(boolean swipeAble){
        if(mVp != null){
            mVp.setSwipeEnabled(swipeAble);
            updateToolbarByCurrentFile();
        }
    }

    @SuppressWarnings("WeakerAccess")
    private class PreviewAdapter extends FragmentStatePagerAdapter {

        private List<AuroraFile> mFiles;

        public PreviewAdapter(FragmentManager fm, List<AuroraFile> files) {
            super(fm);
            mFiles = files;
        }

        @Override
        public Fragment getItem(int position) {
            return FileViewImageFragment.newInstance(mFiles.get(position));
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mFiles.size();
        }
    }
}
