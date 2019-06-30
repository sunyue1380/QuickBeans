package cn.schoolwow.quickbeans.entity;

import cn.schoolwow.quickbeans.annotation.Component;

import javax.annotation.Resource;

@Component
public class Report {
    private long id;
    @Resource(name = "talk")
    private Talk talk;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Talk getTalk() {
        return talk;
    }

    public void setTalk(Talk talk) {
        this.talk = talk;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", talk=" + talk +
                '}';
    }
}
