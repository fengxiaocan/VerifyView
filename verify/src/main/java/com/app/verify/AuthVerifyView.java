package com.app.verify;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

/**
 * 自动生成的验证码生成器
 */

public class AuthVerifyView extends View implements View.OnClickListener {
	
	//默认验证码的字符串范围
	public static final String VERIF_TEXT = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	// 默认验证码长度
	public static final int VERIFY_LENGHT = 4;
	//默认背景色
	public static final @ColorInt
	int DEFAULT_VERIFY_BACKGROUND = 0xff0ba62c;
	//默认字体色
	public static final @ColorInt
	int DEFAULT_VERIFY_COLOR = 0xffffffff;
	//默认画笔粗细
	public static final int VERIFY_STROKE = 5;
	// 默认验证码字体大小
	public static final float VERIFY_SIZE = 16F;
	
	// 验证码的长度
	public int mVerifyLenght = VERIFY_LENGHT;
	//文本
	protected String mVerifyText;
	// 文本的颜色
	protected @ColorInt
	int mVerifyTextColor;
	// 文本的大小
	protected int mVerifyTextSize;
	//背景颜色
	protected @ColorInt
	int mBackgroundColor;
	//画笔粗细
	protected int mVerifyStroke;
	//字符串范围
	protected String mVerifyCode;
	//划线粗细
	protected int mBezierStroke;
	
	private String[] mCheckNum;
	private Random random = new Random();
	//绘制时控制文本绘制的范围
	private Rect mBound;
	
	private Paint mPaint;
	
	private boolean isReflash = true;
	private OnClickListener mOnClickListener;
	
	public AuthVerifyView(Context context,AttributeSet attrs) {
		this(context,attrs,0);
	}
	
	
	public AuthVerifyView(Context context) {
		this(context,null);
	}
	
	/**
	 * 获得我自定义的样式属性
	 *
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public AuthVerifyView(Context context,AttributeSet attrs,int defStyle) {
		super(context,attrs,defStyle);
		init(context,attrs,defStyle);
	}
	
	private void init(Context context,AttributeSet attrs,int defStyle) {
		/**
		 * 获得我们所定义的自定义样式属性
		 */
		TypedArray type = context.getTheme()
		                         .obtainStyledAttributes(attrs,R.styleable.AuthVerifyView,defStyle,
		                                                 0);
		// 默认颜色设置为白色
		mVerifyTextColor = type
				.getColor(R.styleable.AuthVerifyView_verifColor,DEFAULT_VERIFY_COLOR);
		// 默认设置为16sp，TypeValue也可以把sp转化为px
		mVerifyTextSize = type
				.getDimensionPixelSize(R.styleable.AuthVerifyView_verifSize,sp2px(VERIFY_SIZE));
		// 默认背景色是绿色
		mBackgroundColor = type
				.getColor(R.styleable.AuthVerifyView_verifBackground,DEFAULT_VERIFY_BACKGROUND);
		// 默认验证码长度
		mVerifyLenght = type.getInteger(R.styleable.AuthVerifyView_verifLenght,VERIFY_LENGHT);
		// 默认验证码粗细
		mVerifyStroke = type.getInteger(R.styleable.AuthVerifyView_verifStroke,VERIFY_STROKE);
		// 默认划线粗细
		mBezierStroke = type.getInteger(R.styleable.AuthVerifyView_verifBezierStroke,VERIFY_STROKE);
		// 默认验证码范围
		mVerifyCode = type.getString(R.styleable.AuthVerifyView_verifText);
		if (TextUtils.isEmpty(mVerifyCode)) {
			mVerifyCode = VERIF_TEXT;
		}
		type.recycle();
		if (isReflash) { randomText(); }
		/**
		 * 获得绘制文本的宽和高
		 */
		mPaint = new Paint();
		mPaint.setTextSize(mVerifyTextSize);
		mPaint.setStrokeWidth(mVerifyStroke);
		mBound = new Rect();
		mPaint.getTextBounds(mVerifyText,0,mVerifyText.length(),mBound);
		
