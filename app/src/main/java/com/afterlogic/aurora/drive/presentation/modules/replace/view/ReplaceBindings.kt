package com.afterlogic.aurora.drive.presentation.modules.replace.view

import androidx.databinding.BindingAdapter
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import androidx.recyclerview.widget.RecyclerView

import com.afterlogic.aurora.drive.R
import com.afterlogic.aurora.drive.model.Storage
import com.afterlogic.aurora.drive.presentation.modules.replace.viewModel.ReplaceFileViewModel
import com.github.nitrico.lastadapter.LastAdapter

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

object ReplaceBindings {

    @JvmStatic
    @BindingAdapter("replace_filesAdapter")
    fun bindFilesAdapter(list: RecyclerView, items: List<ReplaceFileViewModel>) {
        LastAdapter(items, BR.vm)
                .map(ReplaceFileViewModel::class.java, R.layout.replace_item_list_file)
                .into(list)
    }

    @JvmStatic
    @BindingAdapter("replace_fileTypesAdapter")
    fun bindFileTypesAdapter(pager: ViewPager, storages: List<Storage>) {
        val fm = (pager.context as FragmentActivity).supportFragmentManager
        var adapter = pager.adapter as ReplaceFileTypesPagerAdapter?
        if (adapter == null) {
            adapter = ReplaceFileTypesPagerAdapter(fm)
            pager.adapter = adapter
        }
        adapter.setItems(storages)
    }
}
