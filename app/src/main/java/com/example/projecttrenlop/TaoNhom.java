package com.example.projecttrenlop;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.projecttrenlop.Adapter.GroupAdater;
import com.example.projecttrenlop.Model.Group;
import com.example.projecttrenlop.Model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TaoNhom extends AppCompatActivity {
    Button btnTaoNhom,btnThamGiaNhom;

    TextView tvDanhSach;
    ListView lvDanhSachNhom;
    ArrayList<Group> arrGr;
    GroupAdater adapter;

    private  void anhxa(){
        btnTaoNhom=(Button) findViewById(R.id.btnTaoNhom);
        btnThamGiaNhom=(Button) findViewById(R.id.btnThamGiaNhom);
        tvDanhSach=(TextView) findViewById(R.id.tvDanhSachThamGia);
        lvDanhSachNhom=(ListView) findViewById(R.id.lvGroupList);
//        arrGr =new ArrayList<>();
//       arrGr.add(new Group("Nhóm 1"));
//        arrGr.add(new Group("Nhóm 2"));
//        arrGr.add(new Group("Nhóm 3"));



        btnThamGiaNhom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiaglogThamGia();
            }
        });
        btnTaoNhom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogTaoNhom();
            }
        });

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tao_nhom);


       anhxa();

        //adapter =new GroupAdater(this,R.layout.dong_group,arrGr);
      //  lvDanhSachNhom.setAdapter(adapter);
        try {
            Intent intent=getIntent();
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("data_user"));
            User user = new User();
            user.setId(jsonObject.optInt("id"));
            user.setName(jsonObject.opt("name").toString());
            init( user);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
    private  void DiaglogThamGia(){
        Dialog dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_tham_gia_nhom);

        TextView TVThem=dialog.findViewById(R.id.textThamGia);
        EditText edit= dialog.findViewById(R.id.editTextMaNhom);
        Button btnXacNhan=dialog.findViewById(R.id.buttonThamGia);
        Button btnHuy=dialog.findViewById(R.id.buttonHuyThamGia);

        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           //Lấy mã nhập từ edit
                String maNhom=edit.getText().toString();
                if(maNhom.equals("123")) {
                    Intent intentThamGia = new Intent(TaoNhom.this, NhomThamGia.class);
                    startActivity(intentThamGia);
                }else{
                    // Hiển thị thông báo lỗi nếu mã không đúng
                    Toast.makeText(TaoNhom.this, "Mã nhóm không hợp lệ", Toast.LENGTH_SHORT).show();
                }


            }
        });
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();



    }
    private void DialogTaoNhom(){
        Dialog dialog =new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_tao_nhom);

        TextView edittextTenNhom=dialog.findViewById(R.id.editTextGroupName);
        TextView editViewChuDe=dialog.findViewById(R.id.editTextChude);
        Button btnTao=dialog.findViewById(R.id.buttonCreateGroup);

        btnTao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        dialog.show();
    }
    private Group ConvertGroup(JSONObject jsonObject) throws JSONException {
        Group group = new Group();
        User user = new User();
        group.setIdgroup(jsonObject.optInt("idgroup"));
        group.setName_group(jsonObject.optString("name_group"));
        user.setId(jsonObject.getJSONObject("leader").optInt("id"));
        user.setName(jsonObject.getJSONObject("leader").optString("name"));
        group.setUser(user);
        return group;
    }

    private void init( User user) {
        //Log.d("Group_state", String.valueOf(user.getId()));
        lvDanhSachNhom = (ListView) findViewById(R.id.lvGroupList);
        arrGr = new ArrayList<>();
//        groupArrayList.add(new Group());
//        groupArrayList.add(new Group());
        RequestQueue requestQueue = Volley.newRequestQueue(TaoNhom.this);
        String url = "http://" + getString(R.string.url) + ":8080/getListGroup";
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(Integer.parseInt("0"),user.toJSON());

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        //Toast.makeText(getActivity(), jsonArray.toString(), Toast.LENGTH_SHORT).show();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, jsonArray
                , new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Group_state", response.toString());
                //Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT).show();
                ArrayList<JSONObject> list = new ArrayList<>();
                try {
                    for(int i=0 ; i < response.length() ; i++){
                        list.add((JSONObject) response.get(i));

                    }
                    for(JSONObject j : list){
                        Group group = ConvertGroup(j);
                        arrGr.add(new Group(group.getName_group()));
                        Log.d("nameG", group.getName_group());
                    }
                    adapter = new GroupAdater(TaoNhom.this, R.layout.dong_group, arrGr);

                    lvDanhSachNhom.setAdapter(adapter);
                    lvDanhSachNhom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            try {
                                Log.d("data_checl", user.getId() + " " + list.get(position).getJSONObject("leader").optInt("id"));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            //Toast.makeText(getActivity(), list.get(position).toString(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(TaoNhom.this, NhomThamGia.class);
                            intent.putExtra("dataGroup", list.get(position).toString());
                            intent.putExtra("user_name", user.getName());
                            intent.putExtra("id_user", user.getId());
                            //Log.d("data_sendg", user.getName());
                            startActivity(intent);
                        }
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TaoNhom.this, error.toString(), Toast.LENGTH_SHORT).show();

            }
        });
        requestQueue.add(jsonArrayRequest);
    }

}