package com.trx.yanr;

import java.util.ArrayList;

public class NewsGroups {
	
    int size;
    
    public ArrayList<String> groups = new ArrayList<String>();
    
	public NewsGroups () {
        super ();
        size = 0;
    }
	
	public void addElement(String nextToken) {
		groups.add(nextToken);
	}

	public int size() {
		size = groups.size();
		return size;
	}

	public String elementAt(int i) {
		String GroupName = groups.get(i);
		return GroupName;
	}
}
