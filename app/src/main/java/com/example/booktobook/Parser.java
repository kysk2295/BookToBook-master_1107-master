package com.example.booktobook;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    BookData vo = new BookData();
    String query="";

    public ArrayList<BookData> connectNaver(int start, String myQuery, ArrayList<BookData> list){
        Log.d("Parser","start!");
        try{
//           query= URLEncoder.encode(myQuery,"utf8");
            //네이버에 요청할때 쿼리가 간다.
            int count =10;//검색 결과 10건 표시
            String urlStr="https://openapi.naver.com/v1/search/book_adv.xml?d_isbn="+myQuery;
            //URL클래스를 생성하여 위의 경로로 접근

            URL url= new URL(urlStr);
            Log.d("Parser","ready");



            //url클래스의 연결 정보를 connection 에게 전달
            HttpURLConnection connection=(HttpURLConnection)url.openConnection();

            //네이버 오픈 API는 GET방식 지원(요청하는 정보가 노출됨)
            connection.setRequestMethod("GET");

            //네이버 인증 처리
            //발급받은 ID
            String CLIENT_ID = "AxT_Cffh7tMJYt6Hgqvf";
            String CLIENT_PASSWORD = "MZVhoXVAmW";
            connection.setRequestProperty("X-Naver-Client-Id",CLIENT_ID);
            //발급받은 secret
            connection.setRequestProperty("X-Naver-Client-Secret",CLIENT_PASSWORD);

            //위의 URL을 수행하여 받아올 자원을 대입할 객체
            XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
            XmlPullParser parser= factory.newPullParser();
            parser.setInput(connection.getInputStream(),null);//결과물을 inputstream으로 읽어옴 parser:xml을 들고온다.

            Log.d("Parser","read");
            //파서를 통하여 각 요소들을 반복수행 처리
            int parserEvent= parser.getEventType();
            while (parserEvent != XmlPullParser.END_DOCUMENT){


                if(parserEvent== XmlPullParser.START_TAG) {

                    String tagName= parser.getName();
                    if(tagName.equalsIgnoreCase("title")) { //title태그 발견
                        vo=new BookData();
                        String title= parser.nextText();

                        //네이버는 검색어의 강조를 위해<b> 태그를 붙여서 결과를
                        //반환하는데, 이것을 제거하기 위해 정규식을 사용
                        //java.utill.package
                        Pattern pattern= Pattern.compile("<.*?>");//한글자 태그 패턴
                        Matcher matcher= pattern.matcher(title);
                        if(matcher.find()){
                            String s_title=matcher.replaceAll("");
                            vo.title = s_title;
                        }else{
                            vo.title = title;
                        }

                    }else if(tagName.equalsIgnoreCase("image")){
                        String img= parser.nextText();
                        vo.book_image = img;
                    }else if(tagName.equalsIgnoreCase("author")){

                        String author=parser.nextText();

                        Pattern pattern= Pattern.compile("<.*?>");//한글자 태그 패턴
                        Matcher matcher= pattern.matcher(author);
                        if(matcher.find()){
                            String s_author=matcher.replaceAll("");
                            vo.author = s_author;
                        }else{
                            vo.author = author;
                        }

                    }else if(tagName.equalsIgnoreCase("publisher")){
                        String publisher=parser.nextText();
                        vo.publisher = publisher;
                        list.add(vo);
                    }

                }
                parserEvent=parser.next();//다음요소, 커서가 다음칸으로 내려감
                //END_DOCUMENT:문서의 끝
                //Xml의 끝을 만날때 까지 반복
            }//while

        }catch(Exception e){

        }


        Log.d("Parser-auth",vo.author);
        Log.d("Parser-img",vo.book_image);
        Log.d("Parser-publisher",vo.publisher);
        Log.d("Parser-title",vo.title);

        return list;
    }//connectNaver()

}