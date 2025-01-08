package nws.mc.servers.config.player$data;

import nws.mc.servers.config.command.CommandConfig;
import nws.mc.servers.data$type.PosData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PD {
    public PosData home;
    public List<PosData> backs;
    public PD() {
        home = new PosData();
        backs = new ArrayList<>();
    }

    public PosData getHome() {
        return home;
    }

    public @Nullable PosData getBack() {
        if (backs == null || backs.isEmpty()) return null;
        PosData pd = backs.getLast().copy();
        backs.removeLast();
        return pd;
    }

    public void addBack(PosData pd) {
        if (backs == null) backs = new ArrayList<>();
        else if (backs.size() >= CommandConfig.I.getDatas().backMaxCount) backs.removeFirst();
        backs.add(pd);
    }

}
