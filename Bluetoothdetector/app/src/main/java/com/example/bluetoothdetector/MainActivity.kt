package com.example.bluetoothdetector

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.bluetoothdetector.databinding.ActivityMainBinding
import com.example.bluetoothdetector.work.bluetooth_worker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private var mBinding: ActivityMainBinding? = null   //뷰바인딩
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBinding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val blue_WorkRequest: WorkRequest =
            PeriodicWorkRequestBuilder<bluetooth_worker>(15,TimeUnit.MINUTES).build()
        WorkManager.getInstance()?.enqueue(blue_WorkRequest)
        //WorkManager.getInstance()?.cancelAllWork()
        binding.buttonBt.setOnClickListener(){
            val intent = Intent(this, test_Bluetooth::class.java)
            startActivity(intent)
        }
        binding.buttonOCR.setOnClickListener(){
            val intent = Intent(this, test_Ocr::class.java)
            startActivity(intent)
        }
        binding.testRead.setOnClickListener(){
//            val intent = Intent(this, TestChangeName::class.java)
//            startActivity(intent)
//            val blue_WorkRequest: WorkRequest =
//                OneTimeWorkRequestBuilder<bluetooth_worker>().build()
//            WorkManager.getInstance()?.enqueue(blue_WorkRequest)


        }
    }




}