package com.hwm.rabbitmq;


import com.hwm.domain.MsUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MSMessage {

    private MsUser msUser;
    private long goodsId;

}
