package com.ahmad.makkitvofficial.ui.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.approve.Approval;
import com.paypal.checkout.approve.OnApprove;
import com.paypal.checkout.cancel.OnCancel;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.config.PaymentButtonIntent;
import com.paypal.checkout.config.SettingsConfig;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.ProcessingInstruction;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.error.ErrorInfo;
import com.paypal.checkout.error.OnError;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.CaptureOrderResult;
import com.paypal.checkout.order.Order;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PaymentButton;
import com.paypal.checkout.shipping.OnShippingChange;
import com.paypal.checkout.shipping.ShippingChangeActions;
import com.paypal.checkout.shipping.ShippingChangeData;
import com.ahmad.makkitvofficial.Provider.PrefManager;
import com.ahmad.makkitvofficial.R;
import com.ahmad.makkitvofficial.api.apiClient;
import com.ahmad.makkitvofficial.api.apiRest;
import com.ahmad.makkitvofficial.entity.ApiResponse;
import com.ahmad.makkitvofficial.entity.Plan;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PlansActivity extends AppCompatActivity {

    private PlanAdapter planAdapter;
    private LinearLayout relative_layout_plans;
    private RelativeLayout relative_layout_loading;
    private RecyclerView recycler_view_plans;
    private RelativeLayout relative_layout_select_plan;
    private RelativeLayout relative_layout_select_plan_pp;

    private GridLayoutManager gridLayoutManager;
    private final List<Plan> planList = new ArrayList<>();
    private Integer selected_id = -1;
    private Integer selected_pos = -1;


    private static final int PAYPAL_REQUEST_CODE = 7777;

    private static CheckoutConfig config ;

    private String method = "null";
    private ProgressDialog dialog_progress;
    private TextView text_view_activity_plans_method;
    PaymentButton payPalButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);
        Bundle bundle = getIntent().getExtras() ;
        PrefManager prf= new PrefManager(PlansActivity.this.getApplicationContext());
        this.method =  bundle.getString("method");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            config = new CheckoutConfig(
                    getApplication(),
                    prf.getString("APP_PAYPAL_CLIENT_ID"),
                    Environment.SANDBOX,
                    getResources().getString(R.string.package_name_without_underscores)+"://paypalpay",
                    CurrencyCode.valueOf(new PrefManager(getApplicationContext()).getString("APP_CURRENCY").toUpperCase()),
                    UserAction.PAY_NOW,
                    PaymentButtonIntent.CAPTURE,
                    new SettingsConfig(
                            false,
                            false
                    )
            );

            PayPalCheckout.setConfig(config);
        }

        initView();
        initAction();
        loadPlans();
    }

    private void initView() {
            this.payPalButton = findViewById(R.id.payPalButton);

            this.relative_layout_select_plan_pp = findViewById(R.id.relative_layout_select_plan_pp);
            this.text_view_activity_plans_method = findViewById(R.id.text_view_activity_plans_method);
            this.relative_layout_select_plan = findViewById(R.id.relative_layout_select_plan);
            this.relative_layout_plans = findViewById(R.id.relative_layout_plans);
            this.relative_layout_loading = findViewById(R.id.relative_layout_loading);
            this.recycler_view_plans = findViewById(R.id.recycler_view_plans);
            this.gridLayoutManager = new GridLayoutManager(this, 1);
            this.planAdapter = new PlanAdapter();
            switch (method){
                case "pp":
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        relative_layout_select_plan_pp.setVisibility(View.VISIBLE);
                    }
                    text_view_activity_plans_method.setText("PayPal");
                    this.relative_layout_select_plan.setVisibility(View.GONE);
                    break;
                case "cc":
                    relative_layout_select_plan_pp.setVisibility(View.GONE);

                    text_view_activity_plans_method.setText("Credit card");
                    this.relative_layout_select_plan.setVisibility(View.VISIBLE);

                    break;
                case "cash":
                    relative_layout_select_plan_pp.setVisibility(View.GONE);
                    this.relative_layout_select_plan.setVisibility(View.VISIBLE);

                    text_view_activity_plans_method.setText("Cash");
                    break;

            }

    }

    private void initAction() {

        PrefManager prf= new PrefManager(PlansActivity.this.getApplicationContext());
        Integer id_user=  Integer.parseInt(prf.getString("ID_USER"));
        if (prf.getString("LOGGED").toString().equals("TRUE")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                payPalButton.setup(
                        createOrderActions -> {


                            DecimalFormat df = new DecimalFormat("0.00");


                            ArrayList purchaseUnits = new ArrayList<>();
                            purchaseUnits.add(
                                    new PurchaseUnit.Builder()
                                            .amount(
                                                    new Amount.Builder()
                                                            .currencyCode(CurrencyCode.valueOf(new PrefManager(getApplicationContext()).getString("APP_CURRENCY").toUpperCase()))
                                                            .value(df.format(planList.get(selected_pos).getPrice()).replace(",", "."))
                                                            .build()
                                            )
                                            .customId("user:" + id_user + ",pack:" + planList.get(selected_pos).getId())
                                            .build()
                            );
                            Order order = new Order(
                                    OrderIntent.CAPTURE,
                                    new AppContext.Builder()
                                            .userAction(UserAction.PAY_NOW)
                                            .build(),
                                    purchaseUnits,
                                    ProcessingInstruction.ORDER_COMPLETE_ON_PAYMENT_APPROVAL

                            );
                            createOrderActions.create(order, s -> {
                                Log.i("createOrderActions", s.toString());

                            });

                        },
                        new OnApprove() {
                            @Override
                            public void onApprove(@NotNull Approval approval) {
                                approval.getOrderActions().capture(result -> {
                                    if(result instanceof  CaptureOrderResult.Success) {
                                        submitPayPal(approval.getData().getOrderId().toString());
                                    }else{
                                        Toasty.error(PlansActivity.this,getResources().getString(R.string.operation_canceller), Toast.LENGTH_SHORT).show();

                                    }

                                });


                            }
                        },
                        new OnShippingChange() {
                            @Override
                            public void onShippingChanged(@NotNull ShippingChangeData shippingChangeData, @NotNull ShippingChangeActions shippingChangeActions) {

                            }
                        },
                        new OnCancel() {
                            @Override
                            public void onCancel() {
                                Toasty.error(PlansActivity.this,getResources().getString(R.string.operation_canceller), Toast.LENGTH_SHORT).show();

                            }
                        },
                        new OnError() {
                            @Override
                            public void onError(@NotNull ErrorInfo errorInfo) {
                                Toasty.error(PlansActivity.this,getResources().getString(R.string.operation_canceller), Toast.LENGTH_SHORT).show();

                            }
                        }

                );
            }
        }else{
            finish();
            Intent intent = new Intent(PlansActivity.this,LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
            finish();
        }
        this.relative_layout_select_plan.setOnClickListener(v->{
            if (selected_id == -1){
                Toasty.error(getApplicationContext(),getResources().getString(R.string.select_plan),Toast.LENGTH_LONG).show();
            }else{
                switch (method){
                    case "pp":
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            payPalButton.setVisibility(View.VISIBLE);
                        }
                        text_view_activity_plans_method.setText("PayPal");
                        this.relative_layout_select_plan.setVisibility(View.GONE);
                        break;
                    case "cc":
                        Intent intent = new Intent(PlansActivity.this,StripeActivity.class);
                        intent.putExtra("plan",selected_id);
                        intent.putExtra("name",planList.get(selected_pos).getTitle());
                        intent.putExtra("price",planList.get(selected_pos).getPrice());
                        startActivity(intent);
                        finish();
                        break;
                    case "cash":
                        Intent intent1 = new Intent(PlansActivity.this,CashActivity.class);
                        intent1.putExtra("plan",selected_id);
                        intent1.putExtra("name",planList.get(selected_pos).getTitle());
                        intent1.putExtra("price",planList.get(selected_pos).getPrice());
                        startActivity(intent1);
                        finish();
                        break;

                }
            }
        });
    }

    private void loadPlans() {
        relative_layout_plans.setVisibility(View.GONE);
        relative_layout_loading.setVisibility(View.VISIBLE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Plan>> call = service.getPlans();
        call.enqueue(new Callback<List<Plan>>() {
            @Override
            public void onResponse(Call<List<Plan>> call, final Response<List<Plan>> response) {
                if (response.isSuccessful()){
                    for (int i = 0; i <response.body().size() ; i++) {
                        planList.add(response.body().get(i));
                    }
                    relative_layout_plans.setVisibility(View.VISIBLE);
                    relative_layout_loading.setVisibility(View.GONE);

                    if (response.isSuccessful()) {
                        planList.clear();
                        for (int i = 0; i < response.body().size(); i++) {
                            planList.add(response.body().get(i));
                        }
                    }
                    recycler_view_plans.setHasFixedSize(true);
                    recycler_view_plans.setLayoutManager(gridLayoutManager);
                    recycler_view_plans.setAdapter(planAdapter);
                    planAdapter.notifyDataSetChanged();
                }else{
                    relative_layout_plans.setVisibility(View.GONE);
                    relative_layout_loading.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<List<Plan>> call, Throwable t) {
                relative_layout_plans.setVisibility(View.GONE);
                relative_layout_loading.setVisibility(View.GONE);
            }
        });
    }

    public class PlanAdapter extends  RecyclerView.Adapter<PlanAdapter.PlanHolder>{


        @Override
        public PlanHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan,parent, false);
            PlanHolder mh = new PlanHolder(v);
            return mh;
        }
        @Override
        public void onBindViewHolder(PlanHolder holder, final int position) {
            holder.text_view_plan_discount.setVisibility(View.GONE);
            holder.text_view_plan_description.setVisibility(View.GONE);

            if (planList.get(position).getDiscount() !=  null){
                if (planList.get(position).getDiscount().length()>0){
                    holder.text_view_plan_discount.setVisibility(View.VISIBLE);
                    holder.text_view_plan_discount.setText(planList.get(position).getDiscount());
                }
            }
            if (planList.get(position).getDescription() !=  null){
                if (planList.get(position).getDescription().length()>0){
                    holder.text_view_plan_description.setVisibility(View.VISIBLE);
                    holder.text_view_plan_description.setText(planList.get(position).getDescription());
                }
            }
            holder.text_view_plan_title.setText(planList.get(position).getTitle());
            holder.text_view_plan_price.setText(planList.get(position).getPrice()+ " "+new PrefManager(getApplicationContext()).getString("APP_CURRENCY"));
            if(planList.get(position).getId()   == selected_id){
                holder.card_view_plan.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
            }else{
                holder.card_view_plan.setCardBackgroundColor(getResources().getColor(R.color.dark_gray));
            }
            holder.card_view_plan.setOnClickListener(v -> {
                selected_id = planList.get(position).getId();
                selected_pos = position;
                planAdapter.notifyDataSetChanged();
            });
        }
        @Override
        public int getItemCount() {
            return planList.size();
        }
        public class PlanHolder extends RecyclerView.ViewHolder {
            private final TextView text_view_plan_discount;
            private final CardView card_view_plan;
            private final TextView text_view_plan_title;
            private final TextView text_view_plan_description;
            private final TextView text_view_plan_price;

            public PlanHolder(View itemView) {
                super(itemView);
                this.text_view_plan_discount =  (TextView) itemView.findViewById(R.id.text_view_plan_discount);
                this.card_view_plan =  (CardView) itemView.findViewById(R.id.card_view_plan);
                this.text_view_plan_title =  (TextView) itemView.findViewById(R.id.text_view_plan_title);
                this.text_view_plan_description =  (TextView) itemView.findViewById(R.id.text_view_plan_description);
                this.text_view_plan_price =  (TextView) itemView.findViewById(R.id.text_view_plan_price);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void submitPayPal(String trans_id){
        dialog_progress= ProgressDialog.show(this, null,getResources().getString(R.string.operation_progress), true);

        PrefManager prf= new PrefManager(PlansActivity.this.getApplicationContext());
        if (prf.getString("LOGGED").toString().equals("TRUE")){
            Integer id_user=  Integer.parseInt(prf.getString("ID_USER"));
            String   key_user=  prf.getString("TOKEN_USER");
            Retrofit retrofit = apiClient.getClient();
                apiRest service = retrofit.create(apiRest.class);
                Call<ApiResponse> call = service.SubscriptionPayPal(id_user,key_user,trans_id,selected_id);
                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful()){
                            if (response.body().getCode()==200){
                                Intent intent = new Intent(PlansActivity.this, FinishActivity.class);
                                intent.putExtra("title", response.body().getMessage());
                                startActivity(intent);
                                finish();
                                prf.setString("NEW_SUBSCRIBE_ENABLED","TRUE");
                            }else if (response.body().getCode()==201){
                                Intent intent = new Intent(PlansActivity.this, FinishActivity.class);
                                intent.putExtra("title", response.body().getMessage());
                                startActivity(intent);
                                finish();
                            }else{
                                Toasty.error(PlansActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toasty.error(PlansActivity.this,getResources().getString(R.string.operation_canceller), Toast.LENGTH_SHORT).show();
                        }
                        dialog_progress.dismiss();
                    }
                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toasty.error(PlansActivity.this,getResources().getString(R.string.operation_canceller), Toast.LENGTH_SHORT).show();
                        dialog_progress.dismiss();
                    }
                });
        }else{
            finish();
            Intent intent = new Intent(PlansActivity.this,LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
            dialog_progress.dismiss();

        }
    }

}
