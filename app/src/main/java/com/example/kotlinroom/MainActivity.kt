package com.example.kotlinroom

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.withContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinroom.room.Constant
import com.example.kotlinroom.room.Movie
import com.example.kotlinroom.room.MovieDB
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val db by lazy { MovieDB(this) }
    lateinit var movieAdapter : MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        setuplistener()
        setupRecyclerView()

    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData(){
        CoroutineScope(Dispatchers.IO).launch {
            movieAdapter.setData(db.MovieDao().getMovies())
            withContext(Dispatchers.Main){
                movieAdapter.notifyDataSetChanged()

            }
        }
    }

    private fun setupView(){
        supportActionBar!!.apply {
            title = "Movies"
        }
    }

    private fun setuplistener(){
        add_movie.setOnClickListener {
            intentEdit(Constant.TYPE_CREATE, 0)

        }
    }

    private fun setupRecyclerView(){
        movieAdapter = MovieAdapter(
            arrayListOf(),object : MovieAdapter.OnAdapterListener {
                override fun onClick(movie: Movie) {
                    intentEdit(Constant.TYPE_READ, movie.id)
                }

                override fun onUpdate(movie: Movie) {
                    intentEdit(Constant.TYPE_UPDATE, movie.id)
                }

                override fun onDelete(movie: Movie) {
                    deleteAlert(movie)
                }
            })
        rv_movie.apply{
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = movieAdapter
        }
    }

    private fun intentEdit(intent_type: Int, movie_id: Int) {
        startActivity(
            Intent(this, AddActivity::class.java)
                .putExtra("intent_type", intent_type)
                .putExtra("movie_id", movie_id)
        )
    }

    private fun deleteAlert(movie: Movie){
        val dialog = AlertDialog.Builder(this)
        dialog.apply {
            setTitle("Konfirmasi Hapus")
            setMessage("Yakin hapus ${movie.title}?")
            setNegativeButton("Batal") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            setPositiveButton("Hapus") { dialogInterface, i ->
                CoroutineScope(Dispatchers.IO).launch {
                    db.MovieDao().deleteMovie(movie)
                    dialogInterface.dismiss()
                    loadData()
                }
            }
        }

        dialog.show()
    }
}