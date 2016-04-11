package org.apache.spark.launcher;

public class SparkSecurityException extends SecurityException{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int _value;
    public SparkSecurityException(String msg,int value)
    {
        super(msg + value);
        _value = value;
    }
    public int getValue() {return _value;} 
}

