package gonzalez.jesus.mypokedex_gonzalezjesus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val registerPokemon: Button = findViewById(R.id.addPokemon) as Button

        registerPokemon.setOnClickListener {
            val intent: Intent = Intent(this, RegisterPokemonActivity::class.java)
            startActivity(intent)
        }

        var lvPokemones: ListView = findViewById(R.id.pokemonList)
        var pokemonList: MutableList<Pokemon> = mutableListOf()
        var adapter = PokemonAdapter(this, pokemonList)
        lvPokemones.adapter = adapter

        val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("pokemons")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pokemonList.clear()
                for (data in snapshot.children) {
                    val pokemon = data.getValue(Pokemon::class.java)
                    if (pokemon != null) {
                        pokemonList.add(pokemon)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}

private class PokemonAdapter(private val context: Context, private val pokemonList: List<Pokemon>) : BaseAdapter() {
    override fun getCount(): Int = pokemonList.size
    override fun getItem(position: Int): Any = pokemonList[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_pokemon, parent, false)

        val pokemon = pokemonList[position]
        val nameTextView: TextView = view.findViewById(R.id.pokemonName)
        val numberTextView: TextView = view.findViewById(R.id.pokemonNumber)
        val imageView: ImageView = view.findViewById(R.id.pokemonImage)

        nameTextView.text = pokemon.name
        numberTextView.text = "#${pokemon.number}"

        Glide.with(context)
            .load(pokemon.uri)
            .into(imageView)

        return view
    }
}