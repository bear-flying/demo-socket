package cn.itcast.haoke.im.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
// 指定表的名称 不写这个注解也可以 因为默认会以类名的首字母小写 作为集合的名称
@Document(collection = "message")
@Builder
public class Message {

    @Id //@Id是主键标识
    @JsonSerialize(using = ToStringSerializer.class) //序列化的时候 序列化成字符串
    private ObjectId id;
    private String msg;
    /**
     * 消息状态，1-未读，2-已读
     */
    @Indexed // @Indexed代表该字段需要做索引
    private Integer status;
    @Field("send_date") // 通过@Field注解 来指定名称映射（映射mongo中的字段）
    @Indexed
    private Date sendDate;
    @Field("read_date")
    private Date readDate;
    @Indexed
    private User from; // Message对象中嵌套User对象
    @Indexed
    private User to;

}
