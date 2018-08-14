# DownloadButton
一款支持多种样式的下载按钮，效果图如下
![image](https://github.com/Kunsan16/DownloadButton/blob/master/download.gif)
###1.安装

###2.使用
####xml
 ```xml
      <com.kunsan.library.DownLoadButton
        android:id="@+id/btn_download2"
        android:layout_width="160dp"
        android:layout_height="46dp"
        app:border_width="2dp"
        android:layout_marginTop="10dp"
        app:border_color="@color/colorPrimary"
        app:loading_progress_color="@color/colorPrimary"
        app:percent_show="false"
        app:install_color="@color/green"
        app:progress_textSize="18sp"
        app:text_color="@color/colorPrimary"
        />
```
####kotlin
##### 注册下载按钮的监听，在相应的回调方法里做处理，以下为rxjava模拟下载过程（代码没贴全，详细移步[MainActivity ](https://github.com/Kunsan16/DownloadButton/blob/master/app/src/main/java/com/kunsan/downloadbutton/MainActivity.kt)）:
```kotlin
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
```

 方法名 | 备注
| --- | ---
| border_width | 按钮边框粗细
| border_color | 边框颜色
| border_radius | 边框四个角度
| loading_progress_color | 下载进度条颜色
| install_color | 下载完成后按钮背景色
| progress_textSize | 进度条上的文字
| text_color | 进度条文字颜色
| percent_show | 进度条上显示文字或是进度百分比（true：百分比）
| border_show | 是否显示边框
