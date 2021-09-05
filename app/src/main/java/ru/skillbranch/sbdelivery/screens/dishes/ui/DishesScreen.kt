package ru.skillbranch.sbdelivery.screens.dishes.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.screens.dishes.data.DishesUiState
import ru.skillbranch.sbdelivery.screens.dishes.logic.DishesFeature
import ru.skillbranch.sbdelivery.screens.root.logic.Msg

@Composable
fun DishesScreen(state: DishesFeature.State, accept: (Msg) -> Unit) {
    when (state.list) {

        is DishesUiState.Empty -> Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                Image(
                    painter = painterResource(id = R.drawable.ic_ufo_not_found),
                    contentDescription = "empty",
                    modifier = Modifier.requiredSize(200.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Ничего не найдено (", color = MaterialTheme.colors.onBackground)
            }
        }


        is DishesUiState.Loading -> Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(color = MaterialTheme.colors.secondary)
        }

        is DishesUiState.Value -> LazyGrid(items = state.list.dishes) {
            DishItem(dish = it,
                onClick = { accept(Msg.ClickDish(it.id, it.title))},
                addToCart = { accept(Msg.AddToCart(it.id, it.title))}
            )
        }
    }
}