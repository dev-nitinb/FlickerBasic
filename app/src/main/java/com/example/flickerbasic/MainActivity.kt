package com.example.flickerbasic

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.StringRequestListener
import com.example.flickerbasic.adapter.InterestingPhotoAdapter
import com.example.flickerbasic.adapter.RecentPhotoAdapter
import com.example.flickerbasic.model.Photo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    lateinit var rvPhotos: RecyclerView
    lateinit var btnRecent: AppCompatButton
    lateinit var btnInteresting:AppCompatButton
    lateinit var recentPhotoAdapter: RecentPhotoAdapter
    lateinit var intrestingPhotoAdapter: InterestingPhotoAdapter
    private var PERMISSION_REQUEST_CODE=100
    lateinit var mContext: Context
    lateinit var progressDialog :ProgressDialog
    var flcikerApiKey ="6304d3f2cad0483ca8dd86a7956167e5"
    private var TAG="MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext=this
        checkPermission()
    }

    private fun bindUi(){
        rvPhotos= findViewById<RecyclerView>(R.id.rvPhotos)
        btnRecent=findViewById<AppCompatButton>(R.id.btnRecent)
        btnInteresting=findViewById<AppCompatButton>(R.id.btnInteresting)
        progressDialog = ProgressDialog(mContext)

        getRecentPhotos()
        btnRecent.setOnClickListener {
            getRecentPhotos()
        }
        btnInteresting.setOnClickListener {
            getIntrestingPhotos()
        }
    }


    private fun getRecentPhotos(){
        showProgressDialog()
        AndroidNetworking
            .get("https://api.flickr.com/services/rest")
            .addQueryParameter("method", "flickr.photos.getRecent")
            .addQueryParameter("api_key", flcikerApiKey)
            .addQueryParameter("per_page", "10")
            .addQueryParameter("format", "json")
            .addQueryParameter("page", "1")
            .setPriority(Priority.LOW)
            .build()
            .getAsString(object: StringRequestListener {
                override fun onResponse(response: String?) {
                    Log.e(TAG,"Response:$response")
                    var responseString=response!!.replace("jsonFlickrApi(","",true)
                    responseString=responseString.substring(0,responseString.length-1)
                    Log.e(TAG,"Json Response:$responseString")
                    //to create a type literal for Array
                    try {
                        var jsonObject= JSONObject(responseString)
                        var photoJSONObject=jsonObject.getJSONObject("photos")
                        var photoJSONArray=photoJSONObject.getJSONArray("photo")
                        var photoType=object:TypeToken<ArrayList<Photo>>(){}.type
                        var alPhoto = Gson().fromJson(photoJSONArray.toString(), photoType) as ArrayList<Photo>
                        Log.e(TAG,"alPhoto: $alPhoto")
                        recentPhotoAdapter=RecentPhotoAdapter(mContext,alPhoto)
                        rvPhotos.layoutManager=GridLayoutManager(mContext,2)
                        rvPhotos.adapter=recentPhotoAdapter
                        dimissProgressDialog()
                    } catch (e: Exception) {
                        Log.e(TAG,"Error:${e.message}")
                    }
                }

                override fun onError(anError: ANError?) {
                    Log.e(TAG,"Error: ${anError.toString()}")
                }
            })
    }

    private fun getIntrestingPhotos(){
        showProgressDialog()
        AndroidNetworking
            .get("https://api.flickr.com/services/rest")
            .addQueryParameter("method", "flickr.interestingness.getList")
            .addQueryParameter("api_key", flcikerApiKey)
            .addQueryParameter("per_page", "10")
            .addQueryParameter("format", "json")
            .addQueryParameter("page", "1")
            .setPriority(Priority.LOW)
            .build()
            .getAsString(object: StringRequestListener {
                override fun onResponse(response: String?) {
                    Log.e(TAG,"Response:$response")
                    var responseString=response!!.replace("jsonFlickrApi(","",true)
                    responseString=responseString.substring(0,responseString.length-1)
                    Log.e(TAG,"Json Response:$responseString")
                    //to create a type literal for Array
                    try {
                        var jsonObject= JSONObject(responseString)
                        var photoJSONObject=jsonObject.getJSONObject("photos")
                        var photoJSONArray=photoJSONObject.getJSONArray("photo")
                        var photoType=object:TypeToken<ArrayList<Photo>>(){}.type
                        var alPhoto = Gson().fromJson(photoJSONArray.toString(), photoType) as ArrayList<Photo>
                        Log.e(TAG,"alPhoto: $alPhoto")
                        intrestingPhotoAdapter= InterestingPhotoAdapter(mContext,alPhoto)
                        rvPhotos.layoutManager=GridLayoutManager(mContext,2)
                        rvPhotos.adapter=intrestingPhotoAdapter
                        dimissProgressDialog()
                    } catch (e: Exception) {
                        Log.e(TAG,"Error:${e.message}")
                    }
                }

                override fun onError(anError: ANError?) {
                    Log.e(TAG,"Error: ${anError.toString()}")
                }
            })
    }

    private fun checkPermission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)+
            ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
            !=PackageManager.PERMISSION_GRANTED){
            //permission not granted
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),PERMISSION_REQUEST_CODE
                )
            }
        }
        //permission granted
        else{
            bindUi()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISSION_REQUEST_CODE->{
                if(grantResults.isNotEmpty()){
                    var readStorage=grantResults[0]==PackageManager.PERMISSION_GRANTED
                    var writeStorage=grantResults[0]==PackageManager.PERMISSION_GRANTED
                    if(readStorage && writeStorage){
                        bindUi()
                    }
                    else{
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                                PERMISSION_REQUEST_CODE
                            )
                        }
                    }
                }
            }
        }
    }

    fun showProgressDialog(){
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Please Wait")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.show()
        progressDialog.setCancelable(false)
    }

    fun dimissProgressDialog() {
        try {
            if (progressDialog != null) {
                progressDialog.cancel()
                progressDialog.dismiss()
            }
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
    }

}