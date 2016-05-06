package com.stevenswang.agerasample.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wolun on 16/5/6.
 */
public class FansCommonData {
    private Status status;
    private Data data;

    public Data getData() {
        if (data == null) {
            data = new Data();
        }
        return data;
    }

    public static class Status {
        private int code;

        public int getCode() {
            return code;
        }
    }

    public Status getStatus() {
        if (status == null) {
            status = new Status();
        }
        return status;
    }

    public static class Data {
        private List<String> xAxis;
        private List<String> fans;

        public List<String> getxAxis() {
            if (xAxis == null) {
                xAxis = new ArrayList<>();
            }
            return xAxis;
        }

        public List<String> getFans() {
            if (fans == null) {
                fans = new ArrayList<>();
            }
            return fans;
        }
    }

    public boolean isEmpty() {
        return getData().getFans().isEmpty();
    }
}
