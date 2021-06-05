package com.shumikhin.kotlinkursgb

import android.content.Intent
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
import com.shumikhin.kotlinkursgb.databinding.FragmentThreadsBinding
import java.util.*

class ThreadsFragment : Fragment() {

    private var _binding: FragmentThreadsBinding? = null
    private val binding get() = _binding!!
    private var counterThread = 0


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

        initServiceButton()

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
