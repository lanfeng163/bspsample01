package jp.co.muroo.systems.bsp.contents;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;

import jp.co.muroo.systems.bsp.R;

/**
 * ムロオ計算機の入力キー
 * 「複数のviewを１つにまとめて部品化」
 */
public class MurooKeyView extends RelativeLayout {

    private Button kv_button_7;
    private Button kv_button_8;
    private Button kv_button_9;

    private Button kv_button_4;
    private Button kv_button_5;
    private Button kv_button_6;

    private Button kv_button_1;
    private Button kv_button_2;
    private Button kv_button_3;

    private Button kv_button_0;
    private Button kv_button_00;

    private Button kv_button_bs;
    private Button kv_button_ac;
    private Button kv_button_et;

    private RelativeLayout muroo_key_view_layout;

    /**
     * MurooKeyView(Context context)
     * @param context
     */
    public MurooKeyView(Context context) {
        super(context);
        initView(context);
    }

    /**
     * MurooKeyView(Context context, AttributeSet attrs)
     * @param context
     * @param attrs
     */
    public MurooKeyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    /**
     * MurooKeyView(Context context, AttributeSet attrs, int defStyleAttr)
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public MurooKeyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     *
     * @param context
     */
    public void initView(Context context){
        LayoutInflater.from(context).inflate(R.layout.muroo_key_view, this, true);

        kv_button_7= (Button) findViewById(R.id.keyButton7);
        kv_button_8= (Button) findViewById(R.id.keyButton8);
        kv_button_9= (Button) findViewById(R.id.keyButton9);
        kv_button_4= (Button) findViewById(R.id.keyButton4);
        kv_button_5= (Button) findViewById(R.id.keyButton5);
        kv_button_6= (Button) findViewById(R.id.keyButton6);
        kv_button_1= (Button) findViewById(R.id.keyButton1);
        kv_button_2= (Button) findViewById(R.id.keyButton2);
        kv_button_3= (Button) findViewById(R.id.keyButton3);
        kv_button_0= (Button) findViewById(R.id.keyButton0);
        kv_button_00= (Button) findViewById(R.id.keyButton00);

        kv_button_bs= (Button) findViewById(R.id.keyButtonBS);
        kv_button_ac= (Button) findViewById(R.id.keyButtonAC);
        kv_button_et= (Button) findViewById(R.id.keyButtonET);

        muroo_key_view_layout= (RelativeLayout) findViewById(R.id.muroo_key_view_rootlayout);
    }

    /**
     * 数字 onClickListener
     * @param onClickListener
     */
    public void setNumListener(OnClickListener onClickListener){
        kv_button_0.setOnClickListener(onClickListener);
        kv_button_1.setOnClickListener(onClickListener);
        kv_button_2.setOnClickListener(onClickListener);
        kv_button_3.setOnClickListener(onClickListener);
        kv_button_4.setOnClickListener(onClickListener);
        kv_button_5.setOnClickListener(onClickListener);
        kv_button_6.setOnClickListener(onClickListener);
        kv_button_7.setOnClickListener(onClickListener);
        kv_button_8.setOnClickListener(onClickListener);
        kv_button_9.setOnClickListener(onClickListener);
        kv_button_00.setOnClickListener(onClickListener);
    }

    /**
     * Back space onClickListener
     * @param onClickListener
     */
    public void setBackSpaceListener(OnClickListener onClickListener){
        kv_button_bs.setOnClickListener(onClickListener);
    }

    /**
     * AC onClickListener
     * @param onClickListener
     */
    public void setACListener(OnClickListener onClickListener){
        kv_button_ac.setOnClickListener(onClickListener);
    }

    /**
     * Enter onClickListener
     * @param onClickListener
     */
    public void setEnterListener(OnClickListener onClickListener){
        kv_button_et.setOnClickListener(onClickListener);
    }
}


