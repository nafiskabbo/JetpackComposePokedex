package com.kabbodev.jetpackcomposepokedex.ui.screen.pokemonDetail

import androidx.lifecycle.ViewModel
import com.kabbodev.jetpackcomposepokedex.data.remote.repository.PokemonRepository
import com.kabbodev.jetpackcomposepokedex.data.remote.responses.Pokemon
import com.kabbodev.jetpackcomposepokedex.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> = repository.getPokemonInfo(pokemonName = pokemonName)

}