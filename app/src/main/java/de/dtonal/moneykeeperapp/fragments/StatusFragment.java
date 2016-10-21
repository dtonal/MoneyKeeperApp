package de.dtonal.moneykeeperapp.fragments;

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
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import de.dtonal.moneykeeperapp.R;
import de.dtonal.moneykeeperapp.connection.MoneyKeeperRestClientWithAuth;
import de.dtonal.moneykeeperapp.connection.Urls;
import de.dtonal.moneykeeperapp.model.ModelMapper;
import de.dtonal.moneykeeperapp.model.StatusReport;


/**
 * Fragment to see status of given today relative to the daily, weekly and monthly budget.
 */
public class StatusFragment extends Fragment {
    private static final String TAG = "StatusFragment";

    DateFormat mDefaultDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
    DateFormat mJsonApiDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.GERMANY);


    private OnFragmentInteractionListener mListener;
    private TextView mTextStatusDate;
    private TextView mTextGivenDay;
    private TextView mTextActualDay;
    private TextView mTextResultDay;
    private TextView mTextGivenWeek;
    private TextView mTextActualWeek;
    private TextView mTextResultWeek;
    private TextView mTextGivenMonth;
    private TextView mTextActualMonth;
    private TextView mTextResultMonth;
    private TextView mTextDayOfWeek;
    private TextView mTextDayOfMonth;
    private ImageView mImageStatusDay;
    private ImageView mImageStatusWeek;
    private ImageView mImageStatusMonth;
    private ProgressDialog mProgressDialog;

    public StatusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StatusFragment.
     */
    public static StatusFragment newInstance() {
        StatusFragment fragment = new StatusFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        changeDateTo(new Date());
    }

    private void changeDateTo(Date date) {
        mTextStatusDate.setText(mDefaultDateFormat.format(date));

        RequestParams requestParams = new RequestParams();
        requestParams.put("date", mJsonApiDateFormat.format(date));
        mProgressDialog.show();

        MoneyKeeperRestClientWithAuth.post(Urls.PATH_STATE_REPORTS, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "onSuccess " + response.toString());
                StatusReport report = null;
                try {
                    report = ModelMapper.getInstance().readValue(response.toString(), StatusReport.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (report != null) {
                    updateViewForReport(report);
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(int a, Header[] h, String s, Throwable t) {
                Log.d(TAG, "onFailure " + t.toString());
                mProgressDialog.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "onFailure " + errorResponse.toString());
                mProgressDialog.dismiss();
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
        mTextDayOfWeek.setText(report.getDayOfWeek() + ". Tag");
        mTextDayOfMonth.setText(report.getDayOfMonth() + ". Tag");

        mImageStatusDay.setImageResource(getResourceImageForDeviation(report.getDayState(), report.getDailyBudget()));
        mImageStatusWeek.setImageResource(getResourceImageForDeviation(report.getWeekState(), report.getWeeklyBudget()));
        mImageStatusMonth.setImageResource(getResourceImageForDeviation(report.getMonthState(), report.getMonthlyBudget()));
    }

    private int getResourceImageForDeviation(Double state, Double budget) {
        Double diff = budget - state;
        if (diff / budget > 0) {
            return R.drawable.ic_happy;
        }
        if (diff / budget < -0.15) {
            return R.drawable.ic_sad;
        }
        return R.drawable.ic_okay;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mProgressDialog = new ProgressDialog(this.getContext());
        return inflater.inflate(R.layout.fragment_status, container, false);
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
