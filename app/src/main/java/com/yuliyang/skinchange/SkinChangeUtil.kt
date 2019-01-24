package com.yuliyang.skinchange

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import dalvik.system.DexClassLoader
import java.lang.ref.WeakReference
import java.util.*

object SkinChangeUtil {

    private var observers = WeakHashMap<WeakReference<AutoChangeableA>, String>()

    fun addObserver(chanable: AutoChangeableA, resId: String) {
        observers.put(WeakReference(chanable), resId)
    }

    fun removeObserver(chanable: AutoChangeableA) {
        observers = observers.filter {
            return@filter it.key != chanable
        } as WeakHashMap<WeakReference<AutoChangeableA>, String>
    }

    fun notifyObserver() {
        for ((key, value) in observers) {
            key.get()?.apply {
                val context = MyApplication.provideInstance()
                val drawable = dynamicLoadApk(
                        "${context.cacheDir.absolutePath}/skin.apk",
                        getUninstallApkPkgName(context, "${context.cacheDir.absolutePath}/skin.apk")
                )
                drawable?.let {
                    this.changeWithDrawable(it)
                }
            }
        }
    }

    private fun dynamicLoadApk(pApkFilePath: String, pApkPacketName: String): Drawable? {
        val context = MyApplication.provideInstance()
        val file = context.getDir("dex", Context.MODE_PRIVATE)
        //第一个参数：是dex压缩文件的路径
        //第二个参数：是dex解压缩后存放的目录
        //第三个参数：是C/C++依赖的本地库文件目录,可以为null
        //第四个参数：是上一级的类加载器
        val classLoader = DexClassLoader(pApkFilePath, file.getAbsolutePath(), null, context.getClassLoader())
        try {
            val loadClazz = classLoader.loadClass("$pApkPacketName.R\$drawable")
            //插件中皮肤的名称是skin_one
            val skinOneField = loadClazz.getDeclaredField("test")
            skinOneField.isAccessible = true
            //反射获取skin_one的resousreId
            val resousreId = skinOneField.get(R.id::class.java) as Int
            //可以加载插件资源的Resources
            val mContext = context.createPackageContext(
                    getUninstallApkPkgName(context, "${context.cacheDir.absolutePath}/skin.apk")
                    , Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY
            )
            return mContext.resources.getDrawable(resousreId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getUninstallApkPkgName(context: Context, pApkFilePath: String): String {
        val pm = context.packageManager
        val pkgInfo = pm.getPackageArchiveInfo(pApkFilePath, PackageManager.GET_ACTIVITIES)
        if (pkgInfo != null) {
            val appInfo = pkgInfo.applicationInfo
            return appInfo.packageName
        }
        return ""
    }
}