package se.team4.mamn01_grupp4.ui.quiz;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.team4.mamn01_grupp4.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuizFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizFragment extends Fragment {


    /*
        TODO:
         * Försök hitta en bra lösning på att skaka telefonen åt höger/vänster.
            Kanske detta kan vara något...
            https://stackoverflow.com/questions/6224577/detect-shaking-of-device-in-left-or-right-direction-in-android
         * Lägg in riktiga UI komponenter istället för placeholder bilden
         * Animera telefonen och pilarna för att hinta om interaktionen
         * Hämta data från modellen
         * Feedback vid rätt/fel svar, ev. som navigering.
    */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }
}
