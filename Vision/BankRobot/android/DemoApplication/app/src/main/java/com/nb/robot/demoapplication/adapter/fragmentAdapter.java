package com.nb.robot.demoapplication.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.TextView;

import com.nb.robot.demoapplication.R;
import com.nb.robot.demoapplication.fragment.BatteryFragment;
import com.nb.robot.demoapplication.fragment.DanceFragment;
import com.nb.robot.demoapplication.fragment.ExpressionFragment;
import com.nb.robot.demoapplication.fragment.HumanDectionFragment;
import com.nb.robot.demoapplication.fragment.InitFragment;
import com.nb.robot.demoapplication.fragment.MotionControlFragment;
import com.nb.robot.demoapplication.fragment.ServoFragment;
import com.nb.robot.demoapplication.fragment.SpeakerFragment;
import com.nb.robot.demoapplication.fragment.SpeechFragment;

/**
 * Created by linshangjun on 2017/7/11.
 */

public class fragmentAdapter extends FragmentPagerAdapter {

    private String[] titles={"开始","底盘","手臂","表情","人脸","语音","舞蹈","播报","电池"};
    public fragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new InitFragment();
            case 1:
                return new MotionControlFragment();
            case 2:
                return new ServoFragment();
            case 3:
                return new ExpressionFragment();
            case 4:
                return new HumanDectionFragment();
            case 5:
                return new SpeechFragment();
            case 6:
                return new DanceFragment();
            case 7:
                return new SpeakerFragment();
            case 8:
                return new BatteryFragment();
        }
        return new InitFragment();
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
