package com.example.bluetoothdetector

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat

open class TestChangeName : AppCompatActivity() {
    fun texttoString(S: String): String {
        val result: String?
        val token = S.chunked(1)
        result = "@qn" + token[5] + "ut" + token[2] + "ai" + token[6] + "&%" + token[7] + token[1] + "rn" + token[2] +"ae"
        return result
    }

    fun IsCorrect(S: String): Int {
        val regex = Regex("^@[a-z]+\\d[a-z]+\\d[a-z]+\\d&%\\d+[a-z]+\\d[a-z]+")
        val correct = S.matches(regex)
        var isTrue = 0

        if(correct){
            isTrue = 1
        }else{
            isTrue = 0
        }
        return isTrue
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val testtext = "20200305"
////        val result1 = texttoString(testtext)//암호화 함수
////        if(IsCorrect(result1) == 1){//암호화 양식과 일치하면 1리턴 아니면 0리턴
////            Log.d("test log","격리자가 식별됨")
////        }
////        Notify()

    }
}