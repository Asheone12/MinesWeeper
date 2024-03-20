package com.muen.minesweeper.swipe

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.GridLayout
import com.muen.minesweeper.util.loge
import com.muen.minesweeper.util.logi

class SwipeGridLayout  @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
    var xLength: Int = 9, var yLength: Int = 9, var mineNum: Int = 10//默认9行9列10雷
) : GridLayout(context, attrs, defStyleAttr) {


    var goods:Int//标记剩余未点开的非雷格子，用于判断游戏胜利

    init {
        goods = xLength*yLength-mineNum
    }
    var started = false//按下第一个点之后变为true

    fun fillAll() {
        removeAllViews()
        rowCount = xLength
        columnCount = yLength
        for (j in 0 until xLength) {
            for (i in 0 until yLength) {
                val one = SingleView(context, i, j, this)
//                Log.i(TAG, "fillAll: i:" + i)
                addView(one, width / columnCount, height / rowCount)
            }
        }
    }

    fun onChildClicked(v: SingleView, x: Int, y: Int) {
//        showToast("x:$x,y:$y")
        if (!started) {
            initCurGame(x, y)
            started = true
            listener?.onStart(mineNum)
        }
        // 初始化完游戏之后 每一次（包括第一次）按下之后的逻辑
        when (v.logicState) {
            SingleView.LOGICSTATE_BOMB -> {
                doEachSingle { singleView ->
                    if (singleView.logicState == SingleView.LOGICSTATE_BOMB) {
                        loge("x:${singleView.x},y:${singleView.y}:bomb")
                        singleView.viewState = SingleView.VIEWSTATE_THUNDER
                    }
                    singleView.enable = false
                }
                v.viewState = SingleView.VIEWSTATE_BOMB
                listener?.defeat()
            }
            in 1..8 -> {
                v.viewState = SingleView.VIEWSTATE_TIP
                goods--
            }
            0 -> {
                openAround(v, LogicHelper.getIndexByXY(xLength, yLength, x, y))
            }
        }

        v.enable = false//点过一次之后不能再点击
        doEachSingle { singleView -> singleView.updateInsideImg() }
        if (goods<=0){
            listener?.victory()
            doEachSingle { singleView -> singleView.enable = false }
        }
    }

    fun openAround(v: SingleView, index: Int) {
        v.enable = false
        v.viewState = SingleView.VIEWSTATE_TIP
        goods--
        for (around in LogicHelper.getAroundItems(xLength, yLength, index)) {
            val item = getChildByIndex(around)
            if (item.viewState == SingleView.VIEWSTATE_DEFAULT && item.logicState >= 0){
                if (item.around == 0){
                    logi("x,y:${LogicHelper.getXYbyIndex(xLength,yLength,index).asList()}")
                    openAround(getChildByIndex(around), around)
                }else{
                    item.enable = false
                    item.viewState = SingleView.VIEWSTATE_TIP
                    goods--
                }
            }
        }
    }

    fun initCurGame(firstX: Int, firstY: Int) {
        goods = xLength * yLength - mineNum
        //初始化所有格子
        initAllChildBefore()
        //随机初始化雷 避免点下的第一个
        val randomMines = LogicHelper.randomMines(xLength, yLength, firstX, firstY, mineNum)
        for (i: Int in randomMines) {
            val singleView = getChildByIndex(i)
            singleView.logicState = SingleView.LOGICSTATE_BOMB
            singleView.viewState = SingleView.VIEWSTATE_DEFAULT//test 显示雷
            singleView.updateInsideImg()
        }
        calMinesAround(randomMines)
    }

    fun calMinesAround(randomMines: ArrayList<Int>) {
        for (i: Int in randomMines) {
            for (j in LogicHelper.getAroundItems(xLength, yLength, i)) {
                logi("mine:$i,around:$j")
                val view = getChildByIndex(j)
                view.around++
                if (view.logicState >= 0)//如果不是雷的话
                    view.logicState = view.around
                view.updateInsideImg()
            }
        }


    }

    fun getChildByIndex(index: Int): SingleView {
        return getChildAt(index) as SingleView
    }

    fun initAllChildBefore() {
        doEachSingle { singleView: SingleView ->
            singleView.logicState = 0//初始化逻辑状态 Default
            singleView.viewState = SingleView.VIEWSTATE_DEFAULT//
            singleView.around = 0
            singleView.updateInsideImg()
        }
    }

    var flags = 0
    fun onChildLongClicked(v: SingleView, x: Int, y: Int, viewState: Int) {
//        showToast("long_x:$x,yLength:$y")
        when(viewState){
            SingleView.VIEWSTATE_FLAG ->flags++
            SingleView.VIEWSTATE_UNSURE -> flags--
        }
        listener?.onFlagsChange(flags,mineNum-flags)
    }

    fun getChildByXY(x: Int, y: Int): SingleView {
        return getChildAt(y * columnCount + x) as SingleView
    }

    fun doEachSingle(m: (singleView: SingleView) -> Unit) {
        for (i in 0..childCount - 1) {
            m(getChildByIndex(i))
        }
    }

    /**
     * 开始
     */
    fun reset(){
        doEachSingle {singleView ->
            flags = 0
            goods = xLength * yLength - mineNum
            started = false//要点击第一个之后才会变成true
            singleView.reset()
            singleView.updateInsideImg()
        }
    }


    var listener:GameStateChange? = null
    interface GameStateChange{
        fun onStart(rest:Int)
        fun onFlagsChange(flags:Int,rest:Int)
        fun victory()
        fun defeat()
    }

    /**
     * 画分割线
     * https://blog.csdn.net/dodod2012/article/details/50955434
     */
    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (childCount <= 0)
            return
        val localView1 = getChildAt(0)
        val column = columnCount
        val childCount = childCount
        val localPaint: Paint
        localPaint = Paint()
        localPaint.setStyle(Paint.Style.STROKE)
        localPaint.setColor(Color.BLACK)
        for (i in 0 until childCount) {
            val cellView = getChildAt(i)
//            if ((i + 1) % column == 0) {
//                canvas.drawLine(cellView.left.toFloat(), cellView.bottom.toFloat(),
//                        cellView.right.toFloat(), cellView.bottom.toFloat(), localPaint)
//            } else if (i + 1 > childCount - childCount % column) {
//                canvas.drawLine(cellView.right.toFloat(), cellView.top.toFloat(),
//                        cellView.right.toFloat(), cellView.bottom.toFloat(), localPaint)
//            } else {
            if (i < columnCount) {
                canvas.drawLine(cellView.left.toFloat(), cellView.top.toFloat(),
                    cellView.right.toFloat(), cellView.top.toFloat(), localPaint)
            }
            if (i % columnCount == 0) {
                canvas.drawLine(cellView.left.toFloat(), cellView.top.toFloat(),
                    cellView.left.toFloat(), cellView.bottom.toFloat(), localPaint)
            }
            canvas.drawLine(cellView.right.toFloat(), cellView.top.toFloat(),
                cellView.right.toFloat(), cellView.bottom.toFloat(), localPaint)
            canvas.drawLine(cellView.left.toFloat(), cellView.bottom.toFloat(),
                cellView.right.toFloat(), cellView.bottom.toFloat(), localPaint)
//            }
        }
        if (childCount % column != 0) {
            for (j in 0 until column - childCount % column) {
                val lastView = getChildAt(childCount - 1)
                canvas.drawLine((lastView.right + lastView.width * j).toFloat(), lastView.top.toFloat(),
                    (lastView.right + lastView.width * j).toFloat(), lastView.bottom.toFloat(), localPaint)
            }
        }
    }
}