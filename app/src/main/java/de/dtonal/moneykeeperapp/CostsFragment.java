package de.dtonal.moneykeeperapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CostsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CostsFragment extends Fragment {
    private static final String TAG = "CostsFragment";

    private static final String ARG_MAIL = "mail";
    private static final String ARG_PASS = "pass";


    private String mMail;
    private String mPass;

    private ListView mListView;
    private ArrayAdapter arrayAdapter;

    private OnFragmentInteractionListener mListener;

    public CostsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mail Parameter 1.
     * @param pass Parameter 2.
     * @return A new instance of fragment CostsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CostsFragment newInstance(String mail, String pass) {
        CostsFragment fragment = new CostsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MAIL, mail);
        args.putString(ARG_PASS, pass);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMail = getArguments().getString(ARG_MAIL);
            mPass = getArguments().getString(ARG_PASS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_costs, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = (ListView) getView().findViewById(R.id.costs_list);

        RequestParams requestParams = new RequestParams();
        MoneyKeeperRestClientWithAuth.get("costs.json", requestParams, mMail, mPass, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "onSuccess " + response.toString());

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseArray) {
                Log.d(TAG, "onSuccess " + responseArray.toString());
                ArrayList<String> arrayResponseAsStrings = new ArrayList<String>();
                for(int i = 0; i < responseArray.length() ; i++)
                {

                    try {
                        JSONObject objects = responseArray.getJSONObject(i);
                        String resultString = DecimalFormat.getCurrencyInstance(Locale.GERMANY).format(Double.parseDouble(objects.getString("price")));
                        resultString += " / " + objects.getString("store");
                        arrayResponseAsStrings.add(resultString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Iterate through the elements of the array i.
                    //Get thier value.
                    //Get the value for the first element and the value for the last element.
                }
                setArrayAdapter(arrayResponseAsStrings);
            }

            @Override
            public void onFailure(int a, Header[] h, String s, Throwable t)
            {
                Log.d(TAG, "onFailure " + t.toString());

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "onFailure " + errorResponse.toString());

                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });
    }

    private void setArrayAdapter(ArrayList<String> arrayResponseAsStrings) {
        arrayAdapter = new ArrayAdapter(this.getContext(), android.R.layout.simple_list_item_1, arrayResponseAsStrings);
        mListView.setAdapter(arrayAdapter);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
