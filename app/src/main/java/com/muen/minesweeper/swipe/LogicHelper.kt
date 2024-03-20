package com.muen.minesweeper.swipe

import com.muen.minesweeper.util.loge
import java.util.Random

object LogicHelper {
    fun randomMines(xlength:Int,ylength:Int,x:Int,y:Int,count:Int): ArrayList<Int> {
        var total = xlength*ylength-1
        if (count >= total || count <= 0){
            loge("invalid count")
            throw IllegalArgumentException("invalid count"+count)
        }

        val randoms = arrayListOf(getIndexByXY(xlength, ylength, x, y))

        while (randoms.size < count + 1){
            val next = Random().nextInt(xlength * ylength)
            if (!randoms.contains(next)){
                randoms.add(next)
            }
        }
        randoms.remove(getIndexByXY(xlength, ylength, x, y))
        return randoms
    }

    fun getIndexByXY(xlength:Int,ylength:Int,x:Int,y:Int):Int{
        return x + y * xlength;
    }

    fun getXYbyIndex(xlength:Int,ylength:Int,index:Int):IntArray{
        val x = index%ylength
        val y = index/ylength
        return intArrayOf(x,y)
    }

    /**
     * 返回一个index的周围的index集合
     */
    fun getAroundItems(xlength:Int,ylength:Int,index:Int):ArrayList<Int>{
        val list = ArrayList<Int>()
        val x = index%ylength
        val y = index/ylength
        for (i in x-1..x+1){
            for (j in y-1..y+1){
                if (i >= 0 && i < xlength && j >= 0 && j < ylength && (i != x || j != y)){
                    list.add(getIndexByXY(xlength, ylength, i, j))
                }
            }
        }
        return list
    }

    /**
     * 转换成三位数文字
     */
    fun int2String(int:Int):String{
        return when(int){
            in 0..9 -> "00" + int.toString()
            in 10..99 -> "0" + int.toString()
            else -> int.toString()
        }
    }
}