package com.example.photoapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class DBHelper extends AppCompatActivity {

    static FirebaseFirestore db;

    public static void SaveDetails(MySaveCallBack mySaveCallBack, Map<String, Object> Activity) {


        db = FirebaseFirestore.getInstance();
        db.collection("Activities")
                .add(Activity)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        mySaveCallBack.onCallbackForSaveData(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mySaveCallBack.onCallbackForSaveData(false);
                    }
                });

    }

    public static void LoadSurveyors(MySurveyorsCallback mySurveyorsCallback,String selectedLocation) {

        db = FirebaseFirestore.getInstance();
        db.collection("Surveyors")
                .whereEqualTo("Location", selectedLocation)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<String> result = new ArrayList<String>();
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    String id = Objects.requireNonNull(document.get("EmpId")).toString();
                                    String name = Objects.requireNonNull(document.get("Name")).toString();
                                    result.add(id+"_"+name);
                                }
                            }
                        }

                        mySurveyorsCallback.onCallback(result);

                    }
                });
    }
}