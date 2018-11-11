package com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters;

import androidx.databinding.BindingAdapter;
import androidx.databinding.Observable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.afterlogic.aurora.drive.BR;
import com.afterlogic.aurora.drive.presentation.common.binding.binder.SelectableArrayBinder;
import com.afterlogic.aurora.drive.presentation.common.binding.itemsAdapter.SpinnerViewModelAdapter;
import com.afterlogic.aurora.drive.presentation.common.modules.viewModel.SpinnerViewModel;

import java.util.List;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class SpinnerBinding {

    @BindingAdapter({"adapter", "items"})
    public static <T extends SpinnerViewModel> void setSpinnerItems(Spinner spinner, SpinnerViewModelAdapter<T> adapter, List<T> items){
        setSpinnerItems(spinner, adapter, items, -1);
    }

    @BindingAdapter({"adapter", "items", "selected"})
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

    @BindingAdapter({"adapter", "items"})
    public static <T extends SpinnerViewModel> void setSpinnerItems(Spinner spinner, ViewProvider<SpinnerViewModelAdapter<T>, Spinner > adapter, SelectableArrayBinder<T> items) {
        setSpinnerItems(spinner, adapter.provide(spinner), items);
    }

    @BindingAdapter({"adapter", "items"})
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
}
