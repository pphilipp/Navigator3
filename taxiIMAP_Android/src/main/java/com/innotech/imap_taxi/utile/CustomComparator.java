package com.innotech.imap_taxi.utile;

import java.util.Comparator;
import java.util.Date;

import com.innotech.imap_taxi.model.arch;

public class CustomComparator implements Comparator<arch> {
    @Override
    public int compare(arch o1, arch o2) {
        return new Date(o1.getDate()).compareTo(new Date(o2.getDate()));
    }
}
