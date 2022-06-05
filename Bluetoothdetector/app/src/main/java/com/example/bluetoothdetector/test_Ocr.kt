package com.example.bluetoothdetector

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.googlecode.tesseract.android.TessBaseAPI
import kotlinx.android.synthetic.main.activity_test_ocr.*
import java.io.*


class test_Ocr : AppCompatActivity() {
    private val OPEN_GALLERY = 1
    var image //사용되는 이미지
            : Bitmap? = null
    private var mTess //Tess API reference
            : TessBaseAPI? = null
    var datapath = "" //언어데이터가 있는 경로
    var OCRTextView // OCR 결과뷰
            : TextView? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_ocr)
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter==null){
            System.out.println("블루투스가 사용불가합니다.")
            onDestroy()
        }
        active_bluetooth()
        OCRTextView = findViewById(R.id.OCRTextView)
        openGallery_button.setOnClickListener{ openGallery() }
    }
    private fun openGallery(){
        val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(intent, OPEN_GALLERY)
    }

    /***
     * 이미지에서 텍스트 읽기
     */
    fun processImage(view: View?) {
        var OCRresult: String? = null
        mTess!!.setImage(image)
        OCRresult = mTess!!.utF8Text
        OCRresult = OCRresult.replace("[^0-9]".toRegex(), "")
        OCRTextView!!.text = "\n\n------------------------------------------------------------------------------------------\n" + "   추출 결과\n"+ "------------------------------------------------------------------------------------------\n\n   " + OCRresult
        writetextfile(filesDir.absolutePath,"Ocrdatafile",OCRresult)
        writetextfile(filesDir.absolutePath, "name", "asdasd")
        Device_setname(texttoString(readtextfile(filesDir.absolutePath + "/Ocrdatafile")))
        System.out.println(Device_getname())
        var dialog = AlertDialog.Builder(this)
        dialog.setTitle("알림")
        dialog.setMessage("격리자등록 및 암호화가 완료되었습니다.\n" + readtextfile(filesDir.absolutePath +"/Ocrdatafile") + "일 0시 이후 격리가 해제됩니다." )

        var dialog_listener = object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                when(which){
                    DialogInterface.BUTTON_POSITIVE ->
                        finish()
                }
            }
        }
        dialog.setPositiveButton("YES",dialog_listener)
        dialog.show()
    }
    fun toast_p() {
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }

    fun texttoString(S: String): String {
        val result: String?
        val token = S.chunked(1)
        result = "@qn" + token[5] + "ut" + token[2] + "ai" + token[6] + "&%" + token[7] + token[1] + "rn" + token[2] +"ae"
        return result
    }

    fun active_bluetooth(){ //블루투스활성화
        mBluetoothAdapter!!.enable()
    }
    @SuppressLint("MissingPermission")
    fun Device_getname(): String {
        return mBluetoothAdapter!!.name.toString()
    }
    @SuppressLint("MissingPermission")
    fun Device_setname(name :String){
        mBluetoothAdapter!!.setName(name)
    }

    fun writetextfile(directory: String, filename: String, content: String){
        val dir = File(directory)
        if(!dir.exists()){
            dir.mkdirs()
        }
        val writer= FileWriter(directory+"/"+filename)
        val buffer=BufferedWriter(writer)
        buffer.write(content)
        System.out.println("격리 기간 추출"+ filesDir.absolutePath)
        buffer.close()
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

    /***
     * 언어 데이터 파일, 디바이스에 복사
     */

    // 언어 파일 이름
    private val langFileName = "eng.traineddata"
    private fun copyFiles() {
        try {
            val filepath = datapath + "tessdata/" + langFileName
            val assetManager = assets
            val instream: InputStream = assetManager.open(langFileName)
            val outstream: OutputStream = FileOutputStream(filepath)
            val buffer = ByteArray(1024)
            var read: Int
            while (instream.read(buffer).also { read = it } != -1) {
                outstream.write(buffer, 0, read)
            }
            outstream.flush()
            outstream.close()
            instream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /***
     * 디바이스에 언어 데이터 파일 존재 유무 체크
     * @param dir
     */
    private fun checkFile(dir: File) {
        //디렉토리가 없으면 디렉토리를 만들고 그후에 파일을 카피
        if (!dir.exists() && dir.mkdirs()) {
            copyFiles()
        }
        //디렉토리가 있지만 파일이 없으면 파일카피 진행
        if (dir.exists()) {
            val datafilepath = datapath + "tessdata/" + langFileName
            val datafile = File(datafilepath)
            if (!datafile.exists()) {
                copyFiles()
            }
        }
    }

    fun cropCenterBitmap(uri: Uri?, w: Int, h: Int): Bitmap? {
        var src: Bitmap? = null
        try {
            src = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        } catch (e: FileNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        if (src == null) return null
        val width = src.width
        val height = src.height
        if (width < w && height < h) return src
        var cw = w // crop width
        var ch = h // crop height
        if (w > width) cw = width
        if (h > height) ch = height
        System.out.println(""+cw+" "+ch)
        return Bitmap.createBitmap(src,  1010, 650, cw, ch)
    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode== Activity.RESULT_OK){
            if(requestCode == OPEN_GALLERY){
                var currentImageURL: Uri? = data?.data
                val imageView: ImageView = findViewById(R.id.imageView);
                val imageView2: ImageView = findViewById(R.id.imageView2);
                image = cropCenterBitmap(currentImageURL,240,90)
                imageView.setImageBitmap(MediaStore.Images.Media.getBitmap(contentResolver,currentImageURL))
                imageView2.setImageBitmap(image)
                //언어파일 경로
                datapath = "$filesDir/tesseract/"

                //트레이닝데이터가 카피되어 있는지 체크
                checkFile(File(datapath + "tessdata/"))

                //Tesseract API 언어 세팅
                val lang = "eng"

                mTess = TessBaseAPI()
                mTess!!.init(datapath, lang)
            }
        }else{
            Log.d("ActivityResult", "something wrong")
        }
    }

}
