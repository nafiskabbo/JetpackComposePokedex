package com.kabbodev.jetpackcomposepokedex.repository

import com.kabbodev.jetpackcomposepokedex.data.remote.PokeApi
import com.kabbodev.jetpackcomposepokedex.data.remote.responses.Pokemon
import com.kabbodev.jetpackcomposepokedex.data.remote.responses.PokemonList
import com.kabbodev.jetpackcomposepokedex.utils.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class PokemonRepository @Inject constructor(
    private val api: PokeApi
) {

    suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList> {
        val response = try {
            api.getPokemonList(limit, offset)
        } catch (e: Exception) {
            return Resource.Error(message = "Something went wrong!")
        }
        return Resource.Success(response)
    }

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        val response = try {
            api.getPokemonInfo(pokemonName)
        } catch (e: Exception) {
            return Resource.Error(message = "Something went wrong!")
        }
        return Resource.Success(response)
    }

}