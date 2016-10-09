package de.dtonal.moneykeeperapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/**
 * Created by dtonal on 09.10.16.
 */

public class MonthStatisticsFragment extends Fragment {

    private ObjectMapper mapper = new ObjectMapper();
    private static final String TAG = "MonthStatisticFragment";
    private static final String ARG_MAIL = "mail";
    private static final String ARG_PASS = "pass";
    private Date firstDayInMonth;
    private Date lastDayInMonth;
    private int year;
    DateFormat fromToFormat = new SimpleDateFormat("dd.MM", Locale.GERMANY);
    DateFormat monthFormat = new SimpleDateFormat("MMM yyyy", Locale.GERMANY);
    private Calendar cal = Calendar.getInstance();

    private MonthStatisticsFragment.OnFragmentInteractionListener mListener;
    private TextView mTextMonth;
    private TextView mTextFromTo;
    private List<String> listOfStoreStringsToLoad;
    private Iterator<String> storeIterator;
    private ProgressDialog progressDialog;
    private String mMail;
    private String mPass;
    private SumForStoreAdapter mAdapter;
    private ListView mListView;
    private TextView mTextSum;
    private int month;

    public MonthStatisticsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MonthStatisticsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MonthStatisticsFragment newInstance(String mail, String pass) {
        MonthStatisticsFragment fragment = new MonthStatisticsFragment();
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

    private void changeDateTo(Date date) {
        firstDayInMonth = DateUtil.firstDayOfMonth(date);
        lastDayInMonth = DateUtil.lastDayOfMonth(date);
        mTextFromTo.setText(fromToFormat.format(firstDayInMonth) + "-" + fromToFormat.format(lastDayInMonth));
        mTextMonth.setText(monthFormat.format(firstDayInMonth));
        year = DateUtil.getYear(firstDayInMonth);
        month = DateUtil.getMonth(firstDayInMonth);
        reloadLIst(month, year);
    }

    private void reloadLIst(int monthOfYear, int year) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("month", monthOfYear);
        requestParams.put("year", year);
        progressDialog.show();
        MoneyKeeperRestClientWithAuth.post("monthstats.json", requestParams, mMail, mPass, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseArray) {
                Log.d(TAG, "onSuccess " + responseArray.toString());
                ArrayList<SumForStore> sumForStores = null;
                try {
                    sumForStores = mapper.readValue(responseArray.toString(), new TypeReference<ArrayList<SumForStore>>(){});
                } catch (IOException e) {
                    e.printStackTrace();
                }
                double sum = 0;
                for(SumForStore sumForStore: sumForStores)
                {
                    sum += sumForStore.getSum();
                }
                mTextSum.setText(DecimalFormat.getCurrencyInstance(Locale.GERMANY).format(sum));
                mAdapter = new SumForStoreAdapter(getContext(), sumForStores);
                mListView.setAdapter(mAdapter);
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


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTextMonth = (TextView) getView().findViewById(R.id.textMonth);
        mTextFromTo = (TextView) getView().findViewById(R.id.textFromTo);
        mListView = (ListView) getView().findViewById(R.id.listStoreSumPerMonth);
        mTextSum = (TextView) getView().findViewById(R.id.textMonthStatisticsSum);
        changeDateTo(new Date());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_month_statistics, null, false);
        progressDialog =  new ProgressDialog(this.getContext());
        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                           float velocityY) {
                        Log.i(TAG, "onFling has been called!");
                        final int SWIPE_MIN_DISTANCE = 120;
                        final int SWIPE_MAX_OFF_PATH = 250;
                        final int SWIPE_THRESHOLD_VELOCITY = 200;
                        try {
                            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                                return false;
                            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                Log.i(TAG, "Right to Left");
                                if(!progressDialog.isShowing())
                                {
                                    changeDateTo(DateUtil.addMonth(1, firstDayInMonth));
                                }
                            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                Log.i(TAG, "Left to Right");
                                if(!progressDialog.isShowing())
                                {
                                    changeDateTo(DateUtil.addMonth(-1, firstDayInMonth));
                                }
                            }
                        } catch (Exception e) {
                            // nothing
                        }
                        return super.onFling(e1, e2, velocityX, velocityY);
                    }
                });

        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MonthStatisticsFragment.OnFragmentInteractionListener) {
            mListener = (MonthStatisticsFragment.OnFragmentInteractionListener) context;
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
