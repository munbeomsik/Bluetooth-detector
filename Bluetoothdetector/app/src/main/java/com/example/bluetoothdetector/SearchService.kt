package com.example.bluetoothdetector

import android.annotation.SuppressLint
import android.app.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class SearchService : Service() {
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var arrayDevices = ArrayList<BluetoothDevice>()
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
    override fun onCreate() {
        super.onCreate()
//        mThread!!.start()
        Log.d(TAG, "onCreate")
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        var back_found_filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(back_receiver, back_found_filter)
        back_found_filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(back_receiver, back_found_filter)
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
                arrayDevices.clear()
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
        unregisterReceiver(back_receiver)
    }

    companion object {
        private const val TAG = "MyServiceTag"

        // Notification
        private const val NOTI_ID = 2
    }

    private val back_receiver = object : BroadcastReceiver() {   //검색
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action.toString()
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    if (device!=null && !device.name.isNullOrEmpty()){  //디바이스 탐지됨
                        if(IsCorrect(device.name)){ //격리자 식별되면 알리고 검색 종료
                            Log.d("test log","격리자가 식별됨")
                            Notify()
                            mBluetoothAdapter!!.cancelDiscovery()
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun active_bluetooth(){ //블루투스활성화
        mBluetoothAdapter!!.enable()
    }

    fun IsCorrect(S: String): Boolean {
        val regex = Regex("^@[a-z]+\\d[a-z]+\\d[a-z]+\\d&%\\d+[a-z]+\\d[a-z]+")
        val correct = S.matches(regex)
        System.out.println(S)
        System.out.println(correct)
        return correct
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
    private fun Notify(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Identify"
            val descriptiontext = "This is Identify channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("Identify_channel_id", name, importance).apply { description = descriptiontext }

            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            var builder = NotificationCompat.Builder(this, "Identify_channel_id")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("식별됨!")
                .setContentText("격리자가 근처에서 식별되었습니다.")

            notificationManager.notify(1,builder.build())
        }
    }

}