package com.boryana.android.thesis;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;



public class ServerApiRequests extends AsyncTask<String,Void,String> {
//    AlertDialog alertDialog;
    Context ctx;
    public AsyncResponse finish = null;
    public AsyncStart start = null;

    // you may separate this or combined to caller class.
    public interface AsyncResponse {
        void onProcessFinish(String responce);
    }

    // you may separate this or combined to caller class.
    public interface AsyncStart {
        void onProcessStart();
    }


    public ServerApiRequests(Context ctx, AsyncStart start, AsyncResponse finish){
        this.start = start;
        this.finish = finish;
        this.ctx = ctx;
    }


    @Override
    protected void onPreExecute() {
        start.onProcessStart();
    }


    @Override
    protected String doInBackground(String...params) {

        // list with all URLS and their paramethers
//        String reg_url = "http://boryana.comxa.com/android/register.php";
//        String login_url = "http://boryana.comxa.com/android/login.php";

//        String BASE_SERVER_URL = "http://192.168.0.101:8080/server_bori_app/";
        String BASE_SERVER_URL = "http://boryana.comxa.com/android/";

        String reg_url = BASE_SERVER_URL + "register.php";
        String login_url = BASE_SERVER_URL + "login.php";
        String find_friend_requests_count_url = BASE_SERVER_URL + "get_friend_requests.php";
        String get_all_friends_url = BASE_SERVER_URL + "get_all_friends.php";
        String unfriend_user_url = BASE_SERVER_URL + "remove_friend.php";
        String find_users_url = BASE_SERVER_URL + "get_all_users_not_my_friends.php";
        String send_friend_request_url = BASE_SERVER_URL + "send_friend_request.php";
        String set_friend_request_response_url = BASE_SERVER_URL + "set_friend_request_response.php";
        String change_password_url = BASE_SERVER_URL + "change_password.php";
        String get_report_coords_url = BASE_SERVER_URL + "get_coordinates.php";




        // variable containing method name
        String method = params[0];


        // === REGISTER === write
        if (method.equals("register")) {
            //String user_id = params[1];
            String reg_name = params[1];
            String reg_pass = params[2];
            String reg_date = params[3];

            try {
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                //httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter BW = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

//                String data =
                        //URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8") + "&" +
                String data = URLEncoder.encode("reg_name", "UTF-8") + "=" + URLEncoder.encode(reg_name, "UTF-8") + "&"
                            + URLEncoder.encode("reg_pass", "UTF-8") + "=" + URLEncoder.encode(reg_pass, "UTF-8") + "&"
                            + URLEncoder.encode("reg_date", "UTF-8") + "=" + URLEncoder.encode(reg_date, "UTF-8");

                BW.write(data);
                BW.flush();
                BW.close();

                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader BR = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));

                String response = "";
                String line = "";
                while ((line = BR.readLine()) != null) {
                    response += line;
                }

                BR.close();
                IS.close();
                //httpURLConnection.connect();
                httpURLConnection.disconnect();
                return response;


            } catch (Exception e) {
                e.printStackTrace();
                return "Server error";

            }

        }

        // === LOGIN === read
        else if (method.equals("login")) {

            String login_name = params[1];
            String login_pass = params[2];

            try {
                URL url = new URL(login_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter BW = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data =
                        URLEncoder.encode("login_name", "UTF-8") + "=" + URLEncoder.encode(login_name, "UTF-8") + "&" +
                        URLEncoder.encode("login_pass", "UTF-8") + "=" + URLEncoder.encode(login_pass, "UTF-8");

                BW.write(data);
                BW.flush();
                BW.close();
                OS.close();

                //Toast.makeText(ctx, "login_name", Toast.LENGTH_LONG).show();
                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader BR = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));

                String response = "";
                String line = "";
                while ((line = BR.readLine()) != null) {
                    response += line;
                }
                BR.close();
                IS.close();
                httpURLConnection.disconnect();
                return response;
//                return "Login Success...";


            } catch (Exception e) {
                e.printStackTrace();
                return "Server error";
            }
        }

        // === GET ALL FRIEND REQUEST SENT TO ME ===
        else if (method.equals("find_friend_requests_count_url")) {
            String userId = params[1];

            try {
                URL url = new URL(find_friend_requests_count_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter BW = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data =
                        URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");

                BW.write(data);
                BW.flush();
                BW.close();
                OS.close();

                //Toast.makeText(ctx, "login_name", Toast.LENGTH_LONG).show();
                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader BR = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));

                String response = "";
                String line = "";
                while ((line = BR.readLine()) != null) {
                    response += line;
                }
                BR.close();
                IS.close();
                httpURLConnection.disconnect();
                return response;
//                return "Login Success...";


            } catch (Exception e) {
                e.printStackTrace();
                return "Server error";
            }
        }

        // === GET ALL MY FRIEND ===
        else if (method.equals("get_all_friends_url")){
            String userId = params[1];

            try {
                URL url = new URL(get_all_friends_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter BW = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data =
                        URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");

                BW.write(data);
                BW.flush();
                BW.close();
                OS.close();

                //Toast.makeText(ctx, "login_name", Toast.LENGTH_LONG).show();
                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader BR = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));

                String response = "";
                String line = "";
                while ((line = BR.readLine()) != null) {
                    response += line;
                }
                BR.close();
                IS.close();
                httpURLConnection.disconnect();
                return response;
//                return "Login Success...";


            } catch (Exception e) {
                e.printStackTrace();
                return "Server error";
            }
        }

        // === REMOVE FRIEND FROM MY LIST OF FRIENDS ===
        else if (method.equals("unfriend_user_url")){
            String user_id_one = params[1];
            String user_id_two = params[2];

            try {
                URL url = new URL(unfriend_user_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter BW = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data =
                        URLEncoder.encode("user_id_one", "UTF-8") + "=" + URLEncoder.encode(user_id_one, "UTF-8") + "&" +
                        URLEncoder.encode("user_id_two", "UTF-8") + "=" + URLEncoder.encode(user_id_two, "UTF-8");

                BW.write(data);
                BW.flush();
                BW.close();
                OS.close();

                //Toast.makeText(ctx, "login_name", Toast.LENGTH_LONG).show();
                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader BR = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));

                String response = "";
                String line = "";
                while ((line = BR.readLine()) != null) {
                    response += line;
                }
                BR.close();
                IS.close();
                httpURLConnection.disconnect();
                return response;
//                return "Login Success...";


            } catch (Exception e) {
                e.printStackTrace();
                return "Server error";
            }
        }

        // === SEARCH FOR USERS DIFFERENT THAN MY FRIENDS ===
        else if (method.equals("find_users_url")){
            String user_id = params[1];
            String search_name = params[2];

            try {
                URL url = new URL(find_users_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter BW = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data =
                        URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8") + "&" +
                                URLEncoder.encode("search_name", "UTF-8") + "=" + URLEncoder.encode(search_name, "UTF-8");

                BW.write(data);
                BW.flush();
                BW.close();
                OS.close();

                //Toast.makeText(ctx, "login_name", Toast.LENGTH_LONG).show();
                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader BR = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));

                String response = "";
                String line = "";
                while ((line = BR.readLine()) != null) {
                    response += line;
                }
                BR.close();
                IS.close();
                httpURLConnection.disconnect();
                return response;
//                return "Login Success...";


            } catch (Exception e) {
                e.printStackTrace();
                return "Server error";
            }
        }


        // === SEND A FRIEND REQUEST TO A USER ===
        else if (method.equals("send_friend_request_url")){
            String user_id_one = params[1];
            String user_id_two = params[2];

            try {
                URL url = new URL(send_friend_request_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter BW = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data =
                        URLEncoder.encode("user_id_one", "UTF-8") + "=" + URLEncoder.encode(user_id_one, "UTF-8") + "&" +
                                URLEncoder.encode("user_id_two", "UTF-8") + "=" + URLEncoder.encode(user_id_two, "UTF-8");

                BW.write(data);
                BW.flush();
                BW.close();
                OS.close();

                //Toast.makeText(ctx, "login_name", Toast.LENGTH_LONG).show();
                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader BR = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));

                String response = "";
                String line = "";
                while ((line = BR.readLine()) != null) {
                    response += line;
                }
                BR.close();
                IS.close();
                httpURLConnection.disconnect();
                return response;
//                return "Login Success...";


            } catch (Exception e) {
                e.printStackTrace();
                return "Server error";
            }
        }


        // === SEND A RESPONCE TO A FRIEND REQUEST FROM A USER  ===
        else if (method.equals("set_friend_request_response_url")){
            String user_id_one = params[1];
            String user_id_two = params[2];
            String answer = params[3];

            try {
                URL url = new URL(set_friend_request_response_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter BW = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data =
                        URLEncoder.encode("user_id_one", "UTF-8") + "=" + URLEncoder.encode(user_id_one, "UTF-8") + "&" +
                        URLEncoder.encode("user_id_two", "UTF-8") + "=" + URLEncoder.encode(user_id_two, "UTF-8") + "&" +
                        URLEncoder.encode("answer", "UTF-8") + "=" + URLEncoder.encode(answer, "UTF-8");

                BW.write(data);
                BW.flush();
                BW.close();
                OS.close();

                //Toast.makeText(ctx, "login_name", Toast.LENGTH_LONG).show();
                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader BR = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));

                String response = "";
                String line = "";
                while ((line = BR.readLine()) != null) {
                    response += line;
                }
                BR.close();
                IS.close();
                httpURLConnection.disconnect();
                return response;
//                return "Login Success...";


            } catch (Exception e) {
                e.printStackTrace();
                return "Server error";
            }
        }


        // === CHANGE_USERS_PASSWORD  ===
        else if (method.equals("change_password_url")){
            String user_id = params[1];
            String new_password = params[2];

            try {
                URL url = new URL(change_password_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter BW = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data =
                        URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8") + "&" +
                                URLEncoder.encode("new_password", "UTF-8") + "=" + URLEncoder.encode(new_password, "UTF-8");

                BW.write(data);
                BW.flush();
                BW.close();
                OS.close();

                //Toast.makeText(ctx, "login_name", Toast.LENGTH_LONG).show();
                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader BR = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));

                String response = "";
                String line = "";
                while ((line = BR.readLine()) != null) {
                    response += line;
                }
                BR.close();
                IS.close();
                httpURLConnection.disconnect();
                return response;
//                return "Login Success...";


            } catch (Exception e) {
                e.printStackTrace();
                return "Server error";
            }
        }


        // === GET COORDINATES FOR A REPORT  ===
        else if (method.equals("get_report_coords_url")){
            String user_id = params[1];
            String search_date = params[2];

            try {
                URL url = new URL(get_report_coords_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter BW = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data =
                        URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8") + "&" +
                                URLEncoder.encode("search_date", "UTF-8") + "=" + URLEncoder.encode(search_date, "UTF-8");

                BW.write(data);
                BW.flush();
                BW.close();
                OS.close();

                //Toast.makeText(ctx, "login_name", Toast.LENGTH_LONG).show();
                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader BR = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));

                String response = "";
                String line = "";
                while ((line = BR.readLine()) != null) {
                    response += line;
                }
                BR.close();
                IS.close();
                httpURLConnection.disconnect();
                return response;
//                return "Login Success...";


            } catch (Exception e) {
                e.printStackTrace();
                return "Server error";
            }
        }

        return null;
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        finish.onProcessFinish(result);
    }

}

