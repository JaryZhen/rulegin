package com.github.rulegin.dao;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.Executors;

public abstract class JpaAbstractDaoListeningExecutorService {
    protected ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
}
