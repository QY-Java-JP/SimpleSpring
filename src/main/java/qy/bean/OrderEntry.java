package qy.bean;

import lombok.Data;

@Data
public class OrderEntry<T> {
    // 序号
    private int order;
    // 数据
    private T data;

    public OrderEntry(int order, T data) {
        this.order = order;
        this.data = data;
    }
}
