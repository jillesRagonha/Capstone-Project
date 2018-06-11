package br.com.agilles.capstone.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import br.com.agilles.capstone.models.Ocorrencia;

public class FirebaseUtils {

    FirebaseStorage mFirebaseStorage;
    StorageReference mFotosOcorrenciasStorageReference;
    FirebaseUser user;
    FirebaseFirestore mFirebaseFirestore;
    List<Ocorrencia> ocorrencias;

    private static FirebaseUtils singleton;

    public FirebaseUtils() {

    }

    public static FirebaseUtils pegarInstancia() {
        if (singleton == null) {
            singleton = new FirebaseUtils();
        }
        return singleton;
    }



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

    public void setOcorrencias(List<Ocorrencia> ocorrencias) {
        this.ocorrencias = ocorrencias;
    }

    public List<Ocorrencia> getOcorrencias() {
        return ocorrencias;
    }
}
