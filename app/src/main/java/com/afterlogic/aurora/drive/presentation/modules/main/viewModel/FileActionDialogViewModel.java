package com.afterlogic.aurora.drive.presentation.modules.main.viewModel;

import android.databinding.ObservableField;
import android.net.Uri;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.FileAction;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sashka on 15.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileActionDialogViewModel {

    private final MainFileItemViewModel mTarget;
    private final List<FileActionItemViewModel> mActions;
    private final FileAction.OnActionListener mListener;

    public FileActionDialogViewModel(MainFileItemViewModel target, FileAction.OnActionListener listener) {
        mTarget = target;
        mListener = listener;

        AuroraFile file = target.getModel().getFile();

        List<FileAction> actions = new ArrayList<>();
        actions.add(new FileAction(R.id.action_rename, R.string.prompt_rename, R.drawable.ic_edit));
        actions.add(new FileAction(R.id.action_delete, R.string.prompt_delete, R.drawable.ic_delete));
        if (!file.isFolder() && !file.isLink()){
            actions.add(new FileAction(R.id.action_download, R.string.prompt_action_download, R.drawable.ic_download_black));
            actions.add(new FileAction(R.id.action_send, R.string.prompt_send, R.drawable.ic_email));
            actions.add(new FileAction(R.id.action_offline, R.string.prompt_action_make_offline, R.drawable.ic_offline, true));
        }

        mActions = Stream.of(actions)
                .map(this::toViewModel)
                .collect(Collectors.toList());
    }

    public List<FileActionItemViewModel> getItems(){
        return mActions;
    }

    public ObservableField<String> getTitle(){
        return mTarget.getFileName();
    }

    public ObservableField<Uri> getIcon(){
        return mTarget.getFileIcon();
    }

    private FileActionItemViewModel toViewModel(FileAction action){
        if (action.isCheckable()){
            return new FileCheckableActionItemViewModel(action, mTarget.getOffline().get(), mListener);
        } else {
            return new FileActionItemViewModel(action, mListener);
        }
    }
}
