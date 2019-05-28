package com.example.tomindapp

object GroupFactory {

    private var groupList = arrayListOf<Group>()

    fun addGroup(group: Group){
        groupList.add(group)
    }

    fun getGroup(id:Int):Group{
        return groupList.get(id)
    }

    fun clearGroups(){
        groupList.clear()
    }

    fun getGroups():ArrayList<Group>{
        return groupList
    }

    fun updateGroups(arr:ArrayList<Group>){
        groupList=arr
    }
}