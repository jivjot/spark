package org.apache.spark.launcher;

import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.security.Permission;

public class SparkSecurityManager extends SecurityManager {
    private static final Lock _mutex = new ReentrantLock(true);
    private static final HashSet<ThreadGroup> _threadSet = new HashSet<>();
    private static final ThreadGroup _threadGroup = new ThreadGroup("IgnoreGroup");
    public static void addToIgnoreSet(Thread t)
    {
        try
        {
            _mutex.lock();
            _threadSet.add(t.getThreadGroup());
        }
        finally
        {
            _mutex.unlock();
        }
    }
    public static ThreadGroup getIgnoreThreadGroup()
    {
        return _threadGroup;
    }
    @Override
    public void checkPropertiesAccess()
    {
        //@@@@ giving all rights
        return;
    }
    @Override
    public void checkPropertyAccess(String key)
    {
        //@@@@ giving all rights
        return;
    }
    @Override
    public void checkPermission(Permission perm)
    {
        //@@@@ giving all rights
        return;
    }
    @Override
    public void checkPermission(Permission perm,Object context)
    {
        //@@@@ giving all rights
        return;
    }

    @Override
    public void checkExit(int status) 
    {
        System.out.println("In Exit Check() ");
        Thread.currentThread().dumpStack();
        if(Thread.currentThread().getThreadGroup() == _threadGroup)
        {
            //Thread.currentThread().stop();
            throw new SparkSecurityException("not allow to call System.exit",status);
        }
        /*
        boolean flag = false;
        try
        {
            _mutex.lock();
            if(_threadSet.contains(Thread.currentThread().getThreadGroup()))
            {
                flag = true;
            }
        }
        finally
        {
            _mutex.unlock();
            if(flag == true)
            {
                throw new SparkSecurityException("not allow to call System.exit",status);
            }
        }
        */
    }
}
