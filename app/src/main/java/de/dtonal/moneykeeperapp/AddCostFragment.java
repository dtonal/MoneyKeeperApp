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
import android.widget.Spinner;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddCostFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddCostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddCostFragment extends Fragment {

    private static final String TAG = "AddCostFragment";
    private static final String ARG_MAIL = "mail";
    private static final String ARG_PASS = "pass";

    private String mMail;
    private String mPass;

    private Spinner mCategorySpinner;
    private EditText mEditValue;
    private EditText mEditComment;

    private OnFragmentInteractionListener mListener;

    public AddCostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mail mail
     * @param pass password
     * @return A new instance of fragment AddCostFragment.
     */
    public static AddCostFragment newInstance(String mail, String pass) {
        AddCostFragment fragment = new AddCostFragment();
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
        return inflater.inflate(R.layout.fragment_add_cost, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCategorySpinner = (Spinner) getView().findViewById(R.id.spinnerCategory);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.market_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        mCategorySpinner.setAdapter(adapter);


        mEditValue = (EditText) getView().findViewById(R.id.editValue);
        mEditComment = (EditText) getView().findViewById(R.id.editComment);
        Button mSaveButton = (Button) getView().findViewById(R.id.buttonSave);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tryToSave();
            }
        });
    }

    private void tryToSave() {
        Double value = Double.parseDouble(mEditValue.getText().toString());
        String comment = mEditComment.getText().toString();
        String category = mCategorySpinner.getSelectedItem().toString();

        RequestParams requestParams = new RequestParams();
        requestParams.put("price", value);
        requestParams.put("comment", comment);
        requestParams.put("store", category);


        MoneyKeeperRestClientWithAuth.post("costs.json", requestParams, mMail, mPass, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "onSuccess " + response.toString());

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
        void onFragmentInteraction(Uri uri);
    }
}