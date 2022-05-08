package com.example.bluetoothdetector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bluetoothdetector.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var mBinding: ActivityMainBinding? = null   //뷰바인딩
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.BluetoothButton.setOnClickListener(){
            val intent = Intent(this, test_Bluetooth::class.java)
            startActivity(intent)
        }
        binding.OCRbutton.setOnClickListener(){
            val intent = Intent(this, test_Ocr::class.java)
            startActivity(intent)
        }


    }

}