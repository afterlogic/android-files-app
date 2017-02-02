package com.afterlogic.aurora.drive._unrefactored.presentation.ui.common.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.Api;
import com.afterlogic.aurora.drive._unrefactored.data.common.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive._unrefactored.presentation.ui.common.adapters.FileActionsMenuAdapter;
import com.afterlogic.aurora.drive._unrefactored.core.util.FileUtil;
import com.afterlogic.aurora.drive._unrefactored.core.util.WatchingFileManager;
import com.afterlogic.aurora.drive._unrefactored.core.util.interfaces.OnItemClickListener;

/**
 * Created by sashka on 23.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class FileActionsBottomSheet extends BottomSheetDialogFragment implements
        OnItemClickListener<FileActionsMenuAdapter.ActionItem>,
        FileActionsMenuAdapter.OnCheckedChangeListener
{

    private static final String ARGS_FILE = "ARGS_FILE";

    private AuroraFile mAuroraFile;
    private FileActionListener mListener;

    public static FileActionsBottomSheet newInstance(@NonNull AuroraFile file) {

        Bundle args = new Bundle();
        args.putParcelable(ARGS_FILE, file);
        FileActionsBottomSheet fragment = new FileActionsBottomSheet();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FileActionListener){
            mListener = (FileActionListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            mAuroraFile = getArguments().getParcelable(ARGS_FILE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_sheet_file_actions, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView name = (TextView) view.findViewById(R.id.text);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);

        name.setText(mAuroraFile.getName());
        FilesRepository filesRepository = Api.getApiProvider().getFilesRepository();
        FileUtil.updateIcon(icon, mAuroraFile, filesRepository, getContext());

        FileActionsMenuAdapter menuContent = new FileActionsMenuAdapter(this)
                        .addAction(R.id.action_rename, R.string.prompt_rename, R.drawable.ic_edit)
                        .addAction(R.id.action_delete, R.string.prompt_delete, R.drawable.ic_delete);
        if (!mAuroraFile.isFolder() && !mAuroraFile.isLink()){
            menuContent
                    .addAction(R.id.action_download,
                            R.string.prompt_action_download, R.drawable.ic_download_black)
                    .addAction(R.id.action_send,
                            R.string.prompt_send, R.drawable.ic_email);

            boolean offline = WatchingFileManager.from(getContext()).isOffline(mAuroraFile);
            menuContent.addSwitch(R.id.action_offline_on,
                            R.string.prompt_action_make_offline, R.drawable.ic_offline, offline, this);
        }

        RecyclerView menu = (RecyclerView) view.findViewById(R.id.menu_list);
        menu.setLayoutManager(new LinearLayoutManager(getContext()));
        menu.setAdapter(menuContent);
    }

    @Override
    public void onItemClick(FileActionsMenuAdapter.ActionItem item) {
        mListener.onActionSelected(item.getId(), mAuroraFile);
        dismiss();
    }

    @Override
    public void onCheckedChanged(FileActionsMenuAdapter.ActionSwitch action, boolean isChecked) {
        switch (action.getId()){
            case R.id.action_offline_on:
                mListener.onActionSelected(
                        isChecked ? R.id.action_offline_on : R.id.action_offline_off,
                        mAuroraFile
                );
                break;
        }
    }

    public interface FileActionListener{
        void onActionSelected(int action, AuroraFile file);
    }
}
