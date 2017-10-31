package com.devdroid.sleepassistant.base;

import java.util.ArrayList;

/**
 * Created by litingdong on 16/4/19.
 */
public class CYUser {
    //id
    public String id;
    //好友请求id
    public String request_id;
    //头像
    public String icon;
    //手机号
    public String phone_number;
    //用户名
    public String username;
    public String nickname;
    public String age;
    public String sex;
    public String height;
    public String weight;
    public String message;
    public String application_id;
    //注册时间
    public  String create_time;
    //昵称
    public String name;
    //个人简介
    public String description;
    //电子邮箱
    public String email;
    //性别
    public String gender;
    //生日
    public String birthday;
    //真实姓名
    public String real_name;
    //身份证号
    public String id_number;
    //所在地区
    public ArrayList<String> location;

    //省
    public String province;
    //市
    public String city;
    //县
    public String county;
    // qq
    public String qq;

    public String role;
    public String other_number;
    public String unit1;
    public String unit2;
    public String profession;

    //是否实名认证
    public String is_verified;
    //是否身份认证
    public String is_role_verified;
    //积分
    public String score;
    public String adept_field;
    public String adept_skill;
    public String expect_role;
    public String follow_field;
    public String follow_skill;



    //标签
    public ArrayList<String> tags;
    public ArrayList<String> tag_ids;
    public ArrayList<String> tag_likers;

    //点赞数
    public int liker_count;
    //关注数
    public int followed_count;
    //谁关注了自己
    public int follower_count;
    //好友数
    public int friend_count;
    //粉丝数
    public int fan_count;
    //浏览次数
    public int visitor_count;
    //标记我是否关注该用户
    public Boolean is_like;

    //需求的申请列表中
    public String team_id;

    //是否eid认证
    private  Boolean is_eid_verified;

    public String getnickname() {
        return nickname;
    }

    public void setnickname(String nickname) {
        this.nickname = nickname;
    }
    public String getmessage() {
        return message;
    }

    public void setmessage(String message) {
        this.message = message;
    }
    public String getapplication_id() {
        return application_id;
    }

    public void setapplication_id(String application_id) {
        this.application_id = application_id;
    }
    public String getage() {
        return age;
    }

    public void setage(String age) {
        this.age = age;
    }
    public String getsex() {
        return sex;
    }

    public void setsex(String sex) {
        this.sex = sex;
    }
    public String getheight() {
        return height;
    }

    public void setheight(String height) {
        this.height = height;
    }
    public String getweight() {
        return weight;
    }

    public void setweight(String weight) {
        this.weight = weight;
    }

    public int getFriend_count() {
        return friend_count;
    }

    public void setFriend_count(int friend_count) {
        this.friend_count = friend_count;
    }

    public int getFan_count() {
        return follower_count;
    }

    public void setFan_count(int follower_count) {
        this.follower_count = follower_count;
    }

    public int getVisitor_count() {
        return visitor_count;
    }

    public void setVisitor_count(int visitor_count) {
        this.visitor_count = visitor_count;
    }

    public Boolean getIs_like() {
        return is_like;
    }

    public void setIs_like(Boolean is_like) {
        this.is_like = is_like;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public String getId_number() {
        return id_number;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public ArrayList<String> getLocation() {
        return location;
    }

    public void setLocation(ArrayList<String> location) {
        this.location = location;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public ArrayList<String> getTag_likers() {
        return tag_likers;
    }

    public void setTag_likers(ArrayList<String> tag_likers) {
        this.tag_likers = tag_likers;
    }

    public ArrayList<String> getTag_ids() {
        return tag_ids;
    }

    public void setTag_ids(ArrayList<String> tag_ids) {
        this.tag_ids = tag_ids;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon= icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLiker_count() {
        return liker_count;
    }

    public void setLiker_count(int liker_count) {
        this.liker_count = liker_count;
    }

    public int getFollowed_count() {
        return followed_count;
    }

    public void setFollowed_count(int follow_count) {
        this.followed_count = follow_count;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getOther_number() {
        return other_number;
    }

    public void setOther_number(String other_number) {
        this.other_number = other_number;
    }

    public String getUnit1() {
        return unit1;
    }

    public void setUnit1(String unit1) {
        this.unit1 = unit1;
    }

    public String getUnit2() {
        return unit2;
    }

    public void setUnit2(String unit2) {
        this.unit2 = unit2;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public String getIs_verified() {
        return is_verified;
    }

    public void setIs_verified(String is_verified) {
        this.is_verified = is_verified;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getAdept_field() {
        return adept_field;
    }

    public void setAdept_field(String adept_field) {
        this.adept_field = adept_field;
    }

    public String getAdept_skill() {
        return adept_skill;
    }

    public void setAdept_skill(String adept_skill) {
        this.adept_skill = adept_skill;
    }

    public String getExpect_role() {
        return expect_role;
    }

    public void setExpect_role(String expect_role) {
        this.expect_role = expect_role;
    }

    public String getFollow_field() {
        return follow_field;
    }

    public void setFollow_field(String follow_field) {
        this.follow_field = follow_field;
    }

    public String getFollow_skill() {
        return follow_skill;
    }

    public void setFollow_skill(String follow_skill) {
        this.follow_skill = follow_skill;
    }

    public int getFollower_count() {
        return follower_count;
    }

    public void setFollower_count(int follower_count) {
        this.follower_count = follower_count;
    }

    public Boolean getIs_eid_verified() {
        return is_eid_verified;
    }

    public void setIs_eid_verified(Boolean is_eid_verified) {
        this.is_eid_verified = is_eid_verified;
    }

    public String getIs_role_verified() {
        return is_role_verified;
    }

    public void setIs_role_verified(String is_role_verified) {
        this.is_role_verified = is_role_verified;
    }
}
