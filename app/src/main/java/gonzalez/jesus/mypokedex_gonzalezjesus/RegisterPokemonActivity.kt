package gonzalez.jesus.mypokedex_gonzalezjesus

import android.app.Activity
import android.app.ComponentCaller
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

class RegisterPokemonActivity : AppCompatActivity() {

    val REQUEST_IMAGE_GET = 1
    val CLOUD_NAME = "dlcv1adru"
    val UPLOAD_PRESET = "pokemon-upload"
    var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_pokemon)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initCloudinary()

        val name: EditText = findViewById(R.id.pokemonName) as EditText
        val number: EditText = findViewById(R.id.pokemonNumber) as EditText
        val select: Button = findViewById(R.id.selectImage) as Button
        val save: Button = findViewById(R.id.savePokemon) as Button

        select.setOnClickListener {
            val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_GET)
        }

        save.setOnClickListener{
            savePokemon()
        }

    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK){
            val fullImageUri: Uri? = data?.data

            if (fullImageUri != null){
                changeImage(fullImageUri)
            }
        }
    }

    private fun initCloudinary(){
        val config: MutableMap<String, String> = HashMap<String, String>()
        config["cloud_name"] = CLOUD_NAME
        MediaManager.init(this, config)
    }

    fun changeImage(uri: Uri){
        val thumbnail: ImageView = findViewById(R.id.thumbnail) as ImageView
        try {
            thumbnail.setImageURI(uri)
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun savePokemon(): String{
        var url : String = ""

        if (imageUri != null){
            MediaManager.get().upload(UPLOAD_PRESET).callback(object : UploadCallback{
                override fun onStart(requestId: String?) {
                    Log.d("Cloudinary Quickstart", "Upload start")
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                    Log.d("Cloudinary Quickstart", "Upload progress")
                }

                override fun onSuccess(requestId: String?, resultData: MutableMap<*,*>) {
                    Log.d("Cloudinary Quickstart", "Upload start")
                    url = resultData["secure_url"] as String? ?: ""
                    Log.d("URL}", url)
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    Log.d("Cloudinary Quickstart", "Upload failed")
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                }
            }).dispatch()

        }
        return url
    }
}