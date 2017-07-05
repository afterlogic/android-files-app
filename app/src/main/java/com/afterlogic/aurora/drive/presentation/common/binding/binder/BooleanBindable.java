package com.afterlogic.aurora.drive.presentation.common.binding.binder;

import android.support.annotation.NonNull;
import android.widget.CompoundButton;

import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.core.common.interfaces.Provider;

/**
 * Created by sashka on 02.12.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class BooleanBindable extends Bindable<Boolean> implements CompoundButton.OnCheckedChangeListener {

    public static BooleanBindable create(){
        return new BooleanBindable();
    }

    public static BooleanBindable create(Provider<Boolean> get, Consumer<Boolean> set){
        return new BooleanBindable(get, set);
    }

    private BooleanBindable(){
        super();
    }

    private BooleanBindable(@NonNull Provider<Boolean> get, @NonNull Consumer<Boolean> set) {
        super(get, set);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        set(b);
    }

}
