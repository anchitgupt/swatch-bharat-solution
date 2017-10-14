package ateam.com.clean.StateCity;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 12/10/17.
 */
//// TODO: 12/10/17 if JSON data not found in that case we have to use this  
public class City {

    RequestQueue requestQueue;
    Context context;
    List<String> list;
    ProgressDialog dialog;

    public City(Context context) {
        this.context = context;
    }

    public List<String> getCity(final String state){
        Log.e("City",state);
        requestQueue = Volley.newRequestQueue(context);
        dialog = new ProgressDialog(context);
        dialog.setMessage("Loading..");
        dialog.show();
        JsonArrayRequest jsonArrayRequest =
                new JsonArrayRequest("https://api.myjson.com/bins/urt55", new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            String[] str = new String[response.length()];
                            list = new ArrayList<>();
                            int k=0;
                            for (int i = 0; i < response.length(); i++) {
                                if(response.getJSONObject(i).getString("state").contentEquals(state)) {
                                    str[i] = response.getJSONObject(i).getString("city_name");
                                    //Log.e("City",str[i]);
                                    list.add(str[i]);
                                    k++;
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(context, "Can't Fetch the Cities", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonArrayRequest);
        return list;
    }

}
