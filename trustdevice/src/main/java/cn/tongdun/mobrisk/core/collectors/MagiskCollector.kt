package cn.tongdun.mobrisk.core.collectors

import cn.tongdun.mobrisk.core.tools.EnvUtils
import cn.tongdun.mobrisk.core.tools.JNIHelper
import java.io.File

/**
 * @description:
 * @author: wuzuchang
 * @date: 2023/5/15
 */
interface MagiskInterface {
    fun detectMagisk(): Boolean
}

class MagiskCollector : MagiskInterface {

    override fun detectMagisk(): Boolean {
        return detectMagiskByFile() || detectMagiskByLastModified()
    }

    private fun detectMagiskByLastModified(): Boolean {
        return JNIHelper.call3()
    }

    private fun detectMagiskByFile(): Boolean {
        val paths = arrayOf(
            "/data/adb/magisk",
            "/data/adb/modules",
            "/data/adb/zygisk",
            "/data/adb/modules/zygisk_lsposed",
            "/data/adb/modules/riru_lsposed",
            "/data/adb/modules/riru_edxposed",
            "/system/bin/apd",
            "/data/apatch",
            "/data/adb/ap/bin/apd",
            "/data/adb/ap/bin/busybox",
            "/data/adb/ksu/bin/ksud",
            "/data/adb/ksu/bin/busybox",
            "/data/adb/ksu",
            "/data/adb/ksu_modules"
        )
        return EnvUtils.fileInEnv("magisk") || paths.any { File(it).exists() }
    }
}
