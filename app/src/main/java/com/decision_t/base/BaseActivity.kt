package com.decision_t.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

/**
 * Created by Aiden Chang 2024/07/04
 */
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getBinding().root)
    }

    fun getBinding(): VB {
        if (!::binding.isInitialized) {
            binding = getInflatedBinding()
        }
        return binding
    }

    abstract fun getInflatedBinding(): VB
}
