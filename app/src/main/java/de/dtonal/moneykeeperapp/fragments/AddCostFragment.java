package de.dtonal.moneykeeperapp.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.Arrays;

import cz.msebera.android.httpclient.Header;
import de.dtonal.moneykeeperapp.R;
import de.dtonal.moneykeeperapp.adapter.StoreAdapter;
import de.dtonal.moneykeeperapp.connection.MoneyKeeperRestClientWithAuth;
import de.dtonal.moneykeeperapp.connection.Urls;


/**
 * A fragment to add a new cost to the backend.
 */
public class AddCostFragment extends Fragment {

    private static final String TAG = "AddCostFragment";

    private Spinner mCategorySpinner;
    private EditText mEditValue;
    private EditText mEditComment;
    private Button mSaveButton;
    private TextView mTextProcessing;
    private LinearLayout mLayoutAddCostFragment;

    private OnFragmentInteractionListener mListener;
    private ProgressDialog mProgressDialog;
    private StoreAdapter mStoreAdapter;

    public AddCostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddCostFragment.
     */
    public static AddCostFragment newInstance() {
        AddCostFragment fragment = new AddCostFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_cost, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCategorySpinner = (Spinner) getView().findViewById(R.id.spinnerCategory);
        mLayoutAddCostFragment = (LinearLayout) getView().findViewById(R.id.layoutAddCostFragment);
        mEditValue = (EditText) getView().findViewById(R.id.editValue);
        mEditComment = (EditText) getView().findViewById(R.id.editComment);
        mSaveButton = (Button) getView().findViewById(R.id.buttonSave);
        mTextProcessing = (TextView) getView().findViewById(R.id.textProcessing);

        mStoreAdapter = new StoreAdapter(getContext(), Arrays.asList(getResources().getStringArray(R.array.market_array)));
        mCategorySpinner.setAdapter(mStoreAdapter);
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mLayoutAddCostFragment.setBackgroundResource(mStoreAdapter.getBackgroundResourceForName(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tryToSave();
            }
        });

        mProgressDialog = new ProgressDialog(this.getContext());
    }

    private void tryToSave() {
        Double value = null;
        try {
            value = Double.parseDouble(mEditValue.getText().toString());
        } catch (NumberFormatException e) {
            mEditValue.setError(getResources().getString(R.string.error_invalid_value));
            return;
        }

        String comment = mEditComment.getText().toString();
        String selectedStore = mCategorySpinner.getSelectedItem().toString();

        RequestParams requestParams = new RequestParams();
        requestParams.put("price", value);
        requestParams.put("comment", comment);
        requestParams.put("store", selectedStore);

        mEditValue.setEnabled(false);
        mCategorySpinner.setEnabled(false);
        mEditComment.setEnabled(false);
        mSaveButton.setEnabled(false);
        mProgressDialog.show();


        MoneyKeeperRestClientWithAuth.post(Urls.PATH_COSTS, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "onSuccess " + response.toString());
                mTextProcessing.setVisibility(View.VISIBLE);
                mTextProcessing.setText(getResources().getString(R.string.add_cost_success));
                mTextProcessing.setBackgroundColor(Color.GREEN);
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(int a, Header[] h, String s, Throwable t) {
                Log.d(TAG, "onFailure " + t.toString());
                mTextProcessing.setVisibility(View.VISIBLE);
                mTextProcessing.setBackgroundColor(Color.RED);
                mTextProcessing.setText(getResources().getString(R.string.error_while_add_cost));
                mProgressDialog.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "onFailure " + errorResponse.toString());

                super.onFailure(statusCode, headers, throwable, errorResponse);
                //TODO: Style user feedback.
                mTextProcessing.setBackgroundColor(Color.RED);
                mTextProcessing.setText(R.string.error_while_add_cost);
                mProgressDialog.dismiss();
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
