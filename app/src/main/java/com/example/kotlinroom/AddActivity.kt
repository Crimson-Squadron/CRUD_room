package com.example.kotlinroom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.kotlinroom.room.Constant
import com.example.kotlinroom.room.Movie
import com.example.kotlinroom.room.MovieDB
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddActivity : AppCompatActivity() {

    private val db by lazy { MovieDB(this) }
    private var movieId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        setupView()
        setupListener()
    }

    private fun setupView() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        when (intentType()) {
            Constant.TYPE_CREATE -> {
                supportActionBar!!.title = "BUAT BARU"
                btn_save.visibility = View.VISIBLE
                btn_update.visibility = View.GONE
            }
            Constant.TYPE_READ -> {
                supportActionBar!!.title = "BACA"
                btn_save.visibility = View.GONE
                btn_update.visibility = View.GONE
                getMovie()
            }
            Constant.TYPE_UPDATE -> {
                supportActionBar!!.title = "EDIT"
                btn_save.visibility = View.GONE
                btn_update.visibility = View.VISIBLE
                getMovie()
            }
        }
    }

    private fun setupListener() {
        btn_save.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                db.MovieDao().addMovie(
                    Movie(
                        0, et_title.text.toString(),
                        et_description.text.toString()
                    )
                )

                finish()
            }
        }
        btn_update.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                db.MovieDao().updateMovie(
                    Movie(
                        movieId, et_title.text.toString(),
                        et_description.text.toString()
                    )
                )

                finish()
            }
        }
    }

    private fun getMovie(){
        movieId = intent.getIntExtra("movie_id", 0)
        CoroutineScope(Dispatchers.IO).launch {
            val movies = db.MovieDao().getMovie(movieId).get(0)
            et_title.setText( movies.title )
            et_description.setText( movies.desc )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun intentType(): Int {
        return intent.getIntExtra("intent_type", 0)
    }
}