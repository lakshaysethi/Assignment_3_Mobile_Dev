package com.mobileassignment3.parcel_tracking_app;

import java.util.ArrayList;
import java.util.List;

public  class MyStaticClass {
    public static List myStaticObjectsList = new ArrayList();

    public MyStaticClass() {
    }

    public static List<Object> getMyStaticObjectsList() {
        return myStaticObjectsList;
    }

    public static void setMyStaticObjectsList(List<Object> myStaticObjectsList) {
        MyStaticClass.myStaticObjectsList = myStaticObjectsList;
    }
    public static void addStaticObject(Object staticObject) {
        MyStaticClass.myStaticObjectsList.add(staticObject);
    }
    public static void clearStaticList() {
        MyStaticClass.myStaticObjectsList.clear();
    }
}
