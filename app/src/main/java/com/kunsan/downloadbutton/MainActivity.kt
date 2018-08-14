package com.kunsan.downloadbutton

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.kunsan.library.DownLoadButton
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by moge on 2018/8/14.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerButton(btn_download1)
        registerButton(btn_download2)
        registerButton(btn_download3)
        registerButton(btn_download4)
    }


    private val map = ConcurrentHashMap<DownLoadButton, Disposable>(4)

    private fun registerButton(btn_download: DownLoadButton) {
        btn_download.setStateChangeListener(object : DownLoadButton.StateChangeListener {
            override fun onTaskPause() {

                map[btn_download]!!.dispose()    //取出对应的dispose，暂停事件流
                btn_download.setState(DownLoadButton.STATE_PROGRESS_PAUSE)
            }

            override fun onTaskFinish() {

                btn_download.setState(DownLoadButton.STATE_PROGRESS_FINISH)
                Toast.makeText(this@MainActivity, "正在安装...", Toast.LENGTH_SHORT).show()
            }

            override fun onTaskLoading() {
                dispose = downloadTest(btn_download)
                dispose?.let {
                    map[btn_download] = it   //将四个按钮各自的dispose存起来
                }
            }

        })
    }

     var dispose: Disposable? = null



    private fun downloadTest(btn_download: DownLoadButton): Disposable {
        btn_download.max = 100
        return Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .filter { t -> t < 10 }
                .map { t -> t.toInt() }
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {

                    btn_download.setState(DownLoadButton.STATE_PROGRESS_DOWNLOADING)
                    when (btn_download) {
                        btn_download1 -> onProgress1(btn_download)
                        btn_download2 -> onProgress2(btn_download)
                        btn_download3 -> onProgress3(btn_download)
                        btn_download4 -> onProgress4(btn_download)
                    }

                }

    }


    private var percent1: Int = 0
    private fun onProgress1(btn_download: DownLoadButton) {
        percent1 += 10

        btn_download.progress = percent1
    }

    private var percent2: Int = 0
    private fun onProgress2(btn_download: DownLoadButton) {
        percent2 += 10

        btn_download.progress = percent2
    }


    private var percent3: Int = 0
    private fun onProgress3(btn_download: DownLoadButton) {
        percent3 += 10

        btn_download.progress = percent3
    }

    private var percent4: Int = 0
    private fun onProgress4(btn_download: DownLoadButton) {
        percent4 += 10

        btn_download.progress = percent4
    }


    override fun onDestroy() {
        super.onDestroy()

        dispose!!.dispose()
    }
}