package com.shumikhin.kotlinkursgb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.shumikhin.kotlinkursgb.databinding.FragmentThreadsBinding
import java.util.*

const val TEST_BROADCAST_INTENT_FILTER = "TEST BROADCAST INTENT FILTER"
const val THREADS_FRAGMENT_BROADCAST_EXTRA = "THREADS_FRAGMENT_EXTRA"

class ThreadsFragment : Fragment() {

    private var _binding: FragmentThreadsBinding? = null
    private val binding get() = _binding!!
    private var counterThread = 0

    //Создаём свой BroadcastReceiver (получатель широковещательного сообщения)
    //Этот просто более компактная версия из MainBroadcastReceiver
    private val testReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //Достаём данные из интента
            intent.getStringExtra(THREADS_FRAGMENT_BROADCAST_EXTRA)?.let {
                addView(context, it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       //context?.registerReceiver(testReceiver, IntentFilter(TEST_BROADCAST_INTENT_FILTER))
        context?.let {
            LocalBroadcastManager.getInstance(it).registerReceiver(testReceiver, IntentFilter(TEST_BROADCAST_INTENT_FILTER))
        }
    }

    override fun onDestroy() {
        //context?.unregisterReceiver(testReceiver)
        context?.let {
            LocalBroadcastManager.getInstance(it).unregisterReceiver(testReceiver)
        }
        super.onDestroy()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThreadsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener {
            binding.textView.text = startCalculations(binding.editText.text.toString().toInt())
            binding.mainContainer.addView(AppCompatTextView(it.context).apply {
                text = getString(R.string.in_main_thread)
                textSize = resources.getDimension(R.dimen.main_container_text_size)
            })
        }

        //Приложение может упасть, потому что нельзя
        //обращаться к элементам UI не из UI-потока.
        //Используем - activity?.runOnUiThread чтобы сделать приложение потоко безопасным
        binding.calcThreadBtn.setOnClickListener {
            Thread {
                counterThread++
                val calculatedText = startCalculations(binding.editText.text.toString().toInt())
                activity?.runOnUiThread {
                    binding.textView.text = calculatedText
                    binding.mainContainer.addView(AppCompatTextView(it.context).apply {
                        text = String.format(getString(R.string.from_thread), counterThread)
                        textSize = resources.getDimension(R.dimen.main_container_text_size)
                    })
                }
            }.start()
        }


        //HandlerThread
        // При запуске через HandlerThread все операции выстраиваются в очередь
        val handlerThread = HandlerThread(getString(R.string.my_handler_thread))
        handlerThread.start()// Сатртуем HandlerThread и ждем какую либо задачу

        val handler = Handler(handlerThread.looper)

        binding.calcThreadHandler.setOnClickListener {
            binding.mainContainer.addView(AppCompatTextView(it.context).apply {
                text = String.format(getString(R.string.calculate_in_thread), handlerThread.name)
                textSize = resources.getDimension(R.dimen.main_container_text_size)
            })

            handler.post { //помещаем нашу задачу в HandlerThread
                startCalculations(binding.editText.text.toString().toInt())
                binding.mainContainer.post {//тут post - это помещение в основной UI поток
                    binding.mainContainer.addView(AppCompatTextView(it.context).apply {
                        text = String.format(getString(R.string.calculate_in_thread), Thread.currentThread().name)
                        textSize = resources.getDimension(R.dimen.main_container_text_size)
                    })
                }
            }

        }

        //Service
        initServiceButton()

        //BroadcastReceiver
        initServiceWithBroadcastButton()

    }


    //Service
    //Обычный сервис запускает задачу в основном потоке, что плохо. А IntentService запускает в бэкграунде.
    private fun initServiceButton() {
        binding.serviceButton.setOnClickListener {
            context?.let {
                it.startService(Intent(it, MainService::class.java).apply {
                    putExtra(MAIN_SERVICE_STRING_EXTRA, getString(R.string.hello_from_thread_fragment))
                })
            }
        }
    }

    private fun initServiceWithBroadcastButton() {
        binding.serviceWithBroadcastButton.setOnClickListener {
            context?.let {
                it.startService(Intent(it, MainService::class.java).apply {
                    putExtra(MAIN_SERVICE_INT_EXTRA, binding.editText.text.toString().toInt())
                })
            }
        }
    }

    private fun addView(context: Context, textToShow: String) {
        binding.mainContainer.addView(AppCompatTextView(context).apply {
            text = textToShow
            textSize = resources.getDimension(R.dimen.main_container_text_size)
        })
    }

    private fun startCalculations(seconds: Int): String {
        val date = Date()
        var diffInSec: Long
        do {
            val currentDate = Date()
            val diffInMs: Long = currentDate.time - date.time
            diffInSec = java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(diffInMs)
        } while (diffInSec < seconds)
        return diffInSec.toString()
    }

    companion object {
        fun newInstance() = ThreadsFragment()
    }
}