		super.setOnClickListener(this);
	}
	
	@Override
	public void setOnClickListener(@Nullable OnClickListener l) {
		mOnClickListener = l;
		super.setOnClickListener(this);
	}
	
	//随机产生验证码
	public void randomText() {
		mCheckNum = new String[mVerifyLenght];
		StringBuffer sbReturn = new StringBuffer();
		for (int i = 0;i < mVerifyLenght;i++) {
			StringBuffer sb = new StringBuffer();
			char randomInt = mVerifyCode.charAt(random.nextInt(mVerifyCode.length()));
			mCheckNum[i] = sb.append(randomInt).toString();
			sbReturn.append(randomInt);
		}
		mVerifyText = sbReturn.toString();
	}
	
	
	//重写这个方法，设置自定义view控件的大小
	@Override
	protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec) {
		// super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = 0;
		int height = 0;
		
		/**
		 * 设置宽度
		 */
		int specMode = MeasureSpec.getMode(widthMeasureSpec);
		int specSize = MeasureSpec.getSize(widthMeasureSpec);
		switch (specMode) {
			case MeasureSpec.EXACTLY:// 明确指定了
				width = getPaddingLeft() + getPaddingRight() + specSize;
				break;
			case MeasureSpec.AT_MOST:// 一般为WARP_CONTENT
				width = getPaddingLeft() + getPaddingRight() + mBound.width();
				break;
		}
		
		/**
		 * 设置高度
		 */
		specMode = MeasureSpec.getMode(heightMeasureSpec);
		specSize = MeasureSpec.getSize(heightMeasureSpec);
		switch (specMode) {
			case MeasureSpec.EXACTLY:// 明确指定了
				height = getPaddingTop() + getPaddingBottom() + specSize;
				break;
			case MeasureSpec.AT_MOST:// 一般为WARP_CONTENT
				height = getPaddingTop() + getPaddingBottom() + mBound.height();
				break;
		}
		setMeasuredDimension(width,height);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		//画背景颜色
		mPaint.reset();
		mPaint.setTextSize(mVerifyTextSize);
		mPaint.setColor(mBackgroundColor);
		canvas.drawRect(0,0,getMeasuredWidth(),getMeasuredHeight(),mPaint);
		
		//划线
		mPaint.setColor(mVerifyTextColor);
		mPaint.setStrokeWidth(mBezierStroke);
		mPaint.setTextAlign(Paint.Align.CENTER);
		//绘制验证控件上的文本
		int dx = getWidth() / mVerifyLenght + mBound.width() / mVerifyLenght;
		for (int i = 0;i < mVerifyLenght;i++) {
			mPaint.setTextSkewX((float)((Math.random()) * 0.3 - 0.3));
			canvas.drawText("" + mCheckNum[i],dx,getHeight() / 2 + mBound.height() / 2,mPaint);
			dx += mBound.width() / mVerifyLenght + mBound.width() / mVerifyLenght / 2;
		}
		
		drawBezier(canvas);
		isReflash = false;
	}
	
	private void drawBezier(Canvas canvas) {
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setPathEffect(new CornerPathEffect(30));
		mPaint.setColor(mVerifyTextColor);
		mPaint.setStyle(Paint.Style.STROKE);
		Path p = new Path();
		p.moveTo(getWidth() / 4 - getWidth() / 8,getHeight() / 2 + getHeight() / 6);
		p.cubicTo(getWidth() / 2,0,getWidth() * 3 / 4,getHeight(),
		          getWidth() * 3 / 4 + getWidth() / 8,getHeight() / 2 - getHeight() / 6);
		canvas.drawPath(p,mPaint);
	}
	
	
	@Override
	public void onClick(View v) {
		if (mOnClickListener != null) {
			mOnClickListener.onClick(v);
		}
		isReflash = true;
		randomText();
		postInvalidate();
	}
	
	/**
	 * 检验验证码
	 */
	public boolean checkVerify(String code,boolean ignoreCase) {
		if (ignoreCase) {
			return equalsIgnoreCase(mVerifyText,code);
		}
		else {
			return TextUtils.equals(mVerifyText,code);
		}
	}
	
	/**
	 * sp转px
	 *
	 * @param spValue sp值
	 * @return px值
	 */
	public int sp2px(float spValue) {
		final float fontScale = getResources().getDisplayMetrics().scaledDensity;
		return (int)(spValue * fontScale + 0.5f);
	}
	
	/**
	 * 判断两字符串忽略大小写是否相等
	 *
	 * @param a 待校验字符串a
	 * @param b 待校验字符串b
	 * @return {@code true}: 相等<br>{@code false}: 不相等
	 */
	public boolean equalsIgnoreCase(String a,String b) {
		return a == null ? b == null : a.equalsIgnoreCase(b);
	}
	
	/**
	 * 获取验证码
	 */
	public String getVerifyCode() {
		return mVerifyText;
	}
	
	/**
	 * 设置验证码的范围
	 */
	public void setVerifyCode(String verifyCode) {
		mVerifyCode = verifyCode;
	}
	
	/**
	 * 验证码的长度
	 */
	public void setVerifyLenght(int verifyLenght) {
		mVerifyLenght = verifyLenght;
	}
	
	/**
	 * 设置验证码颜色
	 */
	public void setVerifyTextColor(@ColorInt int verifyTextColor) {
		mVerifyTextColor = verifyTextColor;
	}
	
	/**
	 * 设置验证码的字体大小
	 */
	public void setVerifyTextSize(int verifyTextSize) {
		mVerifyTextSize = verifyTextSize;
	}
	
	/**
	 * 设置验证码的背景色
	 */
	public void setVerifyBackgroundColor(@ColorInt int backgroundColor) {
		mBackgroundColor = backgroundColor;
	}
	
	/**
	 * 设置验证码的粗细
	 */
	public void setVerifyStroke(int verifyStroke) {
		mVerifyStroke = verifyStroke;
	}
	
	/**
	 * 设置滑线的粗细
	 */
	public void setBezierStroke(int bezierStroke) {
		mBezierStroke = bezierStroke;
	}
}
