package com.example.hustholetest1.view.homescreen.mypage;

public class Card {
    String ID,date,content,up,talk,star;
//    Integer img_up,img_talk,img_star,threePoint;
//    , Integer img_star, Integer img_up, Integer img_talk, Integer threePoint
    public Card(String ID, String date, String content, String up, String talk, String star){
        this.ID = ID;
        this.date = date;
        this.content = content;
        this.up = up;
        this.talk = talk;
        this.star = star;
//        this.img_up = img_up;
//        this.img_talk = img_talk;
//        this.img_star = img_star;
//        this.threePoint = threePoint;

    }

    public String getID() {
        return ID;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public String getUp() {
        return up;
    }

    public String getTalk() {
        return talk;
    }

    public String getStar() {
        return star;
    }

//    public Integer getImg_up() {
//        return img_up;
//    }
//
//    public Integer getImg_talk() {
//        return img_talk;
//    }
//
//
//    public Integer getImg_star() {
//        return img_star;
//    }
//
//    public Integer getThreePoint() { return threePoint; }

    public void setID(String ID) {
        this.ID = ID;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setUp(String up) {
        this.up = up;
    }
    public void setTalk(String talk) {
        this.talk = talk;
    }
    public void setStar(String star) {
        this.star = star;
    }

//    public void setImg_up(Integer img_up) {
//        this.img_up = img_up;
//    }
//
//    public void setImg_talk(Integer img_talk) {
//        this.img_talk = img_talk;
//    }
//
//    public void setImg_star(Integer img_star) {
//        this.img_star = img_star;
//    }
//
//    public void setThreePoint(Integer threePoint) {
//        this.threePoint = threePoint;
//    }
}
