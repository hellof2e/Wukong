package com.hellobike.magiccube.demo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hellobike.magiccube.R
import com.hellobike.magiccube.demo.preview.PreviewActivity
import com.hellobike.magiccube.demo.preview.PreviewListActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        MagicCube.preloads(templateUrls.asList())
        initView()
    }

    private fun initView() {
        findViewById<View>(R.id.btn).setOnClickListener {
            startActivity(Intent(this, MagicListActivity::class.java))
//            startActivity(Intent(this, TestActivity::class.java))
        }

        findViewById<View>(R.id.preview).setOnClickListener {
            startActivity(Intent(this, PreviewActivity::class.java))
        }
        findViewById<View>(R.id.previewList).setOnClickListener {
            startActivity(Intent(this, PreviewListActivity::class.java))
        }
    }
}