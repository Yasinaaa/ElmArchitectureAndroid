package ru.skillbranch.sbdelivery.screens.dish.logic

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import ru.skillbranch.sbdelivery.repository.DishRepository
import ru.skillbranch.sbdelivery.screens.root.logic.Eff
import ru.skillbranch.sbdelivery.screens.root.logic.IEffectHandler
import ru.skillbranch.sbdelivery.screens.root.logic.Msg
import javax.inject.Inject

class DishEffHandler @Inject constructor(
    private val repository: DishRepository,
    private val notifyChannel: Channel<Eff.Notification>
) : IEffectHandler<DishFeature.Eff, Msg> {

    private val errHandler = CoroutineExceptionHandler { _, t ->
        t.printStackTrace()
        t.message?.let { notifyChannel.trySend(Eff.Notification.Error(it)) }
    }

    override suspend fun handle(effect: DishFeature.Eff, commit: (Msg) -> Unit) {

        withContext(Dispatchers.Default + errHandler) {
            when (effect) {

                is DishFeature.Eff.AddToCart -> {
                    val count = repository.addToCart(effect.id, effect.count)
//                    val count = repository.cartCount()
                    commit(Msg.UpdateCartCount(count))


                    notifyChannel.send(
                        Eff.Notification.Text("В корзину добавлено ${count} товаров")
                    )
                }

                is DishFeature.Eff.LoadDish -> {
                    val dish = repository.findDish(effect.dishId)
                    commit(DishFeature.Msg.ShowDish(dish).toMsg())
                }

                is DishFeature.Eff.LoadReviews -> {
                    try {
                        val reviews = repository.loadReviews(effect.dishId)
                        commit(DishFeature.Msg.ShowReviews(reviews).toMsg())
                    } catch (t: Throwable) {
                        notifyChannel.send(
                            Eff.Notification.Error(t.message ?: "something error")
                        )
                    }
                }

                is DishFeature.Eff.SendReview -> {
                    val review = repository.sendReview(
                        effect.id, effect.rating, effect.review
                    )
                    repository.loadReviews(effect.id)
                        .plus(review)
                        .let(DishFeature.Msg::ShowReviews)
                        .let(Msg::Dish)
                        .also(commit)
                    notifyChannel.send(Eff.Notification.Text("Отзыв успешно отправлен"))
                }

                is DishFeature.Eff.Terminate -> {
                    //localJob.cancel("Terminate coroutine scope")
                    //localJob = null
                }
            }
        }

    }

    private fun DishFeature.Msg.toMsg(): Msg = Msg.Dish(this)


}



