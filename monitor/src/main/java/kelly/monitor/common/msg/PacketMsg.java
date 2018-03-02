package kelly.monitor.common.msg;

import kelly.monitor.common.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Created by kelly.li on 2018/2/27.
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
public class PacketMsg {

    private String appCode;
    private List<Packet> packets;

}
