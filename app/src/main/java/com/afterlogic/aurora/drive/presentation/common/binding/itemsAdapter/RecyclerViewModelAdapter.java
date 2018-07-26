package com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter;

import androidx.databinding.ObservableList;
import androidx.databinding.ViewDataBinding;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.afterlogic.aurora.drive.BR;
import com.afterlogic.aurora.drive.core.common.interfaces.Provider;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.annimon.stream.Stream;

import java.util.List;

/**
 * Created by sashka on 13.09.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public abstract class RecyclerViewModelAdapter<T> extends RecyclerBindAdapter implements ItemsAdapter<T>{

    private List<T> mList;

    private ObservableListListener mListListener;

    private boolean mIsListenerAttached = false;

    public RecyclerViewModelAdapter() {
        mListListener = onCreateListListener();
    }

    public RecyclerViewModelAdapter(List<T> list) {
        this();
        setItems(list);
    }

    @Override
    public void onBindViewHolder(BindViewHolder holder, int position) {
        ViewDataBinding binding = holder.getDataBinding();
        binding.setVariable(getViewModelVariable(), mList.get(position));
        binding.executePendingBindings();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        attachObservableListListner();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        removeObservableListListener();
    }

    @Override
    public void setItems(@Nullable List<T> list) {
        if (mList == list) return;

        removeObservableListListener();

        mList = list;

        if (!attachObservableListListner()){
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    protected int getViewModelVariable(){
        return BR.viewModel;
    }

    protected ObservableListListener onCreateListListener(){
        return new ObservableListListener(this, () -> 0);
    }

    private void removeObservableListListener(){
        if (mListListener != null && mIsListenerAttached && isObservableList()){
            ObservableList<T> observableList = ((ObservableList<T>) mList);
            observableList.removeOnListChangedCallback(mListListener);
            mIsListenerAttached = false;
        }
    }

    private boolean attachObservableListListner(){
        if (mListListener == null || mIsListenerAttached || !isObservableList()) return false;

        ObservableList<T> observableList = ((ObservableList<T>) mList);
        observableList.addOnListChangedCallback(mListListener);
        mListListener.onChanged(observableList);
        mIsListenerAttached = true;
        return true;
    }

    private boolean isObservableList(){
        return mList != null && mList instanceof ObservableList;
    }

    protected class ObservableListListener extends ObservableList.OnListChangedCallback<ObservableList<T>> {

        private final OptWeakRef<RecyclerViewModelAdapter<T>> mAdapter = OptWeakRef.empty();
        private final Provider<Integer> mItemsStartPosition;

        private int mPreviousSize = 0;

        public ObservableListListener(RecyclerViewModelAdapter<T> adapter, Provider<Integer> itemsStartPosition) {
            mItemsStartPosition = itemsStartPosition;
            mAdapter.set(adapter);
        }

        @Override
        public void onChanged(ObservableList<T> ts) {
            mAdapter.ifPresent(RecyclerViewModelAdapter::notifyDataSetChanged);

            setPreviousSize(mPreviousSize);
        }

        @Override
        public void onItemRangeChanged(ObservableList<T> ts, int position, int count) {
            if (count == mPreviousSize){
                mAdapter.ifPresent(RecyclerView.Adapter::notifyDataSetChanged);
            } else {
                mAdapter.ifPresent(adapter -> adapter.notifyItemRangeChanged(shiftPosition(position), count));
            }

            setPreviousSize(ts.size());
        }

        @Override
        public void onItemRangeInserted(ObservableList<T> ts, int position, int count) {
            if (mPreviousSize == 0){
                mAdapter.ifPresent(RecyclerView.Adapter::notifyDataSetChanged);
            } else {
                mAdapter.ifPresent(adapter -> adapter.notifyItemRangeInserted(shiftPosition(position), count));
            }

            setPreviousSize(ts.size());
        }

        @Override
        public void onItemRangeMoved(ObservableList<T> ts, int from, int to, int count) {
            if (count == mPreviousSize){
                mAdapter.ifPresent(RecyclerView.Adapter::notifyDataSetChanged);
            } else {
                mAdapter.ifPresent(adapter ->
                        Stream.range(0, count).forEach(
                                shift -> adapter.notifyItemMoved(shiftPosition(from) + shift, shiftPosition(to) + shift)
                        )
                );
            }

            setPreviousSize(ts.size());
        }

        @Override
        public void onItemRangeRemoved(ObservableList<T> ts, int position, int count) {
            if (count == mPreviousSize){
                mAdapter.ifPresent(RecyclerView.Adapter::notifyDataSetChanged);
            } else {
                mAdapter.ifPresent(adapter -> adapter.notifyItemRangeRemoved(shiftPosition(position), count));
            }

            setPreviousSize(ts.size());
        }

        private void setPreviousSize(int previousSize) {
            mPreviousSize = previousSize;
        }

        private int shiftPosition(int position){
            return position + mItemsStartPosition.get();
        }
    }
}
