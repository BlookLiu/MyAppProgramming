package com.example.blookliu.myapplication;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.blookliu.myapplication.model.AsyncAudioPlayer;
import com.example.blookliu.myapplication.model.AudioPlayer;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HelloMoonFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HelloMoonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HelloMoonFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Button mPlayBtn;
    private Button mStopBtn;
    /**
     * 0：初始化，1：播放，2：暂停，3：结束
     */
    private int mPlayState = 0;

    private AudioPlayer mPlayer = new AudioPlayer();
    private AsyncAudioPlayer mAsyncAudioPlayer = new AsyncAudioPlayer();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HelloMoonFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HelloMoonFragment newInstance(String param1, String param2) {
        HelloMoonFragment fragment = new HelloMoonFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HelloMoonFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_hello_moon, container, false);
        mPlayBtn = (Button) v.findViewById(R.id.play_btn);
        mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mPlayState) {
                    case 0:
                        mPlayer.play(getActivity());
                        mPlayBtn.setText(R.string.hellomoon_pause);
                        mPlayState = 1;
                        break;
                    case 1:
                        mPlayer.pause();
                        mPlayBtn.setText(R.string.hellomoon_play);
                        mPlayState = 2;
                        break;
                    case 2:
                        mPlayer.restart();
                        mPlayBtn.setText(R.string.hellomoon_pause);
                        mPlayState = 1;
                        break;
                    default:
                        mPlayer.reset();
                        mPlayBtn.setText(R.string.hellomoon_play);
                        mPlayState = 0;
                        break;

                }
//                mAsyncAudioPlayer.play(getActivity());
            }
        });
        mStopBtn = (Button) v.findViewById(R.id.stop_btn);
        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.stop();
//                mAsyncAudioPlayer.stop();
                mPlayBtn.setText(R.string.hellomoon_play);
                mPlayState = 0;
            }
        });
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayer.stop();
//        mAsyncAudioPlayer.stop();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
