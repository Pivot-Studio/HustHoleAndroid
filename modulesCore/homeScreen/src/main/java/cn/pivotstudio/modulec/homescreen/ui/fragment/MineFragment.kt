package cn.pivotstudio.modulec.homescreen.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.databinding.BindingAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.moduleb.rebase.database.MMKVUtil
import cn.pivotstudio.moduleb.rebase.lib.base.ui.fragment.BaseFragment
import cn.pivotstudio.moduleb.rebase.lib.constant.Constant
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.ui.custom_view.PicGenerator
import cn.pivotstudio.modulec.homescreen.databinding.FragmentMineBinding
import cn.pivotstudio.modulec.homescreen.ui.adapter.MineOthersAdapter
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel
import cn.pivotstudio.modulec.homescreen.viewmodel.MyHoleFragmentViewModel
import com.alibaba.android.arouter.launcher.ARouter
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.datamatrix.encoder.SymbolShapeHint
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * 设置加入天数的显示颜色
 */
@BindingAdapter("joinDay")
fun bindDay(
    view: TextView,
    text: String
) {
    val ss = SpannableString(text)
    ss.setSpan(
        ForegroundColorSpan(view.resources.getColor(R.color.star_dust, null)),
        7,
        text.lastIndexOf("天"),
        Spanned.SPAN_INCLUSIVE_EXCLUSIVE
    )
    view.text = ss
}

class MineFragment : BaseFragment(), PicGenerator {
    private lateinit var binding: FragmentMineBinding
    private val viewModel: MineFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMineBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.tip.observe(viewLifecycleOwner) {
            it?.let {
                showMsg(it)
                viewModel.doneShowingTip()
            }
        }

        binding.apply {
            myHole.setOnClickListener {
                this@MineFragment.viewModel.currentProfile.value = MyHoleFragmentViewModel.GET_HOLE
                navigate()
            }
            myStar.setOnClickListener {
                this@MineFragment.viewModel.currentProfile.value = MyHoleFragmentViewModel.GET_FOLLOW
                navigate()
            }
            myReply.setOnClickListener {
                this@MineFragment.viewModel.currentProfile.value = MyHoleFragmentViewModel.GET_REPLY
                navigate()
            }
        }

