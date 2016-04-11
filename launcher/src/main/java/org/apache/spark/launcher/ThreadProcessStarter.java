
package org.apache.spark.launcher;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;


public class ThreadProcessStarter implements Runnable
{
    private String _mainClass;
    private String[] _arguments;
    private int _status;

    @Override
    public void run() 
    {
        _status = 0;
        ClassLoader classLoader = ThreadProcessStarter.
            class.getClassLoader();
        try {
            Class aClass = classLoader.loadClass(_mainClass);
            for(Method m : aClass.getMethods())
            {
                if(m.getName() == "main")
                {
                    m.invoke(null,(Object)_arguments);
                }
            }
        } 
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            _status = -4;
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            _status = -3;
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
            if(e.getTargetException().
                    getClass().equals(SparkSecurityException.class))
            {
                SparkSecurityException se = (SparkSecurityException)e.getTargetException();
                _status = se.getValue();
                return;
            }
            _status = -2;
        }

    }
    public static int start(String mainClass,
            String [] arguments,
            String[] classPathEntries,
            String[] libraryPathEntries,
            String[] javaOpts) throws InstantiationException,InterruptedException
    {
        System.out.println("Inside ThreadProcessStarter");
        System.out.println("mainClass "+mainClass);
        System.out.println("arguments ____________");
        for(String s : arguments)
        {
            System.out.println(s);
        }
        System.out.println("classPathEntries ____________");
        for(String s : classPathEntries)
        {
            System.out.println(s);
        }
        System.out.println("libraryPathEntries ____________");
        for(String s : libraryPathEntries)
        {
            System.out.println(s);
        }
        System.out.println("javaOpts ____________");
        for(String s : javaOpts)
        {
            System.out.println(s);
        }
        ThreadProcessStarter l = new ThreadProcessStarter();
        l._mainClass = mainClass;
        l._arguments = arguments;
        Thread t = new Thread(SparkSecurityManager.getIgnoreThreadGroup(),l);
        SparkSecurityManager.addToIgnoreSet(t);
        t.start();
        do
        {
            try
            {
                t.join();
                return l._status;
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
                System.out.println("Thread State "+t.isAlive());
                System.out.println("Thread State "+t.isInterrupted());
                
                if(t.isInterrupted())
                    throw e;
                //throw e;
                //return -1;
            }
        }
        while(true);
        
    }
}
