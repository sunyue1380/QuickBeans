package cn.schoolwow.quickbeans.entity;

import cn.schoolwow.quickbeans.annotation.Component;
import cn.schoolwow.quickbeans.annotation.Scope;
import cn.schoolwow.quickbeans.annotation.ScopeType;

import javax.annotation.Resource;

@Component(name = "talk")
public class Talk {
    private long id;
    private String content;
    @Resource(name = "user")
    private User user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Talk{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", user=" + user +
                '}';
    }
}
