package com.example.bluetoothdetector

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.*
import java.text.SimpleDateFormat

class bluetooth_worker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    var context: Context = appContext
    private var mBluetoothAdapter: BluetoothAdapter? = null

    override fun doWork(): Result {
        System.out.println("현재시간 "+SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis()))
        val currentdate=SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis())
        System.out.println("현재시간 "+currentdate)
        val reads=readtextfile("/data/data/com.example.bluetoothdetector/files/Ocrdatafile") //날짜읽기
        if (reads!="" && currentdate==reads) {
            System.out.println("격리해제")
            System.out.println(reads)
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (mBluetoothAdapter==null){
            System.out.println("블루투스가 사용불가합니다.")
            }
            else{
                active_bluetooth()
                Device_setname(readtextfile("/data/data/com.example.bluetoothdetector/files/name"))            //원래 이름으로 변경
                writetextfile("/data/data/com.example.bluetoothdetector/files","Ocrdatafile","")
                Notify()
            }
        }

        return Result.success()
    }

    private fun Notify(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Identify"
            val descriptiontext = "This is Identify channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("Identify_channel_id", name, importance).apply { description = descriptiontext }

            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            var builder = NotificationCompat.Builder(context, "Identify_channel_id")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("격리해제 알림!")
                .setContentText("격리가 해제되었습니다.")
            notificationManager.notify(1,builder.build())
        }
    }
    fun writetextfile(directory: String, filename: String, content: String){
        val dir = File(directory)
        if(!dir.exists()){
            dir.mkdirs()
        }
        val writer= FileWriter(directory+"/"+filename)
        val buffer= BufferedWriter(writer)
        buffer.write(content)
        System.out.println("날짜 저장")
        buffer.close()
    }
    fun readtextfile(fullpath:String) : String{
        val file= File(fullpath)
        if(!file.exists()){
            return ""
        }
        val reader = FileReader(file)
        val buffer = BufferedReader(reader)
        var temp = ""
        temp=buffer.readLine()
        if(temp==null)
            return ""
        buffer.close()
        return temp
    }
    @SuppressLint("MissingPermission")
    fun active_bluetooth() { //블루투스활성화
        mBluetoothAdapter!!.enable()
    }
    @SuppressLint("MissingPermission")
    fun Device_setname(name :String){
        mBluetoothAdapter!!.setName(name)
    }
}