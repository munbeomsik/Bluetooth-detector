package com.example.bluetoothdetector.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.bluetoothdetector.R
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.text.SimpleDateFormat

class bluetooth_worker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    var context: Context = appContext
    override fun doWork(): Result {
        Notify()
        System.out.println("현재시간 "+SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis()))
        val currentdate=SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis())
        System.out.println("현재시간 "+currentdate)
        //val reads=readtextfile(filesDir.absolutePath+"/datafile") //날짜읽기
        if (currentdate=="20220605") {
            System.out.println("격리해제")
            //readtextfile(filesDir.absolutePath+"/datafile")

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
                .setContentTitle("백그라운드!")
                .setContentText("백그라운드실행")

            notificationManager.notify(1,builder.build())
        }
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
}