        val adapter = MineOthersAdapter()
        viewModel.myNameList.observe(viewLifecycleOwner) { list -> adapter.submitList(list) }
        adapter.setOnItemClickListener(object :MineOthersAdapter.OnItemClickListener {
            override fun onClick(view: View, position: Int, nameID: Int) {
                if (viewModel.optSwitch[position] == true) {
                    when (position) {
                        MineFragmentViewModel.PERSONAL_SETTING, MineFragmentViewModel.SHIELD_SETTING, MineFragmentViewModel.UPDATE -> {
                            val action =
                                MineFragmentDirections.actionMineFragmentToItemMineFragment(
                                    position
                                )
                            view.findNavController().navigate(action)
                        }
                        MineFragmentViewModel.SHARE -> {
                            initShareCardView()
                        }
                        MineFragmentViewModel.LOGOUT -> {
                            initLogOutDialog()
                        }
                        else -> {
                            val action =
                                MineFragmentDirections.actionMineFragmentToItemDetailFragment2(position)
                            this@MineFragment.findNavController().navigate(action)
                        }
                    }
                } else {
                    Toast.makeText(context,"功能正在维护！", Toast.LENGTH_SHORT).show()
                }
            }
        })
        binding.rvOptions.apply {
            this.adapter = adapter
            addItemDecoration(SpaceItemDecoration(0, 2))
        }
    }

    override fun onResume() {
        viewModel.getMineData()
        super.onResume()
    }

    private fun navigate() {
        val action = MineFragmentDirections.actionMineFragmentToHoleFollowReplyFragment(
            viewModel.currentProfile.value!!
        )
        this@MineFragment.findNavController().navigate(action)
    }

    private fun cancelDarkBackGround() {
        val lp = this.requireActivity().window.attributes
        lp.alpha = 1f // 0.0~1.0
        this.requireActivity().window.attributes = lp
    }   //取消暗背景

    private fun initShareCardView() {
        AsyncLayoutInflater(context).inflate(R.layout.app_share_card,null
        ) { view, _, _ ->
            val ppwShare = PopupWindow(view)
            val shareCardView = View.inflate(context, R.layout.ppw_share, null)
            val shareCard = shareCardView.findViewById<LinearLayout>(R.id.share_card)
            val cancel = shareCardView.findViewById<TextView>(R.id.share_cancel_button)
            val mainContent = view.findViewById<ScrollView>(R.id.main_content)
            val code =  view.findViewById<ImageView>(R.id.QR_code)
            val ppwFunc = PopupWindow(shareCardView)

            val window = this.requireActivity().window
            with(ppwShare) {
                isOutsideTouchable = true  //点击卡片外部退出
                isFocusable = true     //按返回键允许退出
                width = ViewGroup.LayoutParams.WRAP_CONTENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                animationStyle = R.style.Page2Anim
            }
            with(ppwFunc) {
                isOutsideTouchable = false  //点击卡片外部退出
                isFocusable = false     //按返回键允许退出
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                animationStyle = R.style.Page2Anim
            }

            //减弱背景亮度
            window.attributes.alpha = 0.6f
            window.setWindowAnimations(R.style.darkScreenAnim)
            ppwShare.showAtLocation(
                window.decorView, Gravity.TOP,
                0, 0
            )
            ppwFunc.showAtLocation(
                window.decorView, Gravity.BOTTOM,
                0, 0
            )    //设置显示位置
            var bitmap: Bitmap? = null
            viewModel.viewModelScope.launch {
                bitmap = generateQRCode("https://husthole.com/#/download")
                code.setImageBitmap(bitmap)
            }
            ppwShare.setOnDismissListener {
                ppwFunc.dismiss()
                cancelDarkBackGround()
            }
            cancel.setOnClickListener {
                ppwShare.dismiss()
            }
            shareCard.setOnClickListener {
                viewModel.viewModelScope.launch {
                    val now = Date()
                    val ft2 = SimpleDateFormat("yyyyMMddhhmmss", Locale.CHINA)
                    generate(mainContent)?.let {
                        save(it, "AppShare" + ft2.format(now))
                        bitmap?.recycle()
                        ppwShare.dismiss()
                    }
                }
                ppwShare.dismiss()
            }
        }
        //val shareCardView = View.inflate(context, R.layout.ppw_share, null)
    }

    @SuppressLint("InflateParams")
    private fun initLogOutDialog() {
        val dialog = Dialog(requireContext())
        val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_logout, null)
        dialog.setContentView(dialogView)
        val btnCancel = dialogView.findViewById<Button>(R.id.cancel)
        val btnLogout = dialogView.findViewById<Button>(R.id.logout)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnLogout.setOnClickListener {
            dialog.dismiss()
            val mmkvUtil = MMKVUtil.getMMKV(context)
            mmkvUtil.put(Constant.USER_TOKEN, "")
            mmkvUtil.put(Constant.USER_TOKEN_V2, "")
            mmkvUtil.put(Constant.IS_LOGIN, false)
            ARouter.getInstance().build("/loginAndRegister/LARActivity").navigation()
            this.requireActivity().finish()
        }
        dialog.show()
    }
    class SpaceItemDecoration(
        private val leftRight: Int,
        private val topBottom: Int
    ) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val layoutManager: LinearLayoutManager = parent.layoutManager as LinearLayoutManager
            if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
                //最后一项需要 bottom
                if (parent.getChildAdapterPosition(view) == layoutManager.itemCount - 1) {
                    outRect.bottom = topBottom
                }
                if(parent.getChildAdapterPosition(view) == 4 || parent.getChildAdapterPosition(view) == 7) {
                    outRect.top = topBottom + 10
                } else {
                    outRect.top = topBottom
                }
                outRect.left = leftRight
                outRect.right = leftRight
            } else {
                //最后一项需要right
                if (parent.getChildAdapterPosition(view) == layoutManager.itemCount - 1) {
                    outRect.right = leftRight
                }
                outRect.top = topBottom
                outRect.left = leftRight
                outRect.bottom = topBottom
            }
        }
    }

    override suspend fun generate(view: View): Bitmap? {
        val bitmap: Bitmap?
        try {
            bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        }catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "图片生成失败！", Toast.LENGTH_SHORT).show()
            return null
        }
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    override suspend fun save(photo: Bitmap, fileName: String) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val imageSaveFilePath = Environment.DIRECTORY_DCIM + File.separator + "hustHole"
            val file = File(imageSaveFilePath)
            if(!file.exists()) {
                file.mkdirs()
            }
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            contentValues.put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis())
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, imageSaveFilePath)
            var uri: Uri? = null
            var fos: OutputStream? = null
            val localContentResolver = context.contentResolver
            try {
                uri = localContentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = uri?.let { localContentResolver.openOutputStream(it) }

                photo.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos?.flush()
                fos?.close()
                Toast.makeText(context, "保存成功！", Toast.LENGTH_SHORT).show()
            }catch (e: IOException) {
                e.printStackTrace()
                uri?.let {
                    localContentResolver.delete(it, null, null)
                }
                Toast.makeText(context, "文件保存失败！", Toast.LENGTH_SHORT).show()
            }finally {
                photo.recycle()
                try {
                    fos?.let {
                        fos.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }else {
            val path = Environment.getExternalStorageDirectory().absolutePath + File.separator + "hustHole"
            // 创建文件夹
            val file = File(path, fileName)
            try {
                val fos = FileOutputStream(file)
                // 通过io流的方式来压缩保存图片
                photo.compress(Bitmap.CompressFormat.JPEG, 80, fos)
                fos.flush()
                fos.close()
                // 保存图片后发送广播通知更新数据库
                val uri = Uri.fromFile(file)
                context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
                Toast.makeText(context, "保存成功！", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(context, "文件保存失败！", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override suspend fun generateQRCode(url: String): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val hints = Hashtable<EncodeHintType, Any>()
            hints[EncodeHintType.CHARACTER_SET] = "utf-8"
            hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
            hints[EncodeHintType.DATA_MATRIX_SHAPE] = SymbolShapeHint.FORCE_SQUARE
            val matrix = QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, 1080, 1080, hints)
            val width = matrix.width
            val height = matrix.height
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    if (matrix.get(x, y)) {
                        pixels[y * width + x] = Color.BLACK
                    } else {
                        pixels[y * width + x] = Color.WHITE
                    }
                }
            }
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap?.recycle()
            return null
        }
    }
}