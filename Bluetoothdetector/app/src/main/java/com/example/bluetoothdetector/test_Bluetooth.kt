package com.example.bluetoothdetector

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
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
    private var mBluetoothAdapter: BluetoothAdapter? = null
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
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter==null){
            System.out.println("블루투스가 사용불가합니다.")
            onDestroy()
        }
        var found_filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, found_filter)
        var state_filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(BlueCheck, state_filter)

        active_bluetooth()      //블루투스 활성화

        val bleOnOffBtn: ToggleButton = binding.bleOnOffBtn //블루투스 on/off초기설정
        // Device doesn't support Bluetooth
        if(check_bluetooth()==false){
            bleOnOffBtn.isChecked = true
        } else{
            bleOnOffBtn.isChecked = false
        }

        bleOnOffBtn.setOnCheckedChangeListener { _, isChecked ->
            bluetoothOnOff()
        }
        //페어링된 기기 검색
        binding.searchBtn.setOnClickListener(){
            System.out.println("검색전 종료 "+mBluetoothAdapter!!.cancelDiscovery())
            System.out.println(mBluetoothAdapter!!.startDiscovery())    //검색시작
            System.out.println("디바이스 개수는" + arrayDevices.size)

        }
        binding.nexttest.setOnClickListener(){
            val intent = Intent(this, test_Bluetooth2::class.java)
            startActivity(intent)
        }
        binding.getname.setOnClickListener(){
            Device_getname(mBluetoothAdapter!!)
        }
        binding.setname.setOnClickListener(){
            Device_setname(mBluetoothAdapter!!,"jeahunkim")
        }
    }

    fun check_bluetooth(): Boolean? {   //블루투스on/off상태 체크

        System.out.println(mBluetoothAdapter?.isEnabled)
        return mBluetoothAdapter?.isEnabled
    }
    @SuppressLint("MissingPermission")
    fun active_bluetooth(){ //블루투스활성화
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
    }
    @SuppressLint("MissingPermission")  //블루투스명 가져오기
    fun Device_getname(mBluetoothAdapter: BluetoothAdapter){
        System.out.println(mBluetoothAdapter.name)
    }
    @SuppressLint("MissingPermission")  //블루투스명 설정
    fun Device_setname(mBluetoothAdapter: BluetoothAdapter, name :String){
        mBluetoothAdapter.setName(name)
    }

    private val receiver = object : BroadcastReceiver() {   //검색
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action.toString()
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    if (device!=null){
                        val deviceName = device.name
                        val deviceHardwareAddress = device.address // MAC address
                        System.out.println(deviceName)
                        if (!arrayDevices.contains(device) && device.name!=null) {
                            arrayDevices.add(device)
                            System.out.println("arraydevice : "+ device.name)
                        }
                    }
                    else{
                        System.out.println("arraydevice : null")
                    }
                }
            }
            System.out.println("디바이스개수" + arrayDevices.size)
            for(i in arrayDevices){
                println(i.name)
            }
        }
    }
    private val BlueCheck = object : BroadcastReceiver() {  //블루투스 변경체크     완료
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val state  = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,-1)
            System.out.println("블루투스변경감지")
            if (state == BluetoothAdapter.STATE_OFF) { // 블루투스 꺼져 있으면 블루투스 활성화
                active_bluetooth()
                Log.d("bluetoothAdapter","블루투스활성화")
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver)
        unregisterReceiver(BlueCheck)
    }


    @SuppressLint("MissingPermission")
    fun bluetoothOnOff(){
        if (check_bluetooth() == false) { // 블루투스 꺼져 있으면 블루투스 활성화
            active_bluetooth()
            Log.d("bluetoothAdapter","블루투스활성화")
        } else{ // 블루투스 켜져있으면 블루투스 비활성화
            mBluetoothAdapter?.disable()
            Log.d("bluetoothAdapter","블루투스비활성화")
        }
    }
    //검색


}