package com.yuliyang.processor

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class AutoSkin(val value: Int)