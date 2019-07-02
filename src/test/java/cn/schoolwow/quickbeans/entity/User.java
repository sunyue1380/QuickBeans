package cn.schoolwow.quickbeans.entity;

import cn.schoolwow.quickbeans.annotation.Component;
import cn.schoolwow.quickbeans.annotation.Scope;
import cn.schoolwow.quickbeans.annotation.ScopeType;

@Component(name = "user")
@Scope(ScopeType.prototype)
public class User {
    private long id;
    private String username;
    private String password;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void init(){
        System.out.println("调用了User类的初始化方法!");
    }
    public void destroy(){
        System.out.println("调用了User类的销毁方法!");
    }
}
