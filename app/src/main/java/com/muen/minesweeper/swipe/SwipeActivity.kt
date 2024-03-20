package com.muen.minesweeper.swipe

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding
import com.muen.minesweeper.R
import com.muen.minesweeper.databinding.ActivitySwipeBinding
import com.muen.minesweeper.util.BaseActivity
import java.util.Timer
import java.util.TimerTask

class SwipeActivity : BaseActivity<ActivitySwipeBinding>() {
    val timer: Timer = Timer()
    var task: TimerTask? = null

    override fun onCreateViewBinding(): ActivitySwipeBinding {
        return ActivitySwipeBinding.inflate(layoutInflater)
    }

    override fun initView() {
        super.initView()
        viewBinding.gridSwipe.postDelayed({
            viewBinding.gridSwipe.fillAll()
        },120)

        viewBinding.btnStart.setOnClickListener {
            viewBinding.gridSwipe.reset()

            viewBinding.tvRest.setText(LogicHelper.int2String(viewBinding.gridSwipe.mineNum))
            viewBinding.imgEndgame.visibility = View.GONE
            viewBinding.tvTime.setText("000")
            task?.cancel()
        }
        viewBinding.imgEndgame.setOnClickListener { viewBinding.imgEndgame.visibility = View.GONE }
        viewBinding.tvRest.setText(LogicHelper.int2String(viewBinding.gridSwipe.mineNum))
        //val lcdFont = Typeface.createFromAsset(assets, "fonts/lcd2mono.ttf")
        //viewBinding.tvRest.setTypeface(lcdFont)
        //viewBinding.tvTime.setTypeface(lcdFont)
        viewBinding.gridSwipe.listener = object :SwipeGridLayout.GameStateChange{
            override fun onStart(rest:Int) {
//                showToast("开始")
                viewBinding.tvRest.setText(LogicHelper.int2String(rest))
                task?.cancel()
                task = object :TimerTask(){
                    override fun run() {
                        runOnUiThread {
                            val t = viewBinding.tvTime.text.toString().toInt()+1
                            viewBinding.tvTime.setText(LogicHelper.int2String(t))
                        }
                    }
                }
                timer.scheduleAtFixedRate(task,0,1000)
            }

            override fun onFlagsChange(flags: Int, rest: Int) {
                viewBinding.tvRest.setText(LogicHelper.int2String(rest))
            }

            override fun victory() {
//                showToast("胜利")
                viewBinding.imgEndgame.setImageResource(R.drawable.victory)
                viewBinding.imgEndgame.visibility = View.VISIBLE
                task?.cancel()
//                tv_time.setText("000")
            }

            override fun defeat() {
//                showToast("失败")
                viewBinding.imgEndgame.setImageResource(R.drawable.defeat)
                viewBinding.imgEndgame.visibility = View.VISIBLE
                task?.cancel()
//                tv_time.setText("000")
            }

        }
    }


}