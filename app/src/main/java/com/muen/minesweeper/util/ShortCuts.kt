package com.muen.minesweeper.util

import android.util.Log
import android.widget.Toast
import com.muen.minesweeper.AppApplication

const val TAG = "MiniGames"

fun showToast(string: String){
    Toast.makeText(AppApplication.context,string,Toast.LENGTH_SHORT).show()
}

fun showToast(resId:Int){
    Toast.makeText(AppApplication.context,resId,Toast.LENGTH_SHORT).show()
}

fun logi(string: String){
    Log.i(TAG,string)
}

fun loge(string: String){
    Log.e(TAG,string)
}