package com.musicg.math.rank;

import java.util.List;

public interface MapRank {
    List getOrderedKeyList(int numKeys, boolean sharpLimit);
}