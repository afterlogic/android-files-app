package com.afterlogic.aurora.drive.presentation.common.modules.assembly;

import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive.presentation.common.modules.view.PresentationView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by sashka on 01.12.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class PresentationModulesStore {

    private Map<UUID, PresentationComponent> mComponentMap = new HashMap<>();

    public UUID put(@NonNull PresentationComponent component){
        UUID uuid;
        do{
            uuid = UUID.randomUUID();
        }while (mComponentMap.keySet().contains(uuid));

        mComponentMap.put(uuid, component);
        return uuid;
    }

    @SuppressWarnings("unchecked")
    public <V extends PresentationView, T extends V> PresentationComponent<V, T> get(@NonNull UUID uuid){
        return (PresentationComponent<V, T>) mComponentMap.get(uuid);
    }

    public void remove(UUID uuid){
        if (uuid == null) return;

        mComponentMap.remove(uuid);
    }
}
