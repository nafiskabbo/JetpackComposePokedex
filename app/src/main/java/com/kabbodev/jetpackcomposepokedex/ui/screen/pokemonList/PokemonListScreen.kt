package com.kabbodev.jetpackcomposepokedex.ui.screen.pokemonList

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.request.ImageRequest
import coil.transform.Transformation
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState
import com.kabbodev.jetpackcomposepokedex.R
import com.kabbodev.jetpackcomposepokedex.data.models.PokedexListEntry
import com.kabbodev.jetpackcomposepokedex.data.remote.api.PokeApi
import com.kabbodev.jetpackcomposepokedex.data.remote.repository.PokemonRepository
import com.kabbodev.jetpackcomposepokedex.ui.theme.RobotoCondensed

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ShowPokemonListScreen() {
    PokemonListScreen(navController = rememberNavController())
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PokemonListScreen(
    navController: NavController
) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {

        Column {
            Spacer(modifier = Modifier.height(20.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_international_pok_mon_logo),
                contentDescription = "Pokemon",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(CenterHorizontally)
            )
            SearchBar(
                hint = "Search Pokemon",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

            }
            Spacer(modifier = Modifier.height(20.dp))
            LazyVerticalGrid(
                cells = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
            ) {
                items(6) {
                    PokedexEntry(
                        PokedexListEntry("AYAN", "https://picsum.photos/300/300", 1),
                        navController,
                        Modifier
                            .padding(8.dp)
                    )
                }
            }
        }

    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "",
    onSearch: (String) -> Unit = {}
) {
    var text by remember {
        mutableStateOf("")
    }

    Box(modifier = modifier) {
        TextField(
            value = text,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, RoundedCornerShape(26.dp)),
            placeholder = {
                Text(text = hint)
            },
            onValueChange = {
                text = it
                onSearch(it)
            },
            singleLine = true,
            maxLines = 1,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                textColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                placeholderColor = Color.LightGray
            ),
            shape = CircleShape
        )
    }


}


@Composable
fun PokedexEntry(
    entry: PokedexListEntry,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    val defaultDominantColor = MaterialTheme.colors.surface
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }

    Box(
        contentAlignment = Center,
        modifier = modifier
            .fillMaxSize()
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(
                Brush.verticalGradient(
                    listOf(
                        dominantColor,
                        defaultDominantColor
                    )
                )
            )
            .clickable {
                navController.navigate(
                    "pokemon_detail_screen/${dominantColor.toArgb()}/${entry.pokemonName}"
                )
            }
    ) {
        Column {
            var request: Any by remember {
                mutableStateOf(entry.imageUrl)
            }
            val painter = rememberCoilPainter(
                request = request,
                fadeIn = true,
                previewPlaceholder = R.drawable.ic_international_pok_mon_logo
            )

            when (painter.loadState) {
                is ImageLoadState.Loading -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(.4f)
                            .align(CenterHorizontally)
                    )
                }
                is ImageLoadState.Success -> {
                    viewModel.calcDominantColor((painter.loadState as ImageLoadState.Success).result) {
                        dominantColor = it
                    }
                }
                is ImageLoadState.Error -> {
                    request = R.drawable.ic_international_pok_mon_logo
                }
            }

            Image(
                painter = painter,
                contentDescription = entry.pokemonName,
                modifier = Modifier
                    .size(120.dp)
                    .align(CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = entry.pokemonName,
                fontFamily = RobotoCondensed,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }


}