package cn.pivotstudio.modulec.homescreen.ui.fragment.mine

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cn.pivotstudio.moduleb.database.MMKVUtil
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.custom_view.dialog.UpdateDialog
import cn.pivotstudio.modulec.homescreen.databinding.FragmentMineRecycleviewBinding
import cn.pivotstudio.modulec.homescreen.databinding.PpwDarkModeBinding
import cn.pivotstudio.modulec.homescreen.ui.activity.HomeScreenActivity
import cn.pivotstudio.modulec.homescreen.ui.adapter.MineOthersAdapter
import cn.pivotstudio.modulec.homescreen.viewmodel.HomeScreenActivityViewModel
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel.Companion.PERSONAL_SETTING
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel.Companion.SHIELD_SETTING
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel.Companion.UPDATE
import kotlinx.coroutines.launch
import java.io.File


/**
 *@classname ItemMineFragment
 * @description:
 * @date :2022/9/24 13:37
 * @version :1.0
 * @author
 */
class ItemMineFragment : Fragment() {
    private lateinit var binding: FragmentMineRecycleviewBinding
    private val viewModel: MineFragmentViewModel by viewModels()
    private var type: Int = 0
    private val mmkvUtil = MMKVUtil.getMMKV(context)

    override fun onCreate(savedInstanceState: Bundle?) {
        if ((activity as HomeScreenActivity).supportActionBar != null) {
            (activity as HomeScreenActivity).supportActionBar!!.hide()
        }
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getInt("optionType")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMineRecycleviewBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.myImg.setOnClickListener {
            this.findNavController().popBackStack()
        }
        binding.myTitle.text = getString(viewModel.myNameList.value!![type])
        if (type == PERSONAL_SETTING || type == SHIELD_SETTING || type == UPDATE) {
            val adapter = MineOthersAdapter()
            adapter.setOnItemClickListener(object : MineOthersAdapter.OnItemClickListener {
                override fun onClick(view: View, position: Int, nameID: Int) {
                    when (nameID) {
                        R.string.check_update -> {
                            val sharedViewModel =
                                ViewModelProvider(requireActivity())[HomeScreenActivityViewModel::class.java]
                            sharedViewModel.versionInfo.value?.let {
                                if (getVersionCode() < it.versionId.toLong() || viewModel.getVersionName() != it.versionName) {
                                    lifecycleScope.launch {
                                        UpdateDialog(requireContext(), it) {
                                            getInstallIntent(it.downloadUrl.let { str ->
                                                str.substring(
                                                    str.lastIndexOf("/") + 1,
                                                    str.length
                                                )
                                            })
                                        }.show()
                                    }
                                } else {
                                    Toast.makeText(context, "已经是最新版本", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        R.string.dark_mode -> {
                            getPopUpWindows()
                        }
                        else -> {
                            val action =
                                ItemMineFragmentDirections.actionItemMineFragmentToItemDetailFragment2(
                                    nameID
                                )
                            this@ItemMineFragment.findNavController().navigate(action)
                        }
                    }
                }
            })
            when (type) {
                PERSONAL_SETTING -> viewModel.mySettingList.observe(viewLifecycleOwner) { list ->
                    adapter.submitList(list)
                }
                SHIELD_SETTING -> viewModel.shieldList.observe(viewLifecycleOwner) { list ->
                    adapter.submitList(list)
                }
                UPDATE -> viewModel.updateList.observe(viewLifecycleOwner) { list ->
                    adapter.submitList(list)
                }
            }
            binding.rvTitle.apply {
                this.adapter = adapter
            }
        } else {
            binding.rvTitle.visibility = GONE
        }
    }

    override fun onResume() {
        if ((activity as HomeScreenActivity).supportActionBar != null) {
            (activity as HomeScreenActivity).supportActionBar!!.hide()
        }
        super.onResume()
    }

    private fun getVersionCode(): Long {
        val manager = requireContext().packageManager
        var code = 0L
        try {
            val info =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    manager.getPackageInfo(
                        requireActivity().packageName,
                        PackageManager.PackageInfoFlags.of(0)
                    )
                } else {
                    manager.getPackageInfo(
                        requireActivity().packageName,
                        0
                    )
                }
            code = info.longVersionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return code
    }

    /**
     * 返回安装页面的intent
     * @param fileName 要安装apk的文件名
     * @return
     */
    private fun getInstallIntent(fileName: String): Intent? {
        try {
            val file = File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath,
                fileName
            )
            if (!file.exists()) {
                return null
            }
            val uri =
                FileProvider.getUriForFile(
                    requireContext(),
                    "${requireActivity().packageName}.fileprovider",
                    file
                )
            return Intent(Intent.ACTION_VIEW).setDataAndType(
                uri,
                "application/vnd.android.package-archive"
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)

        } catch (e: NullPointerException) {
            Log.e(cn.pivotstudio.modulec.homescreen.ui.adapter.TAG, "file is not exists!")
            e.printStackTrace()
        } catch (e: java.lang.IllegalArgumentException) {
            Log.e(cn.pivotstudio.modulec.homescreen.ui.adapter.TAG, "file can't be accessed!")
            e.printStackTrace()
        }
        return null
    }


    private fun cancelDarkBackGround() {
        val lp = this.requireActivity().window.attributes
        lp.alpha = 1f // 0.0~1.0
        this.requireActivity().window.attributes = lp
    }   //取消暗背景

    private fun getPopUpWindows() {
        val darkBind: PpwDarkModeBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.ppw_dark_mode,
            null,
            false
        )
        val ppwDark = PopupWindow(darkBind.root)
        val window = this.requireActivity().window

        ppwDark.isOutsideTouchable = true  //点击卡片外部退出
        ppwDark.isFocusable = true     //按返回键允许退出
        ppwDark.width = ViewGroup.LayoutParams.MATCH_PARENT
        ppwDark.height = ViewGroup.LayoutParams.WRAP_CONTENT
        ppwDark.animationStyle = R.style.Page2Anim

        window.attributes.alpha = 0.6f
        window.setWindowAnimations(R.style.darkScreenAnim)
        ppwDark.showAtLocation(
            window.decorView, Gravity.BOTTOM,
            0, 0
        )
        ppwDark.setOnDismissListener {
            cancelDarkBackGround()
        }
        darkBind.apply {
            this.mode = mmkvUtil.getInt(Constant.IS_DARK_MODE)
            rlDark.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (mode != DARK) {
                        ppwDark.dismiss()
                        mmkvUtil.put(Constant.IS_DARK_MODE, DARK)
                        mode = DARK
                        setDefaultNightMode(MODE_NIGHT_YES)
                    }
                } else {
                    Toast.makeText(context, "非常抱歉，您手机不支持强制深色模式555~", Toast.LENGTH_SHORT).show()
                }
            }
            rlLight.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (mode != LIGHT) {
                        ppwDark.dismiss()
                        mmkvUtil.put(Constant.IS_DARK_MODE, LIGHT)
                        mode = LIGHT
                        setDefaultNightMode(MODE_NIGHT_NO)
                    }
                } else {
                    Toast.makeText(context, "非常抱歉，您手机不支持强制浅色模式555~", Toast.LENGTH_SHORT).show()
                }
            }
            rlFollowSystem.setOnClickListener {
                if (mode != FOLLOW_SYSTEM) {
                    ppwDark.dismiss()
                    mmkvUtil.put(Constant.IS_DARK_MODE, FOLLOW_SYSTEM)
                    mode = FOLLOW_SYSTEM
                    setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }
        }
    }

    companion object {
        const val TAG = "ItemMineFragment"
        const val DARK = 0
        const val LIGHT = 1
        const val FOLLOW_SYSTEM = 2
    }
}