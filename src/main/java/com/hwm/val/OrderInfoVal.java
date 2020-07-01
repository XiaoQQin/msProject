package com.hwm.val;

import com.hwm.domain.OrderInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfoVal {
    private GoodsVal goods;
    private OrderInfo order;
}
