package com.huawei.hms.cordova.test.basef.handler;


import android.util.Log;

import com.huawei.hms.cordova.test.basef.CordovaBaseModule;
import com.huawei.hms.cordova.test.basef.HMSLog;

import org.apache.cordova.CallbackContext;
import org.json.JSONArray;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CordovaController {
    private CordovaModuleGroupHandler groupHandler;

    public <T extends CordovaBaseModule> CordovaController(List<T> cordovaModules) {
        List<CordovaModuleHandler> cordovaModuleHandlers = new ArrayList<>();
        for (T cordovaModule : cordovaModules) {
            CordovaModuleHandler cordovaModuleHandler = new CordovaModuleHandler(cordovaModule);
            cordovaModuleHandlers.add(cordovaModuleHandler);
        }
        this.groupHandler = new CordovaModuleGroupHandler(cordovaModuleHandlers);
    }

    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) {
        try {
            CordovaModuleHandler moduleHandler = groupHandler.getCordovaModuleHandler(action);
            String methodName = args.optString(0);
            Method method = moduleHandler.getModuleMethod(methodName);
            args.remove(0);
            if(method.isAnnotationPresent(HMSLog.class)) {
                CallbackResultHandlerThread callbackResultHandlerThread = new CallbackResultHandlerThread(callbackContext, null);
                callbackResultHandlerThread.start();
            }
            method.invoke(moduleHandler.getInstance(), args, callbackContext);
            return true;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
    }

    class CallbackResultHandlerThread extends Thread{
        private long timePassed;
        private final CallbackContext callbackContext;
        private Object hmsLogger;
        public CallbackResultHandlerThread(final CallbackContext callbackContext, Object hmsLogger){
            this.callbackContext=callbackContext;
            this.hmsLogger=hmsLogger;
            //startExecution
            timePassed=System.currentTimeMillis();
        }

        @Override
        public void run() {
            while (!callbackContext.isFinished());
            //sendEvent
            timePassed=System.currentTimeMillis()-timePassed;
            Log.i("Logger ", "sendEvent " + timePassed);
        }
    }

}
