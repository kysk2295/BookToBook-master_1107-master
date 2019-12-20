package com.example.booktobook;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import androidx.fragment.app.Fragment;

import java.io.BufferedInputStream;
import java.net.URL;
import java.util.ArrayList;


public class EnrollFragment extends Fragment {

    Intent intent;
    Parser parser;
    ImageButton enrollButton;
    ArrayList<BookData> arrayList;
    Bitmap bitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enroll,container,false);

        enrollButton = view.findViewById(R.id.imgButton_fragment_enroll);
        arrayList = new ArrayList<>();

        //등록 버튼을 누르면
        enrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
                intentIntegrator.forSupportFragment(EnrollFragment.this).initiateScan();
            }
        });

        return view;
    }

    //스캔되는 곳
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // QR코드/ 바코드를 스캔한 결과
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // result.getFormatName() : 바코드 종류
        // result.getContents() : 바코드 값
        Log.d("EnrollFragment",result.getContents());

        //스캔이 완료되면 네이버 api사용
        parser = new Parser();

        new NaverAsync().execute(result.getContents());
    }



    class NaverAsync extends AsyncTask<String, Void, ArrayList<BookData>> {

        @Override
        protected ArrayList<BookData> doInBackground(String... strings) {
            //각종 반복이나 제어등 주된 처리 로직을 담당하는 메서드
            //strings[0]---->검색어;
            //String ... :길이에 제한이 없는 배열

            return parser.connectNaver(1, strings[0], arrayList);
            //백그라운드에서 검색어와 리스트를 파라미터를 갖는다.
        }

        @Override
        protected void onPostExecute(ArrayList<BookData> bookVOS) {
            new DownloadImageTask().execute(arrayList.get(0).getBook_image());


            Log.d("EnrollFragment-auth",arrayList.get(0).getAuthor());
            Log.d("EnrollFragment-img",arrayList.get(0).getBook_image());
            Log.d("EnrollFrag-publisher",arrayList.get(0).getPublisher());
            Log.d("EnrollFragment-title",arrayList.get(0).getTitle());

            intent = new Intent(getContext(),EnrollActivity.class);
            intent.putExtra("title",arrayList.get(0).getTitle());
            intent.putExtra("publisher",arrayList.get(0).getPublisher());
            intent.putExtra("auth",arrayList.get(0).getAuthor());
            intent.putExtra("img_url",arrayList.get(0).book_image);

        }

        private class DownloadImageTask extends AsyncTask{

            @Override
            protected Bitmap doInBackground(Object[] objects) {
                try{
                    URL img_url= new URL(arrayList.get(0).getBook_image());
                    //이미지를 bite단위로 가져온다.A
                    BufferedInputStream bis= new BufferedInputStream(img_url.openStream());
                    //다시 이미지를 bite에서 비트맵으로 조합한다.
                    bitmap= BitmapFactory.decodeStream(bis);
                    bis.close();
                    return bitmap;
                }catch (Exception e){
                    Log.e("EnrollError",e.toString());

                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                intent.putExtra("img",bitmap);
                startActivity(intent);
                arrayList = new ArrayList<BookData>();

//                pictureEnroll.setVisibility(View.GONE);
//                imageView.setImageBitmap(bitmap);
//                imageView.setVisibility(View.VISIBLE);
            }
        }
    }




}


