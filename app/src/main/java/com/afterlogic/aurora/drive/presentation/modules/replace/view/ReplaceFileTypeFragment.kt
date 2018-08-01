package com.afterlogic.aurora.drive.presentation.modules.replace.view

import androidx.lifecycle.ViewModelProvider
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup

import com.afterlogic.aurora.drive.R
import com.afterlogic.aurora.drive.presentation.common.binding.utils.UnbindableObservable
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.BindingUtil
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.InjectableMVVMFragment
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.view.FileListArgs
import com.afterlogic.aurora.drive.presentation.modules.replace.viewModel.ReplaceFileListViewModel

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

class ReplaceFileTypeFragment : InjectableMVVMFragment<ReplaceFileListViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setArgs(FileListArgs(arguments))
    }

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): ViewDataBinding {
        return DataBindingUtil.inflate(inflater, R.layout.replace_files_fragment, container, false)
    }

    override fun createViewModel(provider: ViewModelProvider): ReplaceFileListViewModel {
        return provider.get(ReplaceFileListViewModel::class.java)
    }

    override fun bindStarted(vm: ReplaceFileListViewModel, bag: UnbindableObservable.Bag) {
        super.bindStarted(vm, bag)
        BindingUtil.bindProgressDialog(vm.progress, bag, context)
    }

    companion object {

        fun newInstance(type: String): ReplaceFileTypeFragment {

            val fragment = ReplaceFileTypeFragment()

            val args = FileListArgs()
            args.type = type

            fragment.arguments = args.bundle

            return fragment
        }
    }

}
