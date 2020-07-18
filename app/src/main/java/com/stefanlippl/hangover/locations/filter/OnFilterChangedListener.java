package com.stefanlippl.hangover.locations.filter;

public interface OnFilterChangedListener {

    void onFilterApplied(String[] filters, boolean[] checkedFilters);

    void resetFilters(String[] filters, boolean[] checkedFilters);
}
