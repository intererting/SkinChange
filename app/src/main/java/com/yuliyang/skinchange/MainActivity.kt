package com.yuliyang.skinchange

import android.content.Context
import android.os.Bundle
import android.support.v4.view.LayoutInflaterCompat
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), LayoutInflater.Factory2 {
    override fun onCreateView(parent: View?, name: String?, context: Context?, attrs: AttributeSet?): View {
        println("hook")
        return delegate.createView(parent, name, context!!, attrs!!)
    }

    override fun onCreateView(name: String?, context: Context?, attrs: AttributeSet?): View {
        return onCreateView(null, name, context, attrs)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val clazz = Class.forName("android.support.v7.app.AppCompatDelegateImpl")
        val filed = clazz.getDeclaredField("mAppCompatViewInflater")
        filed.isAccessible = true
        filed.set(delegate, this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageTest.setOnClickListener {
            SkinChangeUtil.notifyObserver()
        }

    }

}
