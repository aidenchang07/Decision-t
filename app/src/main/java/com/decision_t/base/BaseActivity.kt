package com.decision_t.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

/**
 * 頁面的基礎類別
 */
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    protected val binding: VB by lazy { getInflatedBinding() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    abstract fun getInflatedBinding(): VB
}
