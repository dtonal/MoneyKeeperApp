package de.dtonal.moneykeeperapp;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatusFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatusFragment extends Fragment {
    private ObjectMapper mapper = new ObjectMapper();
    private static final String TAG = "StatusFragment";
    private static final String ARG_MAIL = "mail";
    private static final String ARG_PASS = "pass";

    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
    DateFormat dateFormatForApi = new SimpleDateFormat("yyyy/MM/dd", Locale.GERMANY);

    private String mMail;
    private String mPass;

    private OnFragmentInteractionListener mListener;
    private TextView mTextStatusDate;
    private TextView mTextGivenDay;
    private TextView mTextActualDay;
    private TextView mTextResultDay;
    private TextView mTextGivenWeek;
    private TextView mTextActualWeek;
    private TextView mTextResultWeek ;
    private TextView mTextGivenMonth ;
    private TextView mTextActualMonth;
    private TextView mTextResultMonth;
    private TextView mTextDayOfWeek;
    private TextView mTextDayOfMonth;
    private ImageView mImageStatusDay;
    private ImageView mImageStatusWeek;
    private ImageView mImageStatusMonth;
    private TextView mTextMonthlyBudget;
    private TextView mTextDailyBudget;
    private ProgressDialog progressDialog;

    public StatusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mail Parameter 1.
     * @param pass Parameter 2.
     * @return A new instance of fragment StatusFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatusFragment newInstance(String mail, String pass) {
        StatusFragment fragment = new StatusFragment();
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTextStatusDate = (TextView) getView().findViewById(R.id.textStatusDate);
        mTextGivenDay = (TextView) getView().findViewById(R.id.textGivenDay);
        mTextActualDay = (TextView) getView().findViewById(R.id.textActualDay);
        mTextResultDay = (TextView) getView().findViewById(R.id.textResultDay);
        mTextGivenWeek = (TextView) getView().findViewById(R.id.textGivenWeek);
        mTextActualWeek = (TextView) getView().findViewById(R.id.textActualWeek);
        mTextResultWeek = (TextView) getView().findViewById(R.id.textResultWeek);
        mTextGivenMonth = (TextView) getView().findViewById(R.id.textGivenMonth);
        mTextActualMonth = (TextView) getView().findViewById(R.id.textActualMonth);
        mTextResultMonth = (TextView) getView().findViewById(R.id.textResultMonth);
        mTextDayOfWeek = (TextView) getView().findViewById(R.id.textDayOfWeek);
        mTextDayOfMonth = (TextView) getView().findViewById(R.id.textDayOfMonth);
        mImageStatusDay = (ImageView) getView().findViewById(R.id.imageStatusDay);
        mImageStatusWeek = (ImageView) getView().findViewById(R.id.imageStatusWeek);
        mImageStatusMonth = (ImageView) getView().findViewById(R.id.imageStatusMonth);
//        mTextMonthlyBudget = (TextView) getView().findViewById(R.id.textMonthlyBudget);
//        mTextDailyBudget = (TextView) getView().findViewById(R.id.textDailyBudget);
        changeDateTo(new Date());
    }

    private void changeDateTo(Date date) {
        mTextStatusDate.setText(dateFormat.format(date));

        RequestParams requestParams = new RequestParams();
        requestParams.put("date", dateFormatForApi.format(date));
        progressDialog.show();

        MoneyKeeperRestClientWithAuth.post("state_reports.json", requestParams, mMail, mPass, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "onSuccess " + response.toString());
                StatusReport report = null;
                try {
                     report = mapper.readValue(response.toString(), StatusReport.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(report != null)
                {
                    updateViewForReport(report);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int a, Header[] h, String s, Throwable t)
            {
                Log.d(TAG, "onFailure " + t.toString());
                progressDialog.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "onFailure " + errorResponse.toString());
                progressDialog.dismiss();
            }

        });

    }

    private void updateViewForReport(StatusReport report) {
        mTextGivenDay.setText(DecimalFormat.getCurrencyInstance(Locale.GERMANY).format(report.getDailyBudget()));
        mTextActualDay.setText(DecimalFormat.getCurrencyInstance(Locale.GERMANY).format(report.getDayState()));
        mTextResultDay.setText(DecimalFormat.getCurrencyInstance(Locale.GERMANY).format(report.getDailyBudget() - report.getDayState()));
        mTextGivenWeek.setText(DecimalFormat.getCurrencyInstance(Locale.GERMANY).format(report.getWeeklyBudget()));
        mTextActualWeek.setText(DecimalFormat.getCurrencyInstance(Locale.GERMANY).format(report.getWeekState()));
        mTextResultWeek.setText(DecimalFormat.getCurrencyInstance(Locale.GERMANY).format(report.getWeeklyBudget() - report.getWeekState()));
        mTextGivenMonth.setText(DecimalFormat.getCurrencyInstance(Locale.GERMANY).format(report.getMonthlyBudget()));
        mTextActualMonth.setText(DecimalFormat.getCurrencyInstance(Locale.GERMANY).format(report.getMonthState()));
        mTextResultMonth.setText(DecimalFormat.getCurrencyInstance(Locale.GERMANY).format(report.getMonthlyBudget() - report.getMonthState()));
        mTextDayOfWeek.setText(report.getDayOfWeek() +". Tag");
        mTextDayOfMonth.setText(report.getDayOfMonth() +". Tag");

        mImageStatusDay.setImageResource(getResourceImageForDeviation(report.getDayState(), report.getDailyBudget()));
        mImageStatusWeek.setImageResource(getResourceImageForDeviation(report.getWeekState(), report.getWeeklyBudget()));
        mImageStatusMonth.setImageResource(getResourceImageForDeviation(report.getMonthState(), report.getMonthlyBudget()));
    }

    private int getResourceImageForDeviation(Double state, Double budget) {
        Double diff = budget - state;
        if(diff/budget > 0)
        {
            return R.drawable.ic_happy;
        }
        if(diff/budget < -0.15)
        {
            return R.drawable.ic_sad;
        }
        return R.drawable.ic_okay;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progressDialog =  new ProgressDialog(this.getContext());
        return inflater.inflate(R.layout.fragment_status, container, false);
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
