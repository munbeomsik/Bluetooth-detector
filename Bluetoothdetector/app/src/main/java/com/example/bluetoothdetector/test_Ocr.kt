package com.example.bluetoothdetector

import android.app.Activity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_ocr)
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
        OCRTextView!!.text = OCRresult
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
