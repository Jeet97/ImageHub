package com.example.android.imagehub;

/**
 * Created by Jeet on 04-09-2017.
 */

public class getsetclass {


    String artname;
    String url;
    String pic_userpic,user,time;
    String likes, imgname;

    public String getLikes() {
        return likes;
    }

    public String getImgname() {
        return imgname;
    }

    public String getPic_userpic() {
        return pic_userpic;
    }

    public String getUser() {
        return user;
    }

    public String getTime() {
        return time;
    }

    public String getUrl() {

        return url;
    }

    public getsetclass( String artname,  String url,String pic_userpic, String user, String time, String likes, String imgname)
    {

        this.artname = artname;

        this.url = url;

        this.pic_userpic = pic_userpic;
        this.user = user;
        this.time = time;
        this.likes = likes;
        this.imgname = imgname;
    }







    public String getArtname() {
        return artname;
    }
}
