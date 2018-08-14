package com.kunsan.library

import android.content.Context
import android.graphics.*
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar

/**
 * Created by moge on 2018/8/14.
 */
class DownLoadButton: ProgressBar, View.OnClickListener {


    private var mPaint= Paint()                         //按钮画笔
    private var textPaint = Paint()                     //文字画笔
    private var mProgressPath = Path()                  //进度path

    private var mValidWidth:Int = 0
    private var mValidHeight:Int = 0
    private var mBorderColor : Int = Color.BLUE         //边框颜色
    private var mBorderRadius : Float = 0f              //边框四个角的角度
    private var mBorderWidth : Float = 0f               //边框的粗细
    private var mTextSize : Float = 0f                  //文字大小
    private var mTextColor : Int = Color.BLUE           //文字颜色
    private var mProgressColor : Int = Color.RED        //进度条颜色
    private var mInstallColor : Int = Color.GREEN       //下载完成后背景颜色
    private var showPercent : Boolean = true            //是否显示百分比（默认显示百分比）
    private var showBorder : Boolean = true             //是否显示边框（默认显示）

    private var mCurrentState:Int = STATE_PROGRESS_DEFAULT

    companion object {
        val STATE_PROGRESS_DEFAULT        = 0           //默认状态
        val STATE_PROGRESS_DOWNLOADING    = 1           //下载中
        val STATE_PROGRESS_PAUSE          = 2           //暂停
        val STATE_PROGRESS_FINISH         = 3           //完成
    }


    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int):  super(context, attrs,defStyleAttr) {

        initAttrs(context,attrs)
        initView()

        setOnClickListener(this)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.DownloadProgressButton)
        mBorderRadius     = ta.getDimension(R.styleable.DownloadProgressButton_border_radius,0f)
        mBorderWidth      = ta.getDimension(R.styleable.DownloadProgressButton_border_width,6f)
        mBorderColor      = ta.getColor(R.styleable.DownloadProgressButton_border_color, Color.BLUE)
        mTextSize         = ta.getDimension(R.styleable.DownloadProgressButton_progress_textSize, 46f)
        mTextColor        = ta.getColor(R.styleable.DownloadProgressButton_text_color, Color.RED)
        mProgressColor    = ta.getColor(R.styleable.DownloadProgressButton_loading_progress_color, Color.RED)
        mInstallColor     = ta.getColor(R.styleable.DownloadProgressButton_install_color, Color.GREEN)
        showPercent       = ta.getBoolean(R.styleable.DownloadProgressButton_percent_show,true)
        showBorder        = ta.getBoolean(R.styleable.DownloadProgressButton_border_show,true)
        ta.recycle()
    }

    private fun initView() {
        mPaint.isAntiAlias = true
        textPaint.textSize = mTextSize
        textPaint.isAntiAlias = true
    }



    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mValidWidth = width - paddingLeft - paddingRight
        mValidHeight = height - paddingTop - paddingBottom
    }

    /**
     * 处理wrap_content
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val mWidth = (textPaint.measureText("下载")+mBorderWidth * 2).toInt()
        val mHeight =(textPaint.descent() +  Math.abs(textPaint.ascent())+mBorderWidth * 2).toInt()

        // 当布局参数设置为wrap_content时，宽高设置为按钮内文本的宽高（加上边框mBorderWidth粗细）
        if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT && layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, mHeight)
        } else if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, heightSize)
        } else if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, mHeight)
        }

    }


    override fun onDraw(canvas: Canvas) {
        when(mCurrentState){
            STATE_PROGRESS_DEFAULT       ->     drawDefaultProgress(canvas)
            STATE_PROGRESS_DOWNLOADING   ->     drawDownLoadingProgress(canvas)
            STATE_PROGRESS_PAUSE         ->     drawPause(canvas)
            STATE_PROGRESS_FINISH        ->     drawFinish(canvas)
        }
    }


    override fun onClick(v: View?) {

        if (progress == 0 && mCurrentState == STATE_PROGRESS_DEFAULT) {
            //默认状态到开始下载
            mCurrentState = STATE_PROGRESS_DOWNLOADING
            mStateChangeListener!!.onTaskLoading()
        }else if (progress in 0..max && mCurrentState == STATE_PROGRESS_DOWNLOADING){
            //暂停
            mCurrentState = STATE_PROGRESS_PAUSE
            mStateChangeListener!!.onTaskPause()
        }else if (progress in 0..max && mCurrentState == STATE_PROGRESS_PAUSE){
            //继续下载
            mCurrentState = STATE_PROGRESS_DOWNLOADING
            mStateChangeListener!!.onTaskLoading()
        }else if (progress == max && mCurrentState == STATE_PROGRESS_FINISH){
            //下载完成
            mCurrentState = STATE_PROGRESS_FINISH
            mStateChangeListener!!.onTaskFinish()
        }
    }


    /**
     * 按钮默认状态
     */
    private fun drawDefaultProgress(canvas: Canvas?) {
        drawRectFBackground(canvas,mBorderColor)
        drawProgressText(canvas,"下载")
    }


    /**
     * 下载中的进度显示
     */
    private fun drawDownLoadingProgress(canvas: Canvas) {
        showBorder = true   //走进度时默认显示边框
        drawRectFBackground(canvas, mBorderColor)
        drawProgress(canvas,mProgressColor)
        drawProgressText(canvas,if (showPercent)"" else "暂停")
        drawGradientText(canvas,if (showPercent)"" else "暂停")
        if (progress == max){
            mCurrentState = STATE_PROGRESS_FINISH
            postInvalidateDelayed(20)
        }
    }


    /**
     * 绘制下载暂停
     */
    private fun drawPause(canvas: Canvas) {
        drawRectFBackground(canvas,mBorderColor)
        drawProgress(canvas,mProgressColor)
        drawProgressText(canvas,"继续")
        drawGradientText(canvas,"继续")
    }


    /**
     * 绘制下载完成
     */
    private fun drawFinish(canvas: Canvas) {
        drawRectFBackground(canvas,mInstallColor)
        drawProgress(canvas,mInstallColor)
        showBorder = false
        drawProgressText(canvas,"安装")
    }


    /**
     * PorterDuffXfermode绘制进度条
     */
    private fun drawProgress(canvas: Canvas?, mProgressColor:Int) {

        mPaint.style = Paint.Style.FILL

        val progress = mValidWidth * (progress * 1.0f / max)
        val layer = canvas!!.saveLayer(0f,0f,progress,height.toFloat(),mPaint)

        drawRoundRectPath(canvas)
        mPaint.color = mProgressColor

        drawProgressPath(progress)              //绘制src层的path
        mPaint.xfermode = mPorterDuffMode
        canvas.drawPath(mProgressPath,mPaint)   //绘制与dst层的重叠区域，也就是progress
        canvas.restoreToCount(layer)
        mPaint.xfermode = null

    }

    /**
     * 绘制矩形新图层
     */
    private fun drawRoundRectPath(canvas: Canvas) {

        val mRectF = RectF(mBorderWidth+2,mBorderWidth+2,mValidWidth.toFloat()-mBorderWidth-2,mValidHeight.toFloat()-mBorderWidth-2)
        canvas.drawRoundRect(mRectF, mBorderRadius, mBorderRadius, mPaint)

    }

    /**
     * 绘制src图层，也就是进度
     */
    private fun drawProgressPath(progress: Float) {

        mProgressPath.reset()
        val rectF = RectF(0f,0f,progress, mValidHeight.toFloat())
        mProgressPath.addRect(rectF, Path.Direction.CCW)

    }



    /**
     * 绘制按钮
     */
    private fun drawRectFBackground(canvas: Canvas?, color:Int){

        if (showBorder) {
            mPaint.style = Paint.Style.STROKE
        } else {
            mPaint.style = Paint.Style.FILL_AND_STROKE

        }
        mPaint.strokeWidth = mBorderWidth
        mPaint.color = color
        val rect = RectF(mBorderWidth,mBorderWidth,mValidWidth.toFloat()-mBorderWidth,mValidHeight.toFloat()-mBorderWidth)
        canvas!!.drawRoundRect(rect,mBorderRadius,mBorderRadius,mPaint)
    }

    /**
     * 绘制进度文本
     */
    private fun drawProgressText(canvas: Canvas?, text:String) {

        textPaint.color = if (showBorder) mTextColor else Color.WHITE

        val progressText = if (showPercent && TextUtils.isEmpty(text)) getPercent() else text
        val textWidth = textPaint.measureText(progressText)
        val textHeight = textPaint.descent() + textPaint.ascent()
        canvas!!.drawText(progressText,mValidWidth / 2 - textWidth / 2,mValidHeight / 2 - textHeight / 2,textPaint)


    }

    /**
     * 绘制变色文本
     */
    private fun drawGradientText(canvas: Canvas, text:String){
        textPaint.color = Color.WHITE
        val progressText = if (showPercent && TextUtils.isEmpty(text)) getPercent() else text
        val textWidth = textPaint.measureText(progressText)
        val textHeight = textPaint.descent() + textPaint.ascent()
        val xCoordinate = (measuredWidth - textWidth) / 2
        val yCoordinate = mValidHeight / 2 - textHeight / 2
        val progressWidth = (progress - 100 / max) * measuredWidth

        if (progressWidth / 100 > xCoordinate)

        canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())
        canvas.clipRect(xCoordinate,0f,(progressWidth / 100).toFloat(),measuredHeight.toFloat())
        canvas.drawText(progressText,xCoordinate,yCoordinate,textPaint)

    }


    /**
     * 获取当前进度百分比
     */
    private fun getPercent() = TextUtils.concat(calculatePercent().toString(),"%").toString()

    /**
     * 计算进度百分比
     */
    private fun calculatePercent() = (100 * (progress * 1.0f / max)).toInt()


    private var mPorterDuffMode =  PorterDuffXfermode(PorterDuff.Mode.SRC_IN)


    /**
     * 更新下载按钮当前下载状态
     */
    fun setState(state:Int){

        this.mCurrentState=state
        postInvalidateDelayed(10)

    }


    private var mStateChangeListener : StateChangeListener? = null

    fun setStateChangeListener(mStateChangeListener:StateChangeListener){

        this.mStateChangeListener = mStateChangeListener
    }

    interface StateChangeListener  {

        fun onTaskPause()        //暂停下载

        fun onTaskFinish()       //下载完成

        fun onTaskLoading()      //开始下载

    }



}