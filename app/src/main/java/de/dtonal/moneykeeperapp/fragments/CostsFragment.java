package de.dtonal.moneykeeperapp.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;
import de.dtonal.moneykeeperapp.R;
import de.dtonal.moneykeeperapp.adapter.CostsAdapter;
import de.dtonal.moneykeeperapp.connection.MoneyKeeperRestClientWithAuth;
import de.dtonal.moneykeeperapp.connection.Urls;
import de.dtonal.moneykeeperapp.model.Cost;
import de.dtonal.moneykeeperapp.model.ModelMapper;


/**
 * A fragment to see a list of given costs.
 */
public class CostsFragment extends Fragment {
    private static final String TAG = "CostsFragment";

    private ListView mListView;
    private CostsAdapter mAdapter;

    private Button mDeleteButton;

    private OnFragmentInteractionListener mListener;
    private ProgressDialog mProgressDialog;
    private Iterator<Cost> mDeleteIterator;


    public CostsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CostsFragment.
     */
    public static CostsFragment newInstance() {
        CostsFragment fragment = new CostsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_costs, container, false);
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
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteSelectedCosts();
            }
        });

        reloadList();
    }

    private void reloadList() {
        RequestParams requestParams = new RequestParams();
        MoneyKeeperRestClientWithAuth.get(Urls.PATH_COSTS, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray responseArray) {
                Log.d(TAG, "onSuccess " + responseArray.toString());
                ArrayList<Cost> costs = null;
                try {
                    costs = ModelMapper.getInstance().readValue(responseArray.toString(), new TypeReference<ArrayList<Cost>>() {
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mAdapter = new CostsAdapter(getContext(), costs);
                mAdapter.registerDataSetObserver(new DataSetObserver() {
                    @Override
                    public void onChanged() {
                        Log.d(TAG, "onChanged " + mAdapter.getSelectedIds());
                        if (mAdapter.getSelectedIds().size() > 0) {
                            mDeleteButton.setVisibility(View.VISIBLE);
                        } else {
                            mDeleteButton.setVisibility(View.GONE);
                        }
                    }
                });
                mListView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(int a, Header[] h, String s, Throwable t) {
                Log.d(TAG, "onFailure " + t.toString());

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "onFailure " + errorResponse.toString());

                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });
    }

    private void deleteSelectedCosts() {
        SparseBooleanArray selectedIds = mAdapter.getSelectedIds();

        ArrayList<Cost> selectedCosts = new ArrayList<>();
        for (int i = 0; i < selectedIds.size(); i++) {
            selectedCosts.add((Cost) mListView.getItemAtPosition(selectedIds.keyAt(i)));
        }

        mDeleteIterator = selectedCosts.iterator();

        mProgressDialog = new ProgressDialog(this.getContext());
        mProgressDialog.show();

        deleteNextCost();
    }

    private void deleteNextCost() {
        if (mDeleteIterator == null || !mDeleteIterator.hasNext()) {
            mProgressDialog.hide();
            reloadList();

        } else {
            Cost costToDelete = mDeleteIterator.next();

            MoneyKeeperRestClientWithAuth.delete("costs/" + costToDelete.getId().toString() + ".json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d(TAG, "onSuccess " + response.toString());
                    deleteNextCost();

                }

                @Override
                public void onFailure(int a, Header[] h, String s, Throwable t) {
                    Log.d(TAG, "onFailure " + t.toString());
                    mProgressDialog.hide();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d(TAG, "onFailure " + errorResponse.toString());
                    mProgressDialog.hide();

                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

            });

        }

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
