package com.lizardoreyes.superheros

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lizardoreyes.superheros.databinding.ActivityDetailsBinding
import com.lizardoreyes.superheros.models.SuperHero
import com.lizardoreyes.superheros.utils.Constants.SUPERHERO_NAME

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val extras = intent.extras
        val superHero = extras?.getParcelable<SuperHero>(SUPERHERO_NAME)
        val imageDirectory = extras?.getString("image")
        val image = BitmapFactory.decodeFile(imageDirectory)

        // Set ModelBinding
        binding.superHero = superHero
        // Set Image
        image?.let { binding.ivPhoto.setImageBitmap(image) }
    }
}