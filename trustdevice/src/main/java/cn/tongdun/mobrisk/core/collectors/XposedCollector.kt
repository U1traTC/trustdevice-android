package cn.tongdun.mobrisk.core.collectors

import java.io.File

/**
 * @description:
 * @author: wuzuchang
 * @date: 2023/5/15
 */
interface XposedInterface {
    fun findXposed(): Boolean
}

class XposedCollector : XposedInterface {
    override fun findXposed(): Boolean {
        return try {
            findXposedByClass() || findXposedByMaps()
        } catch (ignore: Exception) {
            false
        }
    }

    private fun findXposedByClass(): Boolean {
        val xposedClasses = arrayOf(
            "de.robv.android.xposed.XposedBridge",
            "de.robv.android.xposed.IXposedHookLoadPackage",
            "de.robv.android.xposed.DexposedBridge",
            "org.lsposed.lspd.core.Main",
            "org.meowcat.edxposed.xposedbridge.EdXposedBridge"
        )

        return collectClassLoaders().any { classLoader ->
            xposedClasses.any { className ->
                runCatching { Class.forName(className, false, classLoader) }.isSuccess
            }
        }
    }

    private fun collectClassLoaders(): List<ClassLoader> {
        val result = mutableListOf<ClassLoader>()
        arrayOf(
            XposedCollector::class.java.classLoader,
            Thread.currentThread().contextClassLoader,
            ClassLoader.getSystemClassLoader()
        ).forEach {
            var current = it
            while (current != null) {
                if (!result.contains(current)) result.add(current)
                current = current.parent
            }
        }
        return result
    }

    private fun findXposedByMaps(): Boolean {
        return runCatching {
            val keywords = arrayOf("liblspd.so", "lsposed", "edxposed")
            File("/proc/self/maps").useLines { lines ->
                lines.take(3000).any { line -> keywords.any { line.contains(it, true) } }
            }
        }.getOrDefault(false)
    }
}
