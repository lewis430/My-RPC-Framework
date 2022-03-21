package top.liuyisong.test;

import top.liuyisong.rpc.annotation.Service;
import top.liuyisong.rpc.api.ByeService;

/**
 */
@Service
public class ByeServiceImpl implements ByeService {

    @Override
    public String bye(String name) {
        return "bye, " + name;
    }
}
