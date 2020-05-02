package com.example.robi.budgetize.ui.adapters.viewpager;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.example.robi.budgetize.R;
import com.example.robi.budgetize.backend.APIs.viewpagerforbankaccounts.CardItem;
import com.example.robi.budgetize.backend.viewmodels.ServicesHandlerViewModel;
import com.example.robi.budgetize.data.remotedatabase.remote.rest.utils.AppConstants;
import com.example.robi.budgetize.data.remotedatabase.remote.rest.utils.HttpUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;


public class LinkBanksCardPagerAdapter extends PagerAdapter implements LinkedBanksCardAdapter {

    private List<CardView> mViews;
    private List<CardItem> mData;
    private float mBaseElevation;
    private Context activityContext;

    //OBP ModelView
    private ServicesHandlerViewModel servicesHandlerViewModel;

    public LinkBanksCardPagerAdapter(Context activityContext, ServicesHandlerViewModel servicesHandlerViewModel) {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
        this.activityContext = activityContext;
        this.servicesHandlerViewModel = servicesHandlerViewModel;
    }

    public void addCardItem(CardItem item) {
        mViews.add(null);
        mData.add(item);
    }

    public boolean existCardItem(String selectedBank) {
        for (CardItem cardItem : mData) {
            if (cardItem.getTitle() != null && cardItem.getTitle().equals(selectedBank)) {
                //Log.d("Msg","TEXTUL ESTE: "+selectedBank +"SI " + cardItem.getTitle());
                return true;
            }
        }
        return false;
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    public SparseArray<String> getBankNameAndDataPosition() {
        SparseArray<String> bankNames = new SparseArray<String>();
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getTitle() != null) {
                bankNames.put(i, mData.get(i).getTitle());
            }
        }
        return bankNames;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.bankaccount_adapter, container, false);
        container.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mData.get(position).getTitle() != null) {
                    //TODO: request to the server asking for auth to the corresponding bank. Have to send an object which contains bank name.(mData.get(position) + the position
                    String requestPath = "/authStatus";
                    HttpUrl.Builder httpBuilder = HttpUrl.parse(AppConstants.SERVER_URL + requestPath).newBuilder();
                    httpBuilder.addQueryParameter("BankName", mData.get(position).getTitle());

                    Request request = new Request.Builder()
                            .url(httpBuilder.build())
                            .build();

                    HttpUtils.getClient().newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                            Log.d("Error: ", "exception at POST" + e.toString());
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                throw new IOException("Unexpected code " + response);
                            } else {
                                Log.d("RESPONSE RECEIVED: ", response.body().string());
                                // do something wih the result
                            }
                        }
                    });

                }
            }
        });

        Button button = (Button) view.findViewById(R.id.bank_account_sync_button);
        ImageView icnView = (ImageView) view.findViewById(R.id.bank_link_img_status);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent myIntent = new Intent(activityContext, OAuthActivity.class);
//                activityContext.startActivity(myIntent);
                if (ServicesHandlerViewModel.obpOAuthOK) {

                    servicesHandlerViewModel.unlinkBankAccount();
                    updateUICard_BankLinked(button, icnView);
//                    servicesHandlerViewModel.getAccounts();
                } else {
//                    need to do the OAUTH
                    servicesHandlerViewModel.doOBPOAuth();//move this to account linking(inside of linkedbankaccount
                }
            }
        });
        updateUICard_BankLinked(button, icnView);

        bind(mData.get(position), view);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    private void updateUICard_BankLinked(Button button, ImageView icnView) {
        if(servicesHandlerViewModel.obpOAuthOK) {
            button.setText("Press To UNLink\nBank Account");
            icnView.setImageResource(R.drawable.ic_bookmark_linked_24dp);
        }else{
            button.setText("Press To Link\nBank Account");
            icnView.setImageResource(R.drawable.ic_bookmark_unlinked_24dp);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(CardItem item, View view) {
        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        TextView contentTextView = (TextView) view.findViewById(R.id.contentTextView);
        titleTextView.setText(item.getTitle());
        contentTextView.setText(item.getText());
    }

    //COMMON FUNCTIONS FOR THIS ACTIVITY
    public void startActivity(Class targetClass) {
        Intent myIntent = new Intent(activityContext, targetClass);
        activityContext.startActivity(myIntent);
        //activityContext.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
