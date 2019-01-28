package jp.co.muroo.systems.bsp.contents;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by mChenys on 2016/12/20.
 */
public class MurooListView extends ListView implements AbsListView.OnScrollListener {

    private static final String TAG = "MurooListView";
    private MurooListHeaderView mHeaderView;
    private MurooListFooterView mFooterView;
    private int mDownY;//ListView按下时的y坐标
    private boolean isPullingUp;//标记当前是否是向上滚动滑动
    private OnScrollListener mOnScrollListener;

    boolean isUserRefresh = false;

    //下拉刷新和滚动加载的监听回调方法
    public interface OnRefreshListener {
        //下拉刷新
        void onRefresh();

        //滚动加载更多
        void onLoadMore();
    }

    private OnRefreshListener mOnRefreshListener;

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.mOnRefreshListener = onRefreshListener;
    }

    public MurooListView(Context context) {
        this(context, null);
    }

    public MurooListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MurooListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setSelector(new ColorDrawable());
        setCacheColorHint(Color.TRANSPARENT);
        mHeaderView = new MurooListHeaderView(getContext());
        mFooterView = new MurooListFooterView(getContext());
        addHeaderView(mHeaderView);
        addFooterView(mFooterView);
        super.setOnScrollListener(this);
    }

    /**
     * 通过重写onTouchEvent来实现HeaderView的显示和状态切换
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //タッチされた
                mDownY = (int) ev.getY(); //ListView按下时的y坐标
                break;
            case MotionEvent.ACTION_MOVE:
                //タッチしたまま動いた
                if (mHeaderView.isRefreshing() || mFooterView.isLoadingMore()) {
                    break;
                }

                int deltaY = (int) (ev.getY() - mDownY);
                if (deltaY < 0) {
                    //是向上滑动
                    isPullingUp = true;
                } else {
                    isPullingUp = false;
                }

                if (deltaY > 0 && getFirstVisiblePosition() == 0) {
                    //是向下滑动
                    //現在表示しているリストビューの一番上にあるアイテムのポジションは「０」
                    //在ListView列表的第一个Item可见的时候才有可能执行到下拉刷新

                    //修改headerView的paddingTop来控制显示和隐藏
                    mHeaderView.updateHeaderState(deltaY);
                    isUserRefresh = true;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                //タッチが離れた
                //up事件需要处理当前为下拉刷新和正在刷新的2种情况
                if (mHeaderView.isPullToRefresh()) {
                    //STATE_PULL_REFRESH:下拉可刷新

                    //播放动画 慢慢显示/隐藏
                    mHeaderView.fullyHideSmoothToShow();
                } else if (mHeaderView.isReleaseToRefresh()) {
                    //STATE_RELEASE_REFRESH:释放后刷新

                    //全部显示
                    mHeaderView.fullyShow();

                    //正在刷新
                    mHeaderView.setState(MurooListHeaderView.STATE_REFRESHING);

                    //同时需要通知调用者去处理刷新逻辑
                    if (null != mOnRefreshListener) {
                        //实现类 再检索数据
                        mOnRefreshListener.onRefresh();
                    }
                }
                break;
        }
        //再検索する時、ListItemClickListenerを実行しないように
        //クリックする時、ListItemClickListenerを実行します
        if (isUserRefresh) {
            super.setEnabled(false);
        }
        boolean result = super.onTouchEvent(ev);
        if (isUserRefresh) {
            super.setEnabled(true);
            result = true;
            isUserRefresh = false;
        }
        return result;
    }

    /**
     * 下拉刷新/加载更多成功后需要重置状态,由调用者调用,注意:必须要在UI线程调用
     *
     * @param success true表示下拉刷新成功,将会保存刷新的时间
     */
    public void onRefreshComplete(final boolean success) {
        if (success) {
            mHeaderView.saveRefreshTime();
        }

        mHeaderView.fullyHideSmoothToShow();

        //完全隐藏headerView
        mFooterView.setHide();
        Log.d(TAG, "load more is end.");

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (null != mOnScrollListener) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
        switch (scrollState) {
            case SCROLL_STATE_IDLE://闲置状态，就是手指松开
                if (getLastVisiblePosition() == getCount() - 1 && isPullingUp && mFooterView.isReleaseMore()) {
                    mFooterView.setState(MurooListFooterView.STATE_MORE_LOADING);
                    setSelection(getCount());//让listview最后一条显示出来
                    //通知调用者去处理加载更多的逻辑
                    if (null != mOnRefreshListener) {
                        mOnRefreshListener.onLoadMore();
                    }
                }
                break;
            default: //其他状态
                if (getLastVisiblePosition() == getCount() - 1 && !mFooterView.isLoadingMore() && isPullingUp) {
                    if (!mFooterView.isNoMoreData()) {
                        //如果isNoMoreData = false 表示还有更多的数据,显示"释放后加载更多",否则则显示默认的"没有更多数据了"
                        mFooterView.setState(MurooListFooterView.STATE_RELEASE_MORE);
                    } else if (mFooterView.isNoMoreData()) {
                        mFooterView.setState(MurooListFooterView.STATE_NO_MORE);
                    }
                }
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (null != mOnScrollListener) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }

    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        this.mOnScrollListener = l;
    }

    /**
     * 没有更多数据可加载时,由调用者调用.
     */
    public void onNoMoreData() {
        mFooterView.setState(MurooListFooterView.STATE_NO_MORE);
    }

    /**
     * 加载更多失败,由调用者调用
     */
    public void onLoadMoreFailure() {
        mFooterView.setState(MurooListFooterView.STATE_MORE_FAILURE);
    }
}
