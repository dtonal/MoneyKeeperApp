package de.dtonal.moneykeeperapp;

import android.content.Context;
import android.database.DataSetObserver;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
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

    private ObjectMapper mapper = new ObjectMapper();
    private String mMail;
    private String mPass;

    private ListView mListView;
    private CostsAdapter mAdapter;

    private Button mDeleteButton;

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
        mDeleteButton = (Button) getView().findViewById(R.id.costs_delete);
        mDeleteButton.setVisibility(View.GONE);

        RequestParams requestParams = new RequestParams();
        MoneyKeeperRestClientWithAuth.get("costs.json", requestParams, mMail, mPass, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "onSuccess " + response.toString());

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseArray) {
                Log.d(TAG, "onSuccess " + responseArray.toString());
                ArrayList<Cost> costs = null;
                try {
                    costs = mapper.readValue(responseArray.toString(), new TypeReference<ArrayList<Cost>>(){});
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mAdapter = new CostsAdapter(getContext(), costs);
                mAdapter.registerDataSetObserver(new DataSetObserver()
                {
                    @Override
                    public void onChanged()
                    {
                        Log.d(TAG, "onChanged " + mAdapter.getSelectedIds());
                        if(mAdapter.getSelectedIds().size() > 0)
                        {
                            mDeleteButton.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            mDeleteButton.setVisibility(View.GONE);
                        }
                    }
                });
                mListView.setAdapter(mAdapter);
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
