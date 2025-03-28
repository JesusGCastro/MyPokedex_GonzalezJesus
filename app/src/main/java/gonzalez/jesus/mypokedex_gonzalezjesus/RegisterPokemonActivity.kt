package gonzalez.jesus.mypokedex_gonzalezjesus

import android.app.Activity
import android.app.ComponentCaller
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterPokemonActivity : AppCompatActivity() {

    val REQUEST_IMAGE_GET = 1
    val CLOUD_NAME = "dlcv1adru"
    val UPLOAD_PRESET = "pokemon-upload"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_pokemon)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val name: EditText = findViewById(R.id.pokemonName) as EditText
        val number: EditText = findViewById(R.id.pokemonNumber) as EditText
        val select: Button = findViewById(R.id.selectImage) as Button
        val save: Button = findViewById(R.id.savePokemon) as Button

        select.setOnClickListener {
            val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_GET)
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

    fun changeImage(uri: Uri){
        val thumbnail: ImageView = findViewById(R.id.thumbnail) as ImageView
        try {
            thumbnail.setImageURI(uri)
        } catch (e: Exception){
            e.printStackTrace()
        }
    }
}