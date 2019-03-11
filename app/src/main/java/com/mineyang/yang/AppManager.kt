import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import java.util.*

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 */
class AppManager private constructor() {

    /**
     * 添加Activity到堆栈
     */
    fun addActivity(activity: Activity) {
        if (activityStack == null) {
            throw RuntimeException("请在application中进行初始化")
        }
        activityStack?.add(activity)
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    fun currentActivity(): Activity {
        return activityStack?.lastElement() ?: throw RuntimeException("请在application中进行初始化")
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    fun finishActivity() {
        activityStack?.lastElement()?.let {
            finishActivity(it)
        }
    }

    /**
     * 结束指定的Activity
     */
    fun finishActivity(activity: Activity?) {
        var final = activity
        final?.let {
            activityStack?.remove(it)
            it.finish()
            final = null
        }
    }

    /**
     * 结束指定类名的Activity
     */
    fun finishActivity(cls: Class<*>) {
        activityStack?.filter { it.javaClass == cls }
                ?.forEach {
                    finishActivity(it)
                    return@forEach
                }
    }

    fun toFinish(clazz: Class<*>) {
        activityStack?.filter { clazz.name == it.javaClass.name }
                ?.forEach { activityStack?.remove(it) }
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        activityStack?.filter { it != null }
                ?.forEach { it.finish() }
        activityStack?.clear()
    }


    /**
     * 退出应用程序
     */
    @SuppressLint("MissingPermission")
    fun appExit(context: Context) {
        try {
            finishAllActivity()
            val activityMgr = context.getSystemService(
                    Context.ACTIVITY_SERVICE) as ActivityManager
            activityMgr.killBackgroundProcesses(context.packageName)
            System.exit(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun addActivityList(activity: Activity) {
        if (mStack == null) {
            throw RuntimeException("请在application中进行初始化")
        }
        mStack?.add(activity)
    }

    fun finishActivitList() {
        mStack?.filter { it != null }
                ?.forEach { it.finish() }
        mStack?.clear()
    }

    fun clean() {
        mStack?.clear()
    }

    companion object {
        private var activityStack: Stack<Activity>? = null
        private var mStack: Stack<Activity>? = null
        val instance: AppManager by lazy {
            mStack = Stack()
            activityStack = Stack()
            AppManager()
        }

        val stackNum: Int by lazy {
            activityStack?.size ?: throw RuntimeException("请在application中进行初始化")
        }
    }
}