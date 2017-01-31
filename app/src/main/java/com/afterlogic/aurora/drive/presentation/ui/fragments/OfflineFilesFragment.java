package com.afterlogic.aurora.drive.presentation.ui.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.databinding.ItemListOfflineHeaderBinding;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.data.common.db.DBHelper;
import com.afterlogic.aurora.drive.data.common.db.dao.WatchingFileDAO;
import com.afterlogic.aurora.drive.data.common.db.model.WatchingFile;
import com.afterlogic.aurora.drive.presentation.ui.FilesListActivity;
import com.afterlogic.aurora.drive.presentation.ui.common.adapters.FilesAdapter;
import com.afterlogic.aurora.drive.core.util.interfaces.OnItemClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sashka on 21.04.16.
 * mail: sunnyday.development@gmail.com
 */
public class OfflineFilesFragment extends Fragment implements
        OnItemClickListener<AuroraFile>{

    private static final String KEY_MANUAL = OfflineFilesFragment.class.getName() + ".KEY_MANUAL";

    private List<AuroraFile> mFiles = new ArrayList<>();
    private FilesCallback mCallback;
    private DBHelper mDB;
    private boolean mManualMode = false;

    public static OfflineFilesFragment newInstance(boolean manual) {
        Bundle args = new Bundle();
        args.putBoolean(KEY_MANUAL, manual);
        OfflineFilesFragment fragment = new OfflineFilesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    ////////////////////////////////////////////////
    // [START Override superclass] // <editor-fold desc="Override superclass">
    ////////////////////////////////////////////////


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (FilesCallback) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDB = new DBHelper(getContext());
        collectOfflineFiles();

        if (getArguments() != null){
            mManualMode = getArguments().getBoolean(KEY_MANUAL, false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_files_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        RecyclerView list = (RecyclerView) view.findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(getContext()));

        FilesAdapter filesAdapter = new FilesAdapter(mFiles, mDB, this, null);

        //Offline header by mode
        ItemListOfflineHeaderBinding headerBinding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()),
                R.layout.item_list_offline_header,
                list,
                false
        );
        headerBinding.setManualMode(mManualMode);
        filesAdapter.setHeader(headerBinding.getRoot());

        filesAdapter.setEmptyText(getString(R.string.prompt_offline_folder_is_empty));
        filesAdapter.setShowEmptyView(true);
        list.setAdapter(filesAdapter);

        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        refreshLayout.setOnRefreshListener(() -> {
            if (getActivity() != null){
                FilesListActivity activity = (FilesListActivity) getActivity();
                activity.showOnlineState();
            }
        });
    }

    @Override
    public void onDestroy() {
        mDB.close();
        super.onDestroy();
    }

    ////////////////////////////////////////////////
    // [END Override superclass] // </editor-fold>
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    // [START Implementation] // <editor-fold desc="Implementation">
    ////////////////////////////////////////////////

    /**
     * {@link OnItemClickListener#onItemClick(Object)} with {@link AuroraFile} parameter type implementation.
     */
    @Override
    public void onItemClick(AuroraFile item) {
        mCallback.onFileClicked(item, mFiles);
    }

    ////////////////////////////////////////////////
    // [END Implementation] // </editor-fold>
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    // [START Own methods] // <editor-fold desc="Own methods">
    ////////////////////////////////////////////////

    private void collectOfflineFiles(){
        WatchingFileDAO dao = mDB.getWatchingFileDAO();
        List<WatchingFile> files = dao.getFilesList(WatchingFile.TYPE_OFFLINE);
        if (files != null){
            for (WatchingFile file:files){
                File local = new File(file.getLocalFilePath());
                AuroraFile auroraFile = AuroraFile
                        .createOffline(file.getRemoteFilePath(), file.getRemoteAuroraType(), local);
                mFiles.add(auroraFile);
            }
        }
    }

    ////////////////////////////////////////////////
    // [END Own methods] // </editor-fold>
    ////////////////////////////////////////////////
}
