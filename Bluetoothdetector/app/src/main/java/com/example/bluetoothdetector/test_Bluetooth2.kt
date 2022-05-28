package com.example.bluetoothdetector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
class test_Bluetooth2 : AppCompatActivity() {
    private val REQUEST_ENABLE_BT=1
    private val REQUEST_ALL_PERMISSION= 2
    private val PERMISSIONS = arrayOf(          //기기권한 체크 사용0
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var scanning: Boolean = false
    private var devicesArr = ArrayList<BluetoothDevice>()
    private val SCAN_PERIOD = 1000
    private val handler = Handler()
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var recyclerViewAdapter : RecyclerViewAdapter
    private val mLeScanCallback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    object : ScanCallback() {
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.d("scanCallback", "BLE Scan Failed : " + errorCode)
        }


        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            results?.let{
                // results is not null
                for (result in it){
                    if (ActivityCompat.checkSelfPermission(
                            applicationContext,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    if (!devicesArr.contains(result.device) && result.device.name!=null) {
                        devicesArr.add(result.device)
                        System.out.println("resutl device : "+result.device)
                    }
                    else{
                        System.out.println("batchresult 왜 널이냐")
                    }
                }

            }
        }


        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.let {
                // result is not null
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                if (!devicesArr.contains(it.device) && it.device.name!=null) {
                    System.out.println("it device : "+ it.device)
                    devicesArr.add(it.device)
                }else{
                    System.out.println("Scanresult 왜 널이냐")
                }
                recyclerViewAdapter.notifyDataSetChanged()
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun scanDevice(state:Boolean) = if(state){
        handler.postDelayed({
            scanning = false
            bluetoothAdapter?.bluetoothLeScanner?.stopScan(mLeScanCallback)
        }, SCAN_PERIOD)
        scanning = true
        devicesArr.clear()
        bluetoothAdapter?.bluetoothLeScanner?.startScan(mLeScanCallback)
        System.out.println("저장됐나"+ devicesArr.size )
    }else{
        scanning = false
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(mLeScanCallback)
    }

    private fun hasPermissions(context: Context?, permissions: Array<String>): Boolean {        //기기권한체크 사용0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }
    // Permission check         없어도 되는듯한데 잘 모르겠다
    /*override fun onRequestPermissionsResult(        //기기권한체크 사용0
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ALL_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show()
                    System.out.println("권한허용")
                } else {
                    requestPermissions(permissions, REQUEST_ALL_PERMISSION)
                    Toast.makeText(this, "Permissions must be granted", Toast.LENGTH_SHORT).show()
                    System.out.println("권한허용해주세요")
                }
            }
        }
    }*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_bluetooth2)
        val bleOnOffBtn:ToggleButton = findViewById(R.id.ble_on_off_btn)
        val scanBtn: Button = findViewById(R.id.scanBtn)
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        viewManager = LinearLayoutManager(this)
        recyclerViewAdapter =  RecyclerViewAdapter(devicesArr)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = viewManager
            adapter = recyclerViewAdapter
        }

        if(bluetoothAdapter!=null){
            if(bluetoothAdapter?.isEnabled==false){
                bleOnOffBtn.isChecked = true
                scanBtn.isVisible = false
            } else{
                bleOnOffBtn.isChecked = false
                scanBtn.isVisible = true
            }
        }

        bleOnOffBtn.setOnCheckedChangeListener { _, isChecked ->
            bluetoothOnOff()
            scanBtn.visibility = if (scanBtn.visibility == View.VISIBLE){ View.INVISIBLE } else{ View.VISIBLE }
        }

        scanBtn.setOnClickListener { v:View? -> // Scan Button Onclick
            if (!hasPermissions(this, PERMISSIONS)) {           //기기권한체크 사용0
                requestPermissions(PERMISSIONS, REQUEST_ALL_PERMISSION)
            }
            scanDevice(true)
        }
    }
    fun bluetoothOnOff(){
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.d("bluetoothAdapter","Device doesn't support Bluetooth")
        }else{
            if (bluetoothAdapter?.isEnabled == false) { // 블루투스 꺼져 있으면 블루투스 활성화
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            } else{ // 블루투스 켜져있으면 블루투스 비활성화
                bluetoothAdapter?.disable()
            }
        }
    }
    class RecyclerViewAdapter(private val myDataset: ArrayList<BluetoothDevice>) :
        RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

        class MyViewHolder(val linearView: LinearLayout) : RecyclerView.ViewHolder(linearView)

        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): RecyclerViewAdapter.MyViewHolder {
            // create a new view
            val linearView = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycleview_item, parent, false) as LinearLayout
            return MyViewHolder(linearView)
        }

        // Replace the contents of a view (invoked by the layout manager)
        @SuppressLint("MissingPermission")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val itemName:TextView = holder.linearView.findViewById(R.id.item_name)
            val itemAddress:TextView = holder.linearView.findViewById(R.id.item_address)
            itemName.text = myDataset[position].name
            itemAddress.text = myDataset[position].address
            System.out.println("find : "+myDataset[position].name)
        }

        override fun getItemCount() = myDataset.size
    }
}

private fun Handler.postDelayed(function: () -> Unit?, scanPeriod: Int) {

}