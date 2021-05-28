package com.kabbodev.jetpackcomposepokedex.ui.screen.pokemonList

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.kabbodev.jetpackcomposepokedex.data.models.PokedexListEntry
import com.kabbodev.jetpackcomposepokedex.data.remote.config.Constants.PAGE_SIZE
import com.kabbodev.jetpackcomposepokedex.data.remote.repository.PokemonRepository
import com.kabbodev.jetpackcomposepokedex.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {
    private var curPage = 0

    var pokemonList = mutableStateOf<ArrayList<PokedexListEntry>>(arrayListOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)

    init {
        loadPokemonList()
    }

    fun loadPokemonList() {
        viewModelScope.launch {
            isLoading.value = true

            when (val result = repository.getPokemonList(PAGE_SIZE, curPage * PAGE_SIZE)) {
                is Resource.Success -> {
                    endReached.value = curPage * PAGE_SIZE >= result.data!!.count
                    val pokedexEntries = result.data.results.map { entry ->
                        val number = if (entry.url.endsWith("/")) {
                            entry.url.dropLast(1).takeLastWhile { it.isDigit() }
                        } else {
                            entry.url.takeLastWhile { it.isDigit() }
                        }.toInt()
                        val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"

                        PokedexListEntry(
                            pokemonName = entry.name.capitalize(Locale.ROOT),
                            imageUrl = imageUrl,
                            number = number
                        )
                    }
                    curPage++

                    loadError.value = ""
                    isLoading.value = false
                    if (pokemonList.value.isNullOrEmpty()) {
                        pokemonList.value = pokedexEntries as ArrayList<PokedexListEntry>
                    } else {
                        val list = pokemonList.value
                        list.addAll(pokedexEntries)
                        pokemonList.value = list
                    }
                }
                is Resource.Error -> {
                    loadError.value = result.message!!
                    isLoading.value = false
                }
                is Resource.Loading -> {
                }
            }
        }
    }


    fun calcDominantColor(painter: Painter, onFinish: (Color) -> Unit) {
        val bitmap: Bitmap = Bitmap.createBitmap(painter.intrinsicSize.height.toInt(), painter.intrinsicSize.width.toInt(), Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true)
//        val bitmap: Bitmap = Bitmap.createBitmap(painter.intrinsicSize.height.toInt(), painter.intrinsicSize.width.toInt(), Bitmap.Config.ARGB_8888)

        Palette.from(bitmap).generate { palette ->
            val color = palette?.getDominantColor(Color.White.toArgb())
            color?.let {
                onFinish(Color(it))
            }
        }
    }

}