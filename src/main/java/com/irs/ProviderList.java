package com.irs;

import java.util.ArrayList;

public class ProviderList {
    public ArrayList<Provider> providers;

    // false: not sorted, true: sorted in ASC order
    public boolean isSortedByName; 
    public boolean isSortedByContact; 

    public ProviderList() {
        providers = new ArrayList<>();
        isSortedByName = true; 
        isSortedByContact = false;
    }

    public int size() {
        return providers.size();
    }
}
