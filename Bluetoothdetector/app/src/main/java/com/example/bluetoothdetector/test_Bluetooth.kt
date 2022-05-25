package com.example.bluetoothdetector

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.bluetoothdetector.databinding.ActivityTestBluetoothBinding
import kotlinx.android.synthetic.main.activity_test_bluetooth.*


class test_Bluetooth : AppCompatActivity() {
    private val REQUEST_ENABLE_BT=1
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var mBinding: ActivityTestBluetoothBinding? = null   //뷰바인딩
    private val binding get() = mBinding!!
    private var mScanning: Boolean = false
    private var arrayDevices = ArrayList<BluetoothDevice>()
    private val handler = Handler()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityTestBluetoothBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //권한
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val intent: Intent
        intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        if (mBluetoothAdapter.isEnabled) {
            // 블루투스 관련 실행 진행
        } else {
            // 블루투스 활성화 하도록
            startActivityForResult(intent, 1)
        }
        //
        val bleOnOffBtn: ToggleButton = binding.bleOnOffBtn
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(bluetoothAdapter!=null){             //블루투스 on/off초기설정
            // Device doesn't support Bluetooth
            if(bluetoothAdapter?.isEnabled==false){
                bleOnOffBtn.isChecked = true
            } else{
                bleOnOffBtn.isChecked = false
            }
        }
        bleOnOffBtn.setOnCheckedChangeListener { _, isChecked ->
            bluetoothOnOff()
        }
        //페어링된 기기 검색

        var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)
        //val pairedDevices = mBluetoothAdapter.bondedDevices

        binding.searchBtn.setOnClickListener(){
            /*
            if (pairedDevices.size > 0) {
                System.out.println("검색결과")
                for (device in pairedDevices) {
                    Log.d(TAG, device.name)
                    Log.d(TAG, device.address)
                }
            }
            else{
                System.out.println("검색결과없음")
            }
         */
            //페어링된 기기 검색
            val permission_list = arrayOf<String>(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            ActivityCompat.requestPermissions(this, permission_list, 1)
            val bleEnableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(bleEnableIntent, REQUEST_ENABLE_BT)
            System.out.println(mBluetoothAdapter.startDiscovery())    //검색시작
            System.out.println("디바이스 개수는" + arrayDevices.size)

        }
        //페어링된 기기 검색 끝
//        System.out.println("안드로이드 id")
//        System.out.println(mBluetoothAdapter.name)   //찾았다
//        mBluetoothAdapter.setName("mydevice")
//        System.out.println(mBluetoothAdapter.name)
//        System.out.println(BluetoothAdapter.EXTRA_LOCAL_NAME)
        binding.nexttest.setOnClickListener(){
            val intent = Intent(this, test_Bluetooth2::class.java)
            startActivity(intent)
        }
        binding.getname.setOnClickListener(){
            Device_getname(mBluetoothAdapter)
        }
        binding.setname.setOnClickListener(){
            Device_setname(mBluetoothAdapter,"jeahunkim")
        }
    }
    @SuppressLint("MissingPermission")
    fun Device_getname(mBluetoothAdapter: BluetoothAdapter){
        System.out.println(mBluetoothAdapter.name)
    }
    @SuppressLint("MissingPermission")
    fun Device_setname(mBluetoothAdapter: BluetoothAdapter, name :String){
        mBluetoothAdapter.setName(name)
    }
    private val receiver = object : BroadcastReceiver() {

        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action!!
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    val deviceName = device.name
                    val deviceHardwareAddress = device.address // MAC address
                    System.out.println(deviceName)
                    if (!arrayDevices.contains(device) && device.name!=null) {
                        arrayDevices.add(device)
                        System.out.println("arraydevice : "+ device.name)
                    }
                    else{
                        System.out.println("왜 널이지")
                    }
                }
            }
            System.out.println("디바이스개수" + arrayDevices.size)
            for(i in arrayDevices){
                println(i.name)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver)
    }


    @SuppressLint("MissingPermission")
    fun bluetoothOnOff(){
        if (bluetoothAdapter == null) {     //블루투스를 지원하는지 확인
            // Device doesn't support Bluetooth
            Log.d("bluetoothAdapter","Device doesn't support Bluetooth")
        }else{
            if (bluetoothAdapter?.isEnabled == false) { // 블루투스 꺼져 있으면 블루투스 활성화
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                Log.d("bluetoothAdapter","블루투스활성화")
            } else{ // 블루투스 켜져있으면 블루투스 비활성화
                bluetoothAdapter?.disable()
                Log.d("bluetoothAdapter","블루투스비활성화")
            }
        }
    }
    //검색


}