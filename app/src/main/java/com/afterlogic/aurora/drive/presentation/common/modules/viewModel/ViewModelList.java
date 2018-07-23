package com.afterlogic.aurora.drive.presentation.common.modules.viewModel;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.annotation.Nullable;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.util.ObjectsUtil;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.annimon.stream.Stream;

import java.util.List;

/**
 * Created by sashka on 15.12.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class ViewModelList<V, M>{

    private final Mapper<V, M> mMapper;
    private List<M> mItems;
    private OptWeakRef<ObservableList<V>> mViewItems = OptWeakRef.empty();

    private ObservableListListener<V, M> mObservableListListener;

    public ViewModelList(Mapper<V, M> mapper) {
        this(null, mapper);
    }

    public ViewModelList(@Nullable List<M> items, Mapper<V, M> mapper) {
        mMapper = mapper;
        if (items != null){
            setItems(items);
        }
    }

    public ObservableList<V> getViewModels() {
        return mViewItems.getOrCreate(() -> {
            ObservableList<V> viewItems = new ObservableArrayList<>();
            fillByModel(viewItems);
            return viewItems;
        });
    }

    public void setItems(@Nullable List<M> items) {
        if (items != null){
            if (mItems instanceof ObservableList && mObservableListListener != null){
                ((ObservableList<M>) mItems).removeOnListChangedCallback(mObservableListListener);
            }

            mItems = items;

            if (mItems instanceof ObservableList && mObservableListListener != null){
                mObservableListListener = new ObservableListListener<>(this);
                ((ObservableList<M>) mItems).addOnListChangedCallback(mObservableListListener);
            }
        } else {
            mItems = null;
        }

        notifyChanged();
    }

    public List<M> getItems() {
        return mItems;
    }


    public void add(M model){
        if (mItems == null){
            MyLog.e(this, "Items is null. Ignore adding.");
            return;
        }
        mItems.add(model);
        mViewItems.ifPresent(views -> views.add(mMapper.map(model)));
    }

    public void remove(M model){
        if (mItems == null){
            MyLog.e(this, "Items is null. Ignore removing.");
            return;
        }

        ObservableList<V> viewItems = mViewItems.get();
        if (viewItems == null){
            mItems.remove(model);
        } else {
            if (mItems instanceof List) {
                int i = mItems.indexOf(model);
                if (i != -1) {
                    mItems.remove(i);
                    viewItems.remove(i);
                }
            } else {
                Stream.zip(
                        Stream.of(mItems),
                        Stream.of(viewItems),
                        (value1, value2) -> {
                            if (value1 == model){
                                return value2;
                            } else {
                                return null;
                            }
                        }
                )//-----|
                        .filter(ObjectsUtil::nonNull)
                        .findSingle()
                        .ifPresent(viewItems::remove);
            }
        }
    }

    public V getViewByModel(M value){
        if (value == null) return null;

        int modelIndex = mItems.indexOf(value);
        if (modelIndex == -1) return null;

        if (mViewItems.isPresent()){
            return mViewItems.get().get(modelIndex);
        } else {
            return null;
        }
    }

    public void notifyChanged(){
        mViewItems.ifPresent(viewItems -> {
            if (viewItems.size() > 0){
                viewItems.clear();
            }

            fillByModel(viewItems);
        });
    }

    private void fillByModel(ObservableList<V> viewItems){
        if (mItems != null){
            Stream.of(mItems)
                    .map(mMapper::map)
                    .forEach(viewItems::add);
        }
    }
}
