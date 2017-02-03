package com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters;

import android.databinding.BindingAdapter;
import android.databinding.Observable;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.afterlogic.aurora.drive.BR;
import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.presentation.common.binding.binder.Binder;
import com.afterlogic.aurora.drive.presentation.common.binding.binder.SelectableArrayBinder;
import com.afterlogic.aurora.drive.presentation.common.binding.listAdapter.RecyclerViewModelAdapter;
import com.afterlogic.aurora.drive.presentation.common.binding.listAdapter.SpinnerViewModelAdapter;
import com.afterlogic.aurora.drive.presentation.common.binding.listAdapter.ViewsViewModelBindAdapter;
import com.afterlogic.aurora.drive.presentation.common.modules.viewModel.SpinnerViewModel;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by sashka on 13.09.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
@SuppressWarnings("unused")
public class BindingAdapters {

    private static final String TAG = BindingAdapters.class.getSimpleName();

    @BindingAdapter("bind:backgroundColor")
    public static void setBackgroundColor(View view, int color){
        if (color != -1){
            view.setBackgroundColor(color);
        }
    }

    @BindingAdapter("bind:font")
    public static void setTextViewFont(TextView view, String path){
        Typeface typeface = Typeface.createFromAsset(view.getContext().getAssets(), path);
        view.setTypeface(typeface);
    }

    @BindingAdapter("bind:onClick")
    public static void setClickable(View view, View.OnClickListener onClickListener){
        view.setClickable(true);
        view.setOnClickListener(onClickListener);
    }

    @BindingAdapter("bind:alpha")
    public static void setAlpha(View view, float alpha){
        ViewCompat.setAlpha(view, alpha);
    }

    @BindingAdapter("bind:onCheck")
    public static void setOnCheckedChaneListener(CompoundButton checkBox, CompoundButton.OnCheckedChangeListener listener){
        checkBox.setOnCheckedChangeListener(listener);
    }

    @BindingAdapter("bind:imageUri")
    public static void setImageFromUri(ImageView imageView, Uri uri){
        Glide.clear(imageView);
        if (uri != null) {
            Glide.with(imageView.getContext())
                    .fromUri()
                    .load(uri)
                    .into(imageView);
        } else {
            imageView.setImageDrawable(null);
        }
    }

    @BindingAdapter("bind:imageUrl")
    public static void setImageFromUrl(ImageView imageView, String url){
        setImageFromUri(imageView, Uri.parse(url));
    }

    @BindingAdapter("bind:layoutManager")
    public static void setRecyclerLayoutManager(RecyclerView list, ViewProvider<RecyclerView.LayoutManager, RecyclerView> provider){
        setRecyclerLayoutManager(list, provider.provideFor(list));
    }

    @BindingAdapter("bind:layoutManager")
    public static void setRecyclerLayoutManager(RecyclerView list, RecyclerView.LayoutManager manger){
        list.setLayoutManager(manger);
    }

    @BindingAdapter({"bind:adapter"})
    public static <T extends RecyclerView.Adapter> void setRecyclerViewAdapter(RecyclerView list, ViewProvider<T, RecyclerView> adapter){
        setRecyclerViewAdapter(list, adapter.provideFor(list));
    }

    @BindingAdapter({"bind:adapter"})
    public static <T extends RecyclerView.Adapter> void setRecyclerViewAdapter(RecyclerView list, T adapter){
        if (list.getAdapter() != adapter) {
            list.setAdapter(adapter);
        }
    }

    @BindingAdapter({"bind:adapter", "bind:items"})
    public static <VM> void setRecyclerAdapterWithItems(RecyclerView list, ViewProvider<RecyclerViewModelAdapter<VM>, RecyclerView> adapter, List<VM> items){
        setRecyclerAdapterWithItems(list, adapter.provideFor(list), items);
    }

    @BindingAdapter({"bind:adapter", "bind:items"})
    public static <VM> void setRecyclerAdapterWithItems(RecyclerView list, RecyclerViewModelAdapter<VM> adapter, List<VM> items){
        if (adapter != null) {
            adapter.setItems(items);
        }
        setRecyclerViewAdapter(list, adapter);
    }

    @BindingAdapter({"bind:adapter", "bind:items"})
    public static <T extends SpinnerViewModel> void setSpinnerItems(Spinner spinner, SpinnerViewModelAdapter<T> adapter, List<T> items){
        setSpinnerItems(spinner, adapter, items, -1);
    }

