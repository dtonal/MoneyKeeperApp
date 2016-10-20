package de.dtonal.moneykeeperapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

import static de.dtonal.moneykeeperapp.StoreAdapter.getBackgroundResourceForName;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WeekStatisticFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WeekStatisticFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeekStatisticFragment extends Fragment {

    private ObjectMapper mapper = new ObjectMapper();
    private static final String TAG = "WeekStatisticFragment";
    private static final String ARG_MAIL = "mail";
    private static final String ARG_PASS = "pass";
    private Date firstDayInWeek;
    private Date lastDayInWeek;
    private int weekOfYear;
    private int year;
    DateFormat dateFormat = new SimpleDateFormat("dd.MM", Locale.GERMANY);
    private Calendar cal = Calendar.getInstance();

    private OnFragmentInteractionListener mListener;
    private TextView mTextCalendarWeek;
    private TextView mTextWeek;
    private List<String> listOfStoreStringsToLoad;
    private Iterator<String> storeIterator;
    private ProgressDialog progressDialog;
    private String mMail;
    private String mPass;
    private SumForStoreAdapter mAdapter;
    private ListView mListView;
    private TextView mTextSum;
    private PieChart chart;

    public WeekStatisticFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WeekStatisticFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeekStatisticFragment newInstance(String mail, String pass) {
        WeekStatisticFragment fragment = new WeekStatisticFragment();
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
        firstDayInWeek = DateUtil.firstDayOfWeek(date);
        lastDayInWeek = DateUtil.lastDayOfWeek(date);
        weekOfYear = DateUtil.calenderWeek(date);
        mTextWeek.setText(dateFormat.format(firstDayInWeek) + "-" + dateFormat.format(lastDayInWeek));
        mTextCalendarWeek.setText("KW " + weekOfYear);
        year = DateUtil.getYear(firstDayInWeek);
        reloadLIst(weekOfYear, year);
    }

    private void reloadLIst(int weekOfYear, int year) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("week", weekOfYear);
        requestParams.put("year", year);
        progressDialog.show();
        MoneyKeeperRestClientWithAuth.post("weekstats.json", requestParams, mMail, mPass, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseArray) {
                Log.d(TAG, "onSuccess " + responseArray.toString());
                ArrayList<SumForStore> sumForStores = null;
                List<PieEntry> entries = new ArrayList<PieEntry>();
                List<Integer> colors = new ArrayList<Integer>();
                float pos=0;
                try {
                    sumForStores = mapper.readValue(responseArray.toString(), new TypeReference<ArrayList<SumForStore>>(){});
                } catch (IOException e) {
                    e.printStackTrace();
                }
                double sum = 0;
                Iterator<SumForStore> iterator = sumForStores.iterator();
                while (iterator.hasNext()) {
                    SumForStore sumForStore = iterator.next();
                    sum += sumForStore.getSum();
                    if(sumForStore.getSum() > 0)
                    {
                        pos+=1;
                        entries.add(new PieEntry(new Float(sumForStore.getSum()), sumForStore.getStore()));
                        colors.add(getBackgroundResourceForName(sumForStore.getStore()));
                    }
                    else
                    {
                        iterator.remove();
                    }
                }
                mTextSum.setText(DecimalFormat.getCurrencyInstance(Locale.GERMANY).format(sum));
                mAdapter = new SumForStoreAdapter(getContext(), sumForStores);
                mListView.setAdapter(mAdapter);

                PieDataSet dataSet = new PieDataSet(entries, "LALA");
                int[] colorArray = new int[colors.size()];
                int i = 0;
                for (Integer color: colors) {
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
                chart.setDrawEntryLabels(false);
                chart.setData(data);
                chart.setEntryLabelColor(R.color.blue);
                chart.setDescription("Ausgabenübersicht");
                chart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);
                chart.invalidate();
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

        mTextCalendarWeek = (TextView) getView().findViewById(R.id.textCalendarWeek);
        mTextWeek = (TextView) getView().findViewById(R.id.textWeek);
        mListView = (ListView) getView().findViewById(R.id.listStoreSumPerWeek);
        mTextSum = (TextView) getView().findViewById(R.id.textSum);
        chart = (PieChart) getView().findViewById(R.id.chart);
        changeDateTo(new Date());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_week_statistic, null, false);
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
                                    changeDateTo(DateUtil.addDays(7, firstDayInWeek));
                                }
                            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                Log.i(TAG, "Left to Right");
                                if(!progressDialog.isShowing())
                                {
                                    changeDateTo(DateUtil.addDays(-7, firstDayInWeek));
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
