package falk.falkollection.activities.ui.about;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AboutViewModel extends ViewModel {


    private MutableLiveData<String> mText;
    public AboutViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Trabalho desenvolvido para a disciplina de Programacao para dispositivos moveis na Universidade de Caxias do Sul (UCS).");
    }
    public LiveData<String> getText() {
        return mText;
    }

}