    @BindingAdapter({"bind:adapter", "bind:items", "bind:selected"})
    public static <T extends SpinnerViewModel> void setSpinnerItems(Spinner spinner, SpinnerViewModelAdapter<T> adapter, List<T> items , int selected){
        if (adapter == null){
            spinner.setAdapter(null);
            return;
        }

        adapter.setItems(items);

        if (spinner.getAdapter() != adapter) {
            spinner.setAdapter(adapter);
        }

        if (selected != -1){
            int itemShift = adapter.getItemsShift();
            if (spinner.getSelectedItemPosition() - itemShift != selected){
                spinner.setSelection(selected + itemShift);
            }
        } else {
            spinner.setSelection(0);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                adapter.getItem(i).onViewModelSelected();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @BindingAdapter({"bind:adapter", "bind:items"})
    public static <T extends SpinnerViewModel> void setSpinnerItems(Spinner spinner, ViewProvider<SpinnerViewModelAdapter<T>, Spinner > adapter, SelectableArrayBinder<T> items) {
        setSpinnerItems(spinner, adapter.provideFor(spinner), items);
    }

    @BindingAdapter({"bind:adapter", "bind:items"})
    public static <T extends SpinnerViewModel> void setSpinnerItems(Spinner spinner, SpinnerViewModelAdapter<T> adapter, SelectableArrayBinder<T> items){
        if (adapter == null){
            spinner.setAdapter(null);
            return;
        }

        if (items == null){
            adapter.setItems(null);
            spinner.setOnItemSelectedListener(null);
            return;
        }

        adapter.setItems(items.getItems());

        if (spinner.getAdapter() != adapter) {
            spinner.setAdapter(adapter);
        }

        int selected = items.getSelectedPosition();

        if (selected != -1){
            int itemShift = adapter.getItemsShift();
            if (spinner.getSelectedItemPosition() - itemShift != selected){
                spinner.setSelection(selected + itemShift);
            }
        } else {
            spinner.setSelection(0);
        }

        AdapterView.OnItemSelectedListener selectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                items.setSelected(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };

        spinner.setOnItemSelectedListener(selectedListener);

        items.clearPropertyChangedCallbacks();
        items.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (propertyId == BR.selectedPosition){
                    //noinspection unchecked
                    int selectedPosition = ((SelectableArrayBinder<T>) sender).getSelectedPosition();
                    spinner.setSelection(selectedPosition);
                }
            }
        });
    }

    @BindingAdapter({"bind:childView"})
    public static void setChildView(ViewGroup viewGroup, @Nullable View child){
        setChildView(viewGroup, child, 0);
    }

    @BindingAdapter({"bind:childView", "bind:childViewIndex"})
    public static void setChildView(ViewGroup viewGroup, @Nullable  View child, int index){
        for (int i = 0; i < viewGroup.getChildCount(); i++){
            View view = viewGroup.getChildAt(i);
            if (view.getTag(R.id.bind_view_child) != null){
                viewGroup.removeView(view);
                break;
            }
        }

        if (child != null) {
            child.setTag(R.id.bind_view_child, (byte) 1);
            viewGroup.addView(child, index);
        }
    }

    @BindingAdapter({"bind:childViewAdapter", "bind:items"})
    public static <VM> void setChildViewsAdapter(ViewGroup container, ViewsViewModelBindAdapter<VM> adapter, List<VM> list){
        adapter.setItems(list);
        adapter.onAttach(container);
        container.setTag(R.id.bind_view_child_adapter, adapter);
    }

    @BindingAdapter({"android:text", "bind:defaultText"})
    public static void setCaption(TextView view, String text, String defaultCaption){
        if (text != null){
            view.setText(text);
        } else {
            view.setText(defaultCaption);
        }
    }

    @BindingAdapter({"android:hint", "bind:defaultHint"})
    public static void setCaptionHint(TextView view, String caption, String defaultCaption){
        if (caption != null){
            view.setHint(caption);
        } else {
            view.setHint(defaultCaption);
        }
    }

    @BindingAdapter({"bind:layout_behavior"})
    public static void setLayoutBehavior(View view, String behaviorClassName){
        if (view.getParent() instanceof CoordinatorLayout){
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
            if (behaviorClassName != null) {
                try {
                    Object behavior = Class.forName(behaviorClassName).newInstance();
                    if (behavior != null && behavior instanceof CoordinatorLayout.Behavior) {
                        lp.setBehavior((CoordinatorLayout.Behavior) behavior);
                    }
                } catch (InstantiationException e) {
                    MyLog.majorException(TAG, e);
                } catch (IllegalAccessException e) {
                    MyLog.majorException(TAG, e);
                } catch (ClassNotFoundException e) {
                    MyLog.majorException(TAG, e);
                }
            } else {
                lp.setBehavior(null);
            }
        }
    }

    @BindingAdapter("bind:decoration")
    public static void setRecyclerDecoration(RecyclerView list, ViewProvider<RecyclerView.ItemDecoration, RecyclerView> provider){
        RecyclerView.ItemDecoration prev = (RecyclerView.ItemDecoration) list.getTag(R.id.bind_recycler_decoration);
        RecyclerView.ItemDecoration decoration = provider.provideFor(list);

        if (prev == decoration) return;

        if (prev != null){
            list.removeItemDecoration(prev);
        }

        if (decoration != null) {
            list.addItemDecoration(decoration);
        }
    }

    @BindingAdapter("bind:floatBinder")
    public static void bindRatingBarRate(@NonNull RatingBar ratingBar, @Nullable Binder<Float> binder){
        if (ratingBar.getTag(R.id.bind_target) != binder) {
            if (binder == null){
                ratingBar.setOnRatingBarChangeListener(null);
                ratingBar.setRating(0);
            } else {
                ratingBar.setTag(R.id.bind_target, binder);
                ratingBar.setOnRatingBarChangeListener((ratingBar1, v, b) -> binder.set(v));
            }
        }
        if (binder != null) {
            ratingBar.setRating(binder.get());
        }
    }
}
