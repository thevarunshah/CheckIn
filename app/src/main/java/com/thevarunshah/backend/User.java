package com.thevarunshah.backend;

public class User {

    public int id;
    public String name;
    public boolean checkedIn;

    public User(int id, String name, boolean checkedIn){
        this.id = id;
        this.name = name;
        this.checkedIn = checkedIn;
    }

    public String getCheckedIn(){
        if(checkedIn){
            return "Checked In!";
        }
        else{
            return "Not here";
        }
    }
}
