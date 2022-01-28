package com.itheima.common.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.IOException;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/18 12:06
 * @description 标题
 * @package com.itheima.common.pojo
 */
public class Long2StringSerializer extends JsonSerializer<Long> {//泛型 指定是原本的数据类型


    //方法的目的就是 将原本的数据类型 进行处理（转换成你业务需要的数据类型）
    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if(value!=null){
            gen.writeString(value.toString());
        }
    }

    public final static JsonSerializer instance = new Long2StringSerializer();
}
