package jp.co.muroo.systems.bsp.contents;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import jp.co.muroo.systems.bsp.R;

/**
 * Created by mChenys on 2016/12/20.
 */
public class MurooListFooterView extends FrameLayout {

    private static final String TAG = "MurooListFooterView";

    //ListView底部的4种状态
    public static final int STATE_NO_MORE = -1; //暂时只有那么多数据
    public static final int STATE_RELEASE_MORE = -2;//释放后加载更多
    public static final int STATE_MORE_LOADING = -3;//正在加载更多
    public static final int STATE_MORE_FAILURE = -4;//加载更多失败
    public static final int STATE_NORMAL = -5;//默认状态是隐藏
    private int mState;

    private View mFooterView; //整个加载更多布局
    private ProgressBar mPbMore; //加载更多的加载圈
    private TextView mTvMoreTip;//加载更多的提示

    private boolean supportClickReload; //是否支持重新加载更多

    public MurooListFooterView(Context context) {
        this(context, null);
    }

    public MurooListFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MurooListFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * Viewの初期化
     */
    private void initView() {
        mFooterView = View.inflate(getContext(), R.layout.muroo_list_footer_view, null);

        mPbMore = (ProgressBar) mFooterView.findViewById(R.id.pb_load_more);
        mTvMoreTip = (TextView) mFooterView.findViewById(R.id.tv_more_tip);

        setState(STATE_NORMAL);//默认状态是隐藏

        addView(mFooterView);
    }

    /**
     * 通过状态刷新FootView的状态
     */
    public void setState(int state) {
        switch (state) {
            case STATE_NO_MORE:
                //没有更多数据

                mTvMoreTip.setText(R.string.muroo_list_str6);
                mFooterView.setVisibility(View.VISIBLE);
                mPbMore.setVisibility(View.GONE);
                Log.d(TAG, "没有更多数据了");
                break;
            case STATE_RELEASE_MORE:
                //释放后加载更多

                mTvMoreTip.setText(R.string.muroo_list_str5);
                mFooterView.setVisibility(View.VISIBLE);
                mPbMore.setVisibility(View.GONE);
                Log.d(TAG, "释放后加载更多");
                break;
            case STATE_MORE_LOADING:
                //正在加载更多

                mTvMoreTip.setText(R.string.muroo_list_str8);
                mFooterView.setVisibility(View.VISIBLE);
                mPbMore.setVisibility(View.VISIBLE);
                Log.d(TAG, "正在加载更多...");

                break;
            case STATE_MORE_FAILURE:
                //加载更多失败

//                if (supportClickReload) {
//                    mTvMoreTip.setText(R.string.muroo_list_str7);
//                    mFooterView.setVisibility(View.VISIBLE);
//                    mPbMore.setVisibility(View.GONE);
//                } else {

                mFooterView.setVisibility(View.GONE);
                mPbMore.setVisibility(View.GONE);
                Toast.makeText(getContext(), R.string.muroo_list_str7, Toast.LENGTH_SHORT).show();

//                }

                Log.d(TAG, "重新加载更多時失败");
                break;

            case STATE_NORMAL:
                //默认状态是隐藏
                mFooterView.setVisibility(View.GONE);
                Log.d(TAG, "状态は隐藏をセットします。");
                break;
        }
        this.mState = state;
    }

    public void setHide() {
        //隐藏
        setState(MurooListFooterView.STATE_NORMAL);
    }

    public boolean isReleaseMore() {
        return mState == STATE_RELEASE_MORE;
    }

    public boolean isNoMoreData() {
        return mState == STATE_NO_MORE;
    }

    public boolean isLoadingMore() {
        return mState == STATE_MORE_LOADING;
    }
}
