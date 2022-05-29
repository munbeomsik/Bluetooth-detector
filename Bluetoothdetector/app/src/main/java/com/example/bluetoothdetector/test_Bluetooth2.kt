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
import java.io.*

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
            System.out.println(filesDir.absoluteFile)
            var s=readtextfile(filesDir.absolutePath+"/datafile")
            System.out.println("기존 기기명 : "+s)

        }

        scanBtn.setOnClickListener { v:View? -> // Scan Button Onclick
//            if (!hasPermissions(this, PERMISSIONS)) {           //기기권한체크 사용0
//                requestPermissions(PERMISSIONS, REQUEST_ALL_PERMISSION)
//            }
            writetextfile(filesDir.absolutePath,"datafile","jaehun")
        }
    }
    fun hasPermissions(context: Context?, permissions: Array<String>): Boolean {        //기기권한체크 사용0
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
    fun readtextfile(fullpath:String) : String{
        val file=File(fullpath)
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
    fun writetextfile(directory: String, filename: String, content: String){
        val dir = File(directory)
        if(!dir.exists()){
            dir.mkdirs()
        }
        val writer= FileWriter(directory+"/"+filename)
        val buffer=BufferedWriter(writer)
        buffer.write(content)
        System.out.println("기존 기기명 저장")
        buffer.close()
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