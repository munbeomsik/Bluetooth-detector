package com.example.bluetoothdetector

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bluetoothdetector.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var mBinding: ActivityMainBinding? = null   //뷰바인딩
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBinding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonBt.setOnClickListener(){
            val intent = Intent(this, test_Bluetooth::class.java)
            startActivity(intent)
        }
        binding.buttonOCR.setOnClickListener(){
            val intent = Intent(this, test_Ocr::class.java)
            startActivity(intent)
        }
        binding.testRead.setOnClickListener(){
            val intent = Intent(this, TestChangeName::class.java)
            startActivity(intent)
        }
    }




}