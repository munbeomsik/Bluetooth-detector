package com.example.bluetoothdetector

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.bluetoothdetector.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private var mBinding: ActivityMainBinding? = null   //뷰바인딩
    private val binding get() = mBinding!!
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var arrayDevices = ArrayList<BluetoothDevice>()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBinding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val blue_WorkRequest: WorkRequest =
            PeriodicWorkRequestBuilder<bluetooth_worker>(15,TimeUnit.MINUTES).build()
        WorkManager.getInstance()?.enqueue(blue_WorkRequest)
        //WorkManager.getInstance()?.cancelAllWork()
        System.out.println(filesDir.absolutePath)

        binding.buttonBt.setOnClickListener(){
//            val intent = Intent(this, test_Bluetooth::class.java)
//            startActivity(intent)
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (mBluetoothAdapter==null){
                System.out.println("블루투스가 사용불가합니다.")
//                onDestroy()
            }
            else{
                var found_filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
                registerReceiver(receiver, found_filter)
                found_filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
                registerReceiver(receiver, found_filter)
                active_bluetooth()                      //블루투스 활성화

                System.out.println("검색전 종료 "+mBluetoothAdapter!!.cancelDiscovery()) //검색전 검색종료
                arrayDevices.clear()
                System.out.println(mBluetoothAdapter!!.startDiscovery())    //검색시작
            }

        }
        binding.buttonOCR.setOnClickListener(){
            val intent = Intent(this, test_Ocr::class.java)
            startActivity(intent)
        }

    }

    private val receiver = object : BroadcastReceiver() {   //검색
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action.toString()
            System.out.println("현재 상태"+action)
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    if (device!=null){
                        if (!arrayDevices.contains(device) && device.name!=null) {
                            arrayDevices.add(device)
                        }
//                        if(IsCorrect(device.name) == 1){ //격리자 식별되면 알리고 검색 종료
//                            Log.d("test log","격리자가 식별됨")
//                            Notify()
//                            mBluetoothAdapter!!.cancelDiscovery()
//                        }
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    for(i in arrayDevices){
                        if(IsCorrect(i.name)){//암호화 양식과 일치하면 1리턴 아니면 0리턴
                            Log.d("test log","격리자가 식별됨")
                            Notify()
                            break
                        }
                    }
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
    @SuppressLint("MissingPermission")
    fun active_bluetooth(){ //블루투스활성화
        mBluetoothAdapter!!.enable()
    }
    @SuppressLint("MissingPermission")
    fun Device_getname(): String {
        return mBluetoothAdapter!!.name.toString()
    }
    @SuppressLint("MissingPermission")  //블루투스명 설정
    fun Device_setname(name :String){
        mBluetoothAdapter!!.setName(name)
    }
    fun texttoString(S: String): String {       //기기명 격리자로 암호화
        val result: String?
        val token = S.chunked(1)
        result = "@qn" + token[5] + "ut" + token[2] + "ai" + token[6] + "&%" + token[7] + token[1] + "rn" + token[2] +"ae"
        return result
    }
    fun IsCorrect(S: String): Boolean {
        val regex = Regex("^@[a-z]+\\d[a-z]+\\d[a-z]+\\d&%\\d+[a-z]+\\d[a-z]+")
        val correct = S.matches(regex)
        System.out.println(S)
        System.out.println(correct)
        return correct
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