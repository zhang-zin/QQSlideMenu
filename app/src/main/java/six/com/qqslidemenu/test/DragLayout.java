package six.com.qqslidemenu.test;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;

import six.com.qqslidemenu.ColorUtil;
import six.com.qqslidemenu.R;

/**
 * Created by Administrator on 2018/6/9.
 */

public class DragLayout extends FrameLayout {

    private View redView;// 红孩子
    private View blueView;// 蓝精灵

    private ViewDragHelper mViewDragHelper;

    public DragLayout(Context context) {
        super(context);
        init();
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        mViewDragHelper = ViewDragHelper.create(this, callback);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        redView = getChildAt(0);
        blueView = getChildAt(1);
    }

    /*@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = (int) getResources().getDimension(R.dimen.width);
        int measureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
       *//* redView.measure(measureSpec, measureSpec);
        blueView.measure(measureSpec, measureSpec);*//*

        measureChild(redView, measureSpec, measureSpec);
        measureChild(blueView, measureSpec, measureSpec);
    }*/

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int left = getPaddingLeft();
        int top = getPaddingTop();
        redView.layout(left, top, left + redView.getMeasuredWidth(), top + redView.getMeasuredHeight());
        blueView.layout(left, top + redView.getBottom(),
                left + blueView.getMeasuredWidth(),
                top + redView.getBottom() + blueView.getMeasuredHeight());

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean b = mViewDragHelper.shouldInterceptTouchEvent(ev);
        return b;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 将触摸事件交给ViewDragHelper来解析处理
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        /**
         * 用于判断是否捕获当前child的触摸事件 child: 当前触摸的子View return: true:就捕获并解析 false：不处理
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == blueView || child == redView;
        }

        /**
         * 当view被开始捕获和解析的回调 capturedChild:当前被捕获的子view
         */
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
            // Log.e("tag", "onViewCaptured");
        }

        /**
         * 获取view水平方向的拖拽范围,但是目前不能限制边界,
         * 返回的值目前用在手指抬起的时候view缓慢移动的动画世界的计算上面; 最好不要返回0
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return getMeasuredWidth() - child.getMeasuredWidth();
        }

        /**
         * 获取view垂直方向的拖拽范围，最好不要返回0
         */
        @Override
        public int getViewVerticalDragRange(View child) {
            return getMeasuredHeight() - child.getMeasuredHeight();
        }

        /**
         * 控制child在水平方向的移动 left:
         * 表示ViewDragHelper认为你想让当前child的left改变的值,left=chile.getLeft()+dx dx:
         * 本次child水平方向移动的距离 return: 表示你真正想让child的left变成的值
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (left < 0) {
                left = 0;
            } else if (left > getMeasuredWidth() - child.getMeasuredWidth()) {
                left = getMeasuredWidth() - child.getMeasuredWidth();
            }
            return left;
        }

        /**
         * 控制child在垂直方向的移动 top:
         * 表示ViewDragHelper认为你想让当前child的top改变的值,top=chile.getTop()+dy dy:
         * 本次child垂直方向移动的距离 return: 表示你真正想让child的top变成的值
         */
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            if (top < 0) {
                top = 0;
            } else if (top > getMeasuredHeight() - child.getMeasuredHeight()) {
                top = getMeasuredHeight() - child.getMeasuredHeight();
            }
            return top;
        }

        /**
         * 当child的位置改变的时候执行,一般用来做其他子View的伴随移动 changedView：位置改变的child
         * left：child当前最新的left top: child当前最新的top dx: 本次水平移动的距离 dy: 本次垂直移动的距离
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == redView) {
                blueView.layout(blueView.getLeft() + dx, blueView.getTop() + dy,
                        blueView.getRight() + dx, blueView.getBottom() + dy);
            } else if (changedView == blueView) {
                redView.layout(redView.getLeft() + dx, redView.getTop() + dy,
                        redView.getRight() + dx, redView.getBottom() + dy);
            }

            float fraction = changedView.getLeft() * 1f / (getMeasuredWidth() - changedView.getMeasuredWidth());
            Log.i("fraction", "onViewPositionChanged: " + fraction);
            executeAnim(fraction);
        }

        /**
         * 手指抬起的执行该方法， releasedChild：当前抬起的view xvel: x方向的移动的速度 正：向右移动， 负：向左移动
         * yvel: y方向移动的速度
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int centerLeft = getMeasuredWidth() / 2
                    - releasedChild.getMeasuredWidth() / 2;
            if (releasedChild.getLeft() < centerLeft) {
                // 在左半边，应该向左缓慢移动
                mViewDragHelper.smoothSlideViewTo(releasedChild, 0,
                        releasedChild.getTop());
                ViewCompat.postInvalidateOnAnimation(DragLayout.this);
            } else {
                // 在右半边，应该向右缓慢移动
                mViewDragHelper.smoothSlideViewTo(releasedChild,
                        getMeasuredWidth() - releasedChild.getMeasuredWidth(),
                        releasedChild.getTop());
                ViewCompat.postInvalidateOnAnimation(DragLayout.this);
            }
        }
    };

    private void executeAnim(float fraction) {
        //fraction: 0 - 1
        //缩放
//		ViewHelper.setScaleX(redView, 1+0.5f*fraction);
//		ViewHelper.setScaleY(redView, 1+0.5f*fraction);
        //旋转
//		ViewHelper.setRotation(redView,360*fraction);//围绕z轴转
        ViewHelper.setRotationX(redView, 360 * fraction);//围绕x轴转
//		ViewHelper.setRotationY(redView,360*fraction);//围绕y轴转
        ViewHelper.setRotationX(blueView, 360 * fraction);//围绕z轴转
        //平移
//		ViewHelper.setTranslationX(redView,80*fraction);
        //透明
//		ViewHelper.setAlpha(redView, 1-fraction);

        //设置过度颜色的渐变
        redView.setBackgroundColor((Integer) ColorUtil.evaluateColor(fraction, Color.RED, Color.GREEN));
//		setBackgroundColor((Integer) ColorUtil.evaluateColor(fraction,Color.RED,Color.GREEN));
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(DragLayout.this);
        }
    }
}
