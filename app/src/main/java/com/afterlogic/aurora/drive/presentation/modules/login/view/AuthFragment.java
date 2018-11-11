package com.afterlogic.aurora.drive.presentation.modules.login.view;

import android.animation.ValueAnimator;

import android.webkit.WebSettings;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.databinding.LoginAuthFragmentBinding;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.UnbindableObservable;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.InjectableMVVMFragment;
import com.afterlogic.aurora.drive.presentation.modules.login.viewModel.LoginViewModel;
import com.afterlogic.aurora.drive.presentation.modules.login.viewModel.LoginWebViewState;

/**
 * Created by aleksandrcikin on 11.08.17.
 * mail: mail@sunnydaydev.me
 */

public class AuthFragment extends InjectableMVVMFragment<LoginViewModel> {

    private boolean isFirstWebViewSizeAction = true;

    private ValueAnimator webViewAnimator;

    public static AuthFragment newInstance() {
        
        Bundle args = new Bundle();
        
        AuthFragment fragment = new AuthFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public ViewDataBinding createBinding(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return DataBindingUtil.inflate(inflater, R.layout.login_auth_fragment, container, false);
    }

    @Override
    protected ViewModelProvider createViewModelProvider() {
        return ViewModelProviders.of(getParentFragment());
    }

    @Override
    public LoginViewModel createViewModel(ViewModelProvider provider) {
        return provider.get(LoginViewModel.class);
    }

    @Override
    protected void bindCreated(LoginViewModel vm, UnbindableObservable.Bag bag) {
        super.bindCreated(vm, bag);

        UnbindableObservable.bindToValue(vm.webViewState, bag, state -> {

            LoginAuthFragmentBinding binding = getBinding();

            if (state == LoginWebViewState.NOT_AVAILABLE) {

                ((ConstraintLayout.LayoutParams) binding.inputGuideLine.getLayoutParams()).guidePercent = 1f;
                ((ConstraintLayout.LayoutParams) binding.webViewGuideLine.getLayoutParams()).guidePercent = 1f;

            } else {

                ((ConstraintLayout.LayoutParams) binding.inputGuideLine.getLayoutParams()).guidePercent = 0.5f;

                isFirstWebViewSizeAction = true;
                updateWebViewPercentSize(vm.loginWebViewFullscreen.get() ? 1f : 0.5f);

            }

        });

        UnbindableObservable.bindToValue(vm.loginWebViewFullscreen, bag, fullscreen -> {

            if (vm.webViewState.get() == LoginWebViewState.NOT_AVAILABLE) return;

            updateWebViewPercentSize(fullscreen ? 1f : 0.5f);

        });

    }

    private void updateWebViewPercentSize(@FloatRange(from = 0, to = 1) float value) {
        value = 1 - value;

        if (isFirstWebViewSizeAction) {

            isFirstWebViewSizeAction = false;

            animateWebViewGuideLine(value, false);

        } else {

            animateWebViewGuideLine(value, true);

        }

    }

    private void animateWebViewGuideLine(float targetValue, boolean animate) {

        if (webViewAnimator != null) {
            webViewAnimator.cancel();
            webViewAnimator = null;
        }

        LoginAuthFragmentBinding binding = getBinding();
        Guideline guideline = binding.webViewGuideLine;
        ConstraintLayout.LayoutParams guideLineLp = (ConstraintLayout.LayoutParams) guideline.getLayoutParams();

        float currentValue = guideLineLp.guidePercent;

        if (currentValue == targetValue) return;

        if (animate) {

            webViewAnimator = ValueAnimator.ofFloat(currentValue, targetValue);
            webViewAnimator.addUpdateListener(
                    valueAnimator -> guideline.setGuidelinePercent((float) valueAnimator.getAnimatedValue()));

            long duration = (long) ((Math.max(currentValue, targetValue) - Math.min(currentValue, targetValue)) * 500);

            webViewAnimator.setDuration(duration);
            webViewAnimator.start();

        } else {
            guideline.setGuidelinePercent(targetValue);
        }

    }

}
