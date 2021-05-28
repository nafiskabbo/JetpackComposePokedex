package com.kabbodev.jetpackcomposepokedex.di

import com.kabbodev.jetpackcomposepokedex.data.remote.config.Constants.BASE_URL
import com.kabbodev.jetpackcomposepokedex.data.remote.api.PokeApi
import com.kabbodev.jetpackcomposepokedex.data.remote.repository.PokemonRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providePokemonRepository(api: PokeApi) = PokemonRepository(api)


    @Singleton
    @Provides
    fun providePokeApi(): PokeApi =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(PokeApi::class.java)

}