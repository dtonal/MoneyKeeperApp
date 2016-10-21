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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import de.dtonal.moneykeeperapp.R;
import de.dtonal.moneykeeperapp.connection.MoneyKeeperRestClientWithAuth;
import de.dtonal.moneykeeperapp.connection.Urls;

/**
 * A simple fragment to see and change the budget settings.
 */
public class BudgetSettingsFragment extends Fragment {
    private static final String TAG = "BudgetSettingsFragment";

    private OnFragmentInteractionListener mListener;
    private EditText mEditMonthBudget;
    private Button mSaveBudgetButton;
    private TextView mTextDayBudget;
    private TextView mTextProcessingSaveSettings;
    private ProgressDialog progressDialog;

    public BudgetSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BudgetSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BudgetSettingsFragment newInstance() {
        BudgetSettingsFragment fragment = new BudgetSettingsFragment();Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_budget_settings, container, false);
    }

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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog =  new ProgressDialog(this.getContext());

        mEditMonthBudget = (EditText) getView().findViewById(R.id.editMonthBudget);
        mSaveBudgetButton = (Button) getView().findViewById(R.id.buttonSaveSettings);
        mTextDayBudget = (TextView) getView().findViewById(R.id.textDayBudget);
        mTextProcessingSaveSettings = (TextView) getView().findViewById(R.id.textProcessingSaveSettings);

        mSaveBudgetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tryToSaveBudget();
            }
        });
        loadCurrentBudget();
    }

    private void loadCurrentBudget() {
        mSaveBudgetButton.setEnabled(false);
        progressDialog.show();

        MoneyKeeperRestClientWithAuth.get(Urls.PATH_BUDGETS, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "onSuccess " + response.toString());
                try {
                    setMonthBudget(response.getDouble("value"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mSaveBudgetButton.setEnabled(true);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int a, Header[] h, String s, Throwable t)
            {
                Log.d(TAG, "onFailure " + t.toString());
                progressDialog.dismiss();
                mSaveBudgetButton.setEnabled(true);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "onFailure " + errorResponse.toString());
                progressDialog.dismiss();
                mSaveBudgetButton.setEnabled(true);
            }
        });
    }

    private void setMonthBudget(double monthBudget) {
        mEditMonthBudget.setText(String.valueOf(monthBudget));
        mTextDayBudget.setText(String.valueOf(monthBudget/30));
    }

    private void tryToSaveBudget() {

        mSaveBudgetButton.setEnabled(false);
        progressDialog.show();

        RequestParams params = new RequestParams();
        String newBudget = mEditMonthBudget.getText().toString();
        params.put("value", Double.parseDouble(newBudget));

        MoneyKeeperRestClientWithAuth.post(Urls.PATH_BUDGETS, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "onSuccess " + response.toString());
                try {
                    setMonthBudget(response.getDouble("value"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //TODO: Style user feedback.
                mTextProcessingSaveSettings.setText(getResources().getString(R.string.add_cost_success));
                mTextProcessingSaveSettings.setBackgroundColor(Color.GREEN);

                mSaveBudgetButton.setEnabled(true);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int a, Header[] h, String s, Throwable t)
            {
                Log.d(TAG, "onFailure " + t.toString());
                progressDialog.dismiss();
                mSaveBudgetButton.setEnabled(true);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "onFailure " + errorResponse.toString());
                progressDialog.dismiss();
                mSaveBudgetButton.setEnabled(true);
            }
        });

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
