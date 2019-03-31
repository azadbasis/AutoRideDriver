package org.autoride.driver.utils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AppsUtils {

    private static String st;

    public static String status() {
        DatabaseReference refCh = FirebaseDatabase.getInstance().getReference().child("test");
        refCh.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot d) {
                st = d.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return st;
    }
}