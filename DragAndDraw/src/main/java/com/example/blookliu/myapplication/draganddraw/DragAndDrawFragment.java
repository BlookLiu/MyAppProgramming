package com.example.blookliu.myapplication.draganddraw;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.example.blookliu.myapplication.draganddraw.view.BoxDrawingView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DragAndDrawFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DragAndDrawFragment extends Fragment {
    private static final String TAG = "DragAndDrawFragment";
    private BoxDrawingView mBoxDrawingView;

    public DragAndDrawFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DragAndDrawFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DragAndDrawFragment newInstance(String param1, String param2) {
        DragAndDrawFragment fragment = new DragAndDrawFragment();
/*        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_drag_and_draw, container, false);
        mBoxDrawingView = (BoxDrawingView) v.findViewById(R.id.box_drawing_view);
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "on save instance state");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "on view state restored");
        super.onViewStateRestored(savedInstanceState);
    }

}
