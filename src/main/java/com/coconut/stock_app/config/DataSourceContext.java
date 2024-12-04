package com.coconut.stock_app.config;

public class DataSourceContext {
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public static void useOnPremiseMaster() {
        contextHolder.set("onPremiseMaster");
    }

    public static void useOnPremiseReadReplica() {
        contextHolder.set("onPremiseReadReplica");
    }

    public static void clear() {
        contextHolder.remove();
    }

    public static String getCurrentDataSource() {
        return contextHolder.get();
    }
}
