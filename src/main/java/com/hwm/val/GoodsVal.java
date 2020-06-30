package com.hwm.val;

import com.hwm.domain.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsVal extends Goods {
    private Double msPrice;
    private Integer stockCount;
    private Date startTime;
    private Date endTime;
}
