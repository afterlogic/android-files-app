package com.afterlogic.aurora.drive.presentation.common.binding.binder;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.annotation.NonNull;

import com.afterlogic.aurora.drive.BR;
import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.core.common.interfaces.Provider;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.annimon.stream.Stream;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by sashka on 20.01.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class SelectableArrayBinder<T> extends BaseObservable {

    private final Set<OnPropertyChangedCallback> mCallbacks = new HashSet<>();

    private final Provider<List<T>> mItemsProvider;
    private final Provider<T> mSelectedProvider;
    private final Consumer<Integer> mSelectionConsumer;

    public SelectableArrayBinder(@NonNull Provider<List<T>> itemsProvider,
                                 @NonNull Provider<T> selectedProvider,
                                 @NonNull Consumer<Integer> selectionConsumer) {
        mItemsProvider = itemsProvider;
        mSelectedProvider = selectedProvider;
        mSelectionConsumer = selectionConsumer;
    }

    public List<T> getItems(){
        return mItemsProvider.get();
    }

    @Bindable
    public int getSelectedPosition(){
        List<T> list = mItemsProvider.get();
        if (list == null) return -1;
        
        T selected = mSelectedProvider.get();
        if (selected == null) return -1;
        
        return list.indexOf(selected);
    }
    
    public void setSelected(int position){
        int prevPosition = getSelectedPosition();
        if (prevPosition != position){
            mSelectionConsumer.consume(position);
            notifyPropertyChanged(BR.selectedPosition);
        }
    }

    @Override
    public synchronized void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        super.addOnPropertyChangedCallback(callback);
        mCallbacks.add(callback);
    }

    public void clearPropertyChangedCallbacks(){
        MyLog.i("Clear all property changed callbacks.");
        Stream.of(mCallbacks).forEach(this::removeOnPropertyChangedCallback);
    }
}
