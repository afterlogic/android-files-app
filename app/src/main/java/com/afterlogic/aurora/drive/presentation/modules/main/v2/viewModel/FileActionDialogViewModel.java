package com.afterlogic.aurora.drive.presentation.modules.main.v2.viewModel;

import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.LifecycleViewModel;

/**
 * Created by sashka on 15.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileActionDialogViewModel extends LifecycleViewModel {

    /*
    private final ObservableList<FileActionItemViewModel> items = new ObservableArrayList<>();

    public FileActionDialogViewModel() {

        AuroraFile file = target.getModel().getFile();

        List<FileAction> actions = new ArrayList<>();
        actions.add(new FileAction(R.id.action_rename, R.string.prompt_rename, R.drawable.ic_edit));
        actions.add(new FileAction(R.id.action_delete, R.string.prompt_delete, R.drawable.ic_delete));
        if (!file.isFolder() && !file.isLink()){
            actions.add(new FileAction(R.id.action_download, R.string.prompt_action_download, R.drawable.ic_download_black));
            actions.add(new FileAction(R.id.action_send, R.string.prompt_send, R.drawable.ic_action_share));
            actions.add(new FileAction(R.id.action_offline, R.string.prompt_action_make_offline, R.drawable.ic_offline, true));
        }

        if (!file.isLink()) {
            actions.add(new FileAction(R.id.action_public_link, R.string.prompt_action_public_link, R.drawable.ic_action_public_link, true));
            if(file.isShared()) {
                actions.add(new FileAction(R.id.action_copy_public_link, R.string.prompt_action_public_link_copy, View.NO_ID));
            }
        }

        actions.add(new FileAction(R.id.action_replace, R.string.prompt_action__replace, R.drawable.ic_content_cut));
        actions.add(new FileAction(R.id.action_copy, R.string.prompt_action__copy, R.drawable.ic_action_copy));

        Stream.of(actions)
                .map(this::toViewModel)
                .collect(StreamCollectors.setListByClearAdd(items));
    }

    public List<FileActionItemViewModel> getItems(){
        return items;
    }

    public ObservableField<String> getTitle(){
        return mTarget.getFileName();
    }

    public ObservableField<Uri> getIcon(){
        return mTarget.getFileIcon();
    }

    private FileActionItemViewModel toViewModel(FileAction action){
        if (action.isCheckable()){
            boolean checked;
            switch (action.getId()) {
                case R.id.action_offline:
                    checked = mTarget.getOffline().get();
                    break;
                case R.id.action_public_link:
                    checked = mTarget.getShared().get();
                    break;
                default:
                    checked = false;
            }
            return new FileCheckableActionItemViewModel(action, checked, mListener);
        } else {
            return new FileActionItemViewModel(action, mListener);
        }
    }
    */
}
