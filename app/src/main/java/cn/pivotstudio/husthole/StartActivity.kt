package cn.pivotstudio.husthole

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import cn.pivotstudio.modulec.homescreen.ui.activity.LARActivity

class StartActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rl_preparation)

        val intent = Intent(this, LARActivity::class.java)
        startActivity(intent)
        finish()
    }

}