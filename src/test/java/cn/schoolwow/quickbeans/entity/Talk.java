package cn.schoolwow.quickbeans.entity;

import cn.schoolwow.quickbeans.annotation.Component;

import javax.annotation.Resource;

@Component(name = "talk")
public class Talk {
    private long id;
    @Resource
    private User user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
