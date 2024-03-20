package com.muen.minesweeper.swipe

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import com.muen.minesweeper.R

@SuppressLint("AppCompatCustomView")
class SingleView (context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
    : ImageView(context, attrs, defStyleAttr), View.OnClickListener, View.OnLongClickListener {

    companion object {
        //常量定义
        const val VIEWSTATE_DEFAULT = 0
        const val VIEWSTATE_FLAG = 1//标记这里是雷
        const val VIEWSTATE_UNSURE = 2//？ 不确定
        const val VIEWSTATE_TIP = 3//显示周围雷数 需要配合around参数
        const val VIEWSTATE_THUNDER = 4//游戏结束的时候显示这里的确是雷
        const val VIEWSTATE_BOMB = 5//游戏失败时最后点到的雷

        const val LOGICSTATE_NOT_INITED = -1
        const val LOGICSTATE_BOMB = -2
    }

    //    val mPaint = Paint()
    var around = 0//周围有几颗雷
    set(value) {
        if (value > 8)
            throw IllegalArgumentException("around should not be this value:$value")
        else
            field = value
    }
    var viewState = 0
    var logicState = LOGICSTATE_NOT_INITED//-1 未初始化 0~8周围地雷数 -2雷 只有这几种可能
    var enable = true//点击过以后不能再次点击 或者也有可能设置不可点击就行了？
    var x = -1
    var y = -1
    lateinit var mGrid: SwipeGridLayout


    init {
        isClickable = true
        setOnClickListener(this)
        setOnLongClickListener(this)
        setBackgroundResource(R.drawable.sel_bg_single)
        scaleType = ScaleType.CENTER_CROP

        updateInsideImg()
        if (attrs != null) {
            //nothing to do
        }
    }

    constructor(context: Context?, x: Int, y: Int, mGridLayout: SwipeGridLayout) : this(context) {
        this.x = x
        this.y = y
        this.mGrid = mGridLayout
    }

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?) : this(context, null)

    fun updateInsideImg(): Unit {
        setImageResource(
            when (viewState) {
                VIEWSTATE_DEFAULT -> R.drawable.s
                VIEWSTATE_BOMB -> R.drawable.bomb0
                VIEWSTATE_THUNDER -> R.drawable.bomb
                VIEWSTATE_FLAG -> R.drawable.flag
                VIEWSTATE_UNSURE -> R.drawable.flag2
                VIEWSTATE_TIP -> when (around) {
                    0 -> R.drawable.ar0
                    1 -> R.drawable.ar1
                    2 -> R.drawable.ar2
                    3 -> R.drawable.ar3
                    4 -> R.drawable.ar4
                    5 -> R.drawable.ar5
                    6 -> R.drawable.ar6
                    7 -> R.drawable.ar7
                    8 -> R.drawable.ar8
                    else -> throw IllegalArgumentException("illegal around")
                }
                else -> throw IllegalArgumentException("illegal viewState")
            }
        )
        invalidate()//理论上应该都是在主线程调用的该方法
    }

    override fun onClick(v: View?) {
        if (!enable)
            return
        if (viewState == VIEWSTATE_FLAG)//fix 插旗以后应该不再能点开了，防误触
            return

        mGrid.onChildClicked(this,x, y)
    }

    override fun onLongClick(v: View?): Boolean {
        if (!enable)
            return true
        if (!mGrid.started)
            return true

        viewState = when(viewState){
            VIEWSTATE_DEFAULT -> VIEWSTATE_FLAG
            VIEWSTATE_FLAG -> VIEWSTATE_UNSURE
            VIEWSTATE_UNSURE -> VIEWSTATE_DEFAULT
            else -> viewState
        }
        updateInsideImg()

        mGrid.onChildLongClicked(this,x,y,viewState)
        return true
    }

    fun reset() {
        viewState = VIEWSTATE_DEFAULT
        logicState = LOGICSTATE_NOT_INITED
        around = 0
        enable = true
    }
}