package test;

import production.CordovaBaseModule;
import production.annotations.Bogger;

public class Demo2 extends CordovaBaseModule {

    public void method21(){

    }

    @Bogger(type = "single", name = "method22")
    public void method22(){

    }

    public void dontRun(){

    }
}