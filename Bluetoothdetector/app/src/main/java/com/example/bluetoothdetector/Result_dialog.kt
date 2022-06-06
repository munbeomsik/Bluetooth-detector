package com.example.bluetoothdetector

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.example.bluetoothdetector.R

class result_dialog
    constructor(context : Context) : Dialog(context){
        init{
            setCanceledOnTouchOutside(false)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setContentView(R.layout.result_dialog)
        }
    }
