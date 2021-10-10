package com.example.mydictionary.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.mydictionary.R;
import com.example.mydictionary.WordMeaningActivity;

public class FragmentExample extends Fragment {
    public FragmentExample() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
      View view= inflater.inflate(R.layout.fragment_definition,container,false);
        Context context=getActivity();

        TextView text = (TextView) view.findViewById(R.id.text_def);//Find textView Id

        String example= ((WordMeaningActivity)context).example;
        text.setText(example);

        if(example==null)
        {
            text.setText("No Example found");
        }


        return view;
    }
}
