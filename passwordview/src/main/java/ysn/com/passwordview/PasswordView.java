package ysn.com.passwordview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.util.AttributeSet;

/**
 * @Author yangsanning
 * @ClassName PasswordView
 * @Description 一句话概括作用
 * @Date 2019/4/18
 * @History 2019/4/18 author: description:
 */
public class PasswordView extends AppCompatEditText {

    /**
     * 间隔
     */
    private int space;

    /**
     * 最大个数
     */
    private int maxCount;

    /**
     * 密码大小
     */
    private int circleSize;
    private boolean isCirclePassword;

    private int strokeColor;
    private int strokeWidth;

    private int frameColor, textColor;
    private int textSize;

    /**
     * 光标相关
     */
    private int cursorColor;
    private int cursorWidth, cursorHeight;
    private int cursorTwinkleTime;
    private boolean isCursorEnable, isCursorShowing;

    private int viewWidth, viewHeight;

    /**
     * 计算每个密码框宽度
     */
    private int rectWidth;

    /**
     * 密码框
     */
    private Rect frameRect;
    private Paint framePaint, frameBgPaint;

    private Paint textPaint;
    private Rect textRect;

    private Paint circlePaint;
    private Paint cursorPaint;

    /**
     * 输入结束监听
     */
    private OnFinishListener mOnFinishListener;
    private char[] texts = new char[]{};

    private Handler cursorHandler = new Handler();
    private Runnable cursorRunnable = new Runnable() {
        @Override
        public void run() {
            isCursorShowing = !isCursorShowing;
            postInvalidate();
            cursorHandler.postDelayed(this, cursorTwinkleTime);
        }
    };

    public PasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initPaint();
        initView();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PasswordView);
        maxCount = typedArray.getInteger(R.styleable.PasswordView_pv_max_count, 4);
        isCirclePassword = typedArray.getBoolean(R.styleable.PasswordView_pv_circle_password, false);
        circleSize = typedArray.getDimensionPixelSize(R.styleable.PasswordView_pv_circle_size, 10);
        strokeColor = typedArray.getColor(R.styleable.PasswordView_pv_stroke_color, getResources().getColor(R.color.pv_stroke_color));
        strokeWidth = typedArray.getDimensionPixelSize(R.styleable.PasswordView_pv_stroke_width, 2);
        frameColor = typedArray.getColor(R.styleable.PasswordView_pv_frame_color, getResources().getColor(R.color.pv_frame_color));
        textColor = typedArray.getColor(R.styleable.PasswordView_pv_text_color, getResources().getColor(R.color.pv_text_color));
        textSize = typedArray.getDimensionPixelSize(R.styleable.PasswordView_pv_text_size, 60);
        space = typedArray.getDimensionPixelSize(R.styleable.PasswordView_pv_space, 10);

        cursorColor = typedArray.getColor(R.styleable.PasswordView_pv_cursor_color, getResources().getColor(R.color.pv_cursor_color));
        cursorWidth = typedArray.getDimensionPixelSize(R.styleable.PasswordView_pv_cursor_width, 2);
        cursorTwinkleTime = typedArray.getInteger(R.styleable.PasswordView_pv_cursor_twinkle_time, 500);
        isCursorEnable = typedArray.getBoolean(R.styleable.PasswordView_pv_cursor_enable, true);

        typedArray.recycle();
    }

    private void initPaint() {
        // 初始化密码框
        framePaint = new Paint();
        framePaint.setStyle(Paint.Style.STROKE);
        framePaint.setColor(strokeColor);
        framePaint.setStrokeWidth(2f);
        framePaint.setAntiAlias(true);
        frameRect = new Rect();

        frameBgPaint = new Paint();
        frameBgPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        frameBgPaint.setColor(frameColor);
        frameBgPaint.setAntiAlias(true);

        // 初始化文字画笔
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textRect = new Rect();

        // 初始化密码画笔
        circlePaint = new Paint();
        circlePaint.setColor(textColor);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setAntiAlias(true);

        cursorPaint = new Paint();
        cursorPaint.setColor(cursorColor);
        cursorPaint.setStrokeWidth(cursorWidth);
        cursorPaint.setStyle(Paint.Style.FILL);
        cursorPaint.setAntiAlias(true);
    }

    private void initView() {
        setEnabled(true);
        setFocusable(true);
        setCursorVisible(false);
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxCount)});
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;

        rectWidth = (viewWidth - space * (maxCount - 1)) / maxCount;
        cursorHeight = viewHeight / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 绘制密码框
        for (int i = 0; i < maxCount; i++) {
            frameRect.left = (rectWidth + space) * i + strokeWidth;
            frameRect.top = strokeWidth;
            frameRect.right = frameRect.left + rectWidth - strokeWidth;
            frameRect.bottom = viewHeight - strokeWidth;
            canvas.drawRect(frameRect, frameBgPaint);
            canvas.drawRect(frameRect, framePaint);
        }

        // 绘制密码
        for (int i = 0; i < texts.length; i++) {
            int cx;
            int cy;
            if (isCirclePassword) {
                cx = (rectWidth >> 1) + (rectWidth + space) * i;
                cy = ((viewHeight) + circleSize) >> 1;
                canvas.drawCircle(cx, cy, circleSize, circlePaint);
            } else {
                String text = String.valueOf(texts[i]);
                textPaint.getTextBounds(text, 0, text.length(), textRect);
                cx = ((rectWidth - textRect.right) >> 1) + (rectWidth + space) * i;
                cy = ((viewHeight) + textRect.height()) >> 1;
                canvas.drawText(text, cx, cy, textPaint);
            }
        }

        if (isCursorEnable && isCursorShowing && texts.length != maxCount) {
            int x = (rectWidth >> 1) + (rectWidth + space) * texts.length;
            int startY = (viewHeight - cursorHeight) >> 1;
            int stopY = startY + cursorHeight;
            canvas.drawLine(x, startY, x, stopY, cursorPaint);
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start,
                                 int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        texts = text.toString().toCharArray();
        invalidate();
        if (texts.length == maxCount && mOnFinishListener != null) {
            mOnFinishListener.onFinish(text.toString());
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startCursor();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopCursor();
    }

    public void startCursor() {
        stopCursor();
        cursorHandler.post(cursorRunnable);
    }

    public void stopCursor() {
        cursorHandler.removeCallbacks(cursorRunnable);
    }

    public void setCirclePassword(boolean circlePassword) {
        isCirclePassword = circlePassword;
        invalidate();
    }

    public boolean isCirclePassword() {
        return isCirclePassword;
    }

    public void setOnFinishListener(OnFinishListener onFinishListener) {
        this.mOnFinishListener = onFinishListener;
    }

    /**
     * 密码输入结束监听
     */
    public interface OnFinishListener {

        void onFinish(String password);
    }
}