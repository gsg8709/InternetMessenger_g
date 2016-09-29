package com.msg.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.msg.bean.MiniCardTypeResult.MiniCardType;

public class MiniCardFragmentAdapter extends FragmentStatePagerAdapter {
	private ArrayList<MiniCardType> mTypeList = new ArrayList<MiniCardType>();
	private int mType;

	public MiniCardFragmentAdapter(FragmentManager fm,
			ArrayList<MiniCardType> res, int type) {
		super(fm);
		this.mTypeList = res;
		this.mType = type;
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return MiniCardFragment.newInstance(mTypeList.get(arg0).getCLASSID(),
				mType);
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
