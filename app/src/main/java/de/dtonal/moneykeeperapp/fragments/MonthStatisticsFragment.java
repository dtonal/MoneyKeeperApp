package de.dtonal.moneykeeperapp.fragments;

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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import de.dtonal.moneykeeperapp.R;
import de.dtonal.moneykeeperapp.adapter.SumForStoreAdapter;
import de.dtonal.moneykeeperapp.connection.MoneyKeeperRestClientWithAuth;
import de.dtonal.moneykeeperapp.connection.Urls;
import de.dtonal.moneykeeperapp.model.ModelMapper;
import de.dtonal.moneykeeperapp.model.SumForStore;
import de.dtonal.moneykeeperapp.utils.DateUtil;

import static de.dtonal.moneykeeperapp.adapter.StoreAdapter.getBackgroundResourceForName;

/**
 * Created by dtonal on 09.10.16.
 */

public class MonthStatisticsFragment extends Fragment {
    private static final String TAG = "MonthStatisticFragment";
    private Date mFirstDayInMonth;
    private Date mLastDayInMonth;
    private int mYear;
    private int mMonth;
    private DateFormat mFromToDateFormat = new SimpleDateFormat("dd.MM", Locale.GERMANY);

    private DateFormat mMonthYearDateFormat = new SimpleDateFormat("MMM yyyy", Locale.GERMANY);
    private MonthStatisticsFragment.OnFragmentInteractionListener mListener;
    private TextView mTextMonth;
    private TextView mTextFromTo;
    private ProgressDialog mProgressDialog;
    private SumForStoreAdapter mAdapter;
    private ListView mListView;
    private TextView mTextSum;
    private PieChart mChart;

    public MonthStatisticsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MonthStatisticsFragment.
     */
    public static MonthStatisticsFragment newInstance() {
        MonthStatisticsFragment fragment = new MonthStatisticsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    private void changeDateTo(Date date) {
        mFirstDayInMonth = DateUtil.firstDayOfMonth(date);
        mLastDayInMonth = DateUtil.lastDayOfMonth(date);
        mTextFromTo.setText(mFromToDateFormat.format(mFirstDayInMonth) + "-" + mFromToDateFormat.format(mLastDayInMonth));
        mTextMonth.setText(mMonthYearDateFormat.format(mFirstDayInMonth));
        mYear = DateUtil.getYear(mFirstDayInMonth);
        mMonth = DateUtil.getMonth(mFirstDayInMonth);
        reloadLIst(mMonth, mYear);
    }

    private void reloadLIst(int monthOfYear, int year) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("mMonth", monthOfYear);
        requestParams.put("year", year);
        mProgressDialog.show();
        MoneyKeeperRestClientWithAuth.post(Urls.PATH_MONTHSTATS, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseArray) {
                Log.d(TAG, "onSuccess " + responseArray.toString());
                ArrayList<SumForStore> sumForStores = null;
                List<PieEntry> entries = new ArrayList<PieEntry>();
                List<Integer> colors = new ArrayList<Integer>();
                float pos = 0;
                double sum = 0;
                try {
                    sumForStores = ModelMapper.getInstance().readValue(responseArray.toString(), new TypeReference<ArrayList<SumForStore>>() {
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Iterator<SumForStore> iterator = sumForStores.iterator();
                while (iterator.hasNext()) {
                    SumForStore sumForStore = iterator.next();
                    sum += sumForStore.getSum();
                    if (sumForStore.getSum() > 0) {
                        pos += 1;
                        entries.add(new PieEntry(new Float(sumForStore.getSum()), sumForStore.getStore()));
                        colors.add(getBackgroundResourceForName(sumForStore.getStore()));
                    } else {
                        iterator.remove();
                    }
                }
                mTextSum.setText(DecimalFormat.getCurrencyInstance(Locale.GERMANY).format(sum));
                mAdapter = new SumForStoreAdapter(getContext(), sumForStores);
                mListView.setAdapter(mAdapter);

                PieDataSet dataSet = new PieDataSet(entries, "");
                int[] colorArray = new int[colors.size()];
                int i = 0;
                for (Integer color : colors) {
                    colorArray[i] = color;
                    i++;
                }
                dataSet.setColors(colorArray, getContext());
                PieData data = new PieData(dataSet);
                data.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                        return DecimalFormat.getCurrencyInstance(Locale.GERMANY).format(value);
                    }
                });
                data.setValueTextSize(16f);
                mChart.setDrawEntryLabels(false);
                mChart.setData(data);
                mChart.setEntryLabelColor(R.color.blue);
                mChart.setDescription(getString(R.string.chartDescription));
                mChart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);

                mChart.setHighlightPerTapEnabled(false);
                mChart.invalidate();
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


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTextMonth = (TextView) getView().findViewById(R.id.textMonth);
        mTextFromTo = (TextView) getView().findViewById(R.id.textFromTo);
        mListView = (ListView) getView().findViewById(R.id.listStoreSumPerMonth);
        mTextSum = (TextView) getView().findViewById(R.id.textMonthStatisticsSum);

        mChart = (PieChart) getView().findViewById(R.id.chartMonthStatistics);
        changeDateTo(new Date());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_month_statistics, null, false);
        mProgressDialog = new ProgressDialog(this.getContext());
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
                                if (!mProgressDialog.isShowing()) {
                                    changeDateTo(DateUtil.addMonth(1, mFirstDayInMonth));
                                }
                            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                Log.i(TAG, "Left to Right");
                                if (!mProgressDialog.isShowing()) {
                                    changeDateTo(DateUtil.addMonth(-1, mFirstDayInMonth));
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
        void onFragmentInteraction(Uri uri);
    }
}
