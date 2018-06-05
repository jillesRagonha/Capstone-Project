package br.com.agilles.capstone.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseUtils {


    FirebaseStorage mFirebaseStorage;
    StorageReference mFotosOcorrenciasStorageReference;
    FirebaseUser user;
    FirebaseFirestore mFirebaseFirestore;


    public FirebaseFirestore getmFirebaseFirestore() {
        return FirebaseFirestore.getInstance();
    }

    public FirebaseStorage getmFirebaseStorage() {
        return FirebaseStorage.getInstance();
    }

    public StorageReference getmFotosOcorrenciasStorageReference() {
        return FirebaseStorage.getInstance().getReference().child("fotos_ocorrencias");
    }

    public FirebaseUser getUser() {
        return FirebaseAuth.getInstance().getCurrentUser();

    }
}
