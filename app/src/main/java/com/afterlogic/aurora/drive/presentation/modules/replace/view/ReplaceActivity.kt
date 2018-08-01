package com.afterlogic.aurora.drive.presentation.modules.replace.view

import androidx.lifecycle.ViewModelProvider
import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.SearchView
import android.view.Menu
import android.view.MenuItem

import com.afterlogic.aurora.drive.R
import com.afterlogic.aurora.drive.databinding.ReplaceActivityBinding
import com.afterlogic.aurora.drive.model.AuroraFile
import com.afterlogic.aurora.drive.presentation.common.binding.utils.UnbindableObservable
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.BindingUtil
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.InjectableMVVMActivity
import com.afterlogic.aurora.drive.presentation.modules.replace.viewModel.ReplaceViewModel

import javax.inject.Inject

import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector

/**
 * Created by aleksandrcikin on 28.06.17.
 * mail: mail@sunnydaydev.me
 */

class ReplaceActivity : InjectableMVVMActivity<ReplaceViewModel>(), HasSupportFragmentInjector {

    @Inject
    @Suppress("MemberVisibilityCanBePrivate")
    internal lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    @Suppress("MemberVisibilityCanBePrivate")
    internal lateinit var argsInitializer: ReplaceArgs.Initializer

    private val searchView = ObservableField<SearchView>()

    override fun createViewModel(provider: ViewModelProvider): ReplaceViewModel {
        return provider.get(ReplaceViewModel::class.java)
    }

    override fun onPrepareCreations() {
        super.onPrepareCreations()
        argsInitializer.args = ReplaceArgs(intent.getBundleExtra(KEY_ARGS))
    }

    override fun createBinding(): ViewDataBinding {
        val binding = DataBindingUtil.setContentView<ReplaceActivityBinding>(
                this, R.layout.replace_activity)

        setSupportActionBar(binding.toolbar)
        val ab = supportActionBar
        if (ab != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        return binding
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_replace, menu)

        val searchMenuItem = menu.findItem(R.id.action_search)
        searchView.set(searchMenuItem.actionView as SearchView)

        return true
    }

    override fun bindCreated(vm: ReplaceViewModel, bag: UnbindableObservable.Bag) {
        super.bindCreated(vm, bag)
        UnbindableObservable.bind(vm.title, bag) { field -> title = field.get() }
        UnbindableObservable.bind(vm.subtitle, bag) { field ->
            val ab = supportActionBar
            if (ab != null) {
                ab.subtitle = field.get()
            }
        }
    }

    override fun bindStarted(vm: ReplaceViewModel, bag: UnbindableObservable.Bag) {
        super.bindStarted(vm, bag)
        BindingUtil.bindProgressDialog(vm.progress, bag, this)
        BindingUtil.bindSearchView(searchView, vm.searchQuery, vm.showSearch, bag)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actoin_paste -> {
                viewModel.onPasteAction()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    companion object {

        private const val KEY_ARGS = "args"

        fun newReplaceIntent(context: Context, files: List<AuroraFile>): Intent {
            val args = ReplaceArgs()
            args.isCopyMode = false
            args.files = files
            return Intent(context, ReplaceActivity::class.java)
                    .putExtra(KEY_ARGS, args.bundle)
        }

        fun newCopyIntent(context: Context, files: List<AuroraFile>): Intent {
            val args = ReplaceArgs()
            args.isCopyMode = true
            args.files = files
            return Intent(context, ReplaceActivity::class.java)
                    .putExtra(KEY_ARGS, args.bundle)
        }

    }

}
