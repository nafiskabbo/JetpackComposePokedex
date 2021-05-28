package com.kabbodev.jetpackcomposepokedex.ui.screen.pokemonList

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.PaintDrawable
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import coil.bitmap.BitmapPool
import com.kabbodev.jetpackcomposepokedex.data.remote.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    fun calcDominantColor(painter: Painter, onFinish: (Color) -> Unit) {
//        val bitmap: Bitmap = Bitmap.createBitmap(painter.intrinsicSize.height.toInt(), painter.intrinsicSize.width.toInt(), Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true)
        val bitmap: Bitmap = Bitmap.createBitmap(painter.intrinsicSize.height.toInt(), painter.intrinsicSize.width.toInt(), Bitmap.Config.ARGB_8888)

        Palette.from(bitmap).generate { palette ->
            val color = palette?.getDominantColor(Color.White.toArgb())
            color?.let {
                onFinish(Color(it))
            }
        }
    }

}