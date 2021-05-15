package com.shumikhin.kotlinkursgb

import androidx.fragment.app.Fragment

interface View {
    fun showLoading()
    //fun showError()
    //fun showSuccess()
}

//какой то фрагмент
class MVPFragment : Fragment(), View {

    private val presenter = Presenter(Model())

    //фрагмент готов для работы с ним
    override fun onResume() {
        super.onResume()
        presenter.view = this
    }

    //Фрагмент не готов для работы с ним. Занчит презентр должен знать об этом, делаем  view в presenter - null
    override fun onPause() {
        super.onPause()
        presenter.view = null
    }


    //метод фрагмента
    //onCreate()

    //отображаем загрузку
    override fun showLoading() {
        //progressBar.visibility = View.VISIBLE
    }


    //метод логирования (MVPFragment) который срабатывает при нажатии какой либо кнопки во фрагменте
    private fun login(username: String, password: String) {
        presenter.login(username, password)
    }
}

class Presenter(private val model: Model) {

    var view: View? =
        null //Presenter живет обычно дольше чем активити или фрагмент. И презентр тогда с вьюхой взаимодействовать не должен

    fun login(username: String, password: String) {
        if (username.isEmpty() && password.isEmpty()) {
            //view?.showError("Error message")
            return
        }

        view?.showLoading()

        val result = model.sendLogin(username, password)
        if (result) {
            //view?.showSuccess()
        } else {
            //view?.showError("Message")
        }
    }

}

class Model() {

    fun sendLogin(username: String, password: String): Boolean {
        //кудато заслал логин наш
        //call API
        return true
    }

}
