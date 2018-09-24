package com.inductiveautomation.ignition.examples.task;

import java.io.Serializable;

/**
 * Easy-to-serialize class that is needed due to Wicket saving selection state as we move through the wizard. Adding
 * "implements Serializable" prevents a java.io.NotSerializableException
 */
public class SimpleRemoteGateway implements Serializable {

    private final String name;
    private final String status;
    private boolean isSelected;

    public SimpleRemoteGateway(String name, String status){
        this.name = name;
        this.status = status;
    }

    public String getName(){
        return name;
    }

    public String getStatus(){
        return status;
    }

    public boolean isSelected(){
        return isSelected;
    }

    public void setSelected(boolean isSelected){
        this.isSelected = isSelected;
    }
}
