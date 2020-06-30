package com.hwm.val;

import com.hwm.domain.MsUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsDetailVal {

    private int msStatue=0;
    private int remainSeconds=0;
    private GoodsVal goodsVal;
    private MsUser msUser;


}
