package cn.pivotstudio.modulep.hole.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import cn.pivotstudio.husthole.moduleb.network.model.Reply
import cn.pivotstudio.moduleb.libbase.base.ui.fragment.BaseFragment
import cn.pivotstudio.modulep.hole.R
import cn.pivotstudio.modulep.hole.databinding.FragmentInnerReplyBinding
import cn.pivotstudio.modulep.hole.ui.adapter.InnerReplyAdapter
import cn.pivotstudio.modulep.hole.viewmodel.InnerReplyViewModel
import kotlinx.coroutines.flow.collectLatest

class InnerReplyFragment : BaseFragment() {

    private val args by navArgs<InnerReplyFragmentArgs>()
    private lateinit var binding: FragmentInnerReplyBinding
    private val viewModel: InnerReplyViewModel by viewModels {
        InnerReplyViewModelFactory(args.reply)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_inner_reply, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        val adapter = InnerReplyAdapter()
        binding.apply {
            rvReplies.adapter = adapter
        }

        viewModel.apply {
            lifecycleScope.launchWhenStarted {
                innerReplies.collectLatest {
                    adapter.submitList(it)
                }
            }

        }

    }

}

class InnerReplyViewModelFactory(private val reply: Reply) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InnerReplyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InnerReplyViewModel(reply) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }

}