package com.example.bluetest

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.text.method.MovementMethod
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.loader.content.AsyncTaskLoader
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    val Bt:BluetoothAdapter= BluetoothAdapter.getDefaultAdapter()
    var bt_Socket:BluetoothSocket?=null
//    var i:Int=0

    val btRunnable= Runnable {
        run{
            while(true){
                try{
                    if(bt_Socket!!.isConnected) {
//                        var buffer:ByteArray= ByteArray(1)
//                        bt_Socket!!.inputStream.read()
                        var i:Int = bt_Socket!!.inputStream.read()
                        var C:Char=' '+i-32
                        textView.text =textView.text.toString() + C.toString()

                    }
                }
                catch (e: IOException){

                }
            }

        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView.movementMethod=ScrollingMovementMethod.getInstance()

    }



    fun search_bt(view: View){
        val btn_1:Button=findViewById<Button>(R.id.button)
        if(btn_1.text=="连接蓝牙"){
            if(!Bt.isEnabled){
                Bt.enable()
            }
            textView.visibility=View.INVISIBLE
            mac_list.visibility=View.VISIBLE

            var m_pairedDevices:Set<BluetoothDevice> = Bt!!.bondedDevices
            val list : ArrayList<String> = ArrayList()

            for (device: BluetoothDevice in m_pairedDevices) {

                var s:String= device.name+"\r\n"+device.toString()
                list.add(s)
                Log.i("device", ""+device)
            }
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
            mac_list.adapter=adapter

            mac_list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                val device: BluetoothDevice = m_pairedDevices.elementAt(position)
                val address: String = device.address

                bt_Socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))

                textView.visibility=View.VISIBLE
                mac_list.visibility=View.INVISIBLE

                try {
                    bt_Socket!!.connect()
                    if(btn_1.text=="连接蓝牙"){
                        btn_1.text="发送"
                    }

                    Thread(btRunnable).start()
                }
                catch(e: IOException){
                    textView.visibility=View.INVISIBLE
                    mac_list.visibility=View.VISIBLE
                    val toast:Toast = Toast.makeText(this,"连接失败",Toast.LENGTH_SHORT)
                    toast.show()

                }
            }

        }
        else{
            if(bt_Socket!!.isConnected){
                bt_Socket!!.outputStream.write(et.text.toString().toByteArray())
                if(checkBox.isChecked) {
                    bt_Socket!!.outputStream.write("\r\n".toByteArray())
                }
            }
        }
    }

    fun clear_b(view:View){
        textView.text=""
    }
}