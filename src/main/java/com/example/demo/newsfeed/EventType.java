package com.example.demo.newsfeed;


public enum EventType {

    MYPOSTREPLY("내 글에 리플을 달았어요"),
    FOLLOWERLIKE("팔로워가 좋아요를 눌렀어요"),
    MYPOSTLIKE("내 글에 좋아요를 눌렀어요"),
    FOLLOWERCOMMENT("팔로워가 댓글을 달았어요"),
    FOLLOWERPOST("팔로워가 글을 썼어요"),
    FOLLOWSTART("날 팔로잉 했어요");

    private final String displayName;

    EventType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
