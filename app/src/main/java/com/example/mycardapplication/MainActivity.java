package com.example.mycardapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import ikidou.reflect.TypeBuilder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewAdapter recyclerViewAdapter;
    private Button button;

    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        button = findViewById(R.id.button);

        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);

        button.setOnClickListener(v-> {
            button.setEnabled(false);
            button.setText("请求中");
            getListFromServer();
        });
    }

    void getListFromServer() {
        Request request = new Request.Builder()
                .url("http://ws.attect.studio:2333/jewel-s-free101j/getNewList.php")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.d("Test",result);
                ArrayList<CardInfo> data = gson.fromJson(result, TypeBuilder.newInstance(ArrayList.class).addTypeParam(CardInfo.class).build());
                recyclerViewAdapter.setData(data);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        button.setEnabled(true);
                        button.setText("再抽十次！");
                    }
                });
            }
        });
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView number, name, description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            number = itemView.findViewById(R.id.number);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
        }

        public void setData(int position,CardInfo cardInfo){
            number.setText("#"+position);
            name.setText(cardInfo.name);
            description.setText(cardInfo.comment);
            GlideApp.with(imageView).load(cardInfo.imageUrl).into(imageView);
        }
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {
        private ArrayList<CardInfo> data = new ArrayList<>();

        public void setData(ArrayList<CardInfo> data) {
            this.data.clear();
            this.data.addAll(data);
            runOnUiThread(this::notifyDataSetChanged);

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.listitem, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.setData(position,data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }


}
