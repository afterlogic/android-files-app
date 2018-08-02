package com.afterlogic.aurora.drive.presentation.modules.main.view;

import android.content.Context;
import androidx.appcompat.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.util.ObjectsUtil;
import com.afterlogic.aurora.drive.core.common.util.Optional;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.UnbindableObservable;
import com.afterlogic.aurora.drive.presentation.modules.main.viewModel.MainViewModel;
import com.annimon.stream.Stream;

/**
 * Created by aleksandrcikin on 18.07.17.
 * mail: mail@sunnydaydev.me
 */

public class MultiChoiceActionMode implements ActionMode.Callback {

    private final UnbindableObservable.Bag bag = new UnbindableObservable.Bag();

    private final Optional<ActionMode> optionalMultiChoice;
    private final MainViewModel vm;
    private final Context context;

    MultiChoiceActionMode(Optional<ActionMode> optionalMultiChoice, MainViewModel vm, Context context) {
        this.optionalMultiChoice = optionalMultiChoice;
        this.vm = vm;
        this.context = context;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        optionalMultiChoice.set(mode);

        mode.getMenuInflater().inflate(R.menu.menu_multichoise, menu);

        MenuItem offline = menu.findItem(R.id.action_offline);

        UnbindableObservable.bindToValue(vm.getMultiChoiceCount(), bag, count -> {

            String title = context.getString(R.string.title_action_selected, count);
            mode.setTitle(title);
            offline.setCheckable(count > 0);

        });

        UnbindableObservable.bindToValue(vm.getMultiChoiceDownloadable(), bag, downloadable ->
                Stream.of(R.id.action_offline, R.id.action_download, R.id.action_share)
                        .map(menu::findItem)
                        .filter(ObjectsUtil::nonNull)
                        .forEach(item -> item.setVisible(downloadable))
        );

        UnbindableObservable.bindToValue(vm.getMultiChoiceOfflineEnabled(), bag, offline::setChecked);

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_delete:
                vm.onMultiChoiceDelete();
                return true;


            case R.id.action_download:
                vm.onMultiChoiceDownload();
                return true;


            case R.id.action_share:
                vm.onMultiChoiceShare();
                return true;


            case R.id.action_replace:
                vm.onMultiChoiceReplace();
                return true;


            case R.id.action_copy:
                vm.onMultiChoiceCopy();
                return true;

            case R.id.action_offline:
                vm.onMultiChoiceOffline();
                return true;

        }

        return false;

    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

        optionalMultiChoice.set(null);
        bag.unbindAndClear();
        vm.getMultiChoiceMode().set(false);

    }

}
