package com.sdk.ui.widget.widget.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sdk.ui.core.constant.Constants;
import com.sdk.ui.core.impl.OnLoginSuccessListener;
import com.sdk.ui.core.model.BaseEntry;
import com.sdk.ui.core.model.ResultData;
import com.sdk.ui.core.util.PreferencesUtils;
import com.sdk.ui.guest.GuestManager;
import com.sdk.ui.widget.R;
import com.sdk.ui.widget.base.BaseFragment;
import com.sdk.ui.widget.impl.OnResultClickListener;
import com.sdk.ui.widget.model.BundleData;
import com.sdk.ui.widget.util.AnimationUtils;

import androidx.appcompat.widget.AppCompatImageView;


public class GuestFragment extends BaseFragment implements View.OnClickListener {

    String mAgreementUrl;
    String mPrivacyUrl;
    String googleClientID;
    String LTAppID;
    String LTAppKey;
    String mAdID;
    String mPackageID;
    boolean mServerTest;
    String mFacebookID;
    boolean mIsLoginOut;
    private OnResultClickListener mListener;
    Button mBtnSure, mBtnCancel;
    TextView mTxtTitle, mTxtTips, mTxtContent;
    AppCompatImageView mImgLeft, mImgMiddle, mImgRight;
    RelativeLayout mRytGuest, mRytBottom;


    public static GuestFragment newInstance(BundleData data) {
        Bundle args = new Bundle();
        GuestFragment fragment = new GuestFragment();
        args.putSerializable(ARG_NUMBER, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getFragmentId() {
        return R.layout.fragment_guest;
    }


    @Override
    protected void initView(View view) {
        mBtnCancel = view.findViewById(R.id.btn_guest_cancel);
        mBtnCancel.setOnClickListener(this);

        mBtnSure = view.findViewById(R.id.btn_guest_continue);
        mBtnSure.setOnClickListener(this);

        mTxtTitle = view.findViewById(R.id.txt_guest_title);
        mTxtTips = view.findViewById(R.id.txt_guest_tips);

        mImgLeft = view.findViewById(R.id.img_guest_left);
        mImgMiddle = view.findViewById(R.id.img_guest_middle);
        mImgRight = view.findViewById(R.id.img_guest_right);
        mRytGuest = view.findViewById(R.id.ryt_guest);
        mTxtContent = view.findViewById(R.id.txt_guest_content);
        mRytBottom = view.findViewById(R.id.ryt_guest_bottom);
    }

    @Override
    public void lazyLoadData() {
        super.lazyLoadData();
        Bundle args = getArguments();
        if (args != null) {
            BundleData mData = (BundleData) args.getSerializable(ARG_NUMBER);
            if (mData != null) {
                mAgreementUrl = mData.getAgreementUrl();
                mPrivacyUrl = mData.getPrivacyUrl();
                googleClientID = mData.getGoogleClientID();
                LTAppID = mData.getLTAppID();
                LTAppKey = mData.getLTAppKey();
                mAdID = mData.getmAdID();
                mPackageID = mData.getmPackageID();
                mFacebookID = mData.getmFacebookID();
                mServerTest = mData.getServerTest();
                mIsLoginOut = mData.ismLoginOut();

            }
        }

    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_guest_cancel) {//取消
            backLogin();
        } else if (view.getId() == R.id.btn_guest_continue) {//继续
            login();
            GuestManager.guestLogin(mActivity, mServerTest, LTAppID, LTAppKey, mAdID, mPackageID,
                    new OnLoginSuccessListener() {
                        @Override
                        public void onSuccess(BaseEntry<ResultData> result) {
                            Log.e("TAG","UI"+result.toString());
                            ResultData mData = new ResultData();
                            mData.setLt_uid(result.getData().getLt_uid());
                            mData.setLt_uid_token(result.getData().getLt_uid_token());
                            mData.setApi_token(result.getData().getApi_token());
                            if (mListener != null) {
                                mListener.onResult(mData);
                            }
                            loginEnd();
                            PreferencesUtils.putString(mActivity, Constants.USER_GUEST_FLAG, "2");
                            PreferencesUtils.putString(mActivity, Constants.USER_BIND_FLAG, "2");
                            PreferencesUtils.putString(mActivity, Constants.USER_API_TOKEN, result.getData().getApi_token());
                            PreferencesUtils.putString(mActivity, Constants.USER_LT_UID, result.getData().getLt_uid());
                            PreferencesUtils.putString(mActivity, Constants.USER_LT_UID_TOKEN, result.getData().getLt_uid_token());
                            getProxyActivity().finish();

                        }

                        @Override
                        public void onFailed(String code) {
                            loginEnd();
                            loginFailed();
                        }

                        @Override
                        public void onParameterError(String result) {

                        }
                    });
        }

    }

    /**
     * 返回
     */
    private void backLogin() {
        BundleData data = new BundleData();
        data.setPrivacyUrl(mPrivacyUrl);
        data.setAgreementUrl(mAgreementUrl);
        data.setLTAppKey(LTAppKey);
        data.setLTAppID(LTAppID);
        data.setGoogleClientID(googleClientID);
        data.setmAdID(mAdID);
        data.setmPackageID(mPackageID);
        data.setmFacebookID(mFacebookID);
        data.setServerTest(mServerTest);
        data.setmLoginOut(mIsLoginOut);
        getProxyActivity().addFragment(LoginFragment.newInstance(data),
                false, true);

    }


    /**
     * 登录失败
     */
    private void loginFailed() {
        if (findChildFragment(LoginFailedFragment.class) == null) {
            BundleData data = new BundleData();
            data.setAgreementUrl(mAgreementUrl);
            data.setPrivacyUrl(mPrivacyUrl);
            data.setLTAppKey(LTAppKey);
            data.setLTAppID(LTAppID);
            data.setGoogleClientID(googleClientID);
            data.setmAdID(mAdID);
            data.setmPackageID(mPackageID);
            data.setServerTest(mServerTest);
            data.setmFacebookID(mFacebookID);
            data.setmLoginOut(mIsLoginOut);
            getProxyActivity().addFragment(LoginFailedFragment.newInstance(data),
                    false,
                    true);
        }
    }

    /**
     * 回调
     */
    public void setOnResultClick(OnResultClickListener listener) {
        mListener = listener;
    }

    /**
     * 登录
     */
    private void login() {
        mRytGuest.setVisibility(View.VISIBLE);
        AnimationUtils.changeUI(mActivity, mImgLeft, mImgRight);
        mTxtTitle.setText(getResources().getString(R.string.text_login));
        mTxtTips.setVisibility(View.VISIBLE);
        mTxtContent.setVisibility(View.INVISIBLE);
        mRytBottom.setVisibility(View.GONE);
    }

    /**
     * 登录
     */
    private void loginEnd() {
        mTxtTitle.setText(getResources().getString(R.string.text_sure_guest));
        mTxtTips.setVisibility(View.GONE);
        mTxtContent.setVisibility(View.VISIBLE);
        mRytGuest.setVisibility(View.GONE);
        mRytBottom.setVisibility(View.VISIBLE);
    }
}
