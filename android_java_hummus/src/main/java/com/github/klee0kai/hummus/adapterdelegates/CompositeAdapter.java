package com.github.klee0kai.hummus.adapterdelegates;

import com.hannesdorfmann.adapterdelegates4.AdapterDelegate;
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager;
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter;

import java.util.List;

public class CompositeAdapter extends ListDelegationAdapter<List<Object>> {

    public CompositeAdapter() {
    }

    public CompositeAdapter(AdapterDelegatesManager<List<Object>> delegatesManager) {
        super(delegatesManager);
    }

    public static CompositeAdapter create(AdapterDelegate<List<Object>>... adapterDelegates) {
        AdapterDelegatesManager<List<Object>> manager = new AdapterDelegatesManager<>();
        for (AdapterDelegate<List<Object>> adapterDelegate : adapterDelegates)
            manager.addDelegate(adapterDelegate);
        return new CompositeAdapter(manager);
    }

}
