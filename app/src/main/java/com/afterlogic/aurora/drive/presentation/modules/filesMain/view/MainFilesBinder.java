package com.afterlogic.aurora.drive.presentation.modules.filesMain.view;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters.ViewProvider;

import java.util.WeakHashMap;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFilesBinder {

    private static final WeakHashMap<ViewPager, MainFilesPagerAdapter> ADAPTERS = new WeakHashMap<>();

    public static ViewProvider<MainFilesPagerAdapter, ViewPager> adapter(FragmentManager fm){
        return view -> {
            MainFilesPagerAdapter adapter = ADAPTERS.get(view);
            if (adapter == null){
                adapter = new MainFilesPagerAdapter(fm);
                ADAPTERS.put(view, adapter);
            }
            return adapter;
        };
    }
}
