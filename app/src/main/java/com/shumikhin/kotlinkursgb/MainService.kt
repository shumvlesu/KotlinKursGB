package com.shumikhin.kotlinkursgb

import android.app.IntentService
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

private const val TAG = "MainServiceTAG"
const val MAIN_SERVICE_STRING_EXTRA = "MainServiceExtra"
const val MAIN_SERVICE_INT_EXTRA = "MainServiceIntExtra" //Для броадкаста

//Обычный сервис запускает задачу в основном потоке, что плохо. А IntentService запускает в бэкграунде.
class MainService(name: String = "MainService") : IntentService(name) {

    override fun onHandleIntent(intent: Intent?) {
        //createLogMessage("onHandleIntent ${intent?.getStringExtra(MAIN_SERVICE_STRING_EXTRA)}")
        intent?.let {
            sendBack(it.getIntExtra(MAIN_SERVICE_INT_EXTRA, 0).toString())
        }
    }

    override fun onCreate() {
        createLogMessage("onCreate")
        super.onCreate()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createLogMessage("onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onDestroy() {
        createLogMessage("onDestroy")
        super.onDestroy()
    }
    //Выводим уведомление в строке состояния
    private fun createLogMessage(message: String) {
        Log.d(TAG, message)
    }

    //Отправка уведомления о завершении сервиса
    private fun sendBack(result: String) {
        val broadcastIntent = Intent(TEST_BROADCAST_INTENT_FILTER)
        broadcastIntent.putExtra(THREADS_FRAGMENT_BROADCAST_EXTRA, result)
        //Внимание! Если вам не нужно отсылать броадкасты наружу, чтобы их принимали другие
        //приложения, — об этом более подробно в факультативной части урока — то лучше использовать
        //LocalBroadcastManager. Он более эффективен в пересылке сообщений внутри приложения.

        //sendBroadcast(broadcastIntent)
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
    }

}