package com.msg.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.msg.bean.SmsModeTypeResult.SmsType;

public class SmsModeFragmentAdapter extends FragmentStatePagerAdapter {
	private ArrayList<SmsType> mTypeList = new ArrayList<SmsType>();
	private int mType;

	public SmsModeFragmentAdapter(FragmentManager fm, ArrayList<SmsType> res,
			int type) {
		super(fm);
		this.mTypeList = res;
		this.mType = type;
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return SmsModeFragment.newInstance(arg0, mType);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mTypeList.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mTypeList.get(position).getCLASSNAME();
	}
}
