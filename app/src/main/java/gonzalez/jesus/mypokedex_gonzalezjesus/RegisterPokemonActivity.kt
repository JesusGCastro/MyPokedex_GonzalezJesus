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
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.database.FirebaseDatabase

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

        save.setOnClickListener {
            val nameText = name.text.toString().trim()
            val numberText = number.text.toString().trim()

            if (nameText.isEmpty() || numberText.isEmpty() || imageUri == null) {
                Toast.makeText(this, "Ingresa y selecciona todos los datos de tu Pokémon", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            setUri { imageUrl -> savePokemon(nameText, numberText, imageUrl) }
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
                imageUri = fullImageUri
                changeImage(fullImageUri)
            }
        }
    }

    private fun initCloudinary() {
        try {
            MediaManager.get()
        } catch (e: Exception) {
            val config: MutableMap<String, String> = HashMap()
            config["cloud_name"] = CLOUD_NAME
            MediaManager.init(this, config)
        }
    }


    fun changeImage(uri: Uri){
        val thumbnail: ImageView = findViewById(R.id.thumbnail) as ImageView
        try {
            thumbnail.setImageURI(uri)
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun setUri(callback: (String) -> Unit){
        var url: String = ""

        if (imageUri != null){
            MediaManager.get().upload(imageUri).unsigned(UPLOAD_PRESET).callback(object: UploadCallback {
                override fun onStart(requesId: String) {
                    Log.d("Cloudinary Quickstart", "Upload start")
                }
                override fun onProgress (requesId: String, bytes: Long, totalBytes: Long) {
                    Log.d("Cloudinary Quickstart", "Upload progress")
                }
                override fun onSuccess (requestId: String, resultData: Map<*, *>){
                    Log.d("Cloudinary Quickstart", "Upload success")
                    url = resultData ["secure_url"] as String?:""
                    callback(url)
                }
                override fun onError (requesId: String, error: ErrorInfo) {
                    Log.d("Cloudinary Quickstart", "Upload failed")
                }
                override fun onReschedule (requesId: String, error: ErrorInfo){

                }
            }).dispatch()
        }
    }

    fun savePokemon(name: String, number: String, uri: String){
        val database = FirebaseDatabase.getInstance().getReference("pokemons").child(number)

        val pokemon = Pokemon(name, number.toInt(), uri)

        database.setValue(pokemon).addOnSuccessListener {
            Toast.makeText(this,"Pokemon guardado en Pokedex", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener {
            Toast.makeText(this,"Error al guardar pokemon en Pokedex", Toast.LENGTH_SHORT).show()
        }
    }
}