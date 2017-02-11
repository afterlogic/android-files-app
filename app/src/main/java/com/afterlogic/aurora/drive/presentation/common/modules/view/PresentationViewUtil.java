package com.afterlogic.aurora.drive.presentation.common.modules.view;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.presentation.common.modules.presenter.Presenter;
import com.annimon.stream.Stream;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

class PresentationViewUtil {

    static void reflectiveCollectPresenters(BaseActivity activity){
        reflectiveCollectPresenters(activity, activity.mPresenters, BaseActivity.class);
    }

    static void reflectiveCollectPresenters(BaseFragment fragment){
        reflectiveCollectPresenters(fragment, fragment.mPresenters, BaseFragment.class);
    }

    static void reflectiveCollectPresenters(BaseService service){
        reflectiveCollectPresenters(service, service.mPresenters, BaseService.class);
    }

    private static void reflectiveCollectPresenters(Object obj, Set<Presenter> presenterList, Class stopClass){
        Class classForCheck = obj.getClass();
        while (classForCheck != stopClass && classForCheck != null){
            Stream.of(classForCheck.getDeclaredFields())
                    .filter(PresentationViewUtil::isPresenter)
                    .forEach(field -> {
                        boolean accessible = field.isAccessible();
                        field.setAccessible(true);
                        try {
                            Presenter presenter = (Presenter) field.get(obj);
                            if (presenter != null) {
                                presenterList.add(presenter);
                            } else {
                                MyLog.majorException(obj, "Field marked as ViewPresenter but it is null: " + field.getName());
                            }
                        } catch (IllegalAccessException e) {
                            MyLog.majorException(e);
                        }
                        field.setAccessible(accessible);
                    });
            classForCheck = classForCheck.getSuperclass();
        }
    }

    private static boolean isPresenter(Field field){
        return field.isAnnotationPresent(ViewPresenter.class) && Presenter.class.isAssignableFrom(field.getType());
    }
}
