package br.com.agilles.capstone.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import br.com.agilles.capstone.models.Ocorrencia;

public class FirebaseUtils {

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mFotosOcorrenciasStorageReference;
    private FirebaseUser user;
    private FirebaseFirestore mFirebaseFirestore;
    private List<Ocorrencia> ocorrencias;

    private static FirebaseUtils singleton;

   private FirebaseUtils() {

    }

    private static FirebaseUtils pegarInstancia() {
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

    public void setmFirebaseStorage(FirebaseStorage mFirebaseStorage) {
        this.mFirebaseStorage = mFirebaseStorage;
    }

    public void setmFotosOcorrenciasStorageReference(StorageReference mFotosOcorrenciasStorageReference) {
        this.mFotosOcorrenciasStorageReference = mFotosOcorrenciasStorageReference;
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }

    public void setmFirebaseFirestore(FirebaseFirestore mFirebaseFirestore) {
        this.mFirebaseFirestore = mFirebaseFirestore;
    }


}
