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
import android.os.Handler
import android.util.Log
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.bluetoothdetector.databinding.ActivityTestBluetoothBinding

class test_Bluetooth : AppCompatActivity() {
    private val REQUEST_ENABLE_BT=1
    private val REQUEST_DISCOVER_CODE =100
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
        found_filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(receiver, found_filter)
        var state_filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(BlueCheck, state_filter)
        var name_filter = IntentFilter(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED)
        registerReceiver(name_change, name_filter)


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
            System.out.println("검색전 종료 "+mBluetoothAdapter!!.cancelDiscovery()) //검색전 검색종료
            arrayDevices.clear()
            System.out.println(mBluetoothAdapter!!.startDiscovery())    //검색시작

        }
        binding.nexttest.setOnClickListener(){
            val intent = Intent(this, test_Bluetooth2::class.java)
            startActivity(intent)
            //search_allow(3600)        //검색허용


        }
        binding.getname.setOnClickListener(){
            System.out.println("현재 블루투스 기기명 : "+Device_getname())
        }
        binding.setname.setOnClickListener(){
            val devicename=texttoString("20200305")
            Device_setname(devicename)
        }
    }
    @SuppressLint("MissingPermission")
    fun search_allow(time:Int){     //검색허용
        val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0)
        }
        startActivity(discoverableIntent)
    }
    @SuppressLint("MissingPermission")
    fun check_bluetooth(): Boolean? {   //블루투스on/off상태 체크

        System.out.println(mBluetoothAdapter?.isEnabled)
        return mBluetoothAdapter?.isEnabled
    }
    @SuppressLint("MissingPermission")
    fun active_bluetooth(){ //블루투스활성화
//        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        mBluetoothAdapter!!.enable()
    }
    @SuppressLint("MissingPermission")  //블루투스명 가져오기
    fun Device_getname(): String {
        return mBluetoothAdapter!!.name.toString()
    }
    @SuppressLint("MissingPermission")  //블루투스명 설정
    fun Device_setname(name :String){
        mBluetoothAdapter!!.setName(name)
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
                    System.out.println("디바이스개수" + arrayDevices.size)
                    for(i in arrayDevices){
                        println(i.name)
                    }
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
    private val BlueCheck = object : BroadcastReceiver() {  //블루투스 변경체크     완료
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val state  = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,-1)
            System.out.println("블루투스변경감지")
            if (state == BluetoothAdapter.STATE_OFF) { // 블루투스 꺼져 있으면 블루투스 활성화
                if(IsCorrect(Device_getname())){     //격리자일때 블루투스 종료하면 다시 활성화
                    active_bluetooth()
                    Log.d("bluetoothAdapter","블루투스활성화")
                }
            }
        }
    }

    private val name_change = object : BroadcastReceiver() {  //이름변경 체크     완료
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            System.out.print("이름바뀜 : ")
            val name=Device_getname()
            if (name=="jaehun"){
                System.out.println("이름변경시작")
                val devicename=texttoString("20200305")
                Device_setname(devicename)
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
