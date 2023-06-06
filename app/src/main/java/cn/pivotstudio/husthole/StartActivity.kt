package cn.pivotstudio.husthole

import android.content.Intent
import android.os.Bundle
import cn.pivotstudio.moduleb.libbase.base.ui.activity.BaseActivity
import cn.pivotstudio.modulec.loginandregister.ui.activity.LARActivity

class StartActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rl_preparation)

        val intent = Intent(this, LARActivity::class.java)
        startActivity(intent)
        finish()
    }

}