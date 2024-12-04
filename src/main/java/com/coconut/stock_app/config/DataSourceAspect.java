package com.coconut.stock_app.config;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DataSourceAspect {

    @Before("@annotation(ReadOnlyTransaction)")
    public void useReadReplica() {
        DataSourceContext.useOnPremiseReadReplica();
    }

    @Before("@annotation(WriteTransaction)")
    public void useMaster() {
        DataSourceContext.useOnPremiseMaster();
    }
}
