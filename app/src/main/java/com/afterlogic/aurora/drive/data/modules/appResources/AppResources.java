package com.afterlogic.aurora.drive.data.modules.appResources;

/**
 * Created by sashka on 09.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface AppResources {
    String getString(int id);

    String getString(int id, Object... args);
}
