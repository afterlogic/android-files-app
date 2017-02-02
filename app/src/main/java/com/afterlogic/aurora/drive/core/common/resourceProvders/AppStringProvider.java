package com.afterlogic.aurora.drive.core.common.resourceProvders;

/**
 * Created by sashka on 09.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface AppStringProvider {
    String getString(int id);

    String getString(int id, Object... args);
}
