package com.shumikhin.kotlinkursgb

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment


//Фрагменит (как элемент слоя View) следит за сотоянием ViewModel (за его переменной viewState)
//ViewModel о View ничего "не знает"
class MVVMFragment : Fragment() {

    private val viewModel = ViewModel(Model2())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Следим методом observe за изменением состояния экрана и потом вызываем render отображая во вьюхе изменения
        //observe(viewModel.viewState) // {render(viewState)}
    }

    //метод логирования (MVPFragment) который срабатывает при нажатии какой либо кнопки во фрагменте
    private fun login(username: String, password: String) {
        viewModel.login(username, password)
    }

    private fun render(viewState: ViewState) {
        //loadingBar.visibility = if (viewState.isLoading) View.VISIBLE else View.ONE
    }

}

//Создаем класс объект которого описывает состояние экрана в какой-то момент времени
//переменные не просто так val, они должны быть статичные. Изменеие их состояния в обекте мы производим копированием во ViewModel
//Иначе говоря ViewState это полное описание нашего экрана
data class ViewState(val isLoading: Boolean, val hasError: Boolean)

//ViewModel содержит в себе и бизнес логику и хранит состояние экрана (класс ViewState)
class ViewModel(private val model2: Model2) {
    var viewState = ViewState(false, false)

    fun login(username: String, password: String) {
        if (username.isEmpty() && password.isEmpty()) {
            //view?.showError("Error message")
            return
        }

        //копированием самого себя попутно изменяем состояние экрана
        viewState = viewState.copy(isLoading = true)

        val result = model2.sendLogin(username, password)
        if (result) {
            viewState = viewState.copy(isLoading = false, hasError = false)
        } else {
            viewState = viewState.copy(isLoading = false, hasError = true)
        }
    }

}


class Model2() {

    fun sendLogin(username: String, password: String): Boolean {
        //кудато заслал логин наш
        //call API
        return true
    }

}