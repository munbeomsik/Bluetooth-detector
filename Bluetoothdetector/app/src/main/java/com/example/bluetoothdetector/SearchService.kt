package com.example.bluetoothdetector

import android.annotation.SuppressLint
import android.app.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat

class SearchService : Service() {
    private var mBluetoothAdapter: BluetoothAdapter? = null
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        backNotify()
        mThread!!.start()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    private var mThread: Thread? = object : Thread("My Thread") {
        @SuppressLint("MissingPermission")
        override fun run() {
            super.run()
            var i=1
            while(true) {
                Log.d(TAG, "count : $i , name : ${mBluetoothAdapter!!.name}")
                active_bluetooth()                      //블루투스 활성화
                mBluetoothAdapter!!.cancelDiscovery() //검색전 검색종료
                mBluetoothAdapter!!.startDiscovery()
                try {
                    sleep(60000)
                    i++
                } catch (e: InterruptedException) {
                    currentThread().interrupt()
                    break
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        if (mThread != null) {
            mThread!!.interrupt()
            mThread = null
        }
    }

    companion object {
        private const val TAG = "MyServiceTag"

        // Notification
        private const val NOTI_ID = 2
    }

    @SuppressLint("MissingPermission")
    fun active_bluetooth(){ //블루투스활성화
        mBluetoothAdapter!!.enable()
    }

    @SuppressLint("NewApi")
    private fun backNotify(){
        val name = "background"
        val descriptiontext = "This is background channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("background_channel_id", name, importance).apply { description = descriptiontext }

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        var builder = NotificationCompat.Builder(this, "Identify_channel_id")
            .setSmallIcon(R.drawable.image3)
            .setContentTitle("백그라운드 작동 중")
            .setContentText("격리자 식별 중")

        startForeground(NOTI_ID,builder.build())
    }

}