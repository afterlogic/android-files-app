package com.afterlogic.aurora.drive.presentation.common.modules.view.viewState;

import android.os.SystemClock;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.presentation.common.interfaces.Stoppable;
import com.afterlogic.aurora.drive.presentation.common.modules.view.PresentationView;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.annotations.IgnoreViewActiveState;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.annotations.Repeat;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.annotations.RepeatPolicy;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.annotations.RepeatPolicy.LAST;
import static com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.annotations.RepeatPolicy.LAST_UNHANDLED;

/**
 * Created by sashka on 01.12.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class ViewState<T extends PresentationView> implements Stoppable {

    private T mViewProxy;

    private final OptWeakRef<T> mView = OptWeakRef.empty();

    private final ViewInvocationHandler mInvocationHandler;

    public static <T extends PresentationView> ViewState<T> create(Class<T> type){
        if (!type.isInterface()){
            throw new IllegalArgumentException("Must be an interface.");
        }
        return new ViewState<>(type);
    }

    @SuppressWarnings("unchecked")
    private ViewState(Class<T> type) {
        mInvocationHandler = new ViewInvocationHandler(mView);
        mViewProxy = (T) Proxy.newProxyInstance(
                type.getClassLoader(),
                new Class[]{type},
                mInvocationHandler
        );
    }

    public void setView(T view){
        mView.set(view);
        if (view.isActive()){
            mInvocationHandler.reinvoke();
        }
    }

    public T getViewProxy() {
        return mViewProxy;
    }

    public OptWeakRef<T> getView(){
        return mView;
    }

    @Override
    public void onStart() {
        mInvocationHandler.reinvoke();
    }

    @Override
    public void onStop() {
    }

    private class ViewInvocationHandler implements InvocationHandler{

        private final OptWeakRef<T> mView;

        private final List<MethodInvocation> mInvokes = new ArrayList<>();

        private ViewInvocationHandler(OptWeakRef<T> view) {
            mView = view;
        }

        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            if (method.getReturnType() != Void.TYPE){
                T view = mView.get();
                return view != null ? method.invoke(view, objects) : null;
            }

            MethodInvocation invocation = MethodInvocation.create(method, objects);
            boolean handled = invoke(invocation);

            RepeatPolicy repeatPolicy = invocation.getRepeatPolicy();
            String group = invocation.getGroup();

            boolean noNeedStore = repeatPolicy == RepeatPolicy.NONE ||
                    handled && invocation.isOnceHandle();

            if (group == null && noNeedStore){
                return null;
            }

            synchronized (mInvokes){
                List<MethodInvocation> deleteList = new ArrayList<>();

                //Remove all previously stored same methods if 'repeat last' or 'repeat last unhandled'
                if (oneOfType(repeatPolicy, LAST_UNHANDLED, LAST)) {
                    deleteList.addAll(Stream.of(mInvokes)
                            .filter(stored -> stored.equals(invocation))
                            .collect(Collectors.toList())
                    );
                }

                if (group != null) {
                    //Remove all previously stored different methods with same group
                    deleteList.addAll(Stream.of(mInvokes)
                            .filter(stored -> group.equals(stored.getGroup()) && !stored.equals(invocation))
                            .collect(Collectors.toList())
                    );
                }

                if (!deleteList.isEmpty()){
                    mInvokes.removeAll(deleteList);
                }

                if (!noNeedStore){
                    mInvokes.add(invocation);
                }
            }

            return null;
        }

        private boolean invoke(MethodInvocation invocation){
            T view = mView.get();
            if (view == null || !(view.isActive() || invocation.isIgnoreViewActiveState())){
                return false;
            }

            invocation.invoke(view);

            if (invocation.isOnceHandle()){
                mInvokes.remove(invocation);
            }

            return true;
        }

        void reinvoke(){
            synchronized (mInvokes) {
                Stream.of(new ArrayList<>(mInvokes))
                        .sortBy(MethodInvocation::getCallTime)
                        .forEach(this::invoke);
            }
        }
    }


    private boolean oneOfType(RepeatPolicy source, RepeatPolicy... types){
        return Stream.of(types).anyMatch(type -> type == source);
    }

    @SuppressWarnings("WeakerAccess")
    private static class MethodInvocation {

        private long mCallTime;
        private final Object[] mParams;
        private final Method mMethod;

        private final String mGroup;
        private final RepeatPolicy mRepeatPolicy;
        private final boolean mIgnoreViewActiveState;

        @SuppressWarnings("deprecation")
        public static MethodInvocation create(Method method, Object[] params){
            String repeatGroup = null;
            RepeatPolicy repeatPolicy = RepeatPolicy.NONE;

            if (method.isAnnotationPresent(Repeat.class)){
                Repeat repeatAnnotation = method.getAnnotation(Repeat.class);
                repeatPolicy = repeatAnnotation.value();
                //noinspection StringEquality
                repeatGroup = repeatAnnotation.group() == Repeat.GROUP_NONE ?
                        null : repeatAnnotation.group();
            }

            boolean ignoreActiveState = method.isAnnotationPresent(IgnoreViewActiveState.class);

            return new MethodInvocation(
                    SystemClock.elapsedRealtime(),
                    params,
                    method,
                    repeatGroup,
                    repeatPolicy,
                    ignoreActiveState
            );
        }

        private MethodInvocation(long callTime, Object[] params, Method method, String group, RepeatPolicy repeatPolicy, boolean ignoreViewActiveState) {
            mCallTime = callTime;
            mParams = params;
            mMethod = method;
            mGroup = group;
            mRepeatPolicy = repeatPolicy;
            mIgnoreViewActiveState = ignoreViewActiveState;
        }

        public long getCallTime() {
            return mCallTime;
        }

        public String getGroup() {
            return mGroup;
        }

        public RepeatPolicy getRepeatPolicy() {
            return mRepeatPolicy;
        }

        public Object invoke(Object target){
            try {
                return mMethod.invoke(target, mParams);
            } catch (IllegalAccessException e) {
                MyLog.majorException(e);
                return null;
            } catch(InvocationTargetException e){
                MyLog.majorException(e);
                return null;
            }
        }

        public boolean isIgnoreViewActiveState() {
            return mIgnoreViewActiveState;
        }

        public boolean isOnceHandle(){
            return mRepeatPolicy == RepeatPolicy.EACH_UNHANDLED || mRepeatPolicy == LAST_UNHANDLED;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof MethodInvocation)) return false;
            MethodInvocation invocation = (MethodInvocation) obj;

            return mMethod.equals(invocation.mMethod);
        }

        @Override
        public String toString() {
            String className = mMethod.getDeclaringClass().getSimpleName();
            String params = Stream.of(mMethod.getParameterTypes())
                    .map(Class::getSimpleName)
                    .collect(Collectors.joining(", "));
            return String.format("%s.%s(%s)", className, mMethod.getName(), params);
        }
    }
}
