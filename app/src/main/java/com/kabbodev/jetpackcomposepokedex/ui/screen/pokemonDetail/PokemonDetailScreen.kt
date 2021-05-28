package com.kabbodev.jetpackcomposepokedex.ui.screen.pokemonDetail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Height
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.coil.rememberCoilPainter
import com.kabbodev.jetpackcomposepokedex.data.remote.responses.Pokemon
import com.kabbodev.jetpackcomposepokedex.data.remote.responses.Stat
import com.kabbodev.jetpackcomposepokedex.data.remote.responses.Type
import com.kabbodev.jetpackcomposepokedex.utils.Resource
import com.kabbodev.jetpackcomposepokedex.utils.parseStatToAbbr
import com.kabbodev.jetpackcomposepokedex.utils.parseStatToColor
import com.kabbodev.jetpackcomposepokedex.utils.parseTypeToColor
import kotlin.math.round

@Composable
fun PokemonDetailScreen(
    dominantColor: Color,
    pokemonName: String,
    navController: NavController,
    topPadding: Dp = 20.dp,
    pokemonImageSize: Dp = 200.dp,
    viewModel: PokemonDetailViewModel = hiltViewModel()
) {
    val pokemonInfo = produceState<Resource<Pokemon>>(initialValue = Resource.Loading()) {
        value = viewModel.getPokemonInfo(pokemonName = pokemonName)
    }.value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(dominantColor)
            .padding(bottom = 24.dp)
    ) {

        PokemonDetailTopSection(
            navController = navController,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.2f)
                .align(Alignment.TopCenter)
        )

        PokemonDetailStateWrapper(
            pokemonInfo = pokemonInfo, modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = topPadding + pokemonImageSize / 2f,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
                .shadow(10.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colors.surface)
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            loadingModifier = Modifier
                .size(100.dp)
                .align(Alignment.Center)
                .padding(
                    top = topPadding + pokemonImageSize / 2f,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
        )

        if (pokemonInfo is Resource.Success) {

            pokemonInfo.data?.sprites?.let {
                val painter = rememberCoilPainter(
                    request = it.frontDefault,
                    fadeIn = true
                )

                Image(
                    modifier = Modifier
                        .size(pokemonImageSize)
                        .align(Alignment.TopCenter)
                        .offset(y = topPadding),
                    contentDescription = pokemonName,
                    contentScale = ContentScale.Fit,
                    painter = painter
                )
            }
        }

    }

}

@Composable
fun PokemonDetailTopSection(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.TopStart,
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color.Black,
                        Color.Transparent
                    )
                )
            )
    ) {

        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(36.dp)
                .offset(16.dp, 16.dp)
                .clickable(role = Role.Button) {
                    navController.popBackStack()
                }

        )
    }
}

@Composable
fun PokemonDetailStateWrapper(
    pokemonInfo: Resource<Pokemon>,
    modifier: Modifier = Modifier,
    loadingModifier: Modifier = Modifier
) {
    when (pokemonInfo) {
        is Resource.Error -> {
            Text(
                text = pokemonInfo.message!!,
                color = Color.Red,
                modifier = modifier,
                textAlign = TextAlign.Center
            )
        }
        is Resource.Loading -> {
            CircularProgressIndicator(modifier = loadingModifier)
        }
        is Resource.Success -> {
            pokemonInfo.data?.let { pokemon ->
                PokemonDetailSection(
                    pokemon = pokemon,
                    modifier = modifier.offset(y = (-20).dp)
                )
            }
        }
    }
}

@Composable
fun PokemonDetailSection(
    pokemon: Pokemon,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .offset(y = 100.dp)
    ) {
        Text(
            text = "#${pokemon.id} ${pokemon.name.capitalize(Locale.current)}",
            color = MaterialTheme.colors.onSurface,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            textAlign = TextAlign.Center
        )
        PokemonTypeSection(types = pokemon.types)
        PokemonDetailDataSection(
            pokemonWeight = pokemon.weight,
            pokemonHeight = pokemon.height
        )
        PokemonBaseStats(pokemon = pokemon)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PokemonTypeSection(types: List<Type>) {
    LazyVerticalGrid(
        cells = GridCells.Fixed(types.size),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(types) { type ->
            Text(
                text = type.type.name.capitalize(Locale.current),
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .background(shape = CircleShape, color = parseTypeToColor(type))
                    .padding(8.dp)
                    .fillMaxSize(),
            )
        }
    }
}


@Composable
fun PokemonDetailDataSection(
    pokemonWeight: Int,
    pokemonHeight: Int,
    sectionHeight: Dp = 80.dp
) {
    val pokemonWeightInKg = remember {
        round(pokemonWeight / 10f)
    }
    val pokemonHeightInMeters = remember {
        round(pokemonHeight / 10f)
    }

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        PokemonDetailDataItem(
            dataValue = pokemonWeightInKg,
            dataUnit = "kg",
            dataIcon = Icons.Default.FitnessCenter,
            modifier = Modifier.weight(1f)
        )
        Spacer(
            modifier = Modifier
                .size(1.dp, sectionHeight)
                .background(Color.LightGray)
        )
        PokemonDetailDataItem(
            dataValue = pokemonHeightInMeters,
            dataUnit = "m",
            dataIcon = Icons.Default.Height,
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
fun PokemonDetailDataItem(
    dataValue: Float,
    dataUnit: String,
    dataIcon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Icon(
            imageVector = dataIcon,
            contentDescription = null,
            tint = MaterialTheme.colors.onSurface,
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$dataValue $dataUnit",
            color = MaterialTheme.colors.onSurface
        )
    }
}


@Composable
fun PokemonBaseStats(
    pokemon: Pokemon,
    animDelayPerItem: Int = 100
) {
    val maxBaseStat = remember {
        pokemon.stats.maxOf { it.baseStat }
    }

    Column(
        Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Base Stats",
            color = MaterialTheme.colors.onSurface,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(
            contentPadding = PaddingValues(16.dp)
        ) {
            itemsIndexed(pokemon.stats) { index: Int, stat: Stat ->
                PokemonStats(
                    statName = parseStatToAbbr(stat),
                    statValue = stat.baseStat,
                    statMaxValue = maxBaseStat,
                    statColor = parseStatToColor(stat),
                    animDelay = index * animDelayPerItem
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun PokemonStats(
    statName: String,
    statValue: Int,
    statMaxValue: Int,
    statColor: Color,
    height: Dp = 28.dp,
    animDuration: Int = 1000,
    animDelay: Int = 0
) {
    var animationPlayed by remember {
        mutableStateOf(false)
    }
    val curPercent = animateFloatAsState(
        targetValue = if (animationPlayed) (statValue / statMaxValue.toFloat()) else 0f,
        animationSpec = tween(
            animDuration, animDelay
        )
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(shape = CircleShape, color = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(curPercent.value)
                .background(shape = CircleShape, color = statColor)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = statName,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 16.sp
            )
            Text(
                text = (curPercent.value * statMaxValue).toInt().toString(),
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 16.sp
            )
        }
    }